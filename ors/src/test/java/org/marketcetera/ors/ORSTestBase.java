package org.marketcetera.ors;

import static org.marketcetera.trade.TypesTestBase.getSystemMessageFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.ApplicationContainer;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.exchange.Event;
import org.marketcetera.ors.exchange.FromAppEvent;
import org.marketcetera.ors.exchange.LogonEvent;
import org.marketcetera.ors.exchange.SampleExchange;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.test.context.transaction.TransactionConfiguration;

import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.*;

/* $License$ */

/**
 * Provides a running test ORS object for ORS-based tests.
 * 
 * @author tlerios@marketcetera.com
 * @auther <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.0.0
 * @version $Id$
 */
@TransactionConfiguration(defaultRollback=true)
public class ORSTestBase
        extends TestCaseBase
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
        // reset the database
        new FileSystemXmlApplicationContext(new String[] { "file:src/test/sample_data/conf/dbinit.xml" },
                                            null).close();
        // start test components
        startORS();
    }
    /**
     * Runs once after all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @AfterClass
    public static void tearDownORSTestBase()
            throws Exception
    {
        stopORS();
    }
    /**
     * Starts the ORS and keeps it running.
     *
     * @param inArgs a <code>String[]</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static void startORS(final String inArgs[])
            throws Exception
    {
        // Initialize exchange.
        exchange = new SampleExchange(ApplicationBase.CONF_DIR+"exchange.xml");
        exchange.start();
        // Wait for exchange initialization to complete.
        Thread.sleep(1000);
        // Create and start ORS in a separate thread.
        // create the test context
        final ApplicationContainer application = new ApplicationContainer();
        application.setArguments(inArgs);
        application.start();
        context = application.getContext();
        ors = context.getBean(OrderRoutingSystem.class);
        applicationThread = new Thread("testThread") {
            @Override
            public void run() {
                application.startWaitingForever();
            }
        };
        applicationThread.start();
        // Wait for ORS initialization to complete.
        while(!application.isWaitingForever()) {
            Thread.sleep(1000);
        }
        // Wait for exchange connections (from both brokers) to be
        // set up.
        int logonCount=0;
        while (true) {
            Event event=getExchange().getNext();
            if (event instanceof LogonEvent) {
                logonCount++;
                if (logonCount>=2) {
                    break;
                }
            }
        }
        // Create the administrative client.
        adminClient = new ORSTestClient("admin",
                                        "admin".toCharArray());
    }
    /**
     * Starts the ORS and keeps it running.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected static void startORS()
            throws Exception
    {
        startORS(new String[0]);
    }
    /**
     * Stops the ORS.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected static void stopORS()
            throws Exception
    {
        // Close the administrative client.
        if(getAdminClient()!=null) {
            getAdminClient().close();
            adminClient = null;
        }
        // Shut down ORS waiting thread.
        if(applicationThread != null) {
            applicationThread.interrupt();
            // Wait for ORS waiting thread to terminate.
            while (applicationThread.isAlive()) {
                Thread.sleep(1000);
            }
            applicationThread = null;
        }
        // Shut down the ORS.
        if(getORS() != null) {
            getORS().stop();
            ors = null;
        }
        // Shut down the exchange.
        if(getExchange() != null) {
            getExchange().stop();
            exchange = null;
        }
        if(context != null) {
            context.close();
            context = null;
        }
    }
    /**
     * Gets the test exchange value.
     *
     * @return a <code>SampleExchange</code> value
     */
    protected static SampleExchange getExchange()
    {
        return exchange;
    }
    /**
     * Gets the test ORS value.
     *
     * @return an <code>OrderRoutingSystem</code> value
     */
    protected static OrderRoutingSystem getORS()
    {
        return ors;
    }
    /**
     * Gets the test admin client value.
     *
     * @return an <code>ORSTestClient</code> value
     */
    protected static ORSTestClient getAdminClient()
    {
        return adminClient;
    }
    /**
     * Gets the test brokers value.
     *
     * @return a <code>Broker</code> value
     */
    protected static Brokers getBrokers()
    {
        return getORS().getBrokers();
    }
    /**
     * Gets the first of the test brokers.
     *
     * @return a <code>BrokerID</code> value
     */
    protected static BrokerID getFirstBrokerID()
    {
        return getBrokers().getBrokers().get(0).getBrokerID();
    }
    /**
     * Impels the given broker to respond with the given message.
     *
     * @param inBrokerID a <code>BrokerID</code> value
     * @param inMessage a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static void emulateBrokerResponse(BrokerID inBrokerID,
                                                Message inMessage)
            throws Exception
    {
        if(!inMessage.getHeader().isSetField(SendingTime.FIELD)) {
            inMessage.getHeader().setField(new SendingTime(new Date()));
        }
        SessionID id = getBrokers().getBroker(inBrokerID).getSessionID();
        SampleExchange.sendMessage(inMessage,
                                   new SessionID(id.getBeginString(),
                                                 id.getTargetCompID(),
                                                 id.getSenderCompID()));
    }
    /**
     * Impels the first of the test brokers to respond with the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static void emulateFirstBrokerResponse(Message inMessage)
            throws Exception
    {
        emulateBrokerResponse(getFirstBrokerID(),inMessage);
    }
    /**
     * Waits for and retrieves the next exchange message.
     *
     * @return a <code>Message</code> value
     * @throws InterruptedException if the method is interrupted before the next message is available
     */
    protected static Message getNextExchangeMessage()
            throws InterruptedException
    {
        while(true) {
            Event event = getExchange().getNext();
            if(event instanceof FromAppEvent) {
                return ((FromAppEvent)event).getMessage();
            }
        }
    }
    /**
     * Completes the given message and makes it a valid execution report.
     *
     * @param inMessage a <code>Message</code> value
     */
    protected static void completeExecReport(Message inMessage)
    {
        if(!inMessage.isSetField(AvgPx.FIELD)) {
            inMessage.setField(new AvgPx(0));
        }
        if(!inMessage.isSetField(ClOrdID.FIELD)) {
            inMessage.setField(new ClOrdID("ID"+ counter.getAndIncrement()));
        }
        if(!inMessage.isSetField(CumQty.FIELD)) {
            inMessage.setField(new CumQty(0));
        }
        if(!inMessage.isSetField(ExecID.FIELD)) {
            inMessage.setField(new ExecID("ID"+ counter.getAndIncrement()));
        }
        if(!inMessage.isSetField(ExecTransType.FIELD)) {
            inMessage.setField(new ExecTransType(ExecTransType.NEW));
        }
        if(!inMessage.isSetField(ExecType.FIELD)) {
            inMessage.setField(new ExecType(ExecType.NEW));
        }
        if(!inMessage.isSetField(LeavesQty.FIELD)) {
            inMessage.setField(new LeavesQty(0));
        }
        if(!inMessage.isSetField(OrdStatus.FIELD)) {
            inMessage.setField(new OrdStatus(OrdStatus.NEW));
        }
        if(!inMessage.isSetField(OrderID.FIELD)) {
            inMessage.setField(new OrderID("ID"+ counter.getAndIncrement()));
        }
        if(!inMessage.isSetField(Side.FIELD)) {
            inMessage.setField(new Side(Side.BUY));
        }
        if(!inMessage.isSetField(Symbol.FIELD)) {
            inMessage.setField(new Symbol("IBM"));
        }
    }
    /**
     * Creates an empty order cancel reject.
     *
     * @return a <code>Message</code> value
     */
    protected static Message createEmptyOrderCancelReject()
    {
        return getSystemMessageFactory().newOrderCancelReject();
    }
    /**
     * Completes the given message, making it a valid order cancel reject.
     *
     * @param inMessage a <code>Message</code> value
     */
    protected static void completeOrderCancelReject(Message inMessage)
    {
        if(!inMessage.isSetField(ClOrdID.FIELD)) {
            inMessage.setField(new ClOrdID("ID"+ counter.getAndIncrement()));
        }
        if(!inMessage.isSetField(CxlRejResponseTo.FIELD)) {
            inMessage.setField(new CxlRejResponseTo
                         (CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        }
        if(!inMessage.isSetField(OrdStatus.FIELD)) {
            inMessage.setField(new OrdStatus(OrdStatus.NEW));
        }
        if(!inMessage.isSetField(OrderID.FIELD)) {
            inMessage.setField(new OrderID("ID"+ counter.getAndIncrement()));
        }
        if(!inMessage.isSetField(OrigClOrdID.FIELD)) {
            inMessage.setField(new OrigClOrdID("ID"+ counter.getAndIncrement()));
        }
    }
    /**
     * Creates an empty business level reject message.
     *
     * @return a <code>Message</code> value
     */
    protected static Message createEmptyBusinessMessageReject()
    {
        return getSystemMessageFactory().newBusinessMessageReject("QQ",
                                                                  BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                                                                  "Bad message type");
    }
    /**
     * running application context
     */
    protected static ConfigurableApplicationContext context;
    /**
     * The URL for the JMS broker. It must match the ORS configuration
     * files.
     */
    protected static final String BROKER_URL = "tcp://localhost:61616";
    /**
     * test exchange value
     */
    private static SampleExchange exchange;
    /**
     * test ORS value
     */
    private static OrderRoutingSystem ors;
    /**
     * test application thread value
     */
    private static Thread applicationThread;
    /**
     * test admin client value
     */
    private static ORSTestClient adminClient;
    /**
     * counter used to generate unique ids
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
}
