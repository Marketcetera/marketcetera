package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.module.Module;
import org.marketcetera.core.module.ModuleCreationException;
import org.marketcetera.core.module.ModuleFactory;
import org.marketcetera.core.module.ModuleURN;

/* $License$ */

/**
 * Constructs {@link YahooFeedModule} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: YahooFeedModuleFactory.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.4
 */
public class YahooFeedModuleFactory
        extends ModuleFactory
{

    /**
     * Create a new YahooFeedModuleFactory instance.
     */
    public YahooFeedModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              false,
              false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inArg0)
            throws ModuleCreationException
    {
        try {
            return new YahooFeedModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    /**
     * provider name of the module 
     */
    public static final String IDENTIFIER = YahooFeedFactory.PROVIDER_NAME;
    /**
     * provider URN of the yahoo module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN of the yahoo module
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
