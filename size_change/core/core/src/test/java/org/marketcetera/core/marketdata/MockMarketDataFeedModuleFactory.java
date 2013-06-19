package org.marketcetera.core.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.module.Module;
import org.marketcetera.core.module.ModuleCreationException;
import org.marketcetera.core.module.ModuleFactory;
import org.marketcetera.core.module.ModuleURN;

/* $License$ */

/**
 * Module factory implementation for {@link MockMarketDataFeed}.
 *
 * @version $Id: MockMarketDataFeedModuleFactory.java 82329 2012-04-10 16:28:13Z colin $
 * @since 2.1.0
 */
public class MockMarketDataFeedModuleFactory
        extends ModuleFactory
{
    public MockMarketDataFeedModuleFactory()
    {
        super(PROVIDER_URN,
              null,
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
        try {
            return new MockFeedModule();
        } catch (CoreException e) {
            throw new ModuleCreationException(e.getI18NBoundMessage());
        }
    }
    public static final String IDENTIFIER = "mock";  //$NON-NLS-1$
    /**
     * unique provider URN for the bogus feed market data provider
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:mdata:" + IDENTIFIER);  //$NON-NLS-1$
    /**
     * instance URN for the bogus feed market data provider
     */
    public static final ModuleURN INSTANCE_URN = new ModuleURN(PROVIDER_URN,
                                                               "single");  //$NON-NLS-1$
}
