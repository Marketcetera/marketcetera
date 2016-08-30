package org.marketcetera.marketdata.core;

import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;

/* $License$ */

/**
 * Provides a {@link MarketDataServiceClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataClientFactory<ParameterClazz>
{
    /**
     * Create a {@link MarketDataServiceClient} instance.
     *
     * @param inParameterClazz a <code>ParameterClazz</code> value
     * @return a <code>MarketDataServiceClient</code> value
     */
    MarketDataServiceClient create(ParameterClazz inParameterClazz);
}
