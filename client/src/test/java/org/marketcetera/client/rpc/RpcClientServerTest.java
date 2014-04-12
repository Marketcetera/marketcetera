package org.marketcetera.client.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.BrokerAlgoTest;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.notifications.ServerStatusListener;
import org.marketcetera.core.position.PositionKeyFactory;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXMessageWrapper;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.TradeContextClassProvider;
import org.marketcetera.trade.utils.OrderHistoryManagerTest;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.SessionManager;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Tests {@link RpcClientImpl} and {@link RpcServer}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcClientServerTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        OrderHistoryManagerTest.once();
    }
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        stopClientServer();
        username = "user";
        password = "password";
        hostname = "127.0.0.1";
        port = -1;
        sessionManager = new SessionManager<MockSession>();
        authenticator = new MockAuthenticator();
        serverAdapter = new MockServerAdapter();
        startClientServer();
        assertTrue(server.isRunning());
        assertTrue(client.isServerAlive());
    }
    /**
     * Runs after each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @After
    public void after()
            throws Exception
    {
        stopClientServer();
    }
    /**
     * Tests disconnection and reconnection.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDisconnection()
            throws Exception
    {
        final AtomicBoolean status = new AtomicBoolean(false);
        ServerStatusListener statusListener = new ServerStatusListener() {
            @Override
            public void receiveServerStatus(boolean inStatus)
            {
                status.set(inStatus);
            }
        };
        client.addServerStatusListener(statusListener);
        assertTrue(status.get());
        // kill the server
        server.stop();
        assertFalse(server.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !client.isServerAlive();
            }
        });
        assertFalse(status.get());
        server.start();
        assertTrue(server.isRunning());
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return client.isServerAlive();
            }
        });
        assertTrue(status.get());
    }
    /**
     * Tests position calls.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testPositions()
            throws Exception
    {
        Date origin = new Date(0);
        Instrument[] testInstruments = new Instrument[] { new Equity("AAPL"),Future.fromString("AAPL-201306"),new Currency("USD/BTC"),OptionUtils.getOsiOptionFromString("MSFT  001022P12345123")};
        for(Instrument instrument : testInstruments) {
            generatePositionFor(instrument,
                                "account",
                                "trader");
            generatePositionFor(instrument,
                                null,
                                "trader");
            generatePositionFor(instrument,
                                "account",
                                null);
            generatePositionFor(instrument,
                                null,
                                null);
            if(instrument instanceof Equity) {
                client.getAllEquityPositionsAsOf(origin);
                client.getEquityPositionAsOf(origin,
                                             (Equity)instrument);
            } else if(instrument instanceof Option) {
                client.getAllOptionPositionsAsOf(origin);
                client.getOptionPositionAsOf(origin,
                                             (Option)instrument);
                client.getOptionPositionsAsOf(origin,
                                              instrument.getFullSymbol());
            } else if(instrument instanceof Currency) {
                client.getAllCurrencyPositionsAsOf(origin);
                client.getCurrencyPositionAsOf(origin,
                                               (Currency)instrument);
            } else if(instrument instanceof Future) {
                client.getAllFuturePositionsAsOf(origin);
                client.getFuturePositionAsOf(origin,
                                             (Future)instrument);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
    /**
     * Tests {@link RpcClientImpl#resolveSymbol(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSymbolResolution()
            throws Exception
    {
        Instrument[] testInstruments = new Instrument[] { new Equity("AAPL"),Future.fromString("AAPL-201306"),new Currency("USD/BTC"),new ConvertibleBond("468268KG1"),OptionUtils.getOsiOptionFromString("MSFT  001022P12345123")};
        for(Instrument instrument : testInstruments) {
            serverAdapter.getInstrumentsToResolve().put(instrument.getFullSymbol(),
                                                        instrument);
            assertEquals(instrument,
                         client.resolveSymbol(instrument.getFullSymbol()));
        }
        assertNull(client.resolveSymbol("not-a-known-symbol"));
    }
    /**
     * Tests {@link RpcClientImpl#addReport(org.marketcetera.trade.FIXMessageWrapper, org.marketcetera.trade.BrokerID)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAddReport()
            throws Exception
    {
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport("order-1",
                                                                                  null,
                                                                                  OrderStatus.Canceled);
        client.addReport((FIXMessageWrapper)report1,
                         new BrokerID("broker1"));
        assertEquals(1,
                     serverAdapter.getAddedReports().size());
    }
    /**
     * Tests {@link RpcClientImpl#getReportsSince(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testReportsSince()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                client.getReportsSince(null);
            }
        };
        assertTrue(serverAdapter.getReportsSince().isEmpty());
        ReportBase[] reports = client.getReportsSince(new Date(0));
        assertEquals(0,
                     reports.length);
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport("order-1",
                                                                                  null,
                                                                                  OrderStatus.Canceled);
        serverAdapter.getReportsSince().add((ReportBaseImpl)report1);
        reports = client.getReportsSince(new Date(0));
        assertEquals(1,
                     reports.length);
        OrderCancelReject report2 = OrderHistoryManagerTest.generateOrderCancelReject("order-1",
                                                                                      "order-1");
        serverAdapter.getReportsSince().add((ReportBaseImpl)report2);
        reports = client.getReportsSince(new Date(0));
        assertEquals(2,
                     reports.length);
    }
    /**
     * Tests {@link RpcClientImpl#getOpenOrders()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOpenOrders()
            throws Exception
    {
        assertTrue(serverAdapter.getOpenOrders().isEmpty());
        List<ReportBaseImpl> reports = client.getOpenOrders();
        assertEquals(0,
                     reports.size());
        ExecutionReport report1 = OrderHistoryManagerTest.generateExecutionReport("order-1",
                                                                                  null,
                                                                                  OrderStatus.Canceled);
        serverAdapter.getOpenOrders().add((ReportBaseImpl)report1);
        reports = client.getOpenOrders();
        assertEquals(1,
                     reports.size());
        OrderCancelReject report2 = OrderHistoryManagerTest.generateOrderCancelReject("order-1",
                                                                                      "order-1");
        serverAdapter.getOpenOrders().add((ReportBaseImpl)report2);
        reports = client.getOpenOrders();
        assertEquals(2,
                     reports.size());
    }
    /**
     * Tests {@link RpcClientImpl#getBrokersStatus()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBrokersStatus()
            throws Exception
    {
        assertNull(serverAdapter.getBrokersStatus());
        BrokersStatus brokersStatus = new BrokersStatus();
        assertTrue(brokersStatus.getBrokers().isEmpty());
        serverAdapter.setBrokersStatus(brokersStatus);
        BrokersStatus returnedBrokersStatus = client.getBrokersStatus();
        assertTrue(returnedBrokersStatus.getBrokers().isEmpty());
        brokersStatus = generateBrokersStatus();
        serverAdapter.setBrokersStatus(brokersStatus);
        returnedBrokersStatus = client.getBrokersStatus();
        assertEquals(2,
                     returnedBrokersStatus.getBrokers().size());
    }
    /**
     * 
     *
     *
     * @return
     */
    private BrokersStatus generateBrokersStatus()
    {
        return new BrokersStatus(Lists.newArrayList(generateBrokerStatus(),generateBrokerStatusWithAlgo()));
    }
    /**
     * 
     *
     *
     * @return
     */
    private BrokerStatus generateBrokerStatus()
    {
        BrokerStatus status = new BrokerStatus("broker-name-" + System.nanoTime(),
                                               new BrokerID("broker-"+System.nanoTime()),
                                               true);
        return status;
    }
    /**
     * 
     *
     *
     * @return
     */
    private BrokerStatus generateBrokerStatusWithAlgo()
    {
        BrokerAlgoSpec algo1 = BrokerAlgoTest.generateAlgoSpec();
        BrokerAlgoSpec algo2 = BrokerAlgoTest.generateAlgoSpec();
        BrokerStatus status = new BrokerStatus("broker-other-name-"+System.nanoTime(),
                                               new BrokerID("broker-"+System.nanoTime()),
                                               true,
                                               Sets.newHashSet(algo1,algo2));
        return status;
    }
    /**
     * Stops the test client and server.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void stopClientServer()
            throws Exception
    {
        if(client != null && client.isServerAlive()) {
            try {
                client.close();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        client = null;
        if(server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        server = null;
    }
    /**
     * Generates a random position for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inAccount a <code>String</code> value
     * @param inTraderId a <code>String</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void generatePositionFor(Instrument inInstrument,
                                     String inAccount,
                                     String inTraderId)
            throws Exception
    {
        if(inInstrument instanceof Equity) {
        } else if(inInstrument instanceof Option) {
        } else if(inInstrument instanceof Currency) {
            serverAdapter.getCurrencyPositions().put(PositionKeyFactory.createCurrencyKey(inInstrument.getFullSymbol(),
                                                                                          inAccount,
                                                                                          inTraderId),
                                                     EventTestBase.generateDecimalValue());
        } else if(inInstrument instanceof Future) {
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Starts the test client server and client.
     *
     * @throws Exception if an unexpected error occurs
     */
    private void startClientServer()
            throws Exception
    {
        server = new RpcServer<MockSession>();
        server.setHostname("127.0.0.1");
        if(port == -1) {
            port = assignPort();
        }
        server.setPort(port);
        server.setSessionManager(sessionManager);
        server.setAuthenticator(authenticator);
        server.setServerAdapter(serverAdapter);
        server.setContextClassProvider(TradeContextClassProvider.INSTANCE);
        server.start();
        parms = new RpcClientParameters(username,
                                        password.toCharArray(),
                                        "tcp://127.0.0.1:65535",
                                        hostname,
                                        port,
                                        "px",
                                        1000);
        parms.setUseJms(false);
        List<Class<?>> contextClasses = Lists.newArrayList();
        contextClasses.add(Instrument.class);
        contextClasses.add(ReportBaseImpl.class);
        parms.setContextClassProvider(TradeContextClassProvider.INSTANCE);
        client = new RpcClientImpl(parms);
    }
    /**
     * Assigns a port value that is not in use.
     * 
     * @return an <code>int</code> value
     */
    private int assignPort()
    {
        for(int i=MIN_PORT_NUMBER;i<=MAX_PORT_NUMBER;i++) {
            try(ServerSocket ss = new ServerSocket(i)) {
                ss.setReuseAddress(true);
                try(DatagramSocket ds = new DatagramSocket(i)) {
                    ds.setReuseAddress(true);
                    return i;
                }
            } catch (IOException e) {}
        }
        throw new IllegalArgumentException("No available ports");
    }
    /**
     * 
     */
    private MockServerAdapter serverAdapter;
    /**
     * 
     */
    private MockAuthenticator authenticator;
    /**
     * 
     */
    private SessionManager<MockSession> sessionManager;
    /**
     * 
     */
    private String hostname;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private String username;
    /**
     * 
     */
    private String password;
    /**
     * 
     */
    private static final int MIN_PORT_NUMBER = 10000;
    /**
     * 
     */
    private static final int MAX_PORT_NUMBER = 65535;
    /**
     * 
     */
    private RpcClientParameters parms;
    /**
     * 
     */
    private RpcClientImpl client;
    /**
     * 
     */
    private RpcServer<MockSession> server;
}
