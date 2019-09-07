package org.marketcetera.trade.event.connector;

import java.util.Collection;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageBroadcaster;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Broadcasts incoming trade messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class IncomingTradeMessageBroadcastConnector
        extends AbstractTradeConnector
{
    /**
     * Receive the incoming FIX application message.
     *
     * @param inEvent an <code>IncomingFixAppMessageEvent</code> value
     */
    @Subscribe
    public void receive(TradeMessage inTradeMessage)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inTradeMessage);
        if(tradeMessageBroadcasters == null || tradeMessageBroadcasters.isEmpty()) {
            Messages.NO_TRADE_MESSAGE_PUBLISHER.warn(this,
                                                     inTradeMessage);
        } else {
            for(TradeMessageBroadcaster tradeMessageBroadcaster : tradeMessageBroadcasters) {
                try {
                    tradeMessageBroadcaster.reportTradeMessage(inTradeMessage);
                } catch (Exception e) {
                    PlatformServices.handleException(this,
                                                     "Error publishing trade message",
                                                     e);
                }
            }
        }
    }
    /**
     * provides access to trade services
     */
    @Autowired(required=false)
    private Collection<TradeMessageBroadcaster> tradeMessageBroadcasters = Lists.newArrayList();
}
