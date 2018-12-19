package org.marketcetera.eventbus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
                                       PlatformServices.getServiceName(getClass()));
        eventBusService.register(this);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        Messages.SERVICE_STOPPING.info(this,
                                       PlatformServices.getServiceName(getClass()));
        eventBusService.unregister(this);
    }
    /**
     * provides access to event bus services
     */
    @Autowired
    protected EventBusService eventBusService;
}
