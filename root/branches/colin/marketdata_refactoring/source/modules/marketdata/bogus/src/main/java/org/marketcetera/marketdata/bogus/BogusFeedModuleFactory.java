package org.marketcetera.marketdata.bogus;

import static org.marketcetera.marketdata.bogus.Messages.PROVIDER_DESCRIPTION;

import org.marketcetera.core.CoreException;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * <code>ModuleFactory</code> implementation for the <code>BogusFeed</code> market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$")  //$NON-NLS-1$
public class BogusFeedModuleFactory
        extends ModuleFactory<BogusFeedModule>
{
    /**
     * Create a new BogusFeedModuleFactory instance.
     */
    public BogusFeedModuleFactory()
    {
        super(PROVIDER_URN,
              PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public BogusFeedModule create(Object... inArg0)
            throws ModuleCreationException
    {
        try {
            return new BogusFeedModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    /**
     * unique provider URN for the bogus feed market data provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:bogus");  //$NON-NLS-1$
    /**
     * instance URN for the bogus feed market data provider
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
