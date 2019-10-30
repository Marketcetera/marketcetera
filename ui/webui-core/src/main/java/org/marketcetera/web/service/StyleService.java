package org.marketcetera.web.service;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.stereotype.Service;

import com.vaadin.ui.Component;

/* $License$ */

/**
 * Provides style resolution services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class StyleService
{
    /**
     * Validate and start the service.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              PlatformServices.getServiceName(getClass()));
    }
    public void addStyle(Class<?> inParent,
                         Component inComponent)
    {
        
    }
}
