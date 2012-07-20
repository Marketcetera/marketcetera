package org.marketcetera.module;

import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides configuration properties for a module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ModuleConfiguration.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id: ModuleConfiguration.java 82384 2012-07-20 19:09:59Z colin $")
public class ModuleConfiguration
{
    /**
     * Get the Module URN value.
     *
     * @return a <code>ModuleURN</code> value
     */
    public ModuleURN getModuleUrn()
    {
        return moduleUrn;
    }
    /**
     * Set the Module URN value using the given <code>String</code> value.
     *
     * @param inModuleUrnValue a <code>String</code> value
     */
    public void setModuleUrn(String inModuleUrnValue)
    {
        moduleUrn = new ModuleURN(inModuleUrnValue);
    }
    /**
     * Set the Module URN value.
     *
     * @param inModuleUrn a <code>ModuleURN</code> value
     */
    public void setModuleUrn(ModuleURN inModuleUrn)
    {
        moduleUrn = inModuleUrn;
    }
    /**
     * Get the properties value.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getProperties()
    {
        return properties;
    }
    /**
     * Set the properties value.
     *
     * @param inProperties a <code>Map&lt;String,String&gt;</code> value
     */
    public void setProperties(Map<String,String> inProperties)
    {
        properties = inProperties;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ConfigurationEntry [moduleUrn=").append(moduleUrn).append(", properties=").append(properties)
                .append("]");
        return builder.toString();
    }
    /**
     * module URN value
     */
    private ModuleURN moduleUrn;
    /**
     * properties value
     */
    private Map<String,String> properties;
}
