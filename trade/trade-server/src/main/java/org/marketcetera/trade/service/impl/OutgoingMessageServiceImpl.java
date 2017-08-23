package org.marketcetera.trade.service.impl;

import org.marketcetera.admin.User;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.OutgoingMessage;
import org.marketcetera.trade.OutgoingMessageFactory;
import org.marketcetera.trade.dao.PersistentOutgoingMessage;
import org.marketcetera.trade.dao.PersistentOutgoingMessageDao;
import org.marketcetera.trade.service.OutgoingMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides services for outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class OutgoingMessageServiceImpl
        implements OutgoingMessageService
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.OrderService#recordOutgoingMessage(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID, org.marketcetera.trade.UserID)
     */
    @Override
    public PersistentOutgoingMessage recordOutgoingMessage(Message inOutgoingMessage,
                                                           SessionID inSessionId,
                                                           BrokerID inBrokerId,
                                                           User inActor)
    {
        OutgoingMessage outgoingMessage = outgoingMessageFactory.create(inOutgoingMessage,
                                                                        inBrokerId,
                                                                        inSessionId,
                                                                        inActor);
        return save(outgoingMessage);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.OrderService#save(com.marketcetera.ors.domain.OutgoingMessage)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentOutgoingMessage save(OutgoingMessage inOutgoingMessage)
    {
        PersistentOutgoingMessage pOutgoingMessage;
        if(inOutgoingMessage instanceof PersistentOutgoingMessage) {
            pOutgoingMessage = (PersistentOutgoingMessage)inOutgoingMessage;
        } else {
            pOutgoingMessage = new PersistentOutgoingMessage(inOutgoingMessage);
        }
        return outgoingMessageDao.save(pOutgoingMessage);
    }
    /**
     * Get the outgoingMessageFactory value.
     *
     * @return an <code>OutgoingMessageFactory</code> value
     */
    public OutgoingMessageFactory getOutgoingMessageFactory()
    {
        return outgoingMessageFactory;
    }
    /**
     * Sets the outgoingMessageFactory value.
     *
     * @param inOutgoingMessageFactory an <code>OutgoingMessageFactory</code> value
     */
    public void setOutgoingMessageFactory(OutgoingMessageFactory inOutgoingMessageFactory)
    {
        outgoingMessageFactory = inOutgoingMessageFactory;
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
     * @param inOutgoingMessageDao a <code>PersistentOutgoingMessageDao</code> value
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
    /**
     * creates outgoing message objects
     */
    @Autowired
    private OutgoingMessageFactory outgoingMessageFactory;
}
