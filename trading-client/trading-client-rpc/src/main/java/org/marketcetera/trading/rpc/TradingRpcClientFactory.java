package org.marketcetera.trading.rpc;

import java.util.Locale;

import org.marketcetera.tradingclient.TradingClient;
import org.marketcetera.tradingclient.TradingClientFactory;

/* $License$ */

/**
 * Creates RPC {@link TradingClient} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradingRpcClientFactory
        implements TradingClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.tradingclient.TradingClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public TradingClient create(String inUsername,
                                String inPassword,
                                String inHostname,
                                int inPort)
    {
        return create(inUsername,
                      inPassword,
                      inHostname,
                      inPort,
                      Locale.getDefault());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tradingclient.TradingClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int, java.util.Locale)
     */
    @Override
    public TradingClient create(String inUsername,
                                String inPassword,
                                String inHostname,
                                int inPort,
                                Locale inLocale)
    {
        TradingRpcClient client = new TradingRpcClient();
        client.setHostname(inHostname);
        client.setPassword(inPassword);
        client.setPort(inPort);
        client.setUsername(inUsername);
        client.setLocale(inLocale);
        return client;
    }
}
