package org.marketcetera.ors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;

/**
 * A persister of trade messages (replies) sent by the ORS to
 * clients. It also handles mapping of messages to actors.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ReplyPersister
{

    // INSTANCE DATA.

    private final ReportHistoryServices mHistoryServices; 
    private final Map<String,UserID> mMap=
        new ConcurrentHashMap<String,UserID>();


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
     * Returns the receiver's map of order to actor IDs.
     *
     * @return The map.
     */

    private Map<String,UserID> getMap()
    {
        return mMap;
    }

    /**
     * Persists the given message, which, while doing so, may be
     * modified. Returns the ID of the regular user who may view this
     * message.
     *
     * @param msg The message.
     *
     * @return The viewer ID. It may be null.
     */

    public UserID persistReply
        (TradeMessage msg)
    {
        if (!(msg instanceof ReportBase)) {
            return null;
        }
        UserID viewerID=null;
        try {
            viewerID=getHistoryServices().save((ReportBase)msg);
        } catch (PersistenceException ex) {
            Messages.RP_PERSIST_ERROR.error(this,ex,msg);
        }
        Messages.RP_PERSISTED_REPLY.info(this,msg);        
        // TODO(MT): until database lookup is ready, use memory mapper only.
        // getMap().remove(((ReportBase)msg).getOrderID().getValue());
        return viewerID;
    }

    /**
     * Associates the given actor ID with the given message.
     *
     * @param msg The message.
     * @param actorID The actor ID.
     */

    public void addActorID
        (Message msg,
         UserID actorID)
    {
        String orderID;
        try {
            orderID=msg.getString(ClOrdID.FIELD);
        } catch(FieldNotFound ex) {
            Messages.RP_ADD_TO_MAP_FAILED.info(this,ex,actorID,msg);
            return;
        }
        getMap().put(orderID,actorID);
    }

    /**
     * Returns the actor ID associated with the given message.
     *
     * @param category The logging category.
     * @param msg The message.
     *
     * @return The actor ID. It may be null if the actor cannot be
     * determined.
     */

    public UserID getActorID
        (Message msg)
    {
        String orderID;
        try {
            orderID=msg.getString(ClOrdID.FIELD);
        } catch(FieldNotFound ex) {
            Messages.RP_GET_FROM_MAP_FAILED.info(this,ex,msg);
            return null;
        }
        UserID userID=getMap().get(orderID);
        if (userID!=null) {
            return userID;
        }
        // TODO(MT): database lookup.
        return userID;
    }
}
