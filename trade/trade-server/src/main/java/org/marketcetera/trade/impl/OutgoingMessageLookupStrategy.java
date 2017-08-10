package org.marketcetera.trade.impl;

import java.util.Iterator;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.dao.PersistentOutgoingMessage;
import org.marketcetera.trade.dao.PersistentOutgoingMessageDao;
import org.marketcetera.trade.dao.QPersistentOutgoingMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.querydsl.core.BooleanBuilder;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Looks up the owner of a message based on the outgoing messages table.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OutgoingMessageLookupStrategy
        implements IdentifyOwnerStrategy
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.IdentifyOwnerStrategy#getOwnerOf(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID)
     */
    @Override
    public UserID getOwnerOf(Message inMessage,
                             SessionID inSessionId,
                             BrokerID inBrokerId)
    {
        SLF4JLoggerProxy.debug(this,
                               "Determing owner of {} using outgoing messages",
                               inMessage);
        String orderId = null;
        // does this message have an order id of some kind?
        if(inMessage.isSetField(quickfix.field.ClOrdID.FIELD)) {
            try {
                orderId = inMessage.getString(quickfix.field.ClOrdID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        } else if(inMessage.isSetField(quickfix.field.OrderID.FIELD)) {
            try {
                orderId = inMessage.getString(quickfix.field.OrderID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
        if(orderId == null) {
            // we can't search outgoing orders for the owner of this message because we don't have an order id to match up with
            SLF4JLoggerProxy.debug(this,
                                   "{} has no order id field, so the owner cannot be determined from the outgoing order table",
                                   inMessage);
            return null;
        } else {
            BooleanBuilder where = new BooleanBuilder();
            where = where.and(QPersistentOutgoingMessage.persistentOutgoingMessage.brokerId.eq(inBrokerId));
            where = where.and(QPersistentOutgoingMessage.persistentOutgoingMessage.orderId.eq(orderId));
            Sort sort = new Sort(Sort.Direction.DESC,
                                 QPersistentOutgoingMessage.persistentOutgoingMessage.lastUpdated.getMetadata().getName(),
                                 QPersistentOutgoingMessage.persistentOutgoingMessage.msgSeqNum.getMetadata().getName());
            PageRequest pageRequest = new PageRequest(0,
                                                      1,
                                                      sort);
            // this query, as structured, cannot return multiple rows, but there could be multiple matches in the db and the
            //  matches could each be owned by a different user. this is not a perfect technique as, for example, the original
            //  order could be masked by subsequent order status requests. what if the order status requests were owned by
            //  a different user from the original order? does that even make sense?
            Iterable<PersistentOutgoingMessage> candidates = outgoingMessageDao.findAll(pageRequest);
            Iterator<PersistentOutgoingMessage> candidateIterator = candidates.iterator();
            PersistentUser actor = null;
            if(candidateIterator.hasNext()) {
                PersistentOutgoingMessage outgoingMessage = candidateIterator.next();
                actor = outgoingMessage.getActor();
            }
            if(actor == null) {
                SLF4JLoggerProxy.debug(this,
                                       "No outgoing messages match {}",
                                       inMessage);
                return null;
            } else {
                return actor.getUserID();
            }
        }
    }
    /**
     * Get the outgoingMessageDao value.
     *
     * @return a <code>PersistentOutgoingMessageDao</code> value
     */
    public PersistentOutgoingMessageDao getOutgoingMessageDao()
    {
        return outgoingMessageDao;
    }
    /**
     * Sets the outgoingMessageDao value.
     *
     * @param a <code>PersistentOutgoingMessageDao</code> value
     */
    public void setOutgoingMessageDao(PersistentOutgoingMessageDao inOutgoingMessageDao)
    {
        outgoingMessageDao = inOutgoingMessageDao;
    }
    /**
     * allows datastore access to outgoing messages
     */
    @Autowired
    private PersistentOutgoingMessageDao outgoingMessageDao;
}
