package org.marketcetera.web.services;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

/* $License$ */

/**
 * Provides web message services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class WebMessageService
{
    /**
     * Validate and start the service.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting web message service");
        eventbus = new EventBus();
    }
    /**
     * Register the given object.
     *
     * @param inListener an <code>Object</code> value
     */
    public void register(Object inListener)
    {
        eventbus.register(inListener);
    }
    /**
     * Unregister the given object.
     *
     * @param inListener an <code>Object</code> value
     */
    public void unregister(Object inListener)
    {
        eventbus.unregister(inListener);
    }
    /**
     * Post the given event.
     *
     * @param inEvent an <code>Object</code> value
     */
    public void post(Object inEvent)
    {
        SLF4JLoggerProxy.debug(this,
                               "Posting {}",
                               inEvent);
        eventbus.post(inEvent);
    }
    /**
     * event bus value
     */
    private EventBus eventbus;
}
