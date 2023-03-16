package org.marketcetera.strategy.sample;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.strategy.StrategyClient;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Test strategy that demonstrates how a strategy can be built and use services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@ConfigurationProperties
@EnableAutoConfiguration
@PropertySources({@PropertySource("classpath:application.properties")})
public class TestStrategy
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
        strategyClient.emitMessage(Severity.DEBUG,
                                   "Debug message");
        strategyClient.emitMessage(Severity.INFO,
                                   "Info message");
        strategyClient.emitMessage(Severity.WARN,
                                   "Warn message");
        strategyClient.emitMessage(Severity.ERROR,
                                   "Error message");
    }
    /**
     * provides access to strategy services
     */
    @Autowired
    private StrategyClient strategyClient;
}
