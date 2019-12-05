package org.marketcetera.marketdata.event;

import java.util.Optional;

/* $License$ */

/**
 * Indicates that the implementor has an optional market data request provider value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasOptionalMarketDataRequestProvider
{
    /**
     * Get the requested provider value.
     *
     * @return an <code>Optional&lt;String&gt;</code> value
     */
    Optional<String> getMarketDataRequestProvider();
}
