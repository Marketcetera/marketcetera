package org.marketcetera.eventbus.server.esper;

import org.marketcetera.eventbus.server.EsperEngine;
import org.marketcetera.eventbus.server.EventBusEsperConnector;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 * Provides test configuration for Esper EventBus tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class EventBusEsperServerTestConfiguration
{
    /**
     * Get the Esper Engine value.
     *
     * @return an <code>EsperEngine</code> value
     */
    @Bean
    public EsperEngine getEsperEngine()
    {
        EsperEngine esperRuntime = new EsperEngine();
        esperRuntime.getEventTypes().add(TestManuallyRegisteredEventBean.class);
        return esperRuntime;
    }
    /**
     * Get the event bus Esper connector value.
     *
     * @return an <code>EventBusEsperConnector</code> valu
     */
    @Bean
    public EventBusEsperConnector getEventBusEsperConnector()
    {
        EventBusEsperConnector eventBusEsperConnector = new EventBusEsperConnector();
        return eventBusEsperConnector;
    }
}
