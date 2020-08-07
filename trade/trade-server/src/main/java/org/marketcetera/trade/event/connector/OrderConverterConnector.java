package org.marketcetera.trade.event.connector;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.event.OutgoingOrderEvent;
import org.marketcetera.trade.event.OwnedMessage;
import org.marketcetera.trade.event.SimpleOutgoingOrderStatusEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.eventbus.Subscribe;

import quickfix.SessionID;

/* $License$ */

/**
 * Converts outgoing orders to FIX messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderConverterConnector
        extends AbstractTradeConnector
{
    /**
     * Receive outgoing order events.
     *
     * @param inEvent an <code>OutgoingOrderEvent</code> value
     */
    @Subscribe
    public void receiveOutgoingOrder(OutgoingOrderEvent inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Received {}",
                               inEvent);
        ServerFixSession fixSession;
        try {
            fixSession = tradeService.selectServerFixSession(inEvent.getOrder());
            SLF4JLoggerProxy.debug(this,
                                   "Selected {}",
                                   fixSession);
        } catch (CoreException e) {
            String message = PlatformServices.getMessage(e);
            quickfix.Message fixMessage = (inEvent instanceof HasFIXMessage) ? ((HasFIXMessage)inEvent).getMessage() : null;
            OrderID orderId = (inEvent.getOrder() instanceof OrderBase) ? ((OrderBase)inEvent.getOrder()).getOrderID() : null;
            eventBusService.post(new SimpleOutgoingOrderStatusEvent(message,
                                                                    true,
                                                                    inEvent.getOrder(),
                                                                    orderId,
                                                                    fixMessage));
            return;
        }
        quickfix.Message convertedOrder = tradeService.convertOrder(inEvent.getOrder(),
                                                                    fixSession);
        FIXMessageUtil.setSessionId(convertedOrder,
                                    new SessionID(fixSession.getActiveFixSession().getFixSession().getSessionId()));
        FIXMessageUtil.logMessage(convertedOrder);
        OwnedMessage ownedMessage = new OwnedMessage(inEvent.getUser(),
                                                     new BrokerID(fixSession.getActiveFixSession().getFixSession().getBrokerId()),
                                                     new SessionID(fixSession.getActiveFixSession().getFixSession().getSessionId()),
                                                     convertedOrder);
        eventBusService.post(ownedMessage);
    }
}
