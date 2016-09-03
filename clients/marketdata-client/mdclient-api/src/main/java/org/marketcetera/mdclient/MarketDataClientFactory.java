package org.marketcetera.mdclient;

import org.marketcetera.core.ClientFactory;

/* $License$ */

/**
 * Provides a {@link MDClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataClientFactory
        extends ClientFactory<MDClient,MarketDataClientParameters>
{
    /**
     * Create a {@link MarketDataServiceClient} instance.
     *
     * @param inParameters a <code>MarketDataClientParameters</code> value
     * @return a <code>MarketDataServiceClient</code> value
     */
    @Override
    MDClient create(MarketDataClientParameters inParameters);
}
