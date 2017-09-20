package org.marketcetera.marketdata;

import java.io.Serializable;

import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataStatus;

/* $License$ */

/**
 * Indicates the status of a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataProviderStatus.java 17068 2015-12-07 17:26:31Z colin $
 * @since $Release$
 */
public class MarketDataProviderStatus
        implements Serializable,MarketDataStatus
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatus#getFeedStatus()
     */
    @Override
    public FeedStatus getFeedStatus()
    {
        return feedStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataStatus#getProvider()
     */
    @Override
    public String getProvider()
    {
        return provider;
    }
    /**
     * Sets the provider value.
     *
     * @param a <code>String</code> value
     */
    public void setProvider(String inProvider)
    {
        provider = inProvider;
    }
    /**
     * Sets the feedStatus value.
     *
     * @param inFeedStatus a <code>FeedStatus</code> value
     */
    public void setFeedStatus(FeedStatus inFeedStatus)
    {
        feedStatus = inFeedStatus;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MarketDataProviderStatus [").append(provider).append(' ').append(feedStatus).append("]");
        return builder.toString();
    }
    /**
     * Create a new MarketDataProviderStatus instance.
     *
     * @param inProvider a <code>String</code> value
     * @param inFeedStatus a <code>boolean</code> value
     */
    public MarketDataProviderStatus(String inProvider,
                                    FeedStatus inFeedStatus)
    {
        provider = inProvider;
        feedStatus = inFeedStatus;
    }
    /**
     * Create a new MarketDataProviderStatus instance.
     */
    public MarketDataProviderStatus() {}
    /**
     * indicates the provider
     */
    private String provider;
    /**
     * indicates if the status of the provider
     */
    private FeedStatus feedStatus;
    private static final long serialVersionUID = 7543127425900182686L;
}
