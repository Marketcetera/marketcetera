package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Provides common behavior for market data request cancel events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractMarketDataRequestCancelEvent
        implements HasMarketDataRequestId
{
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
     * Create a new AbstractMarketDataRequestCancelEvent instance.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     */
    protected AbstractMarketDataRequestCancelEvent(String inMarketDataRequestId)
    {
        marketDataRequestId = inMarketDataRequestId;
    }
    /**
     * market data request id value
     */
    private String marketDataRequestId;
}