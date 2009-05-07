package org.marketcetera.messagehistory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;

import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.ReportID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.GroupingList;

/* $License$ */
/**
 * Keeps track of Trading Report History for photon.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TradeReportsHistory {

    private final EventList<ReportHolder> mAllMessages;

    private final EventList<ReportHolder> mReadOnlyAllMessages;

    private final EventList<ReportHolder> mReadOnlyFillMessages;

    private final AveragePriceReportList mAveragePriceList;

    private final EventList<ReportHolder> mReadOnlyAveragePriceList;

    private final FilterList<ReportHolder> mLatestExecutionReportsList;

    private final FilterList<ReportHolder> mLatestMessageList;

    private final EventList<ReportHolder> mOpenOrderList;

    private final EventList<ReportHolder> mReadOnlyOpenOrderList;

    private final Map<OrderID, ReportHolder> mOriginalOrderACKs;

    private final Map<OrderID, OrderID> mOrderIDToGroupMap;

    private final Set<ReportID> mUniqueReportIds = new HashSet<ReportID>();

    private final ca.odell.glazedlists.util.concurrent.Lock mReadLock;

    private final ca.odell.glazedlists.util.concurrent.Lock mWriteLock;

    /**
     * Queue of incoming reports than could not be processed immediately due to an in progress reset
     * operation
     */
    private final Queue<ReportBase> mQueuedReports = new LinkedList<ReportBase>();

    /**
     * Indicates that the queue should be used instead of waiting to add reports directly
     */
    private boolean mQueueMessages = false;

    public TradeReportsHistory(FIXMessageFactory messageFactory) {
        mAllMessages = new BasicEventList<ReportHolder>();
        mReadLock = mAllMessages.getReadWriteLock().readLock();
        mWriteLock = mAllMessages.getReadWriteLock().writeLock();
        mReadOnlyAllMessages = GlazedLists.readOnlyList(mAllMessages);
        mReadOnlyFillMessages = GlazedLists.readOnlyList(new FilterList<ReportHolder>(mAllMessages,
                new ReportFillMatcher()));
        GroupingList<ReportHolder> orderIDList = new GroupingList<ReportHolder>(mAllMessages,
                new ReportGroupIDComparator());
        mLatestExecutionReportsList = new FilterList<ReportHolder>(
                new FunctionList<List<ReportHolder>, ReportHolder>(orderIDList,
                        new LatestExecutionReportFunction()), new NotNullReportMatcher());
        mLatestMessageList = new FilterList<ReportHolder>(
                new FunctionList<List<ReportHolder>, ReportHolder>(orderIDList,
                        new LatestReportFunction()), new NotNullReportMatcher());
        mAveragePriceList = new AveragePriceReportList(messageFactory, mAllMessages);
        mReadOnlyAveragePriceList = GlazedLists.readOnlyList(mAveragePriceList);
        mOpenOrderList = new FilterList<ReportHolder>(
                new FunctionList<List<ReportHolder>, ReportHolder>(orderIDList,
                        new OpenOrderListFunction()), new NotNullReportMatcher());
        mReadOnlyOpenOrderList = GlazedLists.readOnlyList(mOpenOrderList);

        mOriginalOrderACKs = new HashMap<OrderID, ReportHolder>();
        mOrderIDToGroupMap = new HashMap<OrderID, OrderID>();
    }

    /**
     * Resets the history to a new set of reports retrieved using the provided Callable. This method
     * effectively clears the lists and adds the given reports as if they were added using
     * {@link #addIncomingMessage(ReportBase)}.
     * <p>
     * <strong>All reports added before this method call will be lost.</strong>
     * 
     * @param reportsRetriever
     *            retrieves the new reports
     * @throws Exception if reportsRetriever throws an exception
     */
    public void resetMessages(Callable<ReportBase[]> reportsRetriever) throws Exception {
        // queue new incoming messages
        synchronized (mQueuedReports) {
            mQueueMessages = true;
        }
        try {
            // acquire write lock to clear the lists
            mWriteLock.lock();
            try {
                // clear the list and supporting data structures
                mAllMessages.clear();
                mUniqueReportIds.clear();
                mOriginalOrderACKs.clear();
                mOrderIDToGroupMap.clear();
            } finally {
                mWriteLock.unlock();
            }
            // retrieve new reports and add them
            ReportBase[] reports = new ReportBase[0];
            reports = reportsRetriever.call();
            for (ReportBase report : reports) {
                // this call gets the write lock
                internalAddIncomingMessage(report);
            }
        } finally {
            // flush the queue
            synchronized (mQueuedReports) {
                try {
                    for (ReportBase report : mQueuedReports) {
                        internalAddIncomingMessage(report);
                    }
                } finally {
                    mQueueMessages = false;
                    mQueuedReports.clear();
                }
            }
        }
    }

	/**
     * Adds a new report to the base list. Duplicates are ignored.
     * 
     * The report might not be added immediately if a reset is in progress. In this case, it will be
     * queued until the reset has completed.
     * 
     * @param inReport
     */
    public void addIncomingMessage(ReportBase inReport) {
        // wait if queue is being emptied
        synchronized (mQueuedReports) {
            if (mQueueMessages) {
                mQueuedReports.add(inReport);
            } else {
                internalAddIncomingMessage(inReport);
            }
        }
    }

    private void internalAddIncomingMessage(ReportBase inReport) {
        mWriteLock.lock();
        try {
            // check for duplicates
            ReportID uniqueID = inReport.getReportID();
            if (uniqueID == null) {
                SLF4JLoggerProxy.debug(this, "Recieved report without report id: {}", inReport); //$NON-NLS-1$
            } else {
                if (mUniqueReportIds.contains(uniqueID)) {
                    SLF4JLoggerProxy.debug(this, "Skipping duplicate report: {}", inReport); //$NON-NLS-1$
                    return;
                } else {
                    mUniqueReportIds.add(uniqueID);
                }
            }
            if(SLF4JLoggerProxy.isDebugEnabled(this) &&
                    inReport.getSendingTime() != null) {
                long sendingTime =0;
                sendingTime = inReport.getSendingTime().getTime();
                long systemTime = System.currentTimeMillis();
                double diff = (sendingTime-systemTime)/1000.0;
                if(Math.abs(diff) > 1) {
                                SLF4JLoggerProxy.debug(this,
                                        "{}: sendingTime v systemTime: {}",  //$NON-NLS-1$
                                        Thread.currentThread().getName(), diff);
                }
            }
            updateOrderIDMappings(inReport);
            OrderID groupID = getGroupID(inReport);
            ReportHolder messageHolder = new ReportHolder(inReport, groupID);
    
            // The first message that comes in with a specific order id gets stored in a map.  This
            // map is used by #getFirstReport(String) to facilitate CancelReplace
            if (inReport instanceof ExecutionReport
                    && inReport.getOrderID() != null) {
                OrderID id = inReport.getOrderID();
                OrderStatus status = inReport.getOrderStatus();
                if (Originator.Server == ((ExecutionReport) inReport)
                        .getOriginator()
                        && (status == OrderStatus.PendingNew || status == OrderStatus.PendingReplace)) {
                   if (!mOriginalOrderACKs.containsKey(id)) {
                        mOriginalOrderACKs.put(id, messageHolder);
                    }
                }
            }
    
            mAllMessages.add(messageHolder);
        } finally {
            mWriteLock.unlock();
        }
    }

    private void updateOrderIDMappings(ReportBase inReport) {
        if (inReport.getOrderID() != null && inReport.getOriginalOrderID() != null)
        {
            OrderID origOrderID = inReport.getOriginalOrderID();
            OrderID orderID = inReport.getOrderID();
            OrderID groupID;
            // first check to see if the orig is in the map, and if so, use
            // whatever it maps to as the groupID
            if (mOrderIDToGroupMap.containsKey(origOrderID)){
                groupID = getGroupID(origOrderID);
            } else {
                // otherwise, do a mapping from clOrdId -> origOrderID
                groupID = origOrderID;
            }
            mOrderIDToGroupMap.put(orderID, groupID);
        }
    }

    private OrderID getGroupID(ReportBase inReport) {
        return getGroupID(inReport.getOrderID());
    }

    private OrderID getGroupID(OrderID clOrdID) {
        if (mOrderIDToGroupMap.containsKey(clOrdID)){
            return mOrderIDToGroupMap.get(clOrdID);
        } else {
            return clOrdID;
        }
    }


    public EventList<ReportHolder> getFillsList() {
        return mReadOnlyFillMessages;
    }

    public EventList<ReportHolder> getAveragePricesList()
    {
        return mReadOnlyAveragePriceList;
    }

    public int size() {
        mReadLock.lock();
        try {
            return mAllMessages.size();
        } finally {
            mReadLock.unlock();
        }
    }

    public ExecutionReport getLatestExecutionReport(OrderID clOrdID) {
        return (ExecutionReport) getReport(mLatestExecutionReportsList, clOrdID);
    }
    
    private ReportBase getReport(EventList<ReportHolder> list, OrderID clOrdID) {
        mReadLock.lock();
        try {
            OrderID groupID = getGroupID(clOrdID);
            if (groupID != null){
                for (ReportHolder holder : list) {
                    if (groupID.equals(holder.getGroupID())){
                        return holder.getReport();
                    }
                }
            }
            return null;
        } finally {
            mReadLock.unlock();
        }
    }

    public EventList<ReportHolder> getAllMessagesList() {
        return mReadOnlyAllMessages;
    }

    public Message getLatestMessage(OrderID inOrderID) {
        mReadLock.lock();
        try {
            OrderID groupID = getGroupID(inOrderID);
            if (groupID != null)
            {
                for (ReportHolder holder : mLatestMessageList)
                {
                    OrderID holderGroupID = holder.getGroupID();
                    if (holderGroupID != null && groupID.equals(holderGroupID)){
                        return holder.getMessage();
                    }
                }
            }
            return null;
        } finally {
            mReadLock.unlock();
        }
    }

    public EventList<ReportHolder> getOpenOrdersList() {
        return mReadOnlyOpenOrderList;
    }

    public void visitOpenOrdersExecutionReports(MessageVisitor visitor)
    {
        mReadLock.lock();
        try {
            ReportHolder[] holders = mOpenOrderList.toArray(new ReportHolder[mOpenOrderList.size()]);
            for(ReportHolder holder : holders)
            {
                visitor.visitOpenOrderExecutionReports(holder.getReport());
            }
        } finally {
            mReadLock.unlock();
        }
    }

    /**
     * Returns a {@link org.marketcetera.messagehistory.ReportHolder} holding the first report Photon received
     * for the given clOrdID. This is the PENDING NEW or PENDING REPLACE message
     * added via {@link #addIncomingMessage(ReportBase)}.
     *
     * @param inOrderID the orderID.
     * @return the ReportHolder holding the first report
     */
    public ReportHolder getFirstReport(OrderID inOrderID){
        mReadLock.lock();
        try {
            return mOriginalOrderACKs.get(inOrderID);
        } finally {
            mReadLock.unlock();
        }
    }

}
