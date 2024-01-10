package org.marketcetera.marketdata.rest;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@RestController
@SpringBootConfiguration
@ConfigurationProperties
@Tag(name="Market data server operations")
public class MarketdataRestServer
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        serviceName = PlatformServices.getServiceName(getClass());
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              serviceName);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              serviceName);
    }
    /**
     * human-readable name of this service
     */
    private String serviceName;
}
