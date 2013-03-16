package org.marketcetera.core.module;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides an in-memory configurable module configuration provider.
 *
 * @version $Id: InMemoryPropertiesConfigurationProvider.java 82377 2012-06-08 14:50:39Z colin $
 * @since $Release$
 */
@ThreadSafe
public class InMemoryPropertiesConfigurationProvider
        implements ModuleConfigurationProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleConfigurationProvider#getDefaultFor(org.marketcetera.module.ModuleURN, java.lang.String)
     */
    @Override
    public String getDefaultFor(ModuleURN inUrn,
                                String inAttribute)
            throws ModuleException
    {
        SLF4JLoggerProxy.debug(this,
                               "Looking default value of attribute {} for {}",  //$NON-NLS-1$
                               inAttribute,
                               inUrn);
        String propertiesKey = getKeyFor(inUrn);
        Properties p;
        synchronized(this) {
            p = cacheValues.get(propertiesKey);
        }
        SLF4JLoggerProxy.debug(this,
                               "Found {} for {}",  //$NON-NLS-1$
                               p,
                               inUrn);
        if(p != null) {
            return p.getProperty(inAttribute);
        }
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleConfigurationProvider#refresh()
     */
    @Override
    public void refresh()
            throws ModuleException
    {
        // nothing to do
    }
    /**
     * Sets the properties for the given URN.
     *
     * @param inUrn a <code>ModuleURN</code> value
     * @param inProperties a <code>Map&lt;String,String&gt;</code> value
     */
    public void setProperties(ModuleURN inUrn,
                              Map<String,String> inProperties)
    {
        if(inUrn == null) {
            return;
        }
        String propertiesKey = getKeyFor(inUrn);
        synchronized(cacheValues) {
            Properties p = cacheValues.get(propertiesKey);
            if(p == null) {
                p = new Properties();
                cacheValues.put(propertiesKey,
                                p);
            }
            p.clear();
            if(inProperties != null) {
                p.putAll(inProperties);
            }
            SLF4JLoggerProxy.debug(this,
                                   "Set properties for {} to {}",  //$NON-NLS-1$
                                   inUrn,
                                   p);
        }
    }
    /**
     * Constructs a key to use for the given <code>ModuleURN</code>.
     *
     * @param inUrn a <code>ModuleURN</code> value
     * @return a <code>String</code> value
     */
    private static String getKeyFor(ModuleURN inUrn)
    {
        return new StringBuilder().append(inUrn.providerType()).append('_').append(inUrn.providerName()).toString();
    }
    /**
     * cached property values
     */
    private final Map<String,Properties> cacheValues = new HashMap<String,Properties>();
}
