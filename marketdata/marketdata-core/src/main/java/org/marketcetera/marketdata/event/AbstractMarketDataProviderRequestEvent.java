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
public abstract class AbstractMarketDataProviderRequestEvent
        extends AbstractMarketDataRequestEvent
        implements HasMarketDataRequestProvider
{
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
    /**
     * Create a new AbstractMarketDataRequestEvent instance.
     *
     * @param inMarketDataRequest a <code>MarketDataRequest</code> value
     * @param inRequestId a <code>String</code> value
     * @param inProvider a <code>String</code> value
     */
    protected AbstractMarketDataProviderRequestEvent(MarketDataRequest inMarketDataRequest,
                                                     String inRequestId,
                                                     String inProvider)
    {
        super(inMarketDataRequest,
              inRequestId);
        provider = inProvider;
    }
    /**
     * Create a new AbstractMarketDataProviderRequestEvent instance.
     */
    protected AbstractMarketDataProviderRequestEvent()
    {
        super();
    }
    /**
     * provider value
     */
    private String provider;
}
