package org.marketcetera.ors;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.test.TestCaseBase;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.OrdStatus;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;

import static org.junit.Assert.*;
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
        Thread.sleep(1000);

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

        // Close ORS Spring application context.

        getORS().getApplicationContext().close();
    }

    protected OrderRoutingSystem getORS()
    {
        return mORS;
    }

    protected ORSTestClient getAdminClient()
    {
        return mAdminClient;
    }

    protected QuickFIXApplication getQuickFIXApplication()
    {
        return getORS().getQuickFIXApplication();
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
        if (!msg.getHeader().isSetField(AvgPx.FIELD)) {
            msg.setField(new AvgPx(0));
        }
        if (!msg.getHeader().isSetField(CumQty.FIELD)) {
            msg.setField(new CumQty(0));
        }
        if (!msg.getHeader().isSetField(ClOrdID.FIELD)) {
            msg.setField(new ClOrdID("ID"+mNextOrderID.getAndIncrement()));
        }
        if (!msg.getHeader().isSetField(OrdStatus.FIELD)) {
            msg.setField(new OrdStatus(OrdStatus.FILLED));
        }
        if (!msg.getHeader().isSetField(SendingTime.FIELD)) {
            msg.getHeader().setField(new SendingTime(new Date()));
        }
        if (!msg.getHeader().isSetField(Side.FIELD)) {
            msg.setField(new Side(Side.BUY));
        }
        if (!msg.getHeader().isSetField(Symbol.FIELD)) {
            msg.setField(new Symbol("IBM"));
        }
        getQuickFIXApplication().fromApp
            (msg,getBrokers().getBroker(brokerID).getSessionID());
    }

    protected void emulateFirstBrokerResponse
        (Message msg)
        throws Exception
    {
        emulateBrokerResponse(getFirstBrokerID(),msg);
    }

    protected static Message createEmptyOrderCancelReject()
    {
        return getSystemMessageFactory().newOrderCancelReject();
    }

    protected static Message createEmptyBusinessMessageReject()
    {
        return getSystemMessageFactory().newBusinessMessageReject
            ("QQ",BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
             "Bad message type");
    }
}
