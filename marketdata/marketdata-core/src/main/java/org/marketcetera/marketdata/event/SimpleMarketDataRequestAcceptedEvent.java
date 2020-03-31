package org.marketcetera.marketdata.event;

import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Provides a POJO {@link MarketDataRequestAcceptedEvent} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleMarketDataRequestAcceptedEvent
        extends AbstractMarketDataProviderRequestEvent
        implements MarketDataRequestAcceptedEvent
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
     * Create a new SimpleMarketDataRequestAcceptedEvent instance.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inRequestId a <code>String</code> value
     * @param inRequestedProvider a <code>String</code> value
     */
    public SimpleMarketDataRequestAcceptedEvent(MarketDataRequest inMarketDataRequest,
                                                String inRequestId,
                                                String inRequestedProvider)
    {
        super(inMarketDataRequest,
              inRequestId,
              inRequestedProvider);
    }
    /**
     * Create a new SimpleMarketDataRequestAcceptedEvent instance.
     */
    public SimpleMarketDataRequestAcceptedEvent()
    {
        super();
    }
}
