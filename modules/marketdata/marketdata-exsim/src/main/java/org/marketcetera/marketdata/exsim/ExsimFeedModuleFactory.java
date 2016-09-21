package org.marketcetera.marketdata.exsim;

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
public class ExsimFeedModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new ExsimFeedModuleFactory instance.
     */
    public ExsimFeedModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        return new ExsimFeedModule(INSTANCE_URN);
    }
    /**
     * market data provider identifier
     */
    public static final String IDENTIFIER = "exsim";  //$NON-NLS-1$
    /**
     * provider URN value
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN value
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
