package org.marketcetera.eventbus.server;

import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
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
public class EventBusServerTestConfiguration
{
    /**
     * Get the symbol resolver service value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    @Bean
    public SymbolResolverService getSymbolResolverService()
    {
        IterativeSymbolResolver symbolResolverService = new IterativeSymbolResolver();
        symbolResolverService.getSymbolResolvers().add(new PatternSymbolResolver());
        return symbolResolverService;
    }
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
