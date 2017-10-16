package org.marketcetera.dataflow.modules;

import org.marketcetera.dataflow.Messages;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Receives data intended for data receivers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowReceiverModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new DataFlowReceiverModuleFactory instance.
     */
    public DataFlowReceiverModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.DATA_RECEIVER_PROVIDER_DESCRIPTION,
              false,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        return new DataFlowReceiverModule(INSTANCE_URN);
    }
    /**
     * provider URN value
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:dataflow:receiver");  //$NON-NLS-1$
    /**
     * instance URN value
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
