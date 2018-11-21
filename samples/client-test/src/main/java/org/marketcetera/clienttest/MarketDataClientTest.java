package org.marketcetera.clienttest;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataContextClassProvider;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClient;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Demonstrates how to connect to MATP market data services from an external application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Starting market data client test");
        MarketDataRpcClientParameters marketDataParams = new MarketDataRpcClientParameters();
        marketDataParams.setUsername("user");
        marketDataParams.setPassword("password");
        marketDataParams.setHostname("localhost");
        marketDataParams.setPort(8998);
        marketDataParams.setContextClassProvider(new MarketDataContextClassProvider());
        MarketDataRpcClient marketDataClient = new MarketDataRpcClientFactory().create(marketDataParams);
        try {
            marketDataClient.start();
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Connected to market data nexus: {}",
                                  marketDataClient.isRunning());
            MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
            requestBuilder = requestBuilder.withContent(Content.TOP_OF_BOOK,Content.LATEST_TICK).withSymbols("METC").withAssetClass(AssetClass.EQUITY);
            String requestId = marketDataClient.request(requestBuilder.create(),
                                                        new MarketDataListener() {
                /* (non-Javadoc)
                 * @see org.marketcetera.marketdata.MarketDataListener#receiveMarketData(org.marketcetera.event.Event)
                 */
                @Override
                public void receiveMarketData(Event inEvent)
                {
                    SLF4JLoggerProxy.info(ClientTest.class,
                                          "Received {}",
                                          inEvent);
                }
            });
            marketDataClient.cancel(requestId);
            marketDataClient.stop();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(ClientTest.class,
                                  e);
        } finally {
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Ending market data client test");
        }
    }
}
