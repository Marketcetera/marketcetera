package org.marketcetera.mdclient;

import org.marketcetera.core.ClientFactory;
import org.marketcetera.core.ClientParameters;

/* $License$ */

/**
 * Provides a {@link MDClient} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataClientFactory<ParameterClazz extends ClientParameters>
        extends ClientFactory<MDClient,ParameterClazz>
{
    /**
     * Create a {@link MarketDataServiceClient} instance.
     *
     * @param inParameters a <code>ParameterClazz</code> value
     * @return a <code>MarketDataServiceClient</code> value
     */
    @Override
    MDClient create(ParameterClazz inParameters);
}
