package org.marketcetera.messagehistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import ca.odell.glazedlists.AbstractEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/* $License$ */

/**
 * A virtual list of {@link ReportHolder} that tracks the average price of
 * symbols in a source list. This list will have one entry for each unique
 * symbol in the source list.
 * 
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class AveragePriceReportList extends AbstractEventList<ReportHolder> implements ListEventListener<ReportHolder> {

    private final HashMap<SymbolSide, Integer> mAveragePriceIndexes = new HashMap<SymbolSide, Integer>();
    private final ArrayList<ReportHolder> mAveragePricesList = new ArrayList<ReportHolder>();

    private final FIXMessageFactory mMessageFactory;

    public AveragePriceReportList(FIXMessageFactory messageFactory, EventList<ReportHolder> source) {
        super(source.getPublisher());
        this.mMessageFactory = messageFactory;
        source.addListEventListener(this);

        readWriteLock = source.getReadWriteLock();
    }

    public void listChanged(ListEvent<ReportHolder> listChanges) {
        // all of these changes to this list happen "atomically"
        updates.beginEvent(true);

        // handle reordering events
        if(!listChanges.isReordering()) {
            // for all changes, one index at a time
            while(listChanges.next()) {

                // get the current change info
                int changeType = listChanges.getType();

                EventList<ReportHolder> sourceList = listChanges.getSourceList();
                // handle delete events
                if(changeType == ListEvent.UPDATE) {
                    throw new UnsupportedOperationException();
                } else if (changeType == ListEvent.DELETE) {
                    // assume a delete all since this is the only thing supported.
                    clear();
                    updates.commitEvent();
                    return;
                } else if(changeType == ListEvent.INSERT) {
                    ReportHolder deltaReportHolder = sourceList.get(listChanges.getIndex());

                    Integer averagePriceIndex = null;

                    try {
                        Message deltaMessage = deltaReportHolder.getMessage();
                        ReportBase deltaReport = deltaReportHolder.getReport();
                        String symbol = deltaMessage.getString(Symbol.FIELD);
                        String side = deltaMessage.getString(Side.FIELD);
                        SymbolSide symbolSide = new SymbolSide(new MSymbol(symbol), side);
                        averagePriceIndex = mAveragePriceIndexes.get(symbolSide);

                        if(averagePriceIndex != null) {
                            ReportHolder averagePriceReportHolder = mAveragePricesList.get(averagePriceIndex);
                            Message averagePriceMessage = averagePriceReportHolder.getMessage();
                            ExecutionReport averagePriceReport = (ExecutionReport) averagePriceReportHolder.getReport();

                            if (deltaReport instanceof ExecutionReport) {
                                ExecutionReport execReport = (ExecutionReport) deltaReport;
                                BigDecimal lastQuantity = execReport.getLastQuantity();
                                if (lastQuantity == null)
                                    continue;
                                if (lastQuantity.compareTo(BigDecimal.ZERO) > 0) {
                                    double existingCumQty = toDouble(averagePriceReport.getCumulativeQuantity());
                                    double existingAvgPx = toDouble(averagePriceReport.getAveragePrice());
                                    double newLastQty = toDouble(lastQuantity);
                                    double newLastPx = toDouble(execReport.getLastPrice());
                                    double newTotal = existingCumQty + newLastQty;
                                    if (newTotal != 0.0){
                                        double numerator = (existingCumQty * existingAvgPx)+(newLastQty * newLastPx);
                                        double newAvgPx = numerator / newTotal;
                                        averagePriceMessage.setDouble(AvgPx.FIELD, newAvgPx);
                                        averagePriceMessage.setDouble(CumQty.FIELD, newTotal);
                                    }
                                }
                                // The following block is for the PENDING NEW acks from ORS.
                                else if (execReport.getOriginator() == Originator.Server && deltaReport.getOrderStatus() == OrderStatus.PendingNew) {
                                    double orderQty = toDouble(averagePriceReport.getOrderQuantity());
                                    orderQty = orderQty + toDouble(execReport.getOrderQuantity());
                                    averagePriceMessage.setDouble(OrderQty.FIELD, orderQty);
                                }
                            }
                            updates.elementUpdated(averagePriceIndex, averagePriceReportHolder, averagePriceReportHolder);
                        } else {
                            if (deltaReport instanceof ExecutionReport) {
                                ExecutionReport execReport = (ExecutionReport) deltaReport;
                                BigDecimal lastQuantity = execReport.getLastQuantity();
                                if ((execReport.getOriginator() == Originator.Server && deltaReport.getOrderStatus() == OrderStatus.PendingNew) || (lastQuantity != null && lastQuantity.compareTo(BigDecimal.ZERO) > 0)) { 
                                    Message averagePriceMessage = mMessageFactory.createMessage(MsgType.EXECUTION_REPORT);
                                    averagePriceMessage.setField(deltaMessage.getField(new Side()));
                                    averagePriceMessage.setField(deltaMessage.getField(new Symbol()));
                                    // The following block is for the PENDING NEW acks from ORS.
                                    if (execReport.getOriginator() == Originator.Server && deltaReport.getOrderStatus() == OrderStatus.PendingNew){
                                        averagePriceMessage.setField(new OrderQty(execReport.getOrderQuantity()));
                                    } else {
                                        if (execReport.getLeavesQuantity() != null)
                                            averagePriceMessage.setField(new LeavesQty(execReport.getLeavesQuantity()));
                                        averagePriceMessage.setField(new CumQty(lastQuantity));
                                        averagePriceMessage.setField(new AvgPx(execReport.getLastPrice()));
                                    }
                                    if (execReport.getAccount() != null)
                                        averagePriceMessage.setField(new Account(execReport.getAccount()));

                                    try {
                                        ReportHolder newReport = new ReportHolder(Factory
                                                .getInstance().createExecutionReport(
                                                        averagePriceMessage,
                                                        execReport.getBrokerID(),
                                                        Originator.Server, execReport.getActorID(),
                                                        execReport.getViewerID()));
                                        mAveragePricesList.add(newReport);
                                        averagePriceIndex = mAveragePricesList.size()-1;
                                        mAveragePriceIndexes.put(symbolSide, averagePriceIndex);
                                        updates.elementInserted(averagePriceIndex, newReport);
                                    } catch (MessageCreationException e) {
                                        SLF4JLoggerProxy.error(this, "unexpected error", e);  //$NON-NLS-1$
                                    }
                                }
                            }
                        }
                        // if this value was not filtered out, it is now so add a change

                    } catch (FieldNotFound fnf){
                        // ignore...
                    }
                }
            }
        }

        // commit the changes and notify listeners
        updates.commitEvent();
    }

    @Override
    public ReportHolder get(int index) {
        return mAveragePricesList.get(index);
    }

    @Override
    public int size() {
        return mAveragePricesList.size();
    }

    private static double toDouble(BigDecimal inValue) {
        return inValue == null
                ? 0.0
                : inValue.doubleValue();
    }
    
    @Override
    public void clear() {
    	// don't do a clear on an empty set
        if(isEmpty()) return;
        // create the change event
        updates.beginEvent();
        for(int i = 0, size = size(); i < size; i++) {
            updates.elementDeleted(0, get(i));
        }
        // do the actual clear
        mAveragePricesList.clear();
        mAveragePriceIndexes.clear();
        // fire the event
        updates.commitEvent();
    }

    @Override
    public void dispose() {
    }

}