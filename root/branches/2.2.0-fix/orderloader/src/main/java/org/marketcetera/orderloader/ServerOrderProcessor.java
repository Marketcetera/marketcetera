package org.marketcetera.orderloader;

import java.util.concurrent.atomic.AtomicInteger;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.ReportListener;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Originator;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * An order processor that sends orders to the server.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ServerOrderProcessor implements OrderProcessor {

    /**
     * The maximum time, in ms, to wait without receiving any
     * acknowledgements from the ORS until delivery of all sent orders
     * is acknowledged by the ORS.
     */

    public static final long MAXIMUM_DELIVERY_WAIT=60000;

    private AtomicInteger mOrdersOutstanding;

    /**
     * Counts ORS acknowledgements.
     */

    private class CounterListener
        implements ReportListener
    {
        @Override
        public void receiveExecutionReport(ExecutionReport inReport)
        {
            if (inReport.getOriginator()==Originator.Server) {
                mOrdersOutstanding.getAndDecrement();
            }
        }

        @Override
        public void receiveCancelReject(OrderCancelReject inReport)
        {
            if (inReport.getOriginator()==Originator.Server) {
                mOrdersOutstanding.getAndDecrement();
            }
        }
    }

    /**
     * Creates an instance.
     *
     * @param inParameter the parameters to connect to the server.
     *
     * @throws ClientInitException if there were unexpected issues initializing
     * the client.
     * @throws ConnectionException if there were network issues initializing
     * the client.
     */
    public ServerOrderProcessor(ClientParameters inParameter)
            throws ClientInitException, ConnectionException {
        mOrdersOutstanding=new AtomicInteger();
        ClientManager.init(inParameter);
        ClientManager.getInstance().addReportListener(new CounterListener());
    }

    @Override
    public void processOrder(Order inOrder, int inOrderIndex) throws Exception {
        if(inOrder instanceof OrderSingle) {
            ClientManager.getInstance().sendOrder((OrderSingle)inOrder);
        } else if(inOrder instanceof FIXOrder) {
            ClientManager.getInstance().sendOrderRaw((FIXOrder)inOrder);
        } else {
            throw new OrderParsingException(new I18NBoundMessage1P(
                    Messages.UNEXPECTED_ORDER_TYPE, inOrder));
        }
        mOrdersOutstanding.getAndIncrement();
    }

    @Override
    public void done() {
        if (!ClientManager.isInitialized()) {
            return;
        }

        // Wait until a certain timeout for the ORS to acknowledge
        // receipt of orders sent. If we don't wait, because orders
        // are sent via JMS which is asynchronous, we might close the
        // client (a synchronous operation which results in
        // invalidating the ORS session) before the ORS has a chance
        // to see the orders we sent.
        long end=System.currentTimeMillis()+MAXIMUM_DELIVERY_WAIT;
        int lastOrdersOutstanding=mOrdersOutstanding.get();
        while (lastOrdersOutstanding!=0) {
            try {
                // A short delay is used here so that we don't delay
                // exiting for too long after all orders have been
                // sent.
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                break;
            }
            long now=System.currentTimeMillis();
            int ordersOutstanding=mOrdersOutstanding.get();
            if (ordersOutstanding<lastOrdersOutstanding) {
                // Extend the timeout if at least one order has been
                // processed.
                end=now+MAXIMUM_DELIVERY_WAIT;
                lastOrdersOutstanding=ordersOutstanding;
            } else if (now>end) {
                break;
            }
        }
        try {
            ClientManager.getInstance().close();
        } catch (ClientInitException ignore) {
        }
    }
}
