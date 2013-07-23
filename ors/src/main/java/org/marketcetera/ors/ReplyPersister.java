package org.marketcetera.ors;

import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.history.ReportSavedListener;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
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
        if(msg instanceof ExecutionReport) {
            ReportCache.INSTANCE.cache((ExecutionReport)msg);
        }
        try {
            getHistoryServices().save((ReportBase)msg);
        } catch (PersistenceException ex) {
            Messages.RP_PERSIST_ERROR.error(this,ex,msg);
            return;
        }
    }
    /**
     * 
     *
     *
     * @param inMessage
     */
    public void deleteMessage(TradeMessage inMessage)
    {
        if(!(inMessage instanceof ReportBase)) {
            return;
        }
        if(inMessage instanceof ExecutionReport) {
            ReportCache.INSTANCE.clear((ExecutionReport)inMessage);
        }
        try {
            getHistoryServices().delete((ReportBase)inMessage);
        } catch (PersistenceException e) {
            Messages.RP_PERSIST_ERROR.error(this,
                                            e,
                                            inMessage);
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

    public Principals getPrincipals(Message inMessage,
                                     boolean isAck)
    {
        OrderID orderID;
        try {
            orderID = getOrderIDFrom(inMessage,
                                     ClOrdID.FIELD);
        } catch (FieldNotFound ignored) {
            Messages.RP_COULD_NOT_DETERMINE_PRINCIPALS.warn(ReplyPersister.class,
                                                            ClOrdID.FIELD,
                                                            inMessage);
            return Principals.UNKNOWN;
        }
        OrderInfo info;
        if(orderID != null) {
            info = getCache().get(orderID);
            if(info != null){
            	return getPrincipalsFromInfo(inMessage,
            	                             isAck,
            	                             info);
            }
			try {
				Principals principals = getHistoryServices().getPrincipals(orderID);
				if(principals != null &&
				   principals.getActorID() != null	&&
				   principals.getViewerID() != null) {
					return principals;
				}
			} catch (PersistenceException ex) {
				Messages.RP_GET_FROM_DB_FAILED.warn(this, ex, orderID);
				return Principals.UNKNOWN;
			} 
        }
        // hmm, couldn't find info from the orderID,
        //  try using the origOrderID
        OrderID origOrderID;
        try {
            origOrderID = getOrderIDFrom(inMessage,
                                         OrigClOrdID.FIELD);
        } catch (FieldNotFound e) {
            Messages.RP_COULD_NOT_DETERMINE_PRINCIPALS.warn(ReplyPersister.class,
                                                            OrigClOrdID.FIELD,
                                                            inMessage);
            return Principals.UNKNOWN;
        }
        if(origOrderID == null) {
            Messages.RP_NO_ORDER_INFO.warn(ReplyPersister.class,
                                           inMessage);
            return Principals.UNKNOWN;
        }
        info = getCache().get(origOrderID);
        if(info != null){
        	return getPrincipalsFromInfo(inMessage,
                    isAck,
                    info);
        }
		try {
			Principals principals = getHistoryServices().getPrincipals(origOrderID);
			if(principals != null &&
			   principals.getActorID() != null	&&
			   principals.getViewerID() != null) {
				return principals;
			}
		} catch (PersistenceException ex) {
			Messages.RP_GET_FROM_DB_FAILED.warn(this, ex, orderID);
			return Principals.UNKNOWN;
		} 
        
        // so now, we're stuck, we don't have any other way of getting the principals 
        SLF4JLoggerProxy.warn(ReplyPersister.class,
                              "Message {} could not be mapped to a known order so the principals cannot be determined", // TODO this message needs to go to the order catalog
                              inMessage);
        return Principals.UNKNOWN;
    }
    /**
     * Constructs an <code>OrderID</code> object from the given field of the given <code>Message</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @param inField an <code>int</code> value
     * @return an <code>OrderID</code> value or <code>null</code> if the given field does not exist on the given <code>Message</code>
     * @throws FieldNotFound if an error occurs retrieving the field from the <code>Message</code>
     */
    private OrderID getOrderIDFrom(Message inMessage,
                                   int inField)
            throws FieldNotFound
    {
        if(inMessage.isSetField(inField)) {
            return new OrderID(inMessage.getString(inField));
        }
        return null;
    }
    /**
     * Gets the <code>Principals</code> associated with the given <code>Message</code> and <code>OrderInfo</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @param isAck a <code>boolean</code> value
     * @param inInfo an <code>OrderInfo</code> value
     * @return a <code>Principals</code> value
     * @throws IllegalStateException if the given <code>OrderInfo</code> is malformed
     */
    private Principals getPrincipalsFromInfo(Message inMessage,
                                             boolean isAck,
                                             OrderInfo inInfo)
    {
    	OrderID orderID = null;
        
    	if (!inInfo.isViewerIDSet()) {
            orderID=inInfo.getOrigOrderID();
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
            inInfo.setViewerID(viewerID);
        }

        // Update cache entry flags.

        if (isAck) {
            inInfo.setAckProcessed(true);
        } else {
            inInfo.setResponseProcessed(true);
        }
        inInfo.setMessageProcessed(inMessage);

        // Return result.

        return new Principals(inInfo.getActorID(),inInfo.getViewerID());
    }
}
