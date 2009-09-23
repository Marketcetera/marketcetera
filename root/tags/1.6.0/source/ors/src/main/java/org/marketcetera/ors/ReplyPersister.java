package org.marketcetera.ors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;

/**
 * A persister of trade messages (replies) sent by the ORS to
 * clients. It also handles mapping of messages to actors/viewers, via
 * either replies previously persisted, or via an in-memory map (used
 * only until the replies are persisted).
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ReplyPersister
{

    // CLASS DATA.

    /**
     * The information maintained within the in-memory map for each
     * order (until a reply is persisted). Upon construction, the
     * viewer ID is either marked unknown-at-present (if this order is
     * a child of an earlier one), or set to the actor ID (if it's the
     * root of a chain). A boolean is used to distinguish
     * unknown-at-present (to be lazily researched when needed)
     * vs. unknown-upon-investigation (research could not deduce a
     * viewer).
     */

    private static final class OrderInfo
    {
        private final OrderID mOrderID;
        private final OrderID mOrigOrderID;
        private final UserID mActorID;
        private boolean mViewerSet;
        private UserID mViewerID;

        OrderInfo
            (OrderID orderID,
             OrderID origOrderID,
             UserID actorID)
        {
             mOrderID=orderID;
             mOrigOrderID=origOrderID;
             mActorID=actorID;
             if (getOrigOrderID()==null) {
                 setViewerID(getActorID());
             }
        }

        OrderID getOrderID()
        {
            return mOrderID;
        }

        OrderID getOrigOrderID()
        {
            return mOrigOrderID;
        }

        UserID getActorID()
        {
            return mActorID;
        }

        boolean getViewerSet()
        {
            return mViewerSet;
        }

        UserID getViewerID()
        {
            return mViewerID;
        }

        void setViewerID
            (UserID viewerID)
        {
            mViewerSet=true;
            mViewerID=viewerID;
        }
    }


    // INSTANCE DATA.

    private final ReportHistoryServices mHistoryServices; 
    private final Map<OrderID,OrderInfo> mMap=
        new ConcurrentHashMap<OrderID,OrderInfo>();


    // CONSTRUCTORS.

    /**
     * Creates a new persister which relies on the given report
     * history services provider for persistence operations.
     *
     * @param historyServices The report history services provider.
     */    

    public ReplyPersister
        (ReportHistoryServices historyServices)
    {
        mHistoryServices=historyServices;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's report history services provider.
     *
     * @return The provider.
     */

    private ReportHistoryServices getHistoryServices()
    {
        return mHistoryServices;
    }

    /**
     * Returns the receiver's in-memory map of order ID to order
     * information.
     *
     * @return The map.
     */

    private Map<OrderID,OrderInfo> getMap()
    {
        return mMap;
    }

    /**
     * Persists the given message, which, while doing so, may be
     * modified.
     *
     * @param msg The message.
     */

    public synchronized void persistReply
        (TradeMessage msg)
    {
        if (!(msg instanceof ReportBase)) {
            return;
        }
        try {
            getHistoryServices().save((ReportBase)msg);
        } catch (PersistenceException ex) {
            Messages.RP_PERSIST_ERROR.error(this,ex,msg);
            return;
        }
        Messages.RP_PERSISTED_REPLY.info(this,msg);
        OrderID orderID=((ReportBase)msg).getOrderID();
        if (orderID!=null) {
            getMap().remove(orderID);
        }
    }

    /**
     * Adds the given outgoing order message, with the given actorID,
     * to the receiver's in-memory map.
     *
     * @param msg The message.
     * @param actorID The actor ID.
     */

    public void addOutgoingOrder
        (Message msg,
         UserID actorID)
    {
        OrderID orderID;
        try {
            orderID=new OrderID(msg.getString(ClOrdID.FIELD));
        } catch(FieldNotFound ex) {
            Messages.RP_ADD_TO_MAP_FAILED.warn(this,ex,actorID,msg);
            return;
        }
        OrderID origOrderID;
        try {
            origOrderID=new OrderID(msg.getString(OrigClOrdID.FIELD));
        } catch(FieldNotFound ex) {
            origOrderID=null;
        }
        // Using a ConcurrentHashMap eliminates the need to sychronize
        // here, which is essential to keep this method efficient as
        // it is on the execution path where we need to minimize
        // latency.
        getMap().put(orderID,new OrderInfo(orderID,origOrderID,actorID));
    }

    /**
     * Returns the principals associated with the given message.
     *
     * @param msg The message.
     *
     * @return The principals. Any of its properties may be null if
     * the associated principal cannot be determined; that includes
     * the special case of returning {@link Principals#UNKNOWN}.
     */

    public synchronized Principals getPrincipals
        (Message msg)
    {
        OrderID orderID;
        try {
            orderID=new OrderID(msg.getString(ClOrdID.FIELD));
        } catch(FieldNotFound ex) {
            return Principals.UNKNOWN;
        }
        OrderInfo info=getMap().get(orderID);

        // The map entry has been removed hence the actor/view
        // information has moved into the database.

        if (info==null) {
            try {
                return getHistoryServices().getPrincipals(orderID);
            } catch (PersistenceException ex) {
                Messages.RP_GET_FROM_DB_FAILED.warn(this,ex,orderID);
                return Principals.UNKNOWN;
            }
        }

        // Part of (or all of) the order chain is in the map. If the
        // viewer has not yet been determined, copy the parent's
        // viewer; the parent's viewer is assumed to have been set
        // because it should not be possible to create a child before
        // this method is invoked at least once on the parent prior to
        // sending the parent to the ORS client.

        if (!info.getViewerSet()) {
            orderID=info.getOrigOrderID();
            // orderID cannot be null because, if it were, the viewer
            // would have been set to the actor.
            if (orderID==null) {
                throw new IllegalStateException();
            }
            OrderInfo parentInfo=getMap().get(orderID);
            UserID viewerID;
            if (parentInfo!=null) {
                // The parent's viewer should have been set.
                if (!parentInfo.getViewerSet()) {
                    throw new IllegalStateException();
                }
                viewerID=parentInfo.getViewerID();
            } else {
                try {
                    viewerID=getHistoryServices().getPrincipals(orderID).
                        getViewerID();
                } catch (PersistenceException ex) {
                    Messages.RP_GET_FROM_DB_FAILED.warn(this,ex,orderID);
                    viewerID=null;
                }
            }
            info.setViewerID(viewerID);
        }

        // Return result.

        return new Principals(info.getActorID(),info.getViewerID());
    }
}
