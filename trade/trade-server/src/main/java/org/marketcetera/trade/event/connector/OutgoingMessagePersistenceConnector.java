package org.marketcetera.trade.event.connector;

import org.marketcetera.trade.OutgoingMessage;
import org.marketcetera.trade.OutgoingMessageFactory;
import org.marketcetera.trade.event.OwnedMessage;
import org.marketcetera.trade.service.OutgoingMessageService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Persists outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OutgoingMessagePersistenceConnector
        extends AbstractTradeConnector
{
    /**
     * Receive owned message events.
     *
     * @param inMessage an <code>OwnedMessage</code> value
     */
    @Subscribe
    public void receive(OwnedMessage inMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inMessage);
        OutgoingMessage outgoingMessage = outgoingMessageFactory.create(inMessage.getMessage(),
                                                                        inMessage.getBrokerId(),
                                                                        inMessage.getSessionId(),
                                                                        inMessage.getUser());
        outgoingMessage = outgoingMessageService.save(outgoingMessage);
        SLF4JLoggerProxy.debug(this,
                               "Persisted {}",
                               outgoingMessage);
        eventBusService.post(outgoingMessage);
    }
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private OutgoingMessageService outgoingMessageService;
    /**
     * creates {@link OutgoingMessage] objects
     */
    @Autowired
    private OutgoingMessageFactory outgoingMessageFactory;
}
