package org.marketcetera.trade.event.connector;

import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.OrderIntercepted;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.event.IncomingFixAppMessageEvent;
import org.marketcetera.trade.event.InjectedFixMessageEvent;
import org.marketcetera.trade.event.TradeMessagePackage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;

import quickfix.FieldNotFound;

/* $License$ */

/**
 * Converts incoming application FIX messages to trade messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class IncomingTradeMessageConverterConnector
        extends AbstractTradeConnector
{
    /**
     * Receive the injected FIX message.
     *
     * @param inEvent an <code>InjectedFixMessageEvent</code> value
     */
    @Subscribe
    public void receive(InjectedFixMessageEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inEvent);
        handleMessage(inEvent);
    }
    /**
     * Receive the incoming FIX application message.
     *
     * @param inEvent an <code>IncomingFixAppMessageEvent</code> value
     */
    @Subscribe
    public void receive(IncomingFixAppMessageEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inEvent);
        handleMessage(inEvent);
    }
    /**
     * Handle the given FIX message holder.
     *
     * @param inEvent a <code>HasFIXMessage</code> value
     */
    private void handleMessage(HasFIXMessage inEvent)
    {
        quickfix.Message fixTradeMessage = inEvent.getMessage();
        ServerFixSession serverFixSession;
        try {
            serverFixSession = brokerService.getServerFixSession(FIXMessageUtil.getReversedSessionId(FIXMessageUtil.getSessionId(fixTradeMessage)));
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
        if(serverFixSession == null) {
            throw new RuntimeException(new RuntimeException("Message rejected because the session is unknown for: " + fixTradeMessage)); // TODO
        }
        SLF4JLoggerProxy.debug(this,
                               "Received {} for {}",
                               fixTradeMessage,
                               serverFixSession);
        try {
            TradeMessage tradeMessage = tradeService.convertResponse(inEvent,
                                                                     serverFixSession);
            SLF4JLoggerProxy.debug(this,
                                   "Converted {} to {}",
                                   fixTradeMessage,
                                   tradeMessage);
            eventBusService.post(new TradeMessagePackage(serverFixSession,
                                                         tradeMessage));
        } catch (OrderIntercepted e) {
            SLF4JLoggerProxy.info(this,
                                  "{} not re-emitted because it was intercepted",
                                  fixTradeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
}
