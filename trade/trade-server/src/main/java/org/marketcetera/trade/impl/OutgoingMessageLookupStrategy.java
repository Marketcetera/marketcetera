package org.marketcetera.trade.impl;

import java.util.List;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.dao.PersistentOutgoingMessage;
import org.marketcetera.trade.dao.PersistentOutgoingMessageDao;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
     * @see org.marketcetera.trade.IdentifyOwnerStrategy#getOwnerOf(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID)
     */
    @Override
    public UserID getOwnerOf(Message inMessage,
                             SessionID inSessionID,
                             BrokerID inBrokerId)
    {
        try {
            // Use standard JPA methods until QueryDSL Q classes are generated
            Sort sort = Sort.by(Sort.Direction.DESC, "messageId");
            PageRequest page = PageRequest.of(0, 10, sort);

            if(inMessage.getHeader().isSetField(quickfix.field.MsgSeqNum.FIELD)) {
                int msgSeqNum = inMessage.getHeader().getInt(quickfix.field.MsgSeqNum.FIELD);
                String messageType = inMessage.getHeader().getString(quickfix.field.MsgType.FIELD);
                
                // Find messages matching our criteria using standard JPA
                List<PersistentOutgoingMessage> results = outgoingMessageDao.findByBrokerIdAndMsgSeqNumAndMsgType(
                        inBrokerId.getValue(), msgSeqNum, messageType, page);
                
                if(!results.isEmpty()) {
                    PersistentOutgoingMessage message = results.get(0);
                    PersistentUser user = message.getActor(); // Changed from getUser() to getActor()
                    if(user != null) {
                        return user.getUserID();
                    }
                }
                
                SLF4JLoggerProxy.debug(this,
                                      "Unable to identify owner for {} with broker {} seq num {} and message type {}",
                                      inMessage,
                                      inBrokerId,
                                      msgSeqNum,
                                      messageType);
            } else {
                SLF4JLoggerProxy.debug(this,
                                      "Unable to identify owner for {}, MsgSeqNum not set",
                                      inMessage);
            }
        } catch (FieldNotFound e) {
            SLF4JLoggerProxy.debug(this,
                                  e,
                                  "Unable to identify owner for {}",
                                  inMessage);
        }
        return null;
    }
    /**
     * provides access to outgoing message objects
     */
    @Autowired
    private PersistentOutgoingMessageDao outgoingMessageDao;
}