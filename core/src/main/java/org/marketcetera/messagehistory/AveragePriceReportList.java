package org.marketcetera.messagehistory;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.MsgType;
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
                    Message deltaMessage = deltaReportHolder.getMessage();
                    ReportBase deltaReport = deltaReportHolder.getReport();
                    quickfix.field.Side orderSide = new quickfix.field.Side();
                    try {
                        deltaMessage.getField(orderSide);
                    } catch (FieldNotFound e) {
                        orderSide.setValue(quickfix.field.Side.UNDISCLOSED);
                    }
                    String side = String.valueOf(orderSide.getValue());
                    Instrument instrument = InstrumentFromMessage.SELECTOR.forValue(deltaMessage).extract(deltaMessage);
                    SymbolSide symbolSide = new SymbolSide(instrument, side);
                     if(deltaReport instanceof ExecutionReport) {
                        SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                               "Considering {}", //$NON-NLS-1$
                                               deltaReport);
                        ExecutionReport execReport = (ExecutionReport)deltaReport;
                        ExecutionType execType = execReport.getExecutionType();
                       
                        if(execType == null) {
                        	SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                    "Skipping {} because the execType was null", //$NON-NLS-1$
                                    execReport);
                            continue;
                        }
                        
                        if(!EXECTYPE_STATUSES.contains(execType)){
                        	SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                    "Skipping {} because its execution type {} is not in {}", //$NON-NLS-1$
                                    execReport,
                                    execReport.getExecutionType(),
                                    EXECTYPE_STATUSES);
                        	continue;
                        }
                        
                        if(execReport.getOriginator() != Originator.Broker ) {
                            SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                                   "Skipping {} because it came from the ORS", //$NON-NLS-1$
                                                   execReport);
                            continue;
                        }
                        BigDecimal lastQuantity = execReport.getLastQuantity();
                        BigDecimal price = execReport.getLastPrice();
                        if(lastQuantity == null || !(lastQuantity.compareTo(BigDecimal.ZERO) > 0)) {
                            SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                                   "Skipping {} because the last quantity was null/zero", //$NON-NLS-1$
                                                   execReport);
                            continue;
                        }
                        if(price == null) {
                            price = execReport.getPrice();
                        }
                        if(price == null) {
                            SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                                   "Skipping {} because the price was null", //$NON-NLS-1$
                                                   execReport);
                            continue;
                        }
                        Integer averagePriceIndex = mAveragePriceIndexes.get(symbolSide);
                        // decide if we've seen this symbol/side combination in the list of ERs before. if we have, averagePriceIndex will be non-null
                        if(averagePriceIndex != null) {
                            // we have already processed at least one ER with this symbol/side combination. that means the math must take into account the existing
                            //  ERs as well as the current ER
                            ReportHolder averagePriceReportHolder = mAveragePricesList.get(averagePriceIndex);
                            Message averagePriceMessage = averagePriceReportHolder.getMessage();
                            ExecutionReport averagePriceReport = (ExecutionReport) averagePriceReportHolder.getReport();
                            BigDecimal existingCumQty = averagePriceReport.getCumulativeQuantity();
                            BigDecimal existingAvgPx = averagePriceReport.getAveragePrice();
                            BigDecimal newLastQty = lastQuantity;
                            BigDecimal newTotal = existingCumQty.add(newLastQty);
                            if(!newTotal.equals(ZERO)) {
                                BigDecimal numerator = existingCumQty.multiply(existingAvgPx).add(newLastQty.multiply(price));
                                BigDecimal newAvgPx = numerator.divide(newTotal,
                                                                       4,
                                                                       RoundingMode.HALF_UP);
                                averagePriceMessage.setDecimal(AvgPx.FIELD,
                                                               newAvgPx);
                                averagePriceMessage.setDecimal(CumQty.FIELD,
                                                               newTotal);
                                updates.elementUpdated(averagePriceIndex,
                                                       averagePriceReportHolder,
                                                       averagePriceReportHolder);
                            }
                        } else {
                            // we have not seen an ER with this instrument/side combination, make a new average price entry
                            Message averagePriceMessage = mMessageFactory.createMessage(MsgType.EXECUTION_REPORT);
                            averagePriceMessage.setField(orderSide);
                            InstrumentToMessage.SELECTOR.forInstrument(instrument).set(instrument,
                                                                                       mMessageFactory.getBeginString(),
                                                                                       averagePriceMessage);
                            averagePriceMessage.setField(new CumQty(lastQuantity));
                            averagePriceMessage.setField(new AvgPx(price.setScale(4,
                                                                                      RoundingMode.HALF_UP)));
                            try {
                                ReportHolder newReport = new ReportHolder(Factory.getInstance().createExecutionReport(averagePriceMessage,
                                                                                                                      execReport.getBrokerID(),
                                                                                                                      Originator.Broker,
                                                                                                                      execReport.getActorID(),
                                                                                                                      execReport.getViewerID()),
                                                                                                                      deltaReportHolder.getUnderlying());
                                mAveragePricesList.add(newReport);
                                averagePriceIndex = mAveragePricesList.size()-1;
                                mAveragePriceIndexes.put(symbolSide,
                                                         averagePriceIndex);
                                updates.elementInserted(averagePriceIndex,
                                                        newReport);
                                
                            } catch (MessageCreationException e) {
                                Messages.UNEXPECTED_ERROR.error(this,e);
                            }
                        }
                    } else {
                        SLF4JLoggerProxy.debug(AveragePriceReportList.class,
                                               "Skipping {} because it's not an ExecutionReport", //$NON-NLS-1$
                                               deltaReport);
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
    /**
     * exec type status values to include in the average price list view
     */
    private static final Set<ExecutionType> EXECTYPE_STATUSES = EnumSet.of(ExecutionType.Fill, ExecutionType.PartialFill);
    
}
