package org.marketcetera.marketdata.webservices.impl;

import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestBuilder;
import org.marketcetera.marketdata.request.impl.MarketDataRequestBuilderImpl;
import org.marketcetera.marketdata.webservices.MarketDataService;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataServiceImpl
        implements MarketDataService
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#request(org.marketcetera.marketdata.request.MarketDataRequest)
     */
    @Override
    public void request(MarketDataRequest inRequest)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.webservices.MarketDataService#test()
     */
    @Override
    public MarketDataRequest test()
    {
        MarketDataRequestBuilder builder = new MarketDataRequestBuilderImpl();
        return builder.create();
    }
}
