package org.marketcetera.marketdata;

/**
 * Base implementation of {@link IMarketDataFeedFactory}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class MarketDataFeedFactoryBase
        implements IMarketDataFeedFactory
{
    public String[] getAllowedPropertyKeys()
    {
        return new String[0];
    }
}
