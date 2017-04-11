package org.marketcetera.tensorflow;

import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFlowModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new TensorFlowModuleFactory instance.
     */
    public TensorFlowModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              true,
              true,
              ModuleURN.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        return new TensorFlowModule((ModuleURN)inParameters[0]);
    }
    /**
     * instance provider name
     */
    public static final String IDENTIFIER = "tensorflow";  //$NON-NLS-1$
    /**
     * tensor flow module provider URN
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:ml:" + IDENTIFIER);  //$NON-NLS-1$
}
