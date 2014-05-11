package org.marketcetera.marketdata.core;

import java.util.Set;

import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.core.cache.MarketDataCache;
import org.marketcetera.marketdata.core.request.MarketDataRequestToken;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides market data from a specific source.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataProvider.java 16422 2013-01-03 19:43:24Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface MarketDataProvider
        extends MarketDataProviderMBean,MarketDataCache
{
    /**
     * Requests market data as indicated in the given request token.
     * 
     * <p>The given token contains sufficient information to indicate what market data was
     * requested and to whom it should be delivered.
     *
     * @param inRequestToken a <code>MarketDataRequestToken</code> value
     */
    public void requestMarketData(MarketDataRequestToken inRequestToken);
    /**
     * Cancels a market data request.
     *
     * @param inRequestToken a <code>MarketDataRequestToken</code> value
     */
    public void cancelMarketDataRequest(MarketDataRequestToken inRequestToken);
    /**
     * Gets the name of the provider.
     * 
     * <p>This name will identify the source of the market data to the user. The name
     * must be unique among all active market data providers.
     *
     * @return a <code>String</code> value
     */
    public String getProviderName();
    /**
     * Gets the capabilities of this market data provider.
     * 
     * <p>The returned value can be static or dynamic. This method may be invoked
     * more than once. This method should not be invoked until the provider is {@link Lifecycle#start() started}.
     *
     * @return a <code>Set&lt;Capability&gt;</code>
     */
    public Set<Capability> getCapabilities();
    /**
     * Gets the feed type of the provider.
     *
     * @return a <code>FeedType</code> value
     */
    public FeedType getFeedType();
}
