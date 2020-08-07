package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.FeedStatus;

/* $License$ */

/**
 * Provides a POJO {@link MarketDataFeedStatusEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleMarketDataFeedStatusEvent
        implements MarketDataFeedStatusEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.HasFeedStatus#getFeedStatus()
     */
    @Override
    public FeedStatus getFeedStatus()
    {
        return feedStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.HasFeedStatus#setFeedStatus(org.marketcetera.marketdata.FeedStatus)
     */
    @Override
    public void setFeedStatus(FeedStatus inFeedStatus)
    {
        feedStatus = inFeedStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestProvider#getMarketDataRequestProvider()
     */
    @Override
    public String getMarketDataRequestProvider()
    {
        return provider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestProvider#setMarketDataRequestProvider(java.lang.String)
     */
    @Override
    public void setMarketDataRequestProvider(String inProvider)
    {
        provider = inProvider;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataFeedStatusEvent [feedStatus=").append(feedStatus).append(", provider=")
                .append(provider).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleMarketDataFeedStatusEvent instance.
     */
    public SimpleMarketDataFeedStatusEvent() {}
    /**
     * Create a new SimpleMarketDataFeedStatusEvent instance.
     *
     * @param inFeedStatus a <code>FeedStatus</code> value
     * @param inProvider a <code>String</code> value
     */
    public SimpleMarketDataFeedStatusEvent(FeedStatus inFeedStatus,
                                           String inProvider)
    {
        feedStatus = inFeedStatus;
        provider = inProvider;
    }
    /**
     * feed status value
     */
    private FeedStatus feedStatus;
    /**
     * market data provider value
     */
    private String provider;
}
