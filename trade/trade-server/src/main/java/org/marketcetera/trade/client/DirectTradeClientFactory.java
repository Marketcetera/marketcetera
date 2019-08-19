package org.marketcetera.trade.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 * Creates {@link DirectTradeClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DirectTradeClientFactory
        implements TradeClientFactory<DirectTradeClientParameters>
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.client.TradeClientFactory#create(java.lang.Object)
     */
    @Override
    public TradeClient create(DirectTradeClientParameters inParameterClazz)
    {
        DirectTradeClient tradeClient = new DirectTradeClient(applicationContext,
                                                              inParameterClazz.getUsername());
        return tradeClient;
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
}
