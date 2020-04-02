package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Provides a POJO {@link CancelMarketDataRequestEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleCancelMarketDataRequestEvent
        implements CancelMarketDataRequestEvent
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleCancelMarketDataRequestEvent [marketDataRequestId=").append(marketDataRequestId)
                .append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.event.HasMarketDataRequestId#getMarketDataRequestId()
     */
    @Override
    public String getMarketDataRequestId()
    {
        return marketDataRequestId;
    }
    /**
     * Sets the marketDataRequestId value.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     */
    public void setMarketDataRequestId(String inMarketDataRequestId)
    {
        marketDataRequestId = inMarketDataRequestId;
    }
    /**
     * Create a new SimpleCancelMarketDataRequestEvent instance.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     */
    public SimpleCancelMarketDataRequestEvent(String inMarketDataRequestId)
    {
        marketDataRequestId = inMarketDataRequestId;
    }
    /**
     * market data request id value
     */
    private String marketDataRequestId;
}
