package org.marketcetera.module;

import java.util.List;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Describes a Module and its creation parameters, if any.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ModuleDescriptor
{
    /**
     * Get the parameters value.
     *
     * @return a <code>List<Object></code> value
     */
    public List<Object> getParameters()
    {
        return parameters;
    }
    /**
     * Sets the parameters value.
     *
     * @param inParameters a <code>List<Object></code> value
     */
    public void setParameters(List<Object> inParameters)
    {
        parameters = inParameters;
    }
    /**
     * Get the moduleProviderUrn value.
     *
     * @return a <code>ModuleURN</code> value
     */
    public ModuleURN getModuleProviderUrn()
    {
        return moduleProviderUrn;
    }
    /**
     * Sets the moduleProviderUrn value.
     *
     * @param inModuleProviderUrn a <code>ModuleURN</code> value
     */
    public void setModuleProviderUrn(ModuleURN inModuleProviderUrn)
    {
        moduleProviderUrn = inModuleProviderUrn;
    }
    /**
     * Get the moduleInstanceUrn value.
     *
     * @return a <code>ModuleURN</code> value
     */
    public ModuleURN getModuleInstanceUrn()
    {
        return moduleInstanceUrn;
    }
    /**
     * Sets the moduleInstanceUrn value.
     *
     * @param inModuleInstanceUrn a <code>ModuleURN</code> value
     */
    public void setModuleInstanceUrn(ModuleURN inModuleInstanceUrn)
    {
        moduleInstanceUrn = inModuleInstanceUrn;
    }
    /**
     * module provider Urn
     */
    private ModuleURN moduleProviderUrn;
    /**
     * module instance Urn
     */
    private ModuleURN moduleInstanceUrn;
    /**
     * creation parameters
     */
    private List<Object> parameters = Lists.newArrayList();
}
