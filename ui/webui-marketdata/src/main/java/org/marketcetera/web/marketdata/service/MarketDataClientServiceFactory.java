package org.marketcetera.web.marketdata.service;

import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.web.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link MarketDataClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class MarketDataClientServiceFactory
        implements ConnectableServiceFactory<MarketDataClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#create()
     */
    @Override
    public MarketDataClientService create()
    {
        MarketDataClientService service = new MarketDataClientService();
        service.setMarketDataClientFactory(marketDataClientFactory);
        return service;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#getServiceType()
     */
    @Override
    public Class<MarketDataClientService> getServiceType()
    {
        return MarketDataClientService.class;
    }
    /**
     * creates {@link MarketDataClient} objects
     */
    @Autowired
    private MarketDataRpcClientFactory marketDataClientFactory;
}
