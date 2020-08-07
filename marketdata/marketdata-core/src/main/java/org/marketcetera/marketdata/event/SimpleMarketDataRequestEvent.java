package org.marketcetera.marketdata.event;

import java.util.Optional;

import org.marketcetera.marketdata.MarketDataListener;
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
        return marketDataRequestId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataRequestEvent [requestedProvider=").append(requestedProvider)
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.HasMarketDataListener#getMarketDataListener()
     */
    @Override
    public MarketDataListener getMarketDataListener()
    {
        return listener;
    }
    /**
     * Create a new SimpleMarketDataRequestEvent instance.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inMarketDataRequestId a <code>String</code> value
     * @param inRequestedProvider a <code>String</code> value or <code>null</code>
     * @param inListener a <code>MarketDataListener</code> value
     */
    public SimpleMarketDataRequestEvent(MarketDataRequest inMarketDataRequest,
                                        String inMarketDataRequestId,
                                        String inRequestedProvider,
                                        MarketDataListener inListener)
    {
        marketDataRequest = inMarketDataRequest;
        requestedProvider = Optional.ofNullable(inRequestedProvider);
        listener = inListener;
        marketDataRequestId = inMarketDataRequestId;
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
     * market data request id value
     */
    private String marketDataRequestId;
    /**
     * market data listener value
     */
    private MarketDataListener listener;
    /**
     * optional requested provider
     */
    private Optional<String> requestedProvider;
}
