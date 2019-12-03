package org.marketcetera.marketdata.event;

import java.util.Optional;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Provides a POJO {@link MarketDataRequestEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleMarketDataRequestEvent
        implements MarketDataRequestEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.MarketDataRequestEvent#getMarketDataRequest()
     */
    @Override
    public MarketDataRequest getMarketDataRequest()
    {
        return marketDataRequest;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.MarketDataRequestEvent#getRequestId()
     */
    @Override
    public String getMarketDataRequestId()
    {
        return requestId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataRequestEvent [requestId=").append(requestId).append(", requestedProvider=").append(requestedProvider)
                .append(", marketDataRequest=").append(marketDataRequest).append("]");
        return builder.toString();
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
     * Sets the requestId value.
     *
     * @param inRequestId a <code>String</code> value
     */
    public void setRequestId(String inRequestId)
    {
        requestId = inRequestId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.MarketDataRequestEvent#getRequestedProvider()
     */
    @Override
    public Optional<String> getMarketDataRequestProvider()
    {
        return requestedProvider;
    }
    /**
     * Sets the requestedProvider value.
     *
     * @param inRequestedProvider a <code>String</code> value or <code>null</code>
     */
    public void setRequestedProvider(String inRequestedProvider)
    {
        requestedProvider = Optional.ofNullable(inRequestedProvider);
    }
    /**
     * Create a new SimpleMarketDataRequestEvent instance.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inRequestId a <code>String</code> value
     * @param inRequestedProvider a <code>String</code> value or <code>null</code>
     */
    public SimpleMarketDataRequestEvent(MarketDataRequest inMarketDataRequest,
                                        String inRequestId,
                                        String inRequestedProvider)
    {
        marketDataRequest = inMarketDataRequest;
        requestId = inRequestId;
        requestedProvider = Optional.ofNullable(inRequestedProvider);
    }
    /**
     * Create a new SimpleMarketDataRequestEvent instance.
     */
    public SimpleMarketDataRequestEvent() {}
    /**
     * market data request value
     */
    private MarketDataRequest marketDataRequest;
    /**
     * request id value
     */
    private String requestId;
    /**
     * optional requested provider
     */
    private Optional<String> requestedProvider;
}
