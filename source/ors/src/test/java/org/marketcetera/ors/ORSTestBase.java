package org.marketcetera.ors;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
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


    private SampleExchange mExchange;
    private OrderRoutingSystem mORS;
    private Thread mORSThread;
    private ORSTestClient mAdminClient;
    private AtomicInteger mNextOrderID=new AtomicInteger(0);


    protected void startORS
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

        mORS=new OrderRoutingSystem(args);
        mORSThread=new Thread("testThread") {
            @Override
            public void run() {
                getORS().startWaitingForever();
            }
        };
        mORSThread.start();

        // Wait for ORS initialization to complete.

        while (!getORS().isWaitingForever()) {
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

        mAdminClient=new ORSTestClient
            (getORS().getAuth().getUser(),
             getORS().getAuth().getPassword());
    }

    protected void startORS()
        throws Exception
    {
        startORS(new String[0]);
    }

    protected void stopORS()
        throws Exception
    {
        // Close the administrative client.

        getAdminClient().close();

        // Shut down ORS waiting thread.

        mORSThread.interrupt();

        // Wait for ORS waiting thread to terminate.

        while (mORSThread.isAlive()) {
            Thread.sleep(1000);
        }

        // Shut down the ORS.

        getORS().stop();

        // Shut down the exchange.

        getExchange().stop();
    }

    protected SampleExchange getExchange()
    {
        return mExchange;
    }

    protected OrderRoutingSystem getORS()
    {
        return mORS;
    }

    protected ORSTestClient getAdminClient()
    {
        return mAdminClient;
    }

    protected Brokers getBrokers()
    {
        return getORS().getBrokers();
    }

    protected BrokerID getFirstBrokerID()
    {
        return getBrokers().getBrokers().get(0).getBrokerID();
    }

    protected void emulateBrokerResponse
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

    protected void emulateFirstBrokerResponse
        (Message msg)
        throws Exception
    {
        emulateBrokerResponse(getFirstBrokerID(),msg);
    }

    protected Message getNextExchangeMessage()
        throws InterruptedException
    {
        while (true) {
            Event event=getExchange().getNext();
            if (event instanceof FromAppEvent) {
                return ((FromAppEvent)event).getMessage();
            }
        }
    }

    protected void completeExecReport
        (Message msg)
    {
        if (!msg.isSetField(AvgPx.FIELD)) {
            msg.setField(new AvgPx(0));
        }
        if (!msg.isSetField(ClOrdID.FIELD)) {
            msg.setField(new ClOrdID("ID"+mNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(CumQty.FIELD)) {
            msg.setField(new CumQty(0));
        }
        if (!msg.isSetField(ExecID.FIELD)) {
            msg.setField(new ExecID("ID"+mNextOrderID.getAndIncrement()));
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
            msg.setField(new OrderID("ID"+mNextOrderID.getAndIncrement()));
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

    protected void completeOrderCancelReject
        (Message msg)
    {
        if (!msg.isSetField(ClOrdID.FIELD)) {
            msg.setField(new ClOrdID("ID"+mNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(CxlRejResponseTo.FIELD)) {
            msg.setField(new CxlRejResponseTo
                         (CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        }
        if (!msg.isSetField(OrdStatus.FIELD)) {
            msg.setField(new OrdStatus(OrdStatus.NEW));
        }
        if (!msg.isSetField(OrderID.FIELD)) {
            msg.setField(new OrderID("ID"+mNextOrderID.getAndIncrement()));
        }
        if (!msg.isSetField(OrigClOrdID.FIELD)) {
            msg.setField(new OrigClOrdID("ID"+mNextOrderID.getAndIncrement()));
        }
    }

    protected static Message createEmptyBusinessMessageReject()
    {
        return getSystemMessageFactory().newBusinessMessageReject
            ("QQ",BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
             "Bad message type");
    }


    @Before
    public void setupORSTestBase()
        throws Exception
    {
        startORS();
    }

    @After
    public void tearDownORSTestBase()
        throws Exception
    {
        stopORS();
    }
}
