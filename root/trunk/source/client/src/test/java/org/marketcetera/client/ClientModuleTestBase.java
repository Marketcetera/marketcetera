package org.marketcetera.client;

import org.marketcetera.client.jms.OrderEnvelope;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.module.*;
import org.marketcetera.trade.*;

import static org.marketcetera.trade.TypesTestBase.*;
import org.marketcetera.quickfix.FIXVersion;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.apache.commons.lang.ObjectUtils;

import javax.management.JMX;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

/* $License$ */
/**
 * Base class for testing the client module.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@Ignore
public class ClientModuleTestBase extends ModuleTestBase {
    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void info() throws Exception {
        assertProviderInfo(mManager, ClientModuleFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.PROVIDER_DESCRIPTION.getText(),false, false);
        assertModuleInfo(mManager, ClientModuleFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false,
                true, true, true, false);
    }

    @Test
    public void jmx() throws Exception {
        //Verify descriptors
        verifyBeanInfo(getMBeanServer().getMBeanInfo(
                ClientModuleFactory.PROVIDER_URN.toObjectName()));
        verifyBeanInfo(getMBeanServer().getMBeanInfo(
                ClientModuleFactory.INSTANCE_URN.toObjectName()));
        ClientModuleFactoryMXBean factory = JMX.newMXBeanProxy(getMBeanServer(),
                ClientModuleFactory.PROVIDER_URN.toObjectName(),
                ClientModuleFactoryMXBean.class);
        assertEquals(getExpectedURL(), factory.getURL());
        assertEquals(getExpectedUsername(), factory.getUsername());
        ClientModuleMXBean instance = JMX.newMXBeanProxy(getMBeanServer(),
                ClientModuleFactory.INSTANCE_URN.toObjectName(),
                ClientModuleMXBean.class);
        final Date lastTime = ClientManager.getInstance().getLastConnectTime();
        assertEquals(lastTime,
                instance.getLastConnectTime());
        ClientTest.assertCPEquals(ClientManager.getInstance().getParameters(),
                instance.getParameters());
        //Sleep so that we definitely get a different connect time.
        Thread.sleep(100);
        instance.reconnect();
        assertEquals(ClientManager.getInstance().getLastConnectTime(),
                instance.getLastConnectTime());
        assertTrue(instance.getLastConnectTime().compareTo(lastTime) > 0);
    }

    protected Object getExpectedUsername() {
        return null;
    }

    protected Object getExpectedURL() {
        return null;
    }

    @Test
    public void invalidRequests() throws Exception {
        new ExpectedFailure<IllegalRequestParameterValue>(
                Messages.REQUEST_PARAMETER_SPECIFIED) {
            protected void run() throws Exception {
                mManager.createDataFlow(new DataRequest[]{new DataRequest(
                        ClientModuleFactory.INSTANCE_URN, "some string value")});
            }
        };
    }

    @Test
    public void dataFlow() throws Exception {
        //Create all kinds of orders to send
        Order[] orders = new Order[]{
                ClientTest.createOrderSingle(),
                ClientTest.createOrderReplace(),
                Factory.getInstance().createOrderCancel(ClientTest.createExecutionReport()),
                Factory.getInstance().createOrder(
                        FIXVersion.FIX44.getMessageFactory().newLimitOrder(
                                "ord1", quickfix.field.Side.BUY,
                                new BigDecimal(4.3), new MSymbol("IBM",
                                SecurityType.Option), new BigDecimal("93.23"),
                                quickfix.field.TimeInForce.AT_THE_OPENING,
                                "acc"), new BrokerID("broke"))
        };
        //Initialize mock server with reports to return
        ReportBase[] reports = new ReportBase[] {
                ClientTest.createExecutionReport(),
                ClientTest.createExecutionReport(),
                ClientTest.createCancelReject(),
                ClientTest.createExecutionReport()};
        sServer.getHandler().addToSend(reports[0]);
        sServer.getHandler().addToSend(reports[1]);
        sServer.getHandler().addToSend(reports[2]);
        sServer.getHandler().addToSend(reports[3]);
        //Add a sink listener to receive these reports
        ReportSink sink = new ReportSink();
        mManager.addSinkListener(sink);
        //Initialize a module to send this data
        ModuleURN senderURN = mManager.createModule(
                OrderSenderModuleFactory.PROVIDER_URN, "sendOrders", orders);
        assertEquals(0, sServer.getHandler().numReceived());
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(senderURN, null),
                new DataRequest(ClientModuleFactory.INSTANCE_URN, null)
        });
        assertFlowInfo(mManager.getDataFlowInfo(flowID), flowID, 3, true,
                false, null, null);
        //Wait until sink has received all the data And verify each report.
        for (ReportBase report : reports) {
            if (report instanceof ExecutionReport) {
                assertExecReportEquals(
                        ((ExecutionReport) report),
                        (ExecutionReport) sink.getNextData());
            } else {
                assertCancelRejectEquals(
                        ((OrderCancelReject) report),
                        (OrderCancelReject) sink.getNextData());
            }

        }
        //Sleep for a little while to let the sendMessage() call to the MQ
        //Broker to return back. It's been observed that sometimes we receive
        //execution reports even though the sendMessage() call has not returned.
        //Cancelling the data flow at that point results in the sendMessage()
        //call failing, as it's I/O gets interrupted.
        Thread.sleep(1000);
        //All the data has been transmitted, cancel the data flow
        mManager.cancel(flowID);
        //Verify data flow has ended
        assertTrue(mManager.getDataFlows(true).isEmpty());
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        DataFlowInfo info = history.get(0);
        assertFlowInfo(info, flowID, 3, true,
                true, null, null);
        assertFlowStep(info.getFlowSteps()[0], senderURN, true, 4, 0,
                null, false, 0, 0, null, null, null);
        assertFlowStep(info.getFlowSteps()[1], ClientModuleFactory.INSTANCE_URN,
                true, 4, 0, null, true, 4, 0, null,
                ClientModuleFactory.INSTANCE_URN, null);
        assertFlowStep(info.getFlowSteps()[2], SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, 4, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
        //Verify that the server got the orders

        SessionId id = ((ClientImpl)(ClientManager.getInstance())).
            getSessionId();
        assertEquals(4, sServer.getHandler().numReceived());

        OrderEnvelope e=(OrderEnvelope)sServer.getHandler().removeReceived();
        assertEquals(id, e.getSessionId());
        Order order = (Order) e.getOrder();
        assert(order instanceof OrderSingle);
        assertOrderSingleEquals((OrderSingle)orders[0], (OrderSingle) order);

        e=(OrderEnvelope)sServer.getHandler().removeReceived();
        assertEquals(id, e.getSessionId());
        order = (Order) e.getOrder();
        assert(order instanceof OrderReplace);
        assertOrderReplaceEquals((OrderReplace)orders[1], (OrderReplace) order);

        e=(OrderEnvelope)sServer.getHandler().removeReceived();
        assertEquals(id, e.getSessionId());
        order = (Order) e.getOrder();
        assert(order instanceof OrderCancel);
        assertOrderCancelEquals((OrderCancel)orders[2], (OrderCancel) order);

        e=(OrderEnvelope)sServer.getHandler().removeReceived();
        assertEquals(id, e.getSessionId());
        order = (Order) e.getOrder();
        assert(order instanceof FIXOrder);
        assertOrderFIXEquals((FIXOrder)orders[3], (FIXOrder) order);

        mManager.removeSinkListener(sink);
        mManager.stop(senderURN);
        mManager.deleteModule(senderURN);
    }

    @Test
    public void dataFlowUnsupportedTypeError() throws Exception {
        //test suggestion
        OrderSingleSuggestion errorData = Factory.getInstance().
                createOrderSingleSuggestion();
        ModuleURN senderURN = mManager.createModule(
                OrderSenderModuleFactory.PROVIDER_URN, "unsupported",
                new Object[]{
                        errorData,
                        ClientTest.createOrderSingle()});
        ReportSink sink = new ReportSink();
        mManager.addSinkListener(sink);
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(senderURN, null),
                new DataRequest(ClientModuleFactory.INSTANCE_URN, null)
        });
        assertFlowInfo(mManager.getDataFlowInfo(flowID), flowID, 3, true,
                false, null, null);
        //Wait for sink to receive the report in respose to the order.
        Object data = sink.getNextData();
        assertTrue(data.getClass().getName(), data instanceof ExecutionReport);
        //Sleep for a little while to let the sendMessage() call to the MQ
        //Broker to return back. It's been observed that sometimes we receive
        //execution reports even though the sendMessage() call has not returned.
        //Cancelling the data flow at that point results in the sendMessage()
        //call failing, as it's I/O gets interrupted.
        Thread.sleep(1000);
        //We've received all data, cancel the data flow
        mManager.cancel(flowID);
        assertTrue(mManager.getDataFlows(true).isEmpty());
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        DataFlowInfo info = history.get(0);
        assertFlowInfo(info, flowID, 3, true,
                true, null, null);
        assertFlowStep(info.getFlowSteps()[0], senderURN, true, 2, 0,
                null, false, 0, 0, null, null, null);
        assertFlowStep(info.getFlowSteps()[1], ClientModuleFactory.INSTANCE_URN,
                true, 1, 0, null, true, 2, 1,
                Messages.UNSUPPORTED_DATA_TYPE.getText(flowID,
                        ObjectUtils.toString(errorData)),
                ClientModuleFactory.INSTANCE_URN, null);
        assertFlowStep(info.getFlowSteps()[2], SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, 1, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
        mManager.removeSinkListener(sink);
        mManager.stop(senderURN);
        mManager.deleteModule(senderURN);
    }

    @Test(timeout = 60000)
    public void dataFlowNotInitializedError() throws Exception {
        OrderSingle order = ClientTest.createOrderSingle();
        ModuleURN senderURN = mManager.createModule(
                OrderSenderModuleFactory.PROVIDER_URN, "clientNotInit",
                new Object[]{Boolean.FALSE, order});
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(senderURN, null),
                new DataRequest(ClientModuleFactory.INSTANCE_URN, null)
        });
        assertFlowInfo(mManager.getDataFlowInfo(flowID), flowID, 3, true,
                false, null, null);
        //The data flow should terminate as the client is not initialized
        while(!mManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        DataFlowInfo info = history.get(0);
        assertFlowInfo(info, flowID, 3, true,
                true, null, ClientModuleFactory.INSTANCE_URN);
        assertFlowStep(info.getFlowSteps()[0], senderURN, true, 1, 0,
                null, false, 0, 0, null, null, null);
        assertFlowStep(info.getFlowSteps()[1], ClientModuleFactory.INSTANCE_URN,
                true, 0, 0, null, true, 1, 1,
                Messages.SEND_ORDER_FAIL_NO_CONNECT.getText(
                        ObjectUtils.toString(order)),
                ClientModuleFactory.INSTANCE_URN, null);
        assertFlowStep(info.getFlowSteps()[2], SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, 0, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
        mManager.stop(senderURN);
        mManager.deleteModule(senderURN);

    }

    @BeforeClass
    public static void serverSetup() throws Exception {
        sServer = new MockServer();
    }

    @AfterClass
    public static void serverCleanup() throws Exception {
        if (sServer != null) {
        sServer.close();
        sServer = null;
    }
    }
    @Before
    public void clearMessageHandler() {
        if (sServer != null) {
            sServer.getHandler().clear();
        }
    }

    @After
    public void clientCleanup() throws Exception {
        if (mManager != null) {
            mManager.stop();
        }
        mManager = null;
        if (ClientManager.isInitialized()) {
            ClientManager.getInstance().close();
        }
    }

    private static class ReportSink implements SinkDataListener {
        @Override
        public void receivedData(DataFlowID inFlowID, Object inData) {
            //Use add() instead of put() as we don't want this call to block
            mReceived.add(inData);
        }
        public Object getNextData() throws InterruptedException {
            //block until there's data available.
            return mReceived.take();
        }
        private BlockingQueue<Object> mReceived = new LinkedBlockingDeque<Object>();
    }

    protected ModuleManager mManager;
    private static MockServer sServer;
    protected static final String IDPREFIX = "my";
}
