package org.marketcetera.dataflow.config;

import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;

/* $License$ */

/**
 * Provides a data flow to establish on system start.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DataFlowProvider
{
    /**
     * Get the data flow value.
     *
     * @param inModuleManager a <code>ModuleManager</code> value
     * @return a <code>DataRequest[]</code> value
     */
    DataRequest[] getDataFlow(ModuleManager inModuleManager);
    /**
     * Get the data flow name value.
     *
     * @return a <code>String</code> value
     */
    String getName();
    /**
     * Get the data flow description value.
     *
     * @return a <code>String</code> value
     */
    String getDescription();
    /**
     * Receive the data flow ID assigned to this data flow.
     *
     * @param inDataFlowId a <code>DataFlowID</code> value
     */
    void receiveDataFlowId(DataFlowID inDataFlowId);
}
