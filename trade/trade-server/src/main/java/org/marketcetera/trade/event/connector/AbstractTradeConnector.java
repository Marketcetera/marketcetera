package org.marketcetera.trade.event.connector;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides common behaviors for trade connectors.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AbstractTradeConnector
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
        eventBusService.register(this);
    }
    /**
     * provides access to trade services
     */
    @Autowired
    protected TradeService tradeService;
    /**
     * provides access to event bus services
     */
    @Autowired
    protected EventBusService eventBusService;
}
