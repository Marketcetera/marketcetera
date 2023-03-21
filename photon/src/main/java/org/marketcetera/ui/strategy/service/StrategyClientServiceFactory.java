package org.marketcetera.ui.strategy.service;

import org.marketcetera.strategy.StrategyClient;
import org.marketcetera.strategy.StrategyRpcClientFactory;
import org.marketcetera.ui.service.ConnectableServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Creates {@link StrategyClientService} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class StrategyClientServiceFactory
        implements ConnectableServiceFactory<StrategyClientService>
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#create()
     */
    @Override
    public StrategyClientService create()
    {
        StrategyClientService service = new StrategyClientService();
        service.setStrategyClientFactory(strategyClientFactory);
        return service;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableServiceFactory#getServiceType()
     */
    @Override
    public Class<StrategyClientService> getServiceType()
    {
        return StrategyClientService.class;
    }
    /**
     * creates {@link StrategyClient} objects
     */
    @Autowired
    private StrategyRpcClientFactory strategyClientFactory;
}
