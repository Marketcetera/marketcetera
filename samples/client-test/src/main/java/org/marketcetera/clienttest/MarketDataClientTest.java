package org.marketcetera.clienttest;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.Event;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.MarketDataStatusListener;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 * Demonstrates how to connect to market data services from an external application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication
@EnableAutoConfiguration
@SpringBootConfiguration
public class MarketDataClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(MarketDataClientTest.class,
                              inArgs);
        SLF4JLoggerProxy.info(MarketDataClientTest.class,
                              "Starting market data client test");
        try {
            MarketDataClientTest clientTest = MarketDataClientTest.instance;
            clientTest.runTest();
        } catch (Exception e) {
            PlatformServices.handleException(MarketDataClientTest.class,
                                             "Market Data Client Test Error",
                                             e);
        }
        SLF4JLoggerProxy.info(MarketDataClientTest.class,
                              "Ending market data client test");
    }
    /**
     * Run the client test.
     *
     * @throws Exception if an error occurs running the client test
     */
    private void runTest()
            throws Exception
    {
        try {
            MarketDataRpcClientParameters params = new MarketDataRpcClientParameters();
            params.setHostname(hostname);
            params.setPort(port);
            params.setUsername(username);
            params.setPassword(password);
            marketDataClient = marketDataClientFactory.create(params);
            marketDataClient.start();
            SLF4JLoggerProxy.info(MarketDataClientTest.class,
                                  "Market data client connected to {}:{} as {}",
                                  hostname,
                                  port,
                                  username);
            MarketDataStatusListener statusListener = new MarketDataStatusListener() {
                @Override
                public void receiveMarketDataStatus(MarketDataStatus inMarketDataStatus)
                {
                    SLF4JLoggerProxy.info(MarketDataClientTest.class,
                                          "Market data client received: {}",
                                          inMarketDataStatus);
                }
            };
            marketDataClient.addMarketDataStatusListener(statusListener);
            MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
            requestBuilder = requestBuilder.withContent(Content.TOP_OF_BOOK,Content.LATEST_TICK).withSymbols("METC").withAssetClass(AssetClass.EQUITY);
            MarketDataRequest request = requestBuilder.create();
            SLF4JLoggerProxy.info(MarketDataClientTest.class,
                                  "Issuing market data request: {}",
                                  request);
            MarketDataListener marketDataListener = new MarketDataListener() {
                @Override
                public void receiveMarketData(Event inEvent)
                {
                    SLF4JLoggerProxy.info(MarketDataClientTest.class,
                                          "Received {}",
                                          inEvent);
                }
            };
            String requestId = marketDataClient.request(request,
                                                        marketDataListener);
            SLF4JLoggerProxy.info(MarketDataClientTest.class,
                                  "Waiting for events for request [{}]",
                                  requestId);
            Thread.sleep(10000);
            SLF4JLoggerProxy.info(MarketDataClientTest.class,
                                  "Canceling request [{}]",
                                  requestId);
            marketDataClient.cancel(requestId);
            marketDataClient.removeMarketDataStatusListener(statusListener);
        } finally {
            if(marketDataClient != null) {
                marketDataClient.stop();
            }
        }
    }
    /**
     * Create a new MarketDataClientTest instance.
     */
    public MarketDataClientTest()
    {
        instance = this;
    }
    /**
     * Get the trading client factory value.
     *
     * @return a <code>StrategyAgentRpcClientFactory</code> value
     */
    @Bean
    public MarketDataRpcClientFactory getSeClientFactory()
    {
        MarketDataRpcClientFactory clientFactory = new MarketDataRpcClientFactory();
        return clientFactory;
    }
    /**
     * Get the autowired instance.
     *
     * @return a <code>MarketDataClientTest</code> value
     */
    @Bean
    public static MarketDataClientTest getMarketDataClientTest()
    {
        return new MarketDataClientTest();
    }
    /**
     * instance created for autowiring purposes
     */
    private static MarketDataClientTest instance;
    /**
     * hostname value
     */
    @Value("${metc.client.hostname:127.0.0.1}")
    private String hostname;
    /**
     * username value
     */
    @Value("${metc.client.username:trader}")
    private String username;
    /**
     * password value
     */
    @Value("${metc.client.password:trader}")
    private String password;
    /**
     * port value
     */
    @Value("${metc.client.port:8998}")
    private int port;
    /**
     * provides access to market data client services
     */
    private MarketDataClient marketDataClient;
    /**
     * creates {@link MarketDataClient} objects
     */
    @Autowired
    private MarketDataRpcClientFactory marketDataClientFactory;
}
//package org.marketcetera.clienttest;
//
//import java.util.Deque;
//
//import org.marketcetera.core.PlatformServices;
//import org.marketcetera.event.Event;
//import org.marketcetera.marketdata.AssetClass;
//import org.marketcetera.marketdata.Content;
//import org.marketcetera.marketdata.MarketDataContextClassProvider;
//import org.marketcetera.marketdata.MarketDataRequestBuilder;
//import org.marketcetera.marketdata.rpc.client.MarketDataRpcClient;
//import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
//import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
//import org.marketcetera.util.log.SLF4JLoggerProxy;
//
///* $License$ */
//
///**
// * Demonstrates how to connect to MATP market data services from an external application.
// *
// * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
// * @version $Id$
// * @since $Release$
// */
//public class MarketDataClientTest
//{
//    /**
//     * Main run method.
//     *
//     * @param inArgs a <code>String[]</code> value
//     */
//    public static void main(String[] inArgs)
//    {
//        SLF4JLoggerProxy.info(MarketDataClientTest.class,
//                              "Starting market data client test");
//        try {
//            MarketDataRpcClientParameters parameters = new MarketDataRpcClientParameters();
//            parameters.setUsername("user");
//            parameters.setPassword("password");
//            parameters.setHostname("locahost");
//            parameters.setPort(8998);
//            parameters.setContextClassProvider(new MarketDataContextClassProvider());
//            MarketDataRpcClient marketDataClient = new MarketDataRpcClientFactory().create(parameters);
//            marketDataClient.start();
//            SLF4JLoggerProxy.info(MarketDataClientTest.class,
//                                  "Connected to market data nexus: {}",
//                                  marketDataClient.isRunning());
//            MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
//            requestBuilder = requestBuilder.withContent(Content.TOP_OF_BOOK,Content.LATEST_TICK).withSymbols("METC").withAssetClass(AssetClass.EQUITY);
////            long requestId = marketDataClient.request(requestBuilder.create(),
////                                                      true);
////            for(int i=0;i<10;i++) {
////                Thread.sleep(1000);
////                Deque<Event> events = marketDataClient.getEvents(requestId);
////                SLF4JLoggerProxy.info(MarketDataClientTest.class,
////                                      "Retrieved {}",
////                                      events);
////            }
////            marketDataClient.cancel(requestId);
//            marketDataClient.stop();
//        } catch (Exception e) {
//            PlatformServices.handleException(MarketDataClientTest.class,
//                                             "Error executing market data client test",
//                                             e);
//        }
//        SLF4JLoggerProxy.info(MarketDataClientTest.class,
//                              "Ending market data client test");
//    }
//}
