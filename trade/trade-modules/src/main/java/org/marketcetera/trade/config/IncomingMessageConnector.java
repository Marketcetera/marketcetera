package org.marketcetera.trade.config;

import javax.annotation.PostConstruct;

import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.modules.fix.FIXMessageHolder;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.trade.TradeConstants;
import org.marketcetera.trade.event.IncomingFixAppMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Connects incoming messages to data flows.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class IncomingMessageConnector
{
    @PostConstruct
    public void start()
    {
        eventBusService.register(this);
    }
    /**
     * Receive incoming FIX messages.
     *
     * @param inEvent an <code>IncomingFixAppMessageEvent</code> value
     */
    @Subscribe
    public void receiveIncomingFixMessage(IncomingFixAppMessageEvent inEvent)
    {
        HeadwaterModule dataFlowStart = HeadwaterModule.getInstance(TradeConstants.incomingDataFlowName);
        FIXMessageHolder messagePackage = new FIXMessageHolder(inEvent.getSessionId(),
                                                               inEvent.getMessage());
        dataFlowStart.emit(messagePackage);
    }
    /**
     * provides access to event bus services
     */
    @Autowired
    private EventBusService eventBusService;
}
