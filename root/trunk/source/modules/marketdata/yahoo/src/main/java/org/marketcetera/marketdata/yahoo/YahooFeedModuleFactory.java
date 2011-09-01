package org.marketcetera.marketdata.yahoo;

import org.marketcetera.core.CoreException;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link YahooFeedModule} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
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
