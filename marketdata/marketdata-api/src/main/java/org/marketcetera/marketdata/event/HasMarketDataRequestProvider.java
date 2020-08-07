package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Indicates that the implementor has a market data request provider value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasMarketDataRequestProvider
{
    /**
     * Get the requested provider value.
     *
     * @return a <code>String</code> value
     */
    String getMarketDataRequestProvider();
    /**
     * Set the provider value.
     *
     * @param inProvider a <code>String</code> value
     */
    void setMarketDataRequestProvider(String inProvider);
}
