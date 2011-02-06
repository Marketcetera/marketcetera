package org.marketcetera.ors;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.exchange.Event;
import org.marketcetera.ors.exchange.FromAppEvent;
import org.marketcetera.ors.exchange.LogonEvent;
import org.marketcetera.ors.exchange.SampleExchange;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.test.TestCaseBase;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.AvgPx;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;

import static org.marketcetera.trade.TypesTestBase.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ORSTestBase
    extends TestCaseBase
{
    /**
     * The URL for the JMS broker. It must match the ORS configuration
     * files.
     */

    protected static final String BROKER_URL=
        "tcp://localhost:61616";


    private static SampleExchange mExchange;
    private static OrderRoutingSystem sORS;
    private static Thread sORSThread;
    private static ORSTestClient sAdminClient;
    private static AtomicInteger sNextOrderID=new AtomicInteger(0);


    protected static void startORS
        (final String args[])
        throws Exception
    {
        // Initialize database.

        DBInit.initORSDB();

        // Initialize exchange.

        mExchange=new SampleExchange(ApplicationBase.CONF_DIR+"exchange.xml");
        mExchange.start();

        // Wait for exchange initialization to complete.

        Thread.sleep(1000);

        // Create and start ORS in a separate thread.

        sORS =new OrderRoutingSystem();
        sORSThread =new Thread("testThread") {
            @Override
            public void run() {
//                getORS().startWaitingForever(); // TODO the ORS runs as a standalone app?
            }
        };
        sORSThread.start();

        // Wait for ORS initialization to complete.

//        while (!getORS().isWaitingForever()) { // TODO the ORS runs as a standalone app?
//            Thread.sleep(1000);
//        }

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

        sAdminClient =new ORSTestClient
            (getORS().getAuth().getUser(),
             getORS().getAuth().getPassword());
    }

    protected static void startORS()
        throws Exception
    {
        startORS(new String[0]);
    }

    protected static void stopORS()
        throws Exception
    {
        // Close the administrative client.

        if (getAdminClient()!=null) {
            getAdminClient().close();
            sAdminClient =null;
        }

        // Shut down ORS waiting thread.

        if (sORSThread !=null) {
            sORSThread.interrupt();

            // Wait for ORS waiting thread to terminate.

            while (sORSThread.isAlive()) {
                Thread.sleep(1000);
            }
            sORSThread =null;
        }

        // Shut down the ORS.

        if (getORS()!=null) {
            getORS().stop();
            sORS =null;
        }

        // Shut down the exchange.


        if (getExchange()!=null) {
            getExchange().stop();
            mExchange=null;
        }
    }

    protected static SampleExchange getExchange()
    {
        return mExchange;
    }

    protected static OrderRoutingSystem getORS()
    {
        return sORS;
    }

    protected static ORSTestClient getAdminClient()
    {
        return sAdminClient;
    }

    protected static Brokers getBrokers()
    {
        return getORS().getBrokers();
    }

    protected static BrokerID getFirstBrokerID()
    {
        return getBrokers().getBrokers().get(0).getBrokerID();
    }

    protected static void emulateBrokerResponse
        (BrokerID brokerID,
         Message msg)
        throws Exception
    {
        if (!msg.getHeader().isSetField(SendingTime.FIELD)) {
            msg.getHeader().setField(new SendingTime(new Date()));
        }
        SessionID id=getBrokers().getBroker(brokerID).getSessionID();
        SampleExchange.sendMessage(msg,new SessionID
                                   (id.getBeginString(),
                                    id.getTargetCompID(),
                                    id.getSenderCompID()));
    }

    protected static void emulateFirstBrokerResponse
        (Message msg)
        throws Exception
    {
        emulateBrokerResponse(getFirstBrokerID(),msg);
    }

    protected static Message getNextExchangeMessage()
        throws InterruptedException
    {
        while (true) {
            Event event=getExchange().getNext();
            if (event instanceof FromAppEvent) {
                return ((FromAppEvent)event).getMessage();
            }
        }
    }

    protected static void completeExecReport
        (Message msg)
    {
        if (!msg.isSetField(AvgPx.FIELD)) {
            msg.setField(new AvgPx(0));
        }
        if (!msg.isSetField(ClOrdID.FIELD)) {
            msg.setField(new ClOrdID("ID"+ sNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(CumQty.FIELD)) {
            msg.setField(new CumQty(0));
        }
        if (!msg.isSetField(ExecID.FIELD)) {
            msg.setField(new ExecID("ID"+ sNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(ExecTransType.FIELD)) {
            msg.setField(new ExecTransType(ExecTransType.NEW));
        }
        if (!msg.isSetField(ExecType.FIELD)) {
            msg.setField(new ExecType(ExecType.NEW));
        }
        if (!msg.isSetField(LeavesQty.FIELD)) {
            msg.setField(new LeavesQty(0));
        }
        if (!msg.isSetField(OrdStatus.FIELD)) {
            msg.setField(new OrdStatus(OrdStatus.NEW));
        }
        if (!msg.isSetField(OrderID.FIELD)) {
            msg.setField(new OrderID("ID"+ sNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(Side.FIELD)) {
            msg.setField(new Side(Side.BUY));
        }
        if (!msg.isSetField(Symbol.FIELD)) {
            msg.setField(new Symbol("IBM"));
        }
    }

    protected static Message createEmptyOrderCancelReject()
    {
        return getSystemMessageFactory().newOrderCancelReject();
    }

    protected static void completeOrderCancelReject
        (Message msg)
    {
        if (!msg.isSetField(ClOrdID.FIELD)) {
            msg.setField(new ClOrdID("ID"+ sNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(CxlRejResponseTo.FIELD)) {
            msg.setField(new CxlRejResponseTo
                         (CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        }
        if (!msg.isSetField(OrdStatus.FIELD)) {
            msg.setField(new OrdStatus(OrdStatus.NEW));
        }
        if (!msg.isSetField(OrderID.FIELD)) {
            msg.setField(new OrderID("ID"+ sNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(OrigClOrdID.FIELD)) {
            msg.setField(new OrigClOrdID("ID"+ sNextOrderID.getAndIncrement()));
        }
    }

    protected static Message createEmptyBusinessMessageReject()
    {
        return getSystemMessageFactory().newBusinessMessageReject
            ("QQ",BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
             "Bad message type");
    }


    @BeforeClass
    public static void setupORSTestBase()
        throws Exception
    {
        startORS();
    }

    @AfterClass
    public static void tearDownORSTestBase()
        throws Exception
    {
        stopORS();
    }
}
