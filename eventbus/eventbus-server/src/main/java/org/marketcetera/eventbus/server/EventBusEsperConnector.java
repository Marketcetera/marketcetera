package org.marketcetera.eventbus.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.eventbus.EsperEvent;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.eventbus.HasEsperEvent;
import org.marketcetera.metrics.MetricService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.codahale.metrics.Meter;
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
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        String name = PlatformServices.getServiceName(getClass());
        esperEventMetric = metricsService.getMetrics().meter(name);
        eventBusService.register(this);
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        eventBusService.unregister(this);
    }
    /**
     * Receive <code>EsperEvent</code> events.
     *
     * @param inEvent an <code>EsperEvent</code> value
     */
    @Subscribe
    public void receiveEvents(EsperEvent inEvent)
    {
        processEvent(inEvent);
    }
    /**
     * Receive <code>HasEsperEvent</code> events.
     *
     * @param inEvent a <code>HasEsperEvent</code> value
     */
    @Subscribe
    public void receiveEvents(HasEsperEvent inEvent)
    {
        processEvent(inEvent.getEsperEvent());
    }
    /**
     * Process the given Esper event.
     *
     * @param inEvent an <code>EsperEvent</code> value
     */
    private void processEvent(EsperEvent inEvent)
    {
        SLF4JLoggerProxy.trace(this,
                               "Passing {} to Esper",
                               inEvent);
        esperEventMetric.mark();
        esperRuntime.getRuntime().getEventService().sendEventBean(inEvent,
                                                                  inEvent.getEventName());
    }
    /**
     * records the rate at which events enter the Esper space from the event bus
     */
    private Meter esperEventMetric;
    /**
     * provides access to metrics service
     */
    @Autowired
    private MetricService metricsService;
    /**
     * provides access to event bus services
     */
    @Autowired
    private EventBusService eventBusService;
    /**
     * provides access to the Esper engine
     */
    @Autowired
    private EsperEngine esperRuntime;
}
