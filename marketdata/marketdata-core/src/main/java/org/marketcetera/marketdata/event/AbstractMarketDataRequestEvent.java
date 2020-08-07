package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Provides common behavior for market data request events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractMarketDataRequestEvent
        implements HasMarketDataRequest,HasMarketDataRequestId
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestId#getMarketDataRequestId()
     */
    @Override
    public String getMarketDataRequestId()
    {
        return requestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.MarketDataRequestEvent#getMarketDataRequest()
     */
    @Override
    public MarketDataRequest getMarketDataRequest()
    {
        return marketDataRequest;
    }
    /**
     * Sets the marketDataRequest value.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     */
    public void setMarketDataRequest(MarketDataRequest inMarketDataRequest)
    {
        marketDataRequest = inMarketDataRequest;
    }
    /**
     * Create a new AbstractMarketDataRequestEvent instance.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inRequestId a <code>String</code> value
     */
    protected AbstractMarketDataRequestEvent(MarketDataRequest inMarketDataRequest,
                                             String inRequestId)
    {
        marketDataRequest = inMarketDataRequest;
        requestId = inRequestId;
    }
    /**
     * Create a new AbstractMarketDataRequestEvent instance.
     */
    protected AbstractMarketDataRequestEvent() {}
    /**
     * market data request value
     */
    private MarketDataRequest marketDataRequest;
    /**
     * market data request id value
     */
    private String requestId;
}
