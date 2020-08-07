package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Provides a POJO {@link MarketDataRequestCanceledEvent} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleMarketDataRequestCanceledEvent
        extends AbstractMarketDataRequestCancelEvent
        implements MarketDataRequestCanceledEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestProvider#getMarketDataRequestProvider()
     */
    @Override
    public String getMarketDataRequestProvider()
    {
        return marketDataProvider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestProvider#setMarketDataRequestProvider(java.lang.String)
     */
    @Override
    public void setMarketDataRequestProvider(String inProvider)
    {
        marketDataProvider = inProvider;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataRequestCanceledEvent [marketDataRequestId=")
                .append(getMarketDataRequestId()).append(", marketDataProvider=").append(marketDataProvider)
                .append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleMarketDataRequestCanceledEvent instance.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     * @param inMarketDataProvider a <code>String</code> value
     */
    public SimpleMarketDataRequestCanceledEvent(String inMarketDataRequestId,
                                                String inMarketDataProvider)
    {
        super(inMarketDataRequestId);
        marketDataProvider = inMarketDataProvider;
    }
    /**
     * market data provider value
     */
    private String marketDataProvider;
}
