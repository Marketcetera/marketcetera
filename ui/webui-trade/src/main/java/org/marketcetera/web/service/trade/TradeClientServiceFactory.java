package org.marketcetera.web.service.trade;

import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.web.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link TradeClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class TradeClientServiceFactory
        implements ConnectableServiceFactory<TradeClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableServiceFactory#create()
     */
    @Override
    public TradeClientService create()
    {
        TradeClientService tradeClientService = new TradeClientService();
        tradeClientService.setTradeClientFactory(tradeClientFactory);
        return tradeClientService;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#getServiceType()
     */
    @Override
    public Class<TradeClientService> getServiceType()
    {
        return TradeClientService.class;
    }
    /**
     * creates a trade client to connect to the trade server
     */
    @Autowired
    private TradeRpcClientFactory tradeClientFactory;
}
