package org.marketcetera.client.utils;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.marketcetera.client.*;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.utils.OrderHistoryManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides a self-populating {@link OrderHistoryManager} implementation.
 * 
 * <p>Instantiate this class with an origin date. The origin date establishes how far back
 * to look for order history.
 * 
 * <p>Note that there are significant performance and resource implications when using this class.
 * Depending on historical order volume, this class may be required to process thousands or millions
 * of reports. There are two ramifications of this:
 * <ul>
 *   <li>Make an effort to limit the number of instances of this class. Each instance in the same process
 *       has access to the same reports. The only reason to have more than one instance of this class is
 *       if more than one historical range is required. Even then, it is preferable to use a single instance
 *       with the oldest origin date.</li>
 *   <li>Use the most recent origin date feasible. Ideally, make this midnight of the current day, or whatever
 *       makes sense for the current trading session. Obviously, business requirements will dictate what
 *       the origin date is.</li>
 * </ul>
 * 
 * <p>It may take a significant amount of time to {@link #start() start} this object as it must process historical
 * order history. Callers may choose to make this operation asynchronous. The object will report that it 
 * {@link #isRunning() is running} when the processing is complete.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class LiveOrderHistoryManager
        extends OrderHistoryManager
        implements Lifecycle, ReportListener
{
    /**
     * Create a new LiveOrderHistoryManager instance.
     * 
     * @param inReportHistoryOrigin a <code>Date</code> value indicating the point from which to gather order history or <code>null</code>
     * @throws ClientInitException if a connection to the <code>Client</code> cannot be made 
     */
    public LiveOrderHistoryManager(Date inReportHistoryOrigin)
            throws ClientInitException
    {
        client = ClientManager.getInstance();
        if(inReportHistoryOrigin == null) {
            reportHistoryOrigin = new Date(0);
        } else {
            reportHistoryOrigin = inReportHistoryOrigin;
        }
    }
    /**
     * Gets the open orders.
     * 
     * <p>The collection returned by this operation will reflect changes to the underlying order history.
     * 
     * <p>The <code>LiveOrderHistoryManager</code> object must be {@link #start() started} and must have
     * completed processing order history before this operation may be successfully invoked. To safely
     * use this object, do the following:
     * <pre>
     * Date originDate = (establish your history date, the more recent the more performant)
     * LiveOrderHistoryManager orderHistoryManager = new LiveOrderHistoryManager(originDate);
     * orderHistoryManager.start();
     * while(!orderHistoryManager.isRunning()) {
     *   synchronized(orderHistoryManager) {
     *     orderHistoryManager.wait(250);
     *   }
     * }
     * Map<OrderID,ReportBase> openOrders = orderHistoryManager.getOpenOrders();
     * </pre> 
     *
     * @return a <code>Map&lt;OrderID,ReportBase&gt;</code> value
     */
    public Map<OrderID,ReportBase> getOpenOrders()
    {
        if(!isRunning) {
            throw new IllegalStateException(org.marketcetera.client.Messages.OPEN_ORDER_LIST_NOT_READY.getText());
        }
        return Collections.unmodifiableMap(openOrders);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.utils.OrderHistoryManager#add(org.marketcetera.trade.ReportBase)
     */
    @Override
    public void add(ReportBase inReport)
    {
        throw new UnsupportedOperationException(org.marketcetera.client.Messages.DONT_ADD_REPORTS.getText());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ReportListener#receiveExecutionReport(org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void receiveExecutionReport(ExecutionReport inReport)
    {
        SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                               "Received {}", //$NON-NLS-1$
                               inReport);
        updateReports.add(inReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.ReportListener#receiveCancelReject(org.marketcetera.trade.OrderCancelReject)
     */
    @Override
    public void receiveCancelReject(OrderCancelReject inReport)
    {
        SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                               "Received {}", //$NON-NLS-1$
                               inReport);
        updateReports.add(inReport);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public synchronized boolean isRunning()
    {
        return isRunning;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning) {
            stop();
        }
        SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                               "LiveOrderHistoryManager starting - collecting order history since {}", //$NON-NLS-1$
                               reportHistoryOrigin);
        client.addReportListener(this);
        // note that live reports may be flowing in from this point, but we must not process them
        //  until we have processed all snapshot reports
        // collect the snapshot reports
        // note that there is a non-zero chance of getting a duplicate report here (one just came in
        //  between the time we added ourself as a listener and we make the call to get historical reports -
        //  we'll get it in both channels). this actually doesn't matter because the OrderID is the same on
        //  each so we'll just process it twice but it won't create any duplicate records in our map keyed by OrderID
        final Deque<ReportBase> snapshotReports = new LinkedList<ReportBase>();
        try {
            ReportBase[] orderHistory = client.getReportsSince(reportHistoryOrigin);
            SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                                   "{} report(s) to process", //$NON-NLS-1$
                                   orderHistory.length);
            for(ReportBase report : orderHistory) {
                snapshotReports.add(report);
            }
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        // snapshotReports contains all the reports as dictated by the origin date
        if(!snapshotReports.isEmpty()) {
            for(ReportBase report : snapshotReports) {
                LiveOrderHistoryManager.super.add(report);
            }
            snapshotReports.clear();
            compileInitialOpenOrdersView();
            SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                                   "All historical reports processed"); //$NON-NLS-1$
        }
        // create and start the report processor
        reportProcessor = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    while(true) {
                        // process any updates that exist
                        processUpdate(updateReports.take());
                    }
                } catch (InterruptedException ignored) {}
            }
        },
        "LiveOrderHistoryManager Report Processor"); //$NON-NLS-1$
        reportProcessor.start();
        isRunning = true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                               "LiveOrderHistoryManager stopping"); //$NON-NLS-1$
        if(!isRunning) {
            return;
        }
        if(reportProcessor != null) {
            reportProcessor.interrupt();
            try {
                reportProcessor.join();
            } catch (InterruptedException ignored) {}
            reportProcessor = null;
        }
        client.removeReportListener(this);
        openOrders.clear();
        isRunning = false;
    }
    /**
     * Gets the report history origin date used by this order history manager.
     *
     * @return a <code>Date</code> value
     */
    public Date getReportHistoryOrigin()
    {
        return reportHistoryOrigin;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("LiveOrderHistoryManager [").append(isRunning?"running":"not running").append("] with ").append(openOrders.size()).append(" open order").append(openOrders.size() == 1?"":"(s)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
        return builder.toString();
    }
    /**
     * Processes a <code>ReportBase</code> object received as part of an update.
     *
     * @param inReport a <code>ReportBase</code> value
     */
    private void processUpdate(ReportBase inReport)
    {
        super.add(inReport);
        if(inReport.getOrderStatus().isCancellable()) {
            SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                                   "{} represents an open order ({}), updating live order list", //$NON-NLS-1$
                                   inReport.getOrderID(),
                                   inReport.getOrderStatus());
            openOrders.put(inReport.getOrderID(),
                           inReport);
        } else {
            SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                                   "{} represents a closed order ({}) updating live order list", //$NON-NLS-1$
                                   inReport.getOrderID(),
                                   inReport.getOrderStatus());
            openOrders.remove(inReport.getOrderID());
        }
        if(inReport.getOriginalOrderID() != null) {
            SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                                   "{} replaces {}, updating live order list", //$NON-NLS-1$
                                   inReport.getOrderID(),
                                   inReport.getOriginalOrderID());
            openOrders.remove(inReport.getOriginalOrderID());
        }
        synchronized(openOrders) {
            openOrders.notifyAll();
        }
    }
    /**
     * Examines the current order history and compiles a list of open orders from it.
     * 
     * <p>This method assumes synchronized access to the order history collection.
     */
    private void compileInitialOpenOrdersView()
    {
        for(OrderID orderId : getOrderIds()) {
            ReportBase report = getLatestReportFor(orderId);
            if(report != null &&
               report.getOrderStatus().isCancellable()) {
                openOrders.put(orderId,
                               report);
            }
        }
    }
    /**
     * processes incoming reports from the live report channel
     */
    private volatile Thread reportProcessor;
    /**
     * collection which contains incoming reports from the live report channel
     */
    private final BlockingDeque<ReportBase> updateReports = new LinkedBlockingDeque<ReportBase>();
    /**
     * collection containing only the open orders
     */
    private final Map<OrderID,ReportBase> openOrders = new ConcurrentHashMap<OrderID,ReportBase>();
    /**
     * connection to the client 
     */
    private final Client client;
    /**
     * date from which to gather status
     */
    private final Date reportHistoryOrigin;
    /**
     * indicates if the object is active
     */
    private volatile boolean isRunning = false;
}
