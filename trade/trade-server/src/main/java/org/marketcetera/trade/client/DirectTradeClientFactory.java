package org.marketcetera.trade.client;

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
        return new DirectTradeClient();
    }
}
