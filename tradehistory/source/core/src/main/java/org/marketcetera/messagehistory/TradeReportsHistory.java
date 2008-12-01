package org.marketcetera.messagehistory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.event.HasFIXMessage;

import quickfix.Message;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.LastForwardPoints;
import quickfix.field.LastMkt;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LastSpotRate;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.SendingTime;
import quickfix.field.Text;
import quickfix.field.TransactTime;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GroupingList;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;

/* $License$ */
/**
 * Keeps track of Trading Report History for photon.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class TradeReportsHistory {

    private final EventList<ReportHolder> mAllMessages;

    private final FilterList<ReportHolder> mAllFilteredMessages;

    private final FilterList<ReportHolder> mFillMessages;

    private final AveragePriceReportList mAveragePriceList;

    private final FilterList<ReportHolder> mLatestExecutionReportsList;

    private final FilterList<ReportHolder> mLatestMessageList;

    private final FilterList<ReportHolder> mOpenOrderList;

    private final Map<OrderID, ReportHolder> mOriginalOrderACKs;

    private final Map<OrderID, OrderID> mOrderIDToGroupMap;

    private final FIXMessageFactory mMessageFactory;

    public TradeReportsHistory(FIXMessageFactory messageFactory) {
        this.mMessageFactory = messageFactory;

        mAllMessages = new BasicEventList<ReportHolder>();
        mAllFilteredMessages = new FilterList<ReportHolder>(mAllMessages);
        mFillMessages = new FilterList<ReportHolder>(mAllFilteredMessages,
                new ReportFillMatcher());
        GroupingList<ReportHolder> orderIDList = new GroupingList<ReportHolder>(
                mAllMessages, new ReportGroupIDComparator());
        mLatestExecutionReportsList = new FilterList<ReportHolder>(
            new FunctionList<List<ReportHolder>, ReportHolder>(orderIDList,
                new LatestExecutionReportFunction()),
                new NotNullReportMatcher());
        mLatestMessageList = new FilterList<ReportHolder>(
                new FunctionList<List<ReportHolder>, ReportHolder>(orderIDList,
                    new LatestReportFunction()), new NotNullReportMatcher());
        mAveragePriceList = new AveragePriceReportList(messageFactory,
                mAllMessages);
        mOpenOrderList = new FilterList<ReportHolder>(mLatestExecutionReportsList,
                new OpenOrderReportMatcher());

        mOriginalOrderACKs = new HashMap<OrderID, ReportHolder>();
        mOrderIDToGroupMap = new HashMap<OrderID, OrderID>();
    }

    public void addIncomingMessage(ReportBase inReport) {
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
        try {
            mAllMessages.getReadWriteLock().writeLock().lock();
            updateOrderIDMappings(inReport);
            OrderID groupID = getGroupID(inReport);
            ReportHolder messageHolder = new ReportHolder(inReport, groupID);

            // The first message that comes in with a specific order id gets stored in a map.  This
            // map is used by #getFirstReport(String) to facilitate CancelReplace
            // TODO: Change this to look for custom ORS acks
            if(inReport instanceof ExecutionReport &&
                    inReport.getOrderID() != null) {
                OrderID id = inReport.getOrderID();
                OrderStatus status = inReport.getOrderStatus();
                if(status == OrderStatus.PendingNew ||
                        status == OrderStatus.PendingReplace) {
                    synchronized (mOriginalOrderACKs) {
                        if (!mOriginalOrderACKs.containsKey(id)) {
                            mOriginalOrderACKs.put(id, messageHolder);
                        }
                    }
                }
            }

            mAllMessages.add(messageHolder);
            if (inReport instanceof OrderCancelReject &&
                    inReport.getOrderID() != null &&
                    inReport.getOrderStatus() != null){
                // Add a new execution report to the stream to update the order status, using the values from the
                // previous execution report.
                ReportBase executionReport = getLatestExecutionReport(inReport.getOrderID());
                Message newExecutionReport = mMessageFactory.createMessage(MsgType.EXECUTION_REPORT);
                Message oldExecutionReport = null;
                if(executionReport instanceof HasFIXMessage) {
                    oldExecutionReport = ((HasFIXMessage)executionReport).getMessage();
                    FIXMessageUtil.fillFieldsFromExistingMessage(newExecutionReport, oldExecutionReport, false);
                    newExecutionReport.setField(new OrdStatus(
                            inReport.getOrderStatus().getFIXValue()));
                    if (inReport.getText() != null){
                        newExecutionReport.setField(new Text(inReport.getText()));
                    }
/*                  Skip ExecTransType as ExecType serves the same purpose
                    if (newExecutionReport.isSetField(ExecTransType.FIELD)){
                        newExecutionReport.setField(new ExecTransType(ExecTransType.STATUS));
                    }
*/
                    if (newExecutionReport.isSetField(ExecType.FIELD)){
                        newExecutionReport.setField(new ExecType(ExecType.ORDER_STATUS));
                    }
                    if (newExecutionReport.isSetField(TransactTime.FIELD)) {
                        newExecutionReport.setField(new TransactTime(new Date())); //i18n_datetime
                    }
                    newExecutionReport.getHeader().setField(new SendingTime(new Date())); //i18n_datetime

                    newExecutionReport.getHeader().removeField(MsgSeqNum.FIELD);
                    newExecutionReport.removeField(ExecID.FIELD);
                    newExecutionReport.removeField(LastShares.FIELD);
                    newExecutionReport.removeField(LastPx.FIELD);
                    newExecutionReport.removeField(LastSpotRate.FIELD);
                    newExecutionReport.removeField(LastForwardPoints.FIELD);
                    newExecutionReport.removeField(LastMkt.FIELD);

                    try {
                        mAllMessages.add(new ReportHolder(
                                Factory.getInstance().createExecutionReport(
                                        newExecutionReport,
                                        inReport.getDestinationID(), Originator.Server), groupID));
                    } catch (MessageCreationException e) {
                        throw new RuntimeException(Messages.SHOULD_NEVER_HAPPEN_IN_ADDINCOMINGMESSAGE.getText(), e);
                    }
                } else {
                    throw new IllegalArgumentException(inReport.toString());
                }
            }
        } finally {
            mAllMessages.getReadWriteLock().writeLock().unlock();
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


    public FilterList<ReportHolder> getFillsList() {
        return mFillMessages;
    }

    public EventList<ReportHolder> getAveragePricesList()
    {
        return mAveragePriceList;
    }

    public int size() {
        return mAllMessages.size();
    }

    public ReportBase getLatestExecutionReport(OrderID clOrdID) {
        try {
            mLatestExecutionReportsList.getReadWriteLock().readLock().lock();
            OrderID groupID = getGroupID(clOrdID);
            if (groupID != null){
                for (ReportHolder holder : mLatestExecutionReportsList) {
                    if (groupID.equals(holder.getGroupID())){
                        return holder.getReport();
                    }
                }
            }
            return null;
        } finally {
            mLatestExecutionReportsList.getReadWriteLock().readLock().unlock();
        }
    }

    public EventList<ReportHolder> getAllMessagesList() {
        return mAllMessages;
    }

    public Message getLatestMessage(OrderID inOrderID) {
        try {
            mLatestMessageList.getReadWriteLock().readLock().lock();
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
            mLatestMessageList.getReadWriteLock().readLock().unlock();
        }
    }

    public void setMatcherEditor(ThreadedMatcherEditor<ReportHolder> matcherEditor) {
        mAllFilteredMessages.setMatcherEditor(matcherEditor);
    }

    public EventList<ReportHolder> getFilteredMessages() {
        return mAllFilteredMessages;
    }

    public FilterList<ReportHolder> getOpenOrdersList() {
        return mOpenOrderList;
    }

    public void visitOpenOrdersExecutionReports(MessageVisitor visitor)
    {
        try {
            mOpenOrderList.getReadWriteLock().readLock().lock();
            ReportHolder[] holders = mOpenOrderList.toArray(new ReportHolder[mOpenOrderList.size()]);
            for(ReportHolder holder : holders)
            {
                visitor.visitOpenOrderExecutionReports(holder.getMessage());
            }
        } finally {
            mOpenOrderList.getReadWriteLock().readLock().unlock();
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
        synchronized (mOriginalOrderACKs){
            return mOriginalOrderACKs.get(inOrderID);
        }
    }

}