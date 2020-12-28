package org.marketcetera.eventbus.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.eventbus.EsperEvent;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.eventbus.HasEsperEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.eventbus.Subscribe;

/* $License$ */

/**
 * Connects the system Esper default space to the event bus.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventBusEsperConnector
{
    @PostConstruct
    public void start()
    {
        eventBusService.register(this);
    }
    @PreDestroy
    public void stop()
    {
        eventBusService.unregister(this);
    }
    @Subscribe
    public void receiveEvents(EsperEvent inEvent)
    {
        processEvent(inEvent);
    }
    @Subscribe
    public void receiveEvents(HasEsperEvent inEvent)
    {
        processEvent(inEvent.getEsperEvent());
    }
    private void processEvent(EsperEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "Passing {} to Esper",
                               inEvent);
        esperRuntime.getRuntime().getEventService().sendEventBean(inEvent,
                                                                  inEvent.getEventName());
    }
    @Autowired
    private EventBusService eventBusService;
    @Autowired
    private EsperEngine esperRuntime;
}
