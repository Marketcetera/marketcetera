package org.marketcetera.module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a spring-configurable module configuration provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class SpringPropertiesConfigurationProvider
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
                               "Looking for default value of attribute {} for {}",  //$NON-NLS-1$
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
                               propertiesKey);
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
     * @param inEntries a <code>List&lt;ModuleConfiguration&gt;</code> value
     */
    public void setProperties(List<ModuleConfiguration> inEntries)
    {
        synchronized(cacheValues) {
            if(inEntries == null) {
                cacheValues.clear();
                return;
            }
            for(ModuleConfiguration entry : inEntries) {
                String propertiesKey = getKeyFor(entry.getModuleUrn());
                Properties p = cacheValues.get(propertiesKey);
                if(p == null) {
                    p = new Properties();
                    cacheValues.put(propertiesKey,
                                    p);
                } else {
                    p.clear();
                }
                if(entry.getProperties() != null) {
                    p.putAll(entry.getProperties());
                }
                SLF4JLoggerProxy.debug(this,
                                       "Done setting properties for {}",  //$NON-NLS-1$
                                       entry);
            }
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SpringPropertiesConfigurationProvider [cacheValues=").append(cacheValues).append("]");
        return builder.toString();
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
