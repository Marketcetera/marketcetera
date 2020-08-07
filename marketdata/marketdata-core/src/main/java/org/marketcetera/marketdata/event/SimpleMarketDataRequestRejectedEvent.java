package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Provides a POJO {@link MarketDataRequestRejectedEvent} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleMarketDataRequestRejectedEvent
        extends AbstractMarketDataProviderRequestEvent
        implements MarketDataRequestRejectedEvent
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleMarketDataRequestAcceptedEvent [marketDataRequestId=")
                .append(getMarketDataRequestId()).append(", marketDataRequest=").append(getMarketDataRequest())
                .append(", marketDataRequestProvider=").append(getMarketDataRequestProvider()).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleMarketDataRequestRejectedEvent instance.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inRequestId a <code>String</code> value
     * @param inRequestedProvider a <code>String</code> value
     * @param inReason a <code>String</code> value
     */
    public SimpleMarketDataRequestRejectedEvent(MarketDataRequest inMarketDataRequest,
                                                String inRequestId,
                                                String inRequestedProvider,
                                                String inReason)
    {
        super(inMarketDataRequest,
              inRequestId,
              inRequestedProvider);
        setReason(inReason);
    }
    /**
     * Create a new SimpleMarketDataRequestRejectedEvent instance.
     */
    public SimpleMarketDataRequestRejectedEvent()
    {
        super();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.HasReason#getReason()
     */
    @Override
    public String getReason()
    {
        return reason;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.HasReason#setReason(java.lang.String)
     */
    @Override
    public void setReason(String inReason)
    {
        reason = inReason;
    }
    /**
     * reason value
     */
    private String reason;
}
