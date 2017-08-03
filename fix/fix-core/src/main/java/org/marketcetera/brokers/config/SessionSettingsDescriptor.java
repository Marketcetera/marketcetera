package org.marketcetera.brokers.config;

import java.util.Map;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Describes session settings for one or more brokers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionSettingsDescriptor
{
    /**
     * Create a new SessionSettingsDescriptor instance.
     */
    public SessionSettingsDescriptor() {}
    /**
     * Create a new SessionSettingsDescriptor instance.
     *
     * @param inSessionSettings a <code>Map&lt;String,String&gt;</code> value
     */
    public SessionSettingsDescriptor(Map<String,String> inSessionSettings)
    {
        if(inSessionSettings != null) {
            sessionSettings.putAll(inSessionSettings);
        }
    }
    /**
     * Get the sessionSettings value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getSessionSettings()
    {
        return sessionSettings;
    }
    /**
     * Sets the sessionSettings value.
     *
     * @param inSessionSettings a <code>Map&lt;String,String&gt;</code> value
     */
    public void setSessionSettings(Map<String,String> inSessionSettings)
    {
        sessionSettings.clear();
        if(inSessionSettings != null) {
            sessionSettings.putAll(inSessionSettings);
        }
    }
    /**
     * session settings that apply to all brokers
     */
    private final Map<String,String> sessionSettings = Maps.newHashMap();
}
