package org.marketcetera.client.utils;

import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportBaseImpl;
import org.marketcetera.trade.utils.OrderHistoryManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides a historically-aware {@link OrderHistoryManager} implementation.
 * 
 * <p>Instantiate this class with an origin date. The origin date establishes how far back
 * to look for order history. Open orders are always included in the history, regardless of the
 * origin date.
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
 * @since 2.1.4
 */
@ClassVersion("$Id$")
public class LiveOrderHistoryManager
        extends OrderHistoryManager
        implements Lifecycle
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
        if(inReportHistoryOrigin == null) {
            reportHistoryOrigin = new Date(0);
        } else {
            reportHistoryOrigin = inReportHistoryOrigin;
        }
        client = ClientManager.getInstance();
    }
    /**
     * Gets the open orders.
     * 
     * <p>The collection returned by this operation will reflect changes to the underlying order history.
     * 
     * <p>The <code>LiveOrderHistoryManager</code> object must be {@link #start() started} before this operation 
     * may be successfully invoked.
     *
     * @return a <code>Map&lt;OrderID,ExecutionReport&gt;</code> value
     * @throws IllegalStateException if the object has not started
     */
    @Override
    public Map<OrderID,ExecutionReport> getOpenOrders()
    {
        if(!isRunning) {
            throw new IllegalStateException(org.marketcetera.client.Messages.OPEN_ORDER_LIST_NOT_READY.getText());
        }
        return super.getOpenOrders();
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
            final SortedSet<ReportBase> tempSnapshotReports = new TreeSet<ReportBase>(ReportBase.ReportComparator.INSTANCE);
            for(ReportBase report : orderHistory) {
                tempSnapshotReports.add(report);
            }
            List<ReportBaseImpl> openOrders = client.getOpenOrders();
            if(openOrders != null) {
                for(ReportBase openOrder : openOrders) {
                    if(!tempSnapshotReports.contains(openOrder)) {
                        tempSnapshotReports.add(openOrder);
                    }
                }
            }
            snapshotReports.addAll(tempSnapshotReports);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        // snapshotReports contains all the reports as dictated by the origin date
        if(!snapshotReports.isEmpty()) {
            for(ReportBase report : snapshotReports) {
                LiveOrderHistoryManager.super.add(report);
            }
            snapshotReports.clear();
            SLF4JLoggerProxy.debug(LiveOrderHistoryManager.class,
                                   "All historical reports processed"); //$NON-NLS-1$
        }
        // create and start the report processor
        reportProcessor = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    while(isRunning) {
                        // process any updates that exist
                        add(updateReports.take());
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
        clear();
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
        builder.append("LiveOrderHistoryManager [").append(isRunning?"running":"not running").append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return builder.toString();
    }
    /**
     * Get the client value.
     *
     * @return a <code>Client</code> value
     */
    protected Client getClient()
    {
        return client;
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
     * date from which to gather status
     */
    private final Date reportHistoryOrigin;
    /**
     * indicates if the object is active
     */
    private volatile boolean isRunning = false;
    /**
     * connection to the client 
     */
    private final Client client;
}
