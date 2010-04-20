package org.marketcetera.ors;

import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportSavedListener;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;

/**
 * A persister of trade messages (replies) sent by the ORS to
 * clients. It also handles mapping of messages to actors/viewers, via
 * either replies previously persisted, or via an in-memory cache.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ReplyPersister
    implements ReportSavedListener
{

    // INSTANCE DATA.

    private final ReportHistoryServices mHistoryServices; 
    private final OrderInfoCache mCache;


    // CONSTRUCTORS.

    /**
     * Creates a new persister which relies on the given report
     * history services provider for persistence operations.
     *
     * @param historyServices The report history services provider.
     */    

    public ReplyPersister
        (ReportHistoryServices historyServices,
         OrderInfoCache cache)
    {
        mHistoryServices=historyServices;
        mCache=cache;
    }


    // ReportSavedListener.

    @Override
    public void reportSaved
        (ReportBase report,
         boolean status)
    {
        OrderID orderID=report.getOrderID();
        if (orderID==null) {
            return;
        }
        OrderInfo info=getCache().get(orderID);
        if (info==null) {
            return;
        }
        info.setERPersisted(status);
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
     * Returns the receiver's cache of order information.
     *
     * @return The cache.
     */

    private OrderInfoCache getCache()
    {
        return mCache;
    }

    /**
     * Persists the given message, which, while doing so, may be
     * modified. Persistence may be effected synchronously or
     * asynchronously.
     *
     * @param msg The message.
     */

    public void persistReply
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
    }

    /**
     * Adds the given outgoing order message, with the given actorID,
     * to the receiver's cache, and returns the new cache entry.
     *
     * @param msg The message.
     * @param actorID The actor ID.
     *
     * @return The new cache entry, or null if one could not be
     * created.
     */

    public OrderInfo addOutgoingOrder
        (Message msg,
         UserID actorID)
    {
        OrderID orderID;
        try {
            orderID=new OrderID(msg.getString(ClOrdID.FIELD));
        } catch(FieldNotFound ex) {
            Messages.RP_ADD_TO_CACHE_FAILED.warn(this,ex,actorID,msg);
            return null;
        }
        OrderID origOrderID;
        try {
            origOrderID=new OrderID(msg.getString(OrigClOrdID.FIELD));
        } catch(FieldNotFound ex) {
            origOrderID=null;
        }
        return getCache().put(orderID,origOrderID,actorID);
    }

    /**
     * Returns the principals associated with the given message.
     *
     * @param msg The message.
     * @param isAck True if the request is made for the purposes of
     * processing an ORS ack.
     *
     * @return The principals. Any of its properties may be null if
     * the associated principal cannot be determined; that includes
     * the special case of returning {@link Principals#UNKNOWN}.
     */

    public Principals getPrincipals
        (Message msg,
         boolean isAck)
    {
        OrderID orderID;
        try {
            orderID=new OrderID(msg.getString(ClOrdID.FIELD));
        } catch(FieldNotFound ex) {
            return Principals.UNKNOWN;
        }
        OrderInfo info=getCache().get(orderID);

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

        if (!info.isViewerIDSet()) {
            orderID=info.getOrigOrderID();
            // orderID cannot be null because, if it were, the viewer
            // would have been set to the actor.
            if (orderID==null) {
                throw new IllegalStateException();
            }
            OrderInfo parentInfo=getCache().get(orderID);
            UserID viewerID;
            if (parentInfo!=null) {
                // The parent's viewer should have been set.
                if (!parentInfo.isViewerIDSet()) {
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

        // Update cache entry flags.

        if (isAck) {
            info.setAckProcessed(true);
        } else {
            info.setResponseProcessed(true);
        }
        info.setMessageProcessed(msg);

        // Return result.

        return new Principals(info.getActorID(),info.getViewerID());
    }
}
