package org.marketcetera.modules.remote.emitter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Deque;
import java.util.LinkedList;

import javax.management.AttributeChangeNotification;
import javax.management.JMX;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationListener;

import org.junit.Test;
import org.marketcetera.client.ClientTest;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventLevel;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.module.BlockingSinkDataListener;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.MockConfigProvider;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.remote.receiver.ReceiverFactory;
import org.marketcetera.modules.remote.receiver.ReceiverModuleMXBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.TypesTestBase;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link EmitterModule} & its integration with
 * {@link org.marketcetera.modules.remote.receiver.ReceiverModule}.
 * This test also verifies
 * {@link org.marketcetera.modules.remote.receiver.ReceiverModule}
 * functionality.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class EmitterModuleTest extends RemoteEmitterTestBase {
    /**
     * Tests provider and module info values.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void info() throws Exception {
        initManager();
        assertProviderInfo(mManager, EmitterFactory.PROVIDER_URN,
                new String[]{String.class.getName()},
                new Class[]{String.class},
                Messages.PROVIDER_DESCRIPTION.getText(),
                false, true);
        final String myModule = "mine";
        //Attempt to create the instance fails as it fails to start,
        //but the module gets created.
        new ExpectedFailure<ModuleException>(Messages.START_FAIL_NO_URL){
            @Override
            protected void run() throws Exception {
                mManager.createModule(EmitterFactory.PROVIDER_URN, myModule);
            }
        };
        //Verify module instance info
        ModuleInfo info = assertModuleInfo(mManager,
                new ModuleURN(EmitterFactory.PROVIDER_URN, myModule),
                ModuleState.START_FAILED, null, null, false, true, false,
                true, false);
        assertEquals(Messages.START_FAIL_NO_URL.getText(),
                info.getLastStartFailure());
        assertNull(info.getLastStopFailure());
    }

    /**
     * Tests the MXBean interface. The semantics around attribute changes
     * and their impact on module lifecycle operations.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void jmx() throws Exception {
        initManager();
        mManager.createModule(EmitterFactory.PROVIDER_URN,
                TEST_INSTANCE_URN.instanceName());
        assertModuleInfo(mManager, TEST_INSTANCE_URN, ModuleState.STARTED,
                null, null, false, true, false, true, false);
        //verify bean info 
        MBeanInfo beanInfo = getMBeanServer().getMBeanInfo(
                TEST_INSTANCE_URN.toObjectName());
        verifyBeanInfo(beanInfo);
        assertEquals(1, beanInfo.getNotifications().length);
        assertEquals(new MBeanNotificationInfo(
                    new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                    AttributeChangeNotification.class.getName(),
                    Messages.ATTRIB_CHANGE_NOTIFICATION.getText()),
                beanInfo.getNotifications()[0]);
        //Get the MBean
        final EmitterModuleMXBean bean = JMX.newMXBeanProxy(getMBeanServer(),
                TEST_INSTANCE_URN.toObjectName(), EmitterModuleMXBean.class);
        //Verify attribute values.
        assertEquals(null, bean.getLastFailure());
        assertEquals(DEFAULT_URL, bean.getURL());
        assertEquals(DEFAULT_CREDENTIAL, bean.getUsername());
        assertEquals(true, bean.isConnected());
        //Test for failures when setting the attributes
        new ExpectedFailure<IllegalStateException>(
                Messages.ILLEGAL_STATE_CHANGE_PASSWORD.getText()){
            @Override
            protected void run() throws Exception {
                bean.setPassword("value");
            }
        };
        new ExpectedFailure<IllegalStateException>(
                Messages.ILLEGAL_STATE_CHANGE_URL.getText()){
            @Override
            protected void run() throws Exception {
                bean.setURL("tcp://myurl");
            }
        };
        new ExpectedFailure<IllegalStateException>(
                Messages.ILLEGAL_STATE_CHANGE_USERNAME.getText()){
            @Override
            protected void run() throws Exception {
                bean.setUsername("myuser");
            }
        };
        //Stop the module
        mManager.stop(TEST_INSTANCE_URN);
        assertEquals(false, bean.isConnected());
        //Test failures when attributes are not set correctly
        //Set the URL to an incorrect value
        String newURL = "tcp://127.0.0.1:50000";
        bean.setURL(newURL);
        assertEquals(newURL, bean.getURL());
        verifyStartFailure(bean);
        //Try NULL url
        bean.setURL(null);
        assertEquals(null, bean.getURL());
        verifyStartFailure(bean, Messages.START_FAIL_NO_URL);
        //Fix the URL
        bean.setURL(DEFAULT_URL);
        //Test failures when the user name doesn't match.
        String newUser = "who?";
        bean.setUsername(newUser);
        assertEquals(newUser, bean.getUsername());
        verifyStartFailure(bean);
        //Try null value
        bean.setUsername(null);
        assertEquals(null, bean.getUsername());
        verifyStartFailure(bean);
        //fix the user name
        bean.setUsername(DEFAULT_CREDENTIAL);
        //mess up the password
        String newPass = "pass";
        bean.setPassword(newPass);
        verifyStartFailure(bean);
        //try null value
        bean.setPassword(null);
        verifyStartFailure(bean);
        //fix the password
        bean.setPassword(DEFAULT_CREDENTIAL);
        //Should now connect.
        mManager.start(TEST_INSTANCE_URN);
        assertEquals(true, bean.isConnected());
        //Stop and delete the module
        mManager.stop(TEST_INSTANCE_URN);
        mManager.deleteModule(TEST_INSTANCE_URN);
    }

    /**
     * Tests data flows from the remote receiver to the emitter to sink.
     *
     * @throws Exception if there were errors.
     */
    @Test(timeout = 10000)
    public void flows() throws Exception {
        initManager();
        //Create the emitter
        mManager.createModule(EmitterFactory.PROVIDER_URN,
                TEST_INSTANCE_URN.instanceName());
        //Add a sink listener
        BlockingSinkDataListener listener = new BlockingSinkDataListener();
        mManager.addSinkListener(listener);
        //Setup the emitter flow
        DataFlowID eFlowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(TEST_INSTANCE_URN)}, true);
        //Wait for some time to ensure that we do not receive any events yet.
        Thread.sleep(1000);
        assertEquals(0, listener.size());
        //The data to send
        Object [] data = {
                EventTestBase.generateEquityAskEvent(1, 2, new Equity("asym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                EventTestBase.generateEquityBidEvent(3, 4, new Equity("bsym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                EventTestBase.generateEquityTradeEvent(5, 6, new Equity("csym"), "ex", BigDecimal.ONE, BigDecimal.TEN),
                ClientTest.createOrderSingle(),
                ClientTest.createOrderReplace(),
                ClientTest.createOrderCancel(),
                ClientTest.createOrderFIX(),
                ClientTest.createCancelReject(),
                ClientTest.createExecutionReport(),
                org.marketcetera.core.notifications.Notification.high(
                        "Subject", "body", "test.notification"),
                BigInteger.ONE,
                "Test String"
        };
        //Now setup a data flow into the receiver.
        DataFlowID rFlowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, data),
                new DataRequest(ReceiverFactory.INSTANCE_URN)
        }, false);
        Object actual;
        for(Object expected: data) {
            actual = listener.getNextData();
            if(expected instanceof OrderSingle) {
                TypesTestBase.assertOrderSingleEquals((OrderSingle)expected,
                        (OrderSingle)actual);
            } else if(expected instanceof OrderReplace) {
                TypesTestBase.assertOrderReplaceEquals((OrderReplace)expected,
                        (OrderReplace)actual);
            } else if(expected instanceof OrderCancel) {
                TypesTestBase.assertOrderCancelEquals((OrderCancel)expected,
                        (OrderCancel)actual);
            } else if(expected instanceof FIXOrder) {
                TypesTestBase.assertOrderFIXEquals((FIXOrder)expected, 
                        (FIXOrder)actual);
            } else if(expected instanceof OrderCancelReject) {
                TypesTestBase.assertCancelRejectEquals((OrderCancelReject)expected,
                        (OrderCancelReject)actual);
            } else if(expected instanceof ExecutionReport) {
                TypesTestBase.assertExecReportEquals((ExecutionReport)expected,
                        (ExecutionReport)actual);
            } else if(expected instanceof org.marketcetera.core.notifications.Notification) {
                assertEquals(expected.toString(), actual.toString());
            } else {
                assertEquals(expected, actual);
            }
        }
        //cancel the data flows
        mManager.cancel(rFlowID);
        mManager.cancel(eFlowID);
    }

    /**
     * Verifies log event filtering carried out by the
     * receiver.
     *
     * @throws Exception if there's an error.
     */
    @Test
    public void logEventFiltering() throws Exception {
        initManager();
        //Create the emitter
        mManager.createModule(EmitterFactory.PROVIDER_URN,
                TEST_INSTANCE_URN.instanceName());
        //Add a sink listener
        BlockingSinkDataListener listener = new BlockingSinkDataListener();
        mManager.addSinkListener(listener);
        final ReceiverModuleMXBean bean = JMX.newMXBeanProxy(getMBeanServer(),
                ReceiverFactory.INSTANCE_URN.toObjectName(),
                ReceiverModuleMXBean.class);
        //verify the default level
        assertEquals(LogEventLevel.WARN, bean.getLogLevel());
        //test the default log level
        runLogFilterFlow(listener, LogEventLevel.WARN);
        //test out each of the log levels
        for(LogEventLevel level: LogEventLevel.values()) {
            bean.setLogLevel(level);
            assertEquals(level, bean.getLogLevel());
            runLogFilterFlow(listener, level);
        }
    }

    /**
     * Tests attribute change notifications sent during lifecycle changes
     * of the module.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void connectionFailureAndJMXNotifications() throws Exception {
        initManager();
        mManager.createModule(EmitterFactory.PROVIDER_URN,
                TEST_INSTANCE_URN.instanceName());
        final EmitterModuleMXBean bean = JMX.newMXBeanProxy(getMBeanServer(),
                TEST_INSTANCE_URN.toObjectName(), EmitterModuleMXBean.class);
        //Register for attrib change notifications.
        BeanNotificationListener listener = new BeanNotificationListener();
        getMBeanServer().addNotificationListener(
                TEST_INSTANCE_URN.toObjectName(), listener, null,
                new Object());
        assertEquals(true, bean.isConnected());
        assertNull(bean.getLastFailure());
        assertEquals(0, listener.size());
        //Stop the receiver to terminate the connection
        mManager.stop(ReceiverFactory.INSTANCE_URN);
        //Give it some time to disconnect
        Thread.sleep(3000);
        assertEquals(false, bean.isConnected());
        assertNotNull(bean.getLastFailure());
        assertEquals(1, listener.size());
        assertNotification(listener.getLastNotification(), true, false);
        //Stop the emitter
        mManager.stop(TEST_INSTANCE_URN);
        //No extra notifications
        assertEquals(1, listener.size());
        //Restart the receiver
        mManager.start(ReceiverFactory.INSTANCE_URN);
        //verify that we can restart the module
        mManager.start(TEST_INSTANCE_URN);
        assertEquals(true, bean.isConnected());
        assertNull(bean.getLastFailure());
        //verify that start sends a notification
        assertEquals(2, listener.size());
        assertNotification(listener.getLastNotification(), false, true);
        //Now stop the module and verify that it sends notifications
        mManager.stop(TEST_INSTANCE_URN);
        assertEquals(false, bean.isConnected());
        assertNull(bean.getLastFailure());
        assertEquals(3, listener.size());
        assertNotification(listener.getLastNotification(), true, false);
        //Now unregister the listener
        getMBeanServer().removeNotificationListener(
                TEST_INSTANCE_URN.toObjectName(), listener);
        //verify that no notifications are received when module
        //is started or stopped.
        mManager.start(TEST_INSTANCE_URN);
        assertEquals(true, bean.isConnected());
        assertEquals(3, listener.size());
        mManager.stop(TEST_INSTANCE_URN);
        assertEquals(false, bean.isConnected());
        assertEquals(3, listener.size());
        //Delete the module
        mManager.deleteModule(TEST_INSTANCE_URN);
        //no extra notifications
        assertEquals(3, listener.size());
    }

    /**
     * Runs the log filtering data flow test.
     *
     * @param inListener the sink listener instance.
     * @param inCurrentLevel the current log filtering level.
     *
     * @throws Exception if there were errors.
     */
    private void runLogFilterFlow(BlockingSinkDataListener inListener,
                                  LogEventLevel inCurrentLevel)
            throws Exception {
        //Setup the emitter flow
        DataFlowID eFlowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(TEST_INSTANCE_URN)
        }, true);
        //Wait for some time to ensure that we do not receive any events yet.
        Thread.sleep(1000);
        assertEquals(0, inListener.size());
        I18NMessage0P[] msgs = {
                new I18NMessage0P(Messages.LOGGER, "debug"),
                new I18NMessage0P(Messages.LOGGER, "info"),
                new I18NMessage0P(Messages.LOGGER, "warn"),
                new I18NMessage0P(Messages.LOGGER, "error")
        };
        //The data to send
        LogEvent [] data = {
                LogEventBuilder.debug().withMessage(msgs[0]).create(),
                LogEventBuilder.info().withMessage(msgs[1]).create(),
                LogEventBuilder.warn().withMessage(msgs[2]).create(),
                LogEventBuilder.error().withMessage(msgs[3]).create()
        };
        //Now setup a data flow into the receiver.
        DataFlowID rFlowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, data),
                new DataRequest(ReceiverFactory.INSTANCE_URN)
        }, false);
        int numEvents = data.length - inCurrentLevel.ordinal();
        int idx = inCurrentLevel.ordinal();
        while(numEvents-- > 0) {
            assertEquals(data[idx++].getMessage(),
                    ((LogEvent)inListener.getNextData()).getMessage());
        }
        //cancel the data flows
        mManager.cancel(rFlowID);
        mManager.cancel(eFlowID);
    }

    @Override
    protected MockConfigProvider configProviderWithURLValue(String inUrl) {
        MockConfigProvider prov = super.configProviderWithURLValue(inUrl);
        prov.addDefault(EmitterModuleTest.TEST_INSTANCE_URN, "URL", inUrl);
        prov.addDefault(EmitterModuleTest.TEST_INSTANCE_URN, "Username", DEFAULT_CREDENTIAL);
        prov.addDefault(EmitterModuleTest.TEST_INSTANCE_URN, "Password", DEFAULT_CREDENTIAL);
        return prov;
    }

    /**
     * Verifies module start failure.
     *
     * @param inBean the MXBean proxy for the module.
     *
     * @throws Exception if there were errors.
     */
    private void verifyStartFailure(EmitterModuleMXBean inBean)
            throws Exception {
        verifyStartFailure(inBean, Messages.ERROR_STARTING_MODULE);
    }

    /**
     * Verifies module start failure.
     *
     * @param inBean the MXBean proxy for the module.
     * @param inMessage the expected failure message.
     *
     * @throws Exception if there were errors.
     */
    private void verifyStartFailure(EmitterModuleMXBean inBean,
                                    I18NMessage0P inMessage)
            throws Exception {
        new ExpectedFailure<ModuleException>(inMessage){
            @Override
            protected void run() throws Exception {
                mManager.start(TEST_INSTANCE_URN);
            }
        };
        assertEquals(false, inBean.isConnected());
        assertNull(inBean.getLastFailure());
    }

    /**
     * Verifies the supplied notification as an attribute change notification.
     *
     * @param inNotify the notification instance.
     * @param inOldValue the old attribute value.
     * @param inNewValue the new attribute value.
     *
     * @throws Exception if there were errors.
     */
    private static void assertNotification(Notification inNotify,
                                           boolean inOldValue,
                                           boolean inNewValue)
            throws Exception {
        assertEquals(AttributeChangeNotification.ATTRIBUTE_CHANGE,
                inNotify.getType());
        assertEquals(TEST_INSTANCE_URN.toString(),
                inNotify.getSource());
        assertTrue(inNotify.toString(),
                inNotify instanceof AttributeChangeNotification);
        AttributeChangeNotification note = (AttributeChangeNotification) inNotify;
        assertEquals("Connected", note.getAttributeName());
        assertEquals("boolean", note.getAttributeType());
        assertEquals(inOldValue, note.getOldValue());
        assertEquals(inNewValue, note.getNewValue());
    }

    private static final ModuleURN TEST_INSTANCE_URN =
            new ModuleURN(EmitterFactory.PROVIDER_URN, "test");

    /**
     * A notification listener to listen for MBean notifications.
     */
    private static class BeanNotificationListener implements NotificationListener {
        @Override
        public synchronized void handleNotification(Notification notification,
                                                    Object handback) {
            mNotifications.addLast(notification);
        }

        /**
         * The number of notifications received so far.
         *
         * @return number of notifications.
         */
        synchronized int size() {
            return mNotifications.size();
        }

        /**
         * The last notification received.
         *
         * @return the last notification.
         */
        synchronized Notification getLastNotification() {
            return mNotifications.getLast();
        }
        private final Deque<Notification> mNotifications = new LinkedList<Notification>();
    }

}
