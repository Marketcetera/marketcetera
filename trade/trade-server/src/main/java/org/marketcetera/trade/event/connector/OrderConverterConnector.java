package org.marketcetera.trade.event.connector;

import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.event.OutgoingOrderEvent;
import org.marketcetera.trade.event.OwnedMessage;
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
        ServerFixSession fixSession = tradeService.selectServerFixSession(inEvent.getOrder());
        SLF4JLoggerProxy.debug(this,
                               "Selected {}",
                               fixSession);
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
