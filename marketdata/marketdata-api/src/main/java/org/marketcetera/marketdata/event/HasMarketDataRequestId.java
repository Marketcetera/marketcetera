package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Indicates that the implementor has a market data request id value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMarketDataRequestId
{
    /**
     * Get the market data request id value.
     *
     * @return a <code>String</code> value
     */
    String getMarketDataRequestId();
}
