package org.marketcetera.eventbus;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.core.PlatformServices;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides common behavior for event bus services;
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractEventBusService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Messages.SERVICE_STARTING.info(this,
                                       getServiceDescription());
        eventBusService.register(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        Messages.SERVICE_STOPPING.info(this,
                                       getServiceDescription());
        eventBusService.unregister(this);
    }
    /**
     * Get the service description value.
     *
     * @return a <code>String</code> value
     */
    public String getServiceDescription()
    {
        return serviceDescription;
    }
    /**
     * service description value
     */
    private String serviceDescription = PlatformServices.getServiceName(getClass());
    /**
     * provides access to event bus services
     */
    @Autowired
    protected EventBusService eventBusService;
}
