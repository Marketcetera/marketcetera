package org.marketcetera.clienttest;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserFactory;
import org.marketcetera.admin.impl.SimpleUserFactory;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSessionFactory;
import org.marketcetera.fix.impl.SimpleFixSessionFactory;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.MutableOrderSummary;
import org.marketcetera.trade.MutableOrderSummaryFactory;
import org.marketcetera.trade.MutableReport;
import org.marketcetera.trade.MutableReportFactory;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.SimpleOrderSummaryFactory;
import org.marketcetera.trade.SimpleReportFactory;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.client.SendOrderResponse;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.trading.rpc.TradeRpcClientParametersImpl;
import org.marketcetera.trading.rpc.TradeRpcUtil;
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
            TradeRpcUtil.setFixSessionFactory(fixSessionFactory);
            TradeRpcUtil.setOrderSummaryFactory(orderSummaryFactory);
            TradeRpcUtil.setUserFactory(userFactory);
            TradeRpcUtil.setReportFactory(reportFactory);
            TradeRpcClientParametersImpl params = new TradeRpcClientParametersImpl();
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
            // add a broker status listener
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
            // add a trade message listener
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
            // send an order
            Factory factory = Factory.getInstance();
            OrderSingle testOrder = factory.createOrderSingle();
            testOrder.setInstrument(new Option("METC",
                                               "20171210",
                                               BigDecimal.TEN,
                                               OptionType.Put));
            testOrder.setOrderType(OrderType.Limit);
            testOrder.setQuantity(BigDecimal.TEN);
            testOrder.setPrice(BigDecimal.TEN);
            testOrder.setSide(Side.Buy);
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Sending {}",
                                  testOrder);
            SendOrderResponse sendOrderResponse = tradingClient.sendOrder(testOrder);
            // wait a bit to receive some execution reports
            Thread.sleep(5000);
            // query open orders, if there are any
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Checking open orders");
            for(OrderSummary orderSummary : tradingClient.getOpenOrders()) {
                SLF4JLoggerProxy.info(ClientTest.class,
                                      "{}",
                                      orderSummary);
            }
            // test symbol resolution
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Testing symbol resolution");
            Equity equity = new Equity("METC");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  equity.getFullSymbol(),
                                  tradingClient.resolveSymbol(equity.getFullSymbol()));
            Future future = Future.fromString("METC-201812");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  future.getFullSymbol(),
                                  tradingClient.resolveSymbol(future.getFullSymbol()));
            Currency currency = new Currency("USD/GBP");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  currency.getFullSymbol(),
                                  tradingClient.resolveSymbol(currency.getFullSymbol()));
            Option option = new Option("METC",
                                       "20181215",
                                       BigDecimal.TEN,
                                       OptionType.Put);
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  option.getFullSymbol(),
                                  tradingClient.resolveSymbol(option.getFullSymbol()));
            ConvertibleBond convertibleBond = new ConvertibleBond("US013817AT86");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  convertibleBond.getFullSymbol(),
                                  tradingClient.resolveSymbol(convertibleBond.getFullSymbol()));
            // test broker status
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Testing brokers status");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{}",
                                  tradingClient.getBrokersStatus());
            // test root order ID lookup
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Testing root order ID lookup");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  sendOrderResponse.getOrderId(),
                                  tradingClient.findRootOrderIdFor(sendOrderResponse.getOrderId()));
            // test positions
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Testing positions");
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "{} -> {}",
                                  testOrder.getInstrument(),
                                  tradingClient.getPositionAsOf(new Date(),
                                                                testOrder.getInstrument()));
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "All positions -> {}",
                                  tradingClient.getAllPositionsAsOf(new Date()));
            SLF4JLoggerProxy.info(ClientTest.class,
                                  "Option positions by root: {} -> {}",
                                  tradingClient.getOptionPositionsAsOf(new Date(),
                                                                       "METC"));
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
    public TradeRpcClientFactory getTradingClientFactory()
    {
        TradeRpcClientFactory tradingClientFactory = new TradeRpcClientFactory();
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
     * Get the report factory value.
     *
     * @return a <code>MutableReportFactory</code> value
     */
    @Bean
    public MutableReportFactory getReportFactory()
    {
        return new SimpleReportFactory();
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
    private TradeClient tradingClient;
    /**
     * creates {@link TradeClient} objects
     */
    @Autowired
    private TradeRpcClientFactory tradeClientFactory;
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
     * creates {@link MutableReport} objects
     */
    @Autowired
    private MutableReportFactory reportFactory;
}
