package org.marketcetera.client;

import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.client.jms.OrderEnvelope;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateless.Node;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.*;

import static org.marketcetera.trade.TypesTestBase.*;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.FIXVersion;
import org.junit.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

import java.util.List;
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
 * Tests the client functionality including transmission of trades,
 * reports, and broker status to and from a mock server over JMS.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientTest {
    /*
     * This value can be set to a much higher value to assess
     * performance of jms roundtrip communications.
     * Keep the value greater than one to ensure that changes to
     * the unit test do not negatively impact the ability to
     * repeatedly carry out the round trips.
     */
    private static final int NUM_REPEAT = 5;
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
    public void connect() throws Exception {
        initClient();
        assertNotNull(ClientManager.getInstance());
    }

    @Test
    public void connectFailure() throws Exception {
        //Null URL
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_URL){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters("you",
                        "why".toCharArray(), null, Node.DEFAULT_HOST,
                        Node.DEFAULT_PORT));
            }
        };
        //Empty URL
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_URL){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters("you",
                        "why".toCharArray(), "  ", Node.DEFAULT_HOST,
                        Node.DEFAULT_PORT));
            }
        };
        //null user name
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_USERNAME){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters(null,
                        "why".toCharArray(), "tcp://whatever:404",
                        Node.DEFAULT_HOST, Node.DEFAULT_PORT));
            }
        };
        //empty user name
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_USERNAME){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters("   ",
                        "why".toCharArray(), "tcp://whatever:404",
                        Node.DEFAULT_HOST, Node.DEFAULT_PORT));
            }
        };
        //null hostname
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_HOSTNAME){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                        DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                        null, Node.DEFAULT_PORT));
            }
        };
        //empty hostname
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_NO_HOSTNAME){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                        DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                        "  ", Node.DEFAULT_PORT));
            }
        };
        //invalid port number, lower bound
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_INVALID_PORT, -1){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                        DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                        Node.DEFAULT_HOST, -1));
            }
        };
        //invalid port number, upper bound
        new ExpectedFailure<ConnectionException>(
                Messages.CONNECT_ERROR_INVALID_PORT, 65536){
            protected void run() throws Exception {
                ClientManager.init(new ClientParameters(DEFAULT_CREDENTIAL,
                        DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                        Node.DEFAULT_HOST, 65536));
            }
        };
        //no server at port
        final ClientParameters noServerAtPort = new ClientParameters(DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT + 1);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, noServerAtPort.getURL(),
                noServerAtPort.getUsername(), Node.DEFAULT_HOST,
                Node.DEFAULT_PORT + 1){
            protected void run() throws Exception {
                ClientManager.init(noServerAtPort);
            }
        };
        //auth failure
        final ClientParameters parameters = new ClientParameters(DEFAULT_CREDENTIAL,
                "game".toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, parameters.getURL(),
                parameters.getUsername(),Node.DEFAULT_HOST, Node.DEFAULT_PORT){
            protected void run() throws Exception {
                ClientManager.init(parameters);
            }
        };
        //Use the correct password but incorrect port number
        final ClientParameters wrongPort = new ClientParameters(
                parameters.getUsername(), DEFAULT_CREDENTIAL.toCharArray(),
                "tcp://localhost:61617", Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, wrongPort.getURL(),
                wrongPort.getUsername(), Node.DEFAULT_HOST, Node.DEFAULT_PORT){
            protected void run() throws Exception {
                ClientManager.init(wrongPort);
            }
        };
        //Make sure null & empty passwords are accepted
        final ClientParameters nullPass = new ClientParameters(
                parameters.getUsername(), null,
                MockServer.URL, Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, nullPass.getURL(),
                nullPass.getUsername(), Node.DEFAULT_HOST, Node.DEFAULT_PORT){
            protected void run() throws Exception {
                ClientManager.init(nullPass);
            }
        };
        final ClientParameters emptyPass = new ClientParameters(
                parameters.getUsername(), "  ".toCharArray(),
                MockServer.URL, Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_CONNECT_TO_SERVER, emptyPass.getURL(),
                emptyPass.getUsername(), Node.DEFAULT_HOST, Node.DEFAULT_PORT){
            protected void run() throws Exception {
                ClientManager.init(emptyPass);
            }
        };
    }

    @Test
    public void credentialsMatch() throws Exception {
        initClient();
        assertFalse(ClientManager.getInstance().isCredentialsMatch(null, null));
        assertFalse(ClientManager.getInstance().isCredentialsMatch(
                DEFAULT_CREDENTIAL, null));
        assertFalse(ClientManager.getInstance().isCredentialsMatch(null,
                DEFAULT_CREDENTIAL.toCharArray()));
        assertFalse(ClientManager.getInstance().isCredentialsMatch("",
                DEFAULT_CREDENTIAL.toCharArray()));
        String otherUser = "you";
        assertFalse(ClientManager.getInstance().isCredentialsMatch(otherUser,
                DEFAULT_CREDENTIAL.toCharArray()));
        assertFalse(ClientManager.getInstance().isCredentialsMatch(
                DEFAULT_CREDENTIAL, "".toCharArray()));
        assertFalse(ClientManager.getInstance().isCredentialsMatch(
                DEFAULT_CREDENTIAL, otherUser.toCharArray()));
        assertFalse(ClientManager.getInstance().isCredentialsMatch(
                otherUser, otherUser.toCharArray()));
        assertTrue(ClientManager.getInstance().isCredentialsMatch(
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL.toCharArray()));
        //reconnect with different credentials
        ClientParameters parms = new ClientParameters(otherUser,
                otherUser.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        ClientManager.getInstance().reconnect(parms);
        //verify that old credentials don't work
        assertFalse(ClientManager.getInstance().isCredentialsMatch(
                DEFAULT_CREDENTIAL, DEFAULT_CREDENTIAL.toCharArray()));
        //and the new ones do.
        assertTrue(ClientManager.getInstance().isCredentialsMatch(
                otherUser, otherUser.toCharArray()));
    }

    @Test
    public void webServices() throws Exception {
        initClient();

        List<BrokerStatus> ds =
            getClient().getBrokersStatus().getBrokers();
        assertEquals(2,ds.size());
        BrokerStatus d = ds.get(0);
        assertEquals("N1",d.getName());
        assertEquals("ID1",d.getId().getValue());
        d = ds.get(1);
        assertEquals("N2",d.getName());
        assertEquals("ID2",d.getId().getValue());

        // existing user.
        MockServiceImpl.sActive = false;
        UserID id = new UserID(2);
        UserInfo info = getClient().getUserInfo(id, true);
        assertEquals("bob", info.getName());
        assertEquals(id, info.getId());
        assertFalse(info.getActive());
        assertFalse(info.getSuperuser());

        MockServiceImpl.sActive = true;
        // cache contains old value.
        assertFalse(getClient().getUserInfo(id, true).getActive());
        // bypass cache.
        assertTrue(getClient().getUserInfo(id, false).getActive());
        // cache has been updated.
        assertTrue(getClient().getUserInfo(id, true).getActive());

        // nonexistent user.
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_REMOTE_EXECUTION){
            protected void run() throws Exception {
                getClient().getUserInfo(null, true);
            }
        };
        new ExpectedFailure<ConnectionException>(
                Messages.ERROR_REMOTE_EXECUTION){
            protected void run() throws Exception {
                getClient().getUserInfo(null, false);
            }
        };

        Factory f=Factory.getInstance();
        BrokerID dID=new BrokerID("me");
        quickfix.fix44.ExecutionReport er=new quickfix.fix44.ExecutionReport();
        er.set(new OrigClOrdID("42"));
        quickfix.fix44.OrderCancelReject ocr=new quickfix.fix44.OrderCancelReject();
        ocr.set(new OrigClOrdID("43"));
        ExecutionReportImpl reportImpl = (ExecutionReportImpl)
                f.createExecutionReport(er, dID, Originator.Server, null, null);
        //Add report ID to test its serialization
        ReportID reportID = new ReportID(1234);
        ReportBaseImpl.assignReportID(reportImpl, reportID);
        OrderCancelRejectImpl reject = (OrderCancelRejectImpl)
                f.createOrderCancelReject(ocr, dID, Originator.Server, null, null);
        ReportID rejectID = new ReportID(2345);
        ReportBaseImpl.assignReportID(reject, rejectID);
        MockServiceImpl.sReports = new ReportBaseImpl[] {
                reportImpl,
                reject
        };
        ReportBase[] rs = getClient().getReportsSince(new Date());
        assertEquals(2,rs.length);
        ExecutionReport report = (ExecutionReport)rs[0];
        assertEquals(dID,report.getBrokerID());
        assertEquals(Originator.Server,report.getOriginator());
        assertEquals("42",report.getOriginalOrderID().getValue());
        assertEquals(reportID, report.getReportID());
        OrderCancelReject crreport = (OrderCancelReject)rs[1];
        assertEquals(dID,crreport.getBrokerID());
        assertEquals("43",crreport.getOriginalOrderID().getValue());
        assertEquals(rejectID, crreport.getReportID());

        MockServiceImpl.sReports = new ReportBaseImpl[0];
        rs = getClient().getReportsSince(new Date());
        assertEquals(0,rs.length);
        
        MockServiceImpl.sReports = null;
        rs = getClient().getReportsSince(new Date());
        assertEquals(0,rs.length);

        assertEquals(BigDecimal.TEN,getClient().getPositionAsOf
                     (new Date(10),null));
        assertEquals(MockServiceImpl.POSITIONS, getClient().
                getPositionsAsOf(new Date()));
    }

    @Test(timeout=60000)
    public void heartbeats()
        throws Exception
    {
        initClient(SHORT_INTERVAL);
        assertTrue(getClient().isServerAlive());
        int count=sServer.getServiceImpl().getHeartbeatCount();
        while (true) {
            // Keep trying, in case the heartbeat thread is
            // experiencing starvation.
            Thread.sleep(SHORT_INTERVAL*2);
            assertTrue(getClient().isServerAlive());
            if (sServer.getServiceImpl().getHeartbeatCount()>count) {
                break;
            }
        }

        count=sServer.getServiceImpl().getHeartbeatCount();
        closeClient();
        assertFalse(getClient().isServerAlive());
        Thread.sleep(SHORT_INTERVAL*3);
        assertTrue(sServer.getServiceImpl().getHeartbeatCount()<=count+1);
        assertFalse(getClient().isServerAlive());
    }

    @Test
    public void sendOrderSingle() throws Exception {
        //Initialize a client
        initClient();

        for (int i = 0; i < NUM_REPEAT; i++) {
            //Create order
            OrderSingle order = Factory.getInstance().createOrderSingle();
            order.setAccount("my account");
            Map<String,String> map = new HashMap<String,String>();
            map.put("101","value1");
            map.put("201","value2");
            order.setCustomFields(map);
            order.setBrokerID(new BrokerID("brokerA"));
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
            assertTrue(received instanceof OrderEnvelope);
            assertEquals(((ClientImpl)getClient()).getSessionId(),
                         ((OrderEnvelope)received).getSessionId());
            received = ((OrderEnvelope)received).getOrder();
            assertTrue(received instanceof OrderSingle);
            assertOrderSingleEquals(order, (OrderSingle)received);
            //Verify received report
            ReportBase receivedReport = mReplies.getReport();
            assertTrue(receivedReport instanceof ExecutionReport);
            assertExecReportEquals(report, (ExecutionReport) receivedReport);
        }
    }

    @Test
    public void sendOrderReplace() throws Exception {
        initClient();

        for (int i = 0; i < NUM_REPEAT; i++) {
            //Create order
            OrderReplace order = createOrderReplace();

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
            assertTrue(received instanceof OrderEnvelope);
            assertEquals(((ClientImpl)getClient()).getSessionId(),
                         ((OrderEnvelope)received).getSessionId());
            received = ((OrderEnvelope)received).getOrder();
            assertTrue(received instanceof OrderReplace);
            assertOrderReplaceEquals(order, (OrderReplace)received);
            //Verify received report
            ReportBase receivedReport = mReplies.getReport();
            assertTrue(receivedReport instanceof ExecutionReport);
            assertExecReportEquals(report, (ExecutionReport) receivedReport);
        }
    }

    @Test
    public void sendOrderCancel() throws Exception {
        initClient();

        for (int i = 0; i < NUM_REPEAT; i++) {
            //Create cancel order
            OrderCancel order = createOrderCancel();

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
            assertTrue(received instanceof OrderEnvelope);
            assertEquals(((ClientImpl)getClient()).getSessionId(),
                         ((OrderEnvelope)received).getSessionId());
            received = ((OrderEnvelope)received).getOrder();
            assertTrue(received instanceof OrderCancel);
            assertOrderCancelEquals(order, (OrderCancel)received);
            //Verify received report
            ReportBase receivedReport = mReplies.getReport();
            assertTrue(receivedReport instanceof OrderCancelReject);
            assertCancelRejectEquals(report, (OrderCancelReject) receivedReport);
        }
    }

    @Test
    public void sendOrderFIX() throws Exception {
        initClient();

        for (int i = 0; i < NUM_REPEAT; i++) {
            //Create FIX order
            FIXOrder order = createOrderFIX();

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
            assertTrue(received instanceof OrderEnvelope);
            assertEquals(((ClientImpl)getClient()).getSessionId(),
                         ((OrderEnvelope)received).getSessionId());
            received = ((OrderEnvelope)received).getOrder();
            assertTrue(received instanceof FIXOrder);
            assertOrderFIXEquals(order, (FIXOrder)received);
            //Verify received report
            ReportBase receivedReport = mReplies.getReport();
            assertTrue(receivedReport instanceof ExecutionReport);
            assertExecReportEquals(report, (ExecutionReport) receivedReport);
        }
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

    @Test
    public void statusListening() throws Exception {
        initClient();
        //Create our own broker status listener
        BrokerStatusReplyListener chitChat = new BrokerStatusReplyListener();
        //Add it to the client
        getClient().addBrokerStatusListener(chitChat);
        try {
            BrokerStatus status = triggerBrokerStatus();
            //Verify our listener got it.
            BrokerStatus receivedStatus = chitChat.getStatus();
            assertEquals(status.toString(), receivedStatus.toString());
            //Now set our reply listener to fail
            chitChat.setFail(true);
            //Trigger receipt of the status and verify that the main
            //test listener got it. This verifies that exceptions from
            //listener do not impact notifications to other listeners.
            status = triggerBrokerStatus();
            //Verify our listener got it.
            receivedStatus = chitChat.getStatus();
            assertEquals(status.toString(), receivedStatus.toString());

            //Now remove our listener.
            getClient().removeBrokerStatusListener(chitChat);
            chitChat.clear();
            assertNull(chitChat.peekStatus());
            //Send another status
            triggerBrokerStatus();
            //Verify our listener didn't get it
            assertNull(chitChat.peekStatus());
        } finally {
            getClient().removeBrokerStatusListener(chitChat);
        }
    }

    @Test
    public void serverListening() throws Exception {
        initClient(LONG_INTERVAL);
        //Create our own server status listener
        ServerStatusReplyListener chitChat = new ServerStatusReplyListener();
        //Add it to the client
        getClient().addServerStatusListener(chitChat);
        try {
            boolean status = triggerServerStatus();
            //Verify our listener got it.
            boolean receivedStatus = chitChat.getStatus();
            assertEquals(status, receivedStatus);
            //Now set our reply listener to fail
            chitChat.setFail(true);
            //Trigger receipt of the status and verify that the main
            //test listener got it. This verifies that exceptions from
            //listener do not impact notifications to other listeners.
            status = triggerServerStatus();
            //Verify our listener got it.
            receivedStatus = chitChat.getStatus();
            assertEquals(status, receivedStatus);

            //Now remove our listener.
            getClient().removeServerStatusListener(chitChat);
            chitChat.clear();
            assertNull(chitChat.peekStatus());
            //Send another status
            triggerServerStatus();
            //Verify our listener didn't get it
            assertNull(chitChat.peekStatus());
        } finally {
            getClient().removeServerStatusListener(chitChat);
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
                client.addBrokerStatusListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.addServerStatusListener(null);
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
                client.getPositionsAsOf(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getReportsSince(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getBrokersStatus();
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.getUserInfo(null, true);
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
                client.removeServerStatusListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.removeBrokerStatusListener(null);
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrder(createOrderSingle());
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrder(createOrderReplace());
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrder(createOrderCancel());
            }
        };
        new ExpectedFailure<IllegalStateException>(expectedMsg){
            protected void run() throws Exception {
                client.sendOrderRaw(createOrderFIX());
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
        getClient().sendOrder(createOrderReplace());
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

    private BrokerStatus triggerBrokerStatus() throws Exception {
        //Clean up any dirty state from previous failures
        clearAll();
        //Create a status for the mock server to send back
        BrokerStatus status =
            new BrokerStatus("me",new BrokerID("myID"),true);
        sServer.getHandler().addToSendStatus(status);
        //Send the status
        sServer.getStatusSender().convertAndSend(status);
        //Verify received status
        BrokerStatus receivedStatus = mBrokerStatusReplies.getStatus();
        assertEquals(status.toString(),receivedStatus.toString());
        return status;
    }

    private boolean triggerServerStatus() throws Exception {
        //Clean up any dirty state from previous failures
        clearAll();
        //Alter the server's status.
        boolean status = sServer.getServiceImpl().toggleServerStatus();
        //Sleep until the client issues the next hearbeat (and either
        //succeeds or fails to do so, and thus captures the server's
        //status).
        Thread.sleep(SHORT_INTERVAL*2);
        //Verify received status
        boolean receivedStatus = mServerStatusReplies.getStatus();
        assertEquals(status,receivedStatus);
        return status;
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
                "you".toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        getClient().reconnect(parms);
        assertCPEquals(parms, getClient().getParameters());
        assertFalse(oldParms.getUsername().equals(parms.getUsername()));
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
    public static ExecutionReport createExecutionReport() throws Exception {
        return Factory.getInstance().createExecutionReport(FIXVersion.FIX42.
                getMessageFactory().newExecutionReport("ord1", "clord" +
                sCounter.getAndIncrement(),
                "exec1", OrdStatus.NEW, quickfix.field.Side.BUY,
                new BigDecimal("4343.49"), new BigDecimal("498.34"),
                new BigDecimal("783343.49"), new BigDecimal("598.34"),
                new BigDecimal("234343.49"), new BigDecimal("798.34"),
                new MSymbol("IBM", SecurityType.CommonStock), "my acc"),
                new BrokerID("bro"), Originator.Broker, null, null);
    }

    /**
     * Creates a sample cancel reject report for the mock server to send back.
     *
     * @return sample cancel reject report.
     *
     * @throws Exception if there were errors creating the report.
     */
    public static OrderCancelReject createCancelReject() throws Exception {
        return Factory.getInstance().createOrderCancelReject(
                FIXVersion.FIX42.getMessageFactory().newOrderCancelReject(
                        new quickfix.field.OrderID("brok3"),
                        new ClOrdID("clord" + sCounter.getAndIncrement()),
                        new OrigClOrdID("origord1"),
                        "what?", null),
                new BrokerID("bro"), Originator.Broker, null, null);
    }

    public static OrderSingle createOrderSingle() {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setOrderType(OrderType.Limit);
        order.setPrice(new BigDecimal("834.34"));
        order.setQuantity(new BigDecimal("833.343"));
        order.setSide(Side.Buy);
        order.setSymbol(new MSymbol("IBN", SecurityType.CommonStock));
        return order;
    }

    public static OrderReplace createOrderReplace() throws Exception {
        OrderReplace order = Factory.getInstance().createOrderReplace(
                createExecutionReport());
        order.setOrderType(OrderType.Limit);
        return order;
    }

    public static OrderCancel createOrderCancel() throws Exception {
        return Factory.getInstance().createOrderCancel(
                createExecutionReport());
    }

    public static FIXOrder createOrderFIX() throws MessageCreationException {
        return Factory.getInstance().createOrder(
                FIXVersion.FIX42.getMessageFactory().newLimitOrder("clOrd1",
                        quickfix.field.Side.BUY, new BigDecimal("8934.234"),
                        new MSymbol("IBM", SecurityType.Option),
                        new BigDecimal("9834.23"),
                        quickfix.field.TimeInForce.DAY, "no"),
                new BrokerID("bro"));
    }

    /**
     * Compares the client parameters instances ignoring the password value.
     *
     * @param inParms1 the first parameter.
     * @param inParms2 the second parameter.
     */
    static void assertCPEquals(ClientParameters inParms1,
                               ClientParameters inParms2) {
        assertEquals(inParms1.getHostname(), inParms2.getHostname());
        assertEquals(inParms1.getIDPrefix(), inParms2.getIDPrefix());
        assertEquals(inParms1.getPort(), inParms2.getPort());
        assertEquals(inParms1.getURL(), inParms2.getURL());
        assertEquals(inParms1.getUsername(), inParms2.getUsername());
    }

    private void clearAll() {
        mListener.clear();
        mReplies.clear();
        mBrokerStatusReplies.clear();
        mServerStatusReplies.clear();
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
    private void initClient(long heartbeatInterval)
            throws ConnectionException, ClientInitException {
        Date currentTime = new Date();
        ClientParameters parameters = new ClientParameters(DEFAULT_CREDENTIAL,
                DEFAULT_CREDENTIAL.toCharArray(), MockServer.URL,
                Node.DEFAULT_HOST, Node.DEFAULT_PORT);
        ClientImpl.setHeartbeatInterval(heartbeatInterval);
        ClientManager.init(parameters);
        mClient = ClientManager.getInstance();
        mClient.addExceptionListener(mListener);
        mClient.addReportListener(mReplies);
        mClient.addBrokerStatusListener(mBrokerStatusReplies);
        mClient.addServerStatusListener(mServerStatusReplies);
        assertCPEquals(parameters, mClient.getParameters());
        assertNotNull(mClient.getLastConnectTime());
        assertTrue(mClient.getLastConnectTime().compareTo(currentTime) >= 0);
    }
    private void initClient()
            throws ConnectionException, ClientInitException {
        initClient(60000);
    }

    private static void initServer() {
        if (sServer == null) {
            sServer = new MockServer();
        }
    }
    private final ErrorListener mListener = new ErrorListener();
    private final ReplyListener mReplies = new ReplyListener();
    private final BrokerStatusReplyListener mBrokerStatusReplies =
        new BrokerStatusReplyListener();
    private final ServerStatusReplyListener mServerStatusReplies =
        new ServerStatusReplyListener();
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
    private static class BrokerStatusReplyListener
        implements BrokerStatusListener {

        public void receiveBrokerStatus(BrokerStatus inStatus) {
            //Use add() instead of put() as these need to be non-blocking.
            mStatus.add(inStatus);
            if (mFail) {
                throw new IllegalArgumentException("Test Failure");
            }
        }

        public BrokerStatus getStatus() throws InterruptedException {
            //Use take as we should block until a message is available.
            return mStatus.take();
        }
        public BrokerStatus peekStatus() {
            return mStatus.peek();
        }
        public void setFail(boolean isFail) {
            mFail = isFail;
        }
        public void clear() {
            mStatus.clear();
            mFail = false;
        }
        private boolean mFail = false;
        private BlockingQueue<BrokerStatus> mStatus =
                new LinkedBlockingQueue<BrokerStatus>();
    }
    private static class ServerStatusReplyListener
        implements ServerStatusListener {

        public void receiveServerStatus(boolean inStatus) {
            //Use add() instead of put() as these need to be non-blocking.
            mStatus.add(inStatus);
            if (mFail) {
                throw new IllegalArgumentException("Test Failure");
            }
        }

        public Boolean getStatus() throws InterruptedException {
            //Use take as we should block until a message is available.
            return mStatus.take();
        }
        public Boolean peekStatus() {
            return mStatus.peek();
        }
        public void setFail(boolean isFail) {
            mFail = isFail;
        }
        public void clear() {
            mStatus.clear();
            mFail = false;
        }
        private boolean mFail = false;
        private BlockingQueue<Boolean> mStatus =
                new LinkedBlockingQueue<Boolean>();
    }

    private static final String DEFAULT_CREDENTIAL = "name";
    private static final long SHORT_INTERVAL = 2000;
    private static final long LONG_INTERVAL = 60000;
}
