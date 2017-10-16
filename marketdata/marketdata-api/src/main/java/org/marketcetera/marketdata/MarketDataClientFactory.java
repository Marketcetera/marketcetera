package org.marketcetera.marketdata;

import org.marketcetera.core.ClientFactory;
import org.marketcetera.core.ClientParameters;

/* $License$ */

/**
 * Provides a {@link MarketDataClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataClientFactory<ParameterClazz extends ClientParameters>
        extends ClientFactory<MarketDataClient,ParameterClazz>
{
    /**
     * Create a {@link MarketDataServiceClient} instance.
     *
     * @param inParameters a <code>ParameterClazz</code> value
     * @return a <code>MarketDataServiceClient</code> value
     */
    @Override
    MarketDataClient create(ParameterClazz inParameters);
}
