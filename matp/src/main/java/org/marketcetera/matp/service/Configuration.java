package org.marketcetera.matp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class Configuration
{
    /**
     * Get the appName value.
     *
     * @return a <code>String</code> value
     */
    public String getAppName()
    {
        return appName;
    }
    /**
     * Sets the appName value.
     *
     * @param a <code>String</code> value
     */
    public void setAppName(String inAppName)
    {
        appName = inAppName;
    }
    @Value("${spring.application.name}")
    private String appName;
}
