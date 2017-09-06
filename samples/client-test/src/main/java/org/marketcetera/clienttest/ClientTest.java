package org.marketcetera.clienttest;

import java.math.BigDecimal;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.symbol.IterativeSymbolResolver;
import org.marketcetera.symbol.PatternSymbolResolver;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.SimpleOrderSummaryFactory;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.TradingClient;
import org.marketcetera.trading.rpc.TradingRpcClientFactory;
import org.marketcetera.trading.rpc.TradingRpcClientParametersImpl;
import org.marketcetera.trading.rpc.TradingUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Demonstrates how to connect to MATP order services from an external application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication
@EnableAutoConfiguration
@SpringBootConfiguration
public class ClientTest
{
    /**
     * Main run method.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(ClientTest.class,
                              inArgs);
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Starting client test");
        try {
            ClientTest clientTest = ClientTest.instance;
            clientTest.runTest();
        } catch (Exception e) {
            PlatformServices.handleException(ClientTest.class,
                                             "Client Test Error",
                                             e);
        }
        SLF4JLoggerProxy.info(ClientTest.class,
                              "Ending client test");
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
            TradingUtil.setFixSessionFactory(fixSessionFactory);
            TradingUtil.setOrderSummaryFactory(orderSummaryFactory);
            TradingUtil.setUserFactory(userFactory);
            TradingUtil.setSymbolResolverService(symbolResolverService);
            TradingRpcClientParametersImpl params = new TradingRpcClientParametersImpl();
            params.setHostname(hostname);
            params.setPort(port);
            params.setUsername(username);
            params.setPassword(password);
            tradingClient = tradeClientFactory.create(params);
            tradingClient.start();
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Client connected to {}:{} as {}",
                                  hostname,
                                  port,
                                  username);
            BrokerStatusListener brokerStatusListener = new BrokerStatusListener() {
                @Override
                public void receiveBrokerStatus(BrokerStatus inStatus)
                {
                    SLF4JLoggerProxy.info(ClientTest.this,
                                          "Received {}",
                                          inStatus);
                }
            };
            tradingClient.addBrokerStatusListener(brokerStatusListener);
            TradeMessageListener tradeMessageListener = new TradeMessageListener() {
                @Override
                public void receiveTradeMessage(TradeMessage inTradeMessage)
                {
                    SLF4JLoggerProxy.info(ClientTest.this,
                                          "Received {}",
                                          inTradeMessage);
                }
            };
            tradingClient.addTradeMessageListener(tradeMessageListener);
            Factory factory = Factory.getInstance();
            OrderSingle testOrder = factory.createOrderSingle();
            testOrder.setInstrument(new Equity("METC2"));
            testOrder.setOrderType(OrderType.Limit);
            testOrder.setQuantity(BigDecimal.TEN);
            testOrder.setPrice(BigDecimal.TEN);
            testOrder.setSide(Side.Buy);
            testOrder.setExecutionDestination("COLIN");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Sending {}",
                                  testOrder);
            tradingClient.sendOrder(testOrder);
            Thread.sleep(5000);
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Checking open orders");
            for(OrderSummary orderSummary : tradingClient.getOpenOrders()) {
                SLF4JLoggerProxy.info(ClientTest.class,
                                      "{}",
                                      orderSummary);
            }
            tradingClient.removeTradeMessageListener(tradeMessageListener);
            tradingClient.removeBrokerStatusListener(brokerStatusListener);
        } finally {
            if(tradingClient != null) {
                tradingClient.stop();
            }
        }
    }
    /**
     * Create a new ClientTest instance.
     */
    public ClientTest()
    {
        instance = this;
    }
    /**
     * Get the trading client factory value.
     *
     * @return a <code>TradingRpcClientFactory</code> value
     */
    @Bean
    public TradingRpcClientFactory getTradingClientFactory()
    {
        TradingRpcClientFactory tradingClientFactory = new TradingRpcClientFactory();
        return tradingClientFactory;
    }
    /**
     * Get the autowired instance.
     *
     * @return a <code>ClientTest</code> value
     */
    @Bean
    public static ClientTest getClientTest()
    {
        return new ClientTest();
    }
    /**
     * Get the FIX session factory value.
     *
     * @return a <code>FixSessionFactory</code> value
     */
    @Bean
    public FixSessionFactory getFixSessionFactory()
    {
        return new SimpleFixSessionFactory();
    }
    /**
     * Get the mutable order summary factory value.
     *
     * @return a <code>MutableOrderSummaryFactory</code> value
     */
    @Bean
    public MutableOrderSummaryFactory getMutableOrderSummaryFactory()
    {
        return new SimpleOrderSummaryFactory();
    }
    /**
     * Get the user factory value.
     *
     * @return a <code>UserFactory</code> value
     */
    @Bean
    public UserFactory getUserFactory()
    {
        return new SimpleUserFactory();
    }
    /**
     * Get the symbol resolver service value.
     *
     * @return a <code>SymbolResolverService</code> value
     */
    @Bean
    public SymbolResolverService getSymbolResolverService()
    {
        IterativeSymbolResolver symbolResolverService = new IterativeSymbolResolver();
        symbolResolverService.setSymbolResolvers(Lists.newArrayList(new PatternSymbolResolver()));
        TradingUtil.setSymbolResolverService(symbolResolverService);
        return symbolResolverService;
    }
    /**
     * instance created for autowiring purposes
     */
    private static ClientTest instance;
    /**
     * creates {@link FixSessionFactory} objects
     */
    @Autowired
    private FixSessionFactory fixSessionFactory;
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
     * provides access to trading client services
     */
    private TradingClient tradingClient;
    /**
     * creates {@link TradingClient} objects
     */
    @Autowired
    private TradingRpcClientFactory tradeClientFactory;
    /**
     * creates {@link MutableOrderSummary} objects
     */
    @Autowired
    private MutableOrderSummaryFactory orderSummaryFactory;
    /**
     * creates {@link User} objects
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * resolves symbols
     */
    @Autowired
    private SymbolResolverService symbolResolverService;
}
