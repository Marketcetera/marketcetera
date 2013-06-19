package org.marketcetera.marketdata;

/* $License$ */

/**
 * Indicates the type of market data returned by a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FeedType.java 16325 2012-10-25 23:13:12Z colin $
 * @since $Release$
 */
public enum FeedType
{
    /**
     * real market data delivered with no delay
     */
    LIVE,
    /**
     * real market data delivered with unspecified delay
     */
    DELAYED,
    /**
     * simulated market data
     */
    SIMULATED,
    /**
     * type of market data is unknown
     */
    UNKNOWN
}
