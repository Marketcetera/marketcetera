package org.marketcetera.ors;

import java.beans.ExceptionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ReportListener;
import org.marketcetera.client.ServerStatusListener;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.ors.config.SpringConfig;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.ReportBase;

import static org.junit.Assert.*;

/**
 * A wrapper around a production client, for use in ORS testing.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class ORSTestClient
{

    // CLASS DATA.

    public static class Accumulator<T>
    {
        private BlockingQueue<T> mItems=new LinkedBlockingQueue<T>();

        public void add
            (T item)
        {
            mItems.add(item);
        }

        public T getNext()
            throws InterruptedException 
        {
            return mItems.take();
        }

        public void clear()
        {
            mItems.clear();
        }

        public void assertEmpty()
        {
            assertEquals(0,mItems.size());
        }
    }

    public static class ExceptionListenerImpl
        extends Accumulator<Exception>
        implements ExceptionListener
    {
        @Override
        public void exceptionThrown
            (Exception exception)
        {
            add(exception);
        }
    }

    public static class ReportListenerImpl
        extends Accumulator<ReportBase>
        implements ReportListener
    {
        @Override
        public void receiveExecutionReport
            (ExecutionReport report)
        {
            add(report);
        }

        @Override
        public void receiveCancelReject
            (OrderCancelReject report)
        {
            add(report);
        }
    }

    public static class BrokerStatusListenerImpl
        extends Accumulator<BrokerStatus>
        implements BrokerStatusListener
    {
        @Override
        public void receiveBrokerStatus
            (BrokerStatus status)
        {
            add(status);
        }
    }

    public static class ServerStatusListenerImpl
        extends Accumulator<Boolean>
        implements ServerStatusListener
    {
        @Override
        public void receiveServerStatus
            (boolean status)
        {
            add(status);
        }
    }


    // INSTANCE DATA.

    private final Client mClient;
    private final ExceptionListenerImpl mExceptionListener;
    private final ReportListenerImpl mReportListener;
    private final BrokerStatusListenerImpl mBrokerStatusListener;
    private final ServerStatusListenerImpl mServerStatusListener;


    // CONSTRUCTOR.

    public ORSTestClient
        (String user,
         char[] password)
        throws Exception
    {

        // Create client.

        ClientManager.init
            (new ClientParameters
             (user,password,
              ORSTestBase.BROKER_URL,
              SpringConfig.getSingleton().getServerHost(),
              SpringConfig.getSingleton().getServerPort()));
        mClient=ClientManager.getInstance();

        // Create and register listeners.

        mExceptionListener=new ExceptionListenerImpl();
        getClient().addExceptionListener(getExceptionListener());
        mReportListener=new ReportListenerImpl();
        getClient().addReportListener(getReportListener());
        mBrokerStatusListener=new BrokerStatusListenerImpl();
        getClient().addBrokerStatusListener(getBrokerStatusListener());
        mServerStatusListener=new ServerStatusListenerImpl();
        getClient().addServerStatusListener(getServerStatusListener());
    }


    // INSTANCE METHODS.    

    public void close()
        throws Exception
    {
        // Wait for messages in transit to complete.

        Thread.sleep(5000);

        // Close client.

        getClient().close();

        // Consume client closing notification.

        assertFalse(getServerStatusListener().getNext());

        // Ensure no other queued up notifications exist.

        assertEmptyAccumulators();
    }

    public Client getClient()
    {
        return mClient;
    }

    public ExceptionListenerImpl getExceptionListener()
    {
        return mExceptionListener;
    }

    public ReportListenerImpl getReportListener()
    {
        return mReportListener;
    }

    public BrokerStatusListenerImpl getBrokerStatusListener()
    {
        return mBrokerStatusListener;
    }

    public ServerStatusListenerImpl getServerStatusListener()
    {
        return mServerStatusListener;
    }

    public void assertEmptyAccumulators()
    {
        getExceptionListener().assertEmpty();
        getBrokerStatusListener().assertEmpty();
        getReportListener().assertEmpty();
        getServerStatusListener().assertEmpty();
    }
}
