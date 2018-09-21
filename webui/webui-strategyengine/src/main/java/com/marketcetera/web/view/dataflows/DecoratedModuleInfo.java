package com.marketcetera.web.view.dataflows;

import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Provides a view of a {@link ModuleURN} and {@link ModuleInfo} combination.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DecoratedModuleInfo
{
    /**
     * Create a new DecoratedModuleInfo instance.
     *
     * @param inModuleUrn a <code>ModuleURN</code> value
     * @param inModuleInfo a <code>ModuleInfo</code> value
     */
    public DecoratedModuleInfo(ModuleURN inModuleUrn,
                               ModuleInfo inModuleInfo)
    {
        moduleUrn = inModuleUrn;
        moduleInfo = inModuleInfo;
        participatingDataFlows = inModuleInfo.getParticipatingDataFlows()==null?0:inModuleInfo.getParticipatingDataFlows().length;
        initiatedDataFlows = inModuleInfo.getInitiatedDataFlows()==null?0:inModuleInfo.getInitiatedDataFlows().length;
    }
    /**
     * Get the state of the module.
     *
     * @return a <code>ModuleState</code> value
     */
    public ModuleState getState()
    {
        return moduleInfo.getState();
    }
    /**
     * Get the moduleUrn value.
     *
     * @return a <code>ModuleURN</code> value
     */
    public ModuleURN getUrn()
    {
        return moduleUrn;
    }
    /**
     * Sets the moduleUrn value.
     *
     * @param inModuleUrn a <code>ModuleURN</code> value
     */
    public void setUrn(ModuleURN inModuleUrn)
    {
        moduleUrn = inModuleUrn;
    }
    /**
     * Get the moduleInfo value.
     *
     * @return a <code>ModuleInfo</code> value
     */
    public ModuleInfo getInfo()
    {
        return moduleInfo;
    }
    /**
     * Sets the moduleInfo value.
     *
     * @param inModuleInfo a <code>ModuleInfo</code> value
     */
    public void setInfo(ModuleInfo inModuleInfo)
    {
        moduleInfo = inModuleInfo;
    }
    /**
     * Get the participatingDataFlows value.
     *
     * @return an <code>int</code> value
     */
    public int getParticipatingDataFlows()
    {
        return participatingDataFlows;
    }
    /**
     * Sets the participatingDataFlows value.
     *
     * @param inParticipatingDataFlows an <code>int</code> value
     */
    public void setParticipatingDataFlows(int inParticipatingDataFlows)
    {
        participatingDataFlows = inParticipatingDataFlows;
    }
    /**
     * Get the initiatedDataFlows value.
     *
     * @return an <code>int</code> value
     */
    public int getInitiatedDataFlows()
    {
        return initiatedDataFlows;
    }
    /**
     * Sets the initiatedDataFlows value.
     *
     * @param inInitiatedDataFlows an <code>int</code> value
     */
    public void setInitiatedDataFlows(int inInitiatedDataFlows)
    {
        initiatedDataFlows = inInitiatedDataFlows;
    }
    /**
     * Get the moduleUrn value.
     *
     * @return a <code>ModuleURN</code> value
     */
    public ModuleURN getModuleUrn()
    {
        return moduleUrn;
    }
    /**
     * Sets the moduleUrn value.
     *
     * @param inModuleUrn a <code>ModuleURN</code> value
     */
    public void setModuleUrn(ModuleURN inModuleUrn)
    {
        moduleUrn = inModuleUrn;
    }
    /**
     * Get the moduleInfo value.
     *
     * @return a <code>ModuleInfo</code> value
     */
    public ModuleInfo getModuleInfo()
    {
        return moduleInfo;
    }
    /**
     * Sets the moduleInfo value.
     *
     * @param inModuleInfo a <code>ModuleInfo</code> value
     */
    public void setModuleInfo(ModuleInfo inModuleInfo)
    {
        moduleInfo = inModuleInfo;
    }
    /**
     * data flows in which this module is participating
     */
    private int participatingDataFlows;
    /**
     * data flows which this module initiated
     */
    private int initiatedDataFlows;
    /**
     * Module URN value
     */
    private ModuleURN moduleUrn;
    /**
     * Module info value
     */
    private ModuleInfo moduleInfo;
    
}
