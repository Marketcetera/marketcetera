package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.*;
import static org.marketcetera.trade.TypesTestBase.*;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.math.BigDecimal;
import java.beans.ExceptionListener;
import java.lang.reflect.Method;

import quickfix.field.OrdStatus;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;

/* $License$ */
/**
 * Tests the client functionality including transmission of trades and reports
 * to and from a mock server over JMS. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientTest {
    @BeforeClass
    public static void setup() throws Exception {
        LoggerConfiguration.logSetup();
        FIXDataDictionaryManager.initialize(FIXVersion.FIX42,
                FIXVersion.FIX42.getDataDictionaryURL());
        initServer();
    }

    @AfterClass
    public static void closeServer() throws Exception {
        if (sServer != null) {
            sServer.close();
            sServer = null;
        }
    }

    @After
    public void closeClient() {
        if(mClient != null) {
            mClient.close();
        }
        clearAll();
    }
    @Test
    public void unimplemented() throws Exception {
        initClient();
        new ExpectedFailure<UnsupportedOperationException>(null){
            protected void run() throws Exception {
                getClient().getReportsSince(null);
            }
        };
        new ExpectedFailure<UnsupportedOperationException>(null){
            protected void run() throws Exception {
                getClient().getPositionAsOf(null,null);
            }
        };
    }
    @Test
    public void connect() throws Exception {
        initClient();
        assertNotNull(ClientManager.getInstance());
    }
    @Test
    public void connectFailure() throws Exception {
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_URL){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters("you",
                        "why".toCharArray(), null));
            }
        };
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_URL){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters("you",
                        "why".toCharArray(), "  "));
            }
        };
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_USERNAME){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters(null,
                        "why".toCharArray(), "tcp://whatever:404"));
            }
        };
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_USERNAME){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters("   ",
                        "why".toCharArray(), "tcp://whatever:404"));
            }
        };
        final ClientParameters parameters = new ClientParameters("name",
                "game".toCharArray(), MockServer.URL);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, parameters.getURL(),
                parameters.getUsername()){
            protected void run() throws Exception {
                ClientManager.init(parameters);
            }
        };
        //Use the correct password but incorrect port number
        final ClientParameters wrongPort = new ClientParameters(
                parameters.getUsername(), "name".toCharArray(),
                "tcp://localhost:61617");
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, wrongPort.getURL(),
                wrongPort.getUsername()){
            protected void run() throws Exception {
                ClientManager.init(wrongPort);
            }
        };
        //Make sure null & empty passwords are accepted
        final ClientParameters nullPass = new ClientParameters(
                parameters.getUsername(), null,
                MockServer.URL);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, nullPass.getURL(),
                nullPass.getUsername()){
            protected void run() throws Exception {
                ClientManager.init(nullPass);
            }
        };
        final ClientParameters emptyPass = new ClientParameters(
                parameters.getUsername(), "  ".toCharArray(),
                MockServer.URL);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, emptyPass.getURL(),
                emptyPass.getUsername()){
            protected void run() throws Exception {
                ClientManager.init(emptyPass);
            }
        };
    }
    @Test
    public void sendOrderSingle() throws Exception {
        //Initialize a client
        initClient();

        //Create order
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setAccount("my account");
        Map<String,String> map = new HashMap<String,String>();
        map.put("101","value1");
        map.put("201","value2");
        order.setCustomFields(map);
        order.setDestinationID(new DestinationID("brokerA"));
        order.setOrderID(new OrderID("ord1"));
        order.setOrderType(OrderType.Limit);
        order.setPrice(new BigDecimal("83.43"));
        order.setQuantity(new BigDecimal("823.443"));
        order.setSide(Side.Buy);
        order.setSymbol(new MSymbol("IBM", SecurityType.CommonStock));
        order.setTimeInForce(TimeInForce.AtTheClose);

        //Create an execution report for the mock server to send back
        ExecutionReport report = createExecutionReport();
        sServer.getHandler().addToSend(report);

        //Send an order
        getClient().sendOrder(order);
        if(mListener.getException() != null) {
            SLF4JLoggerProxy.error(this, "Unexpected exception",
                    mListener.getException());
        }
        //Verify we got no exception when sending the order
        assertNull(mListener.getException());
        //Verify transmitted order
        Object received = sServer.getHandler().removeReceived();
        assertNotNull(received);
        assertTrue(received instanceof OrderSingle);
        assertOrderSingleEquals(order, (OrderSingle)received);
        //Verify received report
        ReportBase receivedReport = mReplies.getReport();
        assertTrue(receivedReport instanceof ExecutionReport);
        assertExecReportEquals(report, (ExecutionReport) receivedReport);
    }
    @Test
    public void sendOrderReplace() throws Exception {
        initClient();

        //Create order
        OrderReplace order = Factory.getInstance().createOrderReplace(
                createExecutionReport());

        //Create an execution report for the mock server to send back
        ExecutionReport report = createExecutionReport();
        sServer.getHandler().addToSend(report);

        //Send an order
        getClient().sendOrder(order);
        //Verify we got no exception when sending the order
        if(mListener.getException() != null) {
            SLF4JLoggerProxy.error(this, "Unexpected exception",
                    mListener.getException());
        }
        //Verify no errors
        assertNull(mListener.getException());
        //Verify transmitted order
        Object received = sServer.getHandler().removeReceived();
        assertNotNull(received);
        assertTrue(received instanceof OrderReplace);
        assertOrderReplaceEquals(order, (OrderReplace)received);
        //Verify received report
        ReportBase receivedReport = mReplies.getReport();
        assertTrue(receivedReport instanceof ExecutionReport);
        assertExecReportEquals(report, (ExecutionReport) receivedReport);
    }

    @Test
    public void sendOrderCancel() throws Exception {
        initClient();

        //Create cancel order
        OrderCancel order = Factory.getInstance().createOrderCancel(
                createExecutionReport());

        //Create a reject for the mock server to send back
        OrderCancelReject report = createCancelReject();
        sServer.getHandler().addToSend(report);

        //Send an order
        getClient().sendOrder(order);
        //Verify we got no exception when sending the order
        if(mListener.getException() != null) {
            SLF4JLoggerProxy.error(this, "Unexpected exception",
                    mListener.getException());
        }
        //Verify no errors
        assertNull(mListener.getException());
        //Verify transmitted order
        Object received = sServer.getHandler().removeReceived();
        assertNotNull(received);
        assertTrue(received instanceof OrderCancel);
        assertOrderCancelEquals(order, (OrderCancel)received);
        //Verify received report
        ReportBase receivedReport = mReplies.getReport();
        assertTrue(receivedReport instanceof OrderCancelReject);
        assertCancelRejectEquals(report, (OrderCancelReject) receivedReport);
    }

    @Test
    public void sendOrderFIX() throws Exception {
        initClient();

        //Create FIX order
        FIXOrder order = Factory.getInstance().createOrder(
                FIXVersion.FIX42.getMessageFactory().newLimitOrder("clOrd1",
                        quickfix.field.Side.BUY, new BigDecimal("8934.234"),
                        new MSymbol("IBM", SecurityType.Option),
                        new BigDecimal("9834.23"),
                        quickfix.field.TimeInForce.DAY, "no"),
                new DestinationID("bro"));

        //Create a report for the mock server to send back
        ExecutionReport report = createExecutionReport();
        sServer.getHandler().addToSend(report);

        //Send an order
        getClient().sendOrderRaw(order);
        //Verify we got no exception when sending the order
        if(mListener.getException() != null) {
            SLF4JLoggerProxy.error(this, "Unexpected exception",
                    mListener.getException());
        }
        //Verify no errors
        assertNull(mListener.getException());
        //Verify transmitted order
        Object received = sServer.getHandler().removeReceived();
        assertNotNull(received);
        assertTrue(received instanceof FIXOrder);
        assertOrderFIXEquals(order, (FIXOrder)received);
        //Verify received report
        ReportBase receivedReport = mReplies.getReport();
        assertTrue(receivedReport instanceof ExecutionReport);
        assertExecReportEquals(report, (ExecutionReport) receivedReport);
    }

    @Test
    public void reportListening() throws Exception {
        initClient();
        //Create our own report listener
        ReplyListener chitChat = new ReplyListener();
        //Add it to the client
        getClient().addReportListener(chitChat);
        try {
            ExecutionReport report = sendVanillaOrder();
            //Verify our listener got it.
            ReportBase receivedReport = chitChat.getReport();
            assertTrue(receivedReport instanceof ExecutionReport);
            assertExecReportEquals(report, (ExecutionReport) receivedReport);
            //Now set our reply listener to fail
            chitChat.setFail(true);
            //Send the order and verify that the main test listener got it
            //this verifies that exceptions from listener do not impact
            //notifications to other listeners
            report = sendVanillaOrder();
            //Verify our listener got it.
            receivedReport = chitChat.getReport();
            assertTrue(receivedReport instanceof ExecutionReport);
            assertExecReportEquals(report, (ExecutionReport) receivedReport);

            //Now remove our listener.
            getClient().removeReportListener(chitChat);
            chitChat.clear();
            assertNull(chitChat.peekReport());
            //Send another order
            sendVanillaOrder();
            //Verify our listener didn't get it
            assertNull(chitChat.peekReport());
        } finally {
            getClient().removeReportListener(chitChat);
        }

    }

    /**
     * Verifies the client's behavior after it's been closed.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void closedBehavior() throws Exception {
        initClient();
        final Client client = ClientManager.getInstance();
        client.close();
        String expectedMsg = Messages.CLIENT_CLOSED.getText();
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.addExceptionListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.addReportListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getLastConnectTime();
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getParameters();
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getPositionAsOf(null, null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getReportsSince(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.reconnect();
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.reconnect(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.removeExceptionListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.removeReportListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrder((OrderSingle)null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrder((OrderReplace)null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrder((OrderCancel) null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrderRaw(null);
            }
        };
        //we can call close again
        client.close();
    }

    private ExecutionReport sendVanillaOrder() throws Exception {
        //Clean up any dirty state from previous failures
        clearAll();
        //Create a report for the mock server to send back
        ExecutionReport report = createExecutionReport();
        if (sServer != null) {
            sServer.getHandler().addToSend(report);
        }
        //Send an order
        getClient().sendOrder(Factory.getInstance().createOrderReplace(
                    createExecutionReport()));
        //Verify we got no exception when sending the order
        if(mListener.getException() != null) {
            SLF4JLoggerProxy.error(this, "Unexpected exception",
                    mListener.getException());
        }
        //Verify no errors
        assertNull(mListener.getException());
        //Verify received report
        ReportBase receivedReport = mReplies.getReport();
        assertTrue(receivedReport instanceof ExecutionReport);
        assertExecReportEquals(report, (ExecutionReport) receivedReport);
        return report;
    }

    @Test
    public void exceptionListening() throws Exception {
        initClient();
        //Create our own exception listener
        ErrorListener earl = new ErrorListener();
        //Add it to the client
        getClient().addExceptionListener(earl);
        try {
            //Send a plain order without any errors.
            sendVanillaOrder();
            //Verify the listener is not notified.
            assertNull(earl.getException());
            //Close client internally to generate errors
            Method m = getClient().getClass().getDeclaredMethod("internalClose");
            m.setAccessible(true);
            m.invoke(getClient());
            //Verify sending order fails
            ConnectionException exception = new ExpectedFailure<ConnectionException>(
                    Messages.ERROR_SEND_MESSAGE) {
                protected void run() throws Exception {
                    sendVanillaOrder();
                }
            }.getException();
            //Verify we got the exception
            assertEquals(exception,  earl.getException());
            //Verify that the main listener got it as well.
            assertEquals(exception,  mListener.getException());
            //Set our listener to fail
            earl.setFail(true);
            //Send order and verify that both listeners got the exception
            //in spite of throwing an exception
            exception = new ExpectedFailure<ConnectionException>(
                    Messages.ERROR_SEND_MESSAGE) {
                protected void run() throws Exception {
                    sendVanillaOrder();
                }
            }.getException();
            assertEquals(exception,  earl.getException());
            assertEquals(exception,  mListener.getException());
            //Now remove our listener
            getClient().removeExceptionListener(earl);
            earl.clear();
            //Send order again, verify that our listener doesn't get notified
            exception = new ExpectedFailure<ConnectionException>(
                    Messages.ERROR_SEND_MESSAGE) {
                protected void run() throws Exception {
                    sendVanillaOrder();
                }
            }.getException();
            assertNull(earl.getException());
            assertEquals(exception,  mListener.getException());

        } finally {
            getClient().removeExceptionListener(earl);
        }
    }

    /**
     * Verifies the interplay between client initialization, reconnect & close.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void lifecycle() throws Exception {
        initClient();
        assertTrue(ClientManager.isInitialized());
        //Verify that attempt to re-init the client fails
        new ExpectedFailure<ClientInitException>(
                Messages.CLIENT_ALREADY_INITIALIZED){
            protected void run() throws Exception {
                initClient();
            }
        };
        //Close client and verify that we init it again
        closeClient();
        assertFalse(ClientManager.isInitialized());
        initClient();
        assertTrue(ClientManager.isInitialized());
        //Shutdown the server
        closeServer();
        //get reconnect to fail
        new ExpectedFailure
                <ConnectionException>(Messages.ERROR_CONNECT_TO_SERVER){
            protected void run() throws Exception {
                getClient().reconnect();
            }
        };
        assertTrue(ClientManager.isInitialized());
        //Verify that we cannot init client
        new ExpectedFailure<ClientInitException>(
                Messages.CLIENT_ALREADY_INITIALIZED){
            protected void run() throws Exception {
                initClient();
            }
        };
        //Close the client
        closeClient();
        assertFalse(ClientManager.isInitialized());
        //Verify that we can now attempt to reconnect but it fails because
        //server is not up
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER){
            protected void run() throws Exception {
                initClient();
            }
        };
        assertFalse(ClientManager.isInitialized());
        //Restart the server
        initServer();
        //verify that we can init the client now
        initClient();
        assertTrue(ClientManager.isInitialized());
    }
    @Test
    public void reconnect() throws Exception {
        initClient();
        Date connectTime = getClient().getLastConnectTime();
        //Test a round trip
        sendVanillaOrder();
        //Sleep to ensure we have different connect time
        Thread.sleep(100);
        //do a reconnect
        getClient().reconnect();
        assertTrue(getClient().getLastConnectTime().compareTo(connectTime) > 0);
        connectTime = getClient().getLastConnectTime();
        //Test another round trip
        sendVanillaOrder();
        //Shutdownn the server
        closeServer();
        //Verify failure sending an order
        ConnectionException exception = new ExpectedFailure
                <ConnectionException>(Messages.ERROR_SEND_MESSAGE){
            protected void run() throws Exception {
                sendVanillaOrder();
            }
        }.getException();
        //Verify we got the exception
        assertNotNull(mListener.getException());
        assertEquals(exception, mListener.getException());
        assertEquals(connectTime,  getClient().getLastConnectTime());
        //Verify reconnect fails
        new ExpectedFailure
                <ConnectionException>(Messages.ERROR_CONNECT_TO_SERVER){
            protected void run() throws Exception {
                getClient().reconnect();
            }
        };
        //Verify that the time is unchanged
        assertEquals(connectTime,  getClient().getLastConnectTime());
        //Verify that the client is still initialized
        assertTrue(ClientManager.isInitialized());
        //Restart the server
        initServer();
        //Verify order still fails
        exception = new ExpectedFailure
                <ConnectionException>(Messages.ERROR_SEND_MESSAGE) {
            protected void run() throws Exception {
                sendVanillaOrder();
            }
        }.getException();
        assertEquals(new ClientInitException(Messages.NOT_CONNECTED_TO_SERVER),
                exception.getCause());
        //Verify we got the exception
        assertNotNull(mListener.getException());
        assertEquals(exception, mListener.getException());
        //Sleep to ensure we have different connect time
        Thread.sleep(100);
        //Now reconnect
        getClient().reconnect();
        assertTrue(getClient().getLastConnectTime().compareTo(connectTime) > 0);
        //Verify order goes through
        sendVanillaOrder();
    }
    @Test
    public void reconnectParameters() throws Exception {
        initClient();
        Date connectTime = getClient().getLastConnectTime();
        //Test a round trip
        sendVanillaOrder();
        ClientParameters oldParms = getClient().getParameters();
        //Sleep to ensure we have different connect time
        Thread.sleep(100);
        //Now reconnect the client using a different parameters
        ClientParameters parms = new ClientParameters("you",
                "you".toCharArray(), MockServer.URL);
        getClient().reconnect(parms);
        assertEquals(parms, getClient().getParameters());
        assertFalse(oldParms.equals(parms));
        assertTrue(getClient().getLastConnectTime().compareTo(connectTime) > 0);
        //Test a round trip
        sendVanillaOrder();
    }

    /**
     * Creates a sample execution report for the mock server to send back.
     *
     * @return mock execution report.
     *
     * @throws Exception if there were errors creating the execution report.
     */
    static ExecutionReport createExecutionReport() throws Exception {
        return Factory.getInstance().createExecutionReport(FIXVersion.FIX42.
                getMessageFactory().newExecutionReport("ord1", "clord" +
                sCounter.getAndIncrement(),
                "exec1", OrdStatus.NEW, quickfix.field.Side.BUY,
                new BigDecimal("4343.49"), new BigDecimal("498.34"),
                new BigDecimal("783343.49"), new BigDecimal("598.34"),
                new BigDecimal("234343.49"), new BigDecimal("798.34"),
                new MSymbol("IBM", SecurityType.CommonStock), "my acc"),
                new DestinationID("bro"), Originator.Destination);
    }

    /**
     * Creates a sample cancel reject report for the mock server to send back.
     *
     * @return sample cancel reject report.
     *
     * @throws Exception if there were errors creating the report.
     */
    static OrderCancelReject createCancelReject() throws Exception {
        return Factory.getInstance().createOrderCancelReject(
                FIXVersion.FIX42.getMessageFactory().newOrderCancelReject(
                        new quickfix.field.OrderID("brok3"),
                        new ClOrdID("clord" + sCounter.getAndIncrement()),
                        new OrigClOrdID("origord1"),
                        "what?", null),
                new DestinationID("bro"));
    }

    private void clearAll() {
        mListener.clear();
        mReplies.clear();
        if (sServer != null) {
            sServer.getHandler().clear();
        }
    }

    private Client getClient() {
        if(mClient == null) {
            throw new NullPointerException("Call initClient() first");
        }
        return mClient;
    }
    private void initClient()
            throws ConnectionException, ClientInitException {
        Date currentTime = new Date();
        ClientParameters parameters = new ClientParameters("name",
                "name".toCharArray(), MockServer.URL);
        ClientManager.init(parameters);
        mClient = ClientManager.getInstance();
        mClient.addExceptionListener(mListener);
        mClient.addReportListener(mReplies);
        assertEquals(parameters, mClient.getParameters());
        assertNotNull(mClient.getLastConnectTime());
        assertTrue(mClient.getLastConnectTime().compareTo(currentTime) >= 0);
    }

    private static void initServer() {
        if (sServer == null) {
            sServer = new MockServer();
        }
    }
    private final ErrorListener mListener = new ErrorListener();
    private final ReplyListener mReplies = new ReplyListener();
    private static MockServer sServer;
    private Client mClient;
    private static final AtomicLong sCounter = new AtomicLong();
    private static class ErrorListener implements ExceptionListener {
        public void exceptionThrown(Exception e) {
            SLF4JLoggerProxy.debug(this, e);
            mException = e;
            if (mFail) {
                throw new IllegalArgumentException("Test Failure");
            }
        }

        public Exception getException() {
            return mException;
        }
        public void clear() {
            mException = null;
            mFail = false;
        }
        public void setFail(boolean isFail) {
            mFail = isFail;
        }

        private boolean mFail = false;
        private Exception mException;
    }
    private static class ReplyListener implements ReportListener {

        public void receiveExecutionReport(ExecutionReport inReport) {
            //Use add() instead of put() as these need to be non-blocking.
            mReports.add(inReport);
            if (mFail) {
                throw new IllegalArgumentException("Test Failure");
            }
        }

        public void receiveCancelReject(OrderCancelReject inReport) {
            //Use add() instead of put() as these need to be non-blocking.
            mReports.add(inReport);
            if (mFail) {
                throw new IllegalArgumentException("Test Failure");
            }
        }

        public ReportBase getReport() throws InterruptedException {
            //Use take as we should block until a message is available.
            return mReports.take();
        }
        public ReportBase peekReport() {
            return mReports.peek();
        }
        public void setFail(boolean isFail) {
            mFail = isFail;
        }
        public void clear() {
            mReports.clear();
            mFail = false;
        }
        private boolean mFail = false;
        private BlockingQueue<ReportBase> mReports =
                new LinkedBlockingQueue<ReportBase>();
    }
}
