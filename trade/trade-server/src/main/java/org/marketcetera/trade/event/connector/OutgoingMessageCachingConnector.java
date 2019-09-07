package org.marketcetera.trade.event.connector;

import org.marketcetera.trade.event.OwnedMessage;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Caches outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OutgoingMessageCachingConnector
        extends AbstractTradeConnector
{
    /**
     * Receive owned messages.
     *
     * @param inMessage an <code>OwnedMessage</code> value
     */
    @Subscribe
    public void receiveOwnedMessage(OwnedMessage inMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inMessage);
        messageOwnerService.cacheMessageOwner(inMessage.getMessage(),
                                              inMessage.getUser().getUserID());
    }
    /**
     * provides access to message owner services
     */
    @Autowired
    private MessageOwnerService messageOwnerService;
}
