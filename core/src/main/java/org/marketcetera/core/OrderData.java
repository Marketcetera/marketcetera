package org.marketcetera.core;

import static org.marketcetera.core.PlatformServices.divisionContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ExecutionTransType;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.ExecRefID;
import quickfix.field.ExecTransType;
import quickfix.field.MsgType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderData
{
    /**
     * Add a price/qty pair.
     *
     * @param inPrice a <code>BigDecimal</code> value
     * @param inQty a <code>BigDecimal</code> value
     */
    public void add(BigDecimal inPrice,
                    BigDecimal inQty)
    {
        tuples.add(new PriceQtyTuple(inPrice,inQty));
    }
    /**
     * Calculate the average price from the collected quantities.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal calculateAveragePrice()
    {
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal extendedQty = BigDecimal.ZERO;
        for(PriceQtyTuple amount : tuples) {
            totalQty = totalQty.add(amount.getQty());
            extendedQty = extendedQty.add(amount.getPrice().multiply(amount.getQty()));
        }
        if(totalQty.compareTo(BigDecimal.ZERO) != 0) {
            result = extendedQty.divide(totalQty,
                                        divisionContext);
        }
        return result;
    }
    /**
     * Calculate the cumulative quantity from the collected quantities.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal calculateCumQty()
    {
        BigDecimal result = BigDecimal.ZERO;
        for(PriceQtyTuple amount : tuples) {
            result = result.add(amount.getQty());
        }
        return result;
    }
    /**
     * Calculate the leaves quantity from the order data.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal calculateLeavesQty()
    {
        return orderQuantity.subtract(calculateCumQty());
    }
    /**
     * Calculate the last price from the collected quantities.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal calculateLastPx()
    {
        BigDecimal result = BigDecimal.ZERO;
        if(!tuples.isEmpty()) {
            result = tuples.get(tuples.size()-1).getPrice();
        }
        return result;
    }
    /**
     * Calculate the last qty from the collected quantities.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal calculateLastQty()
    {
        BigDecimal result = BigDecimal.ZERO;
        if(!tuples.isEmpty()) {
            result = tuples.get(tuples.size()-1).getQty();
        }
        return result;
    }
    /**
     * Get the orderPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOrderPrice()
    {
        return orderPrice;
    }
    /**
     * Sets the orderPrice value.
     *
     * @param inOrderPrice a <code>BigDecimal</code> value
     */
    public void setOrderPrice(BigDecimal inOrderPrice)
    {
        orderPrice = inOrderPrice;
    }
    /**
     * Get the orderQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /**
     * Sets the orderQuantity value.
     *
     * @param inOrderQuantity a <code>BigDecimal</code> value
     */
    public void setOrderQuantity(BigDecimal inOrderQuantity)
    {
        orderQuantity = inOrderQuantity;
    }
    /**
     * Get the orderId value.
     *
     * @return a <code>String</code> value
     */
    public String getOrderId()
    {
        return orderId;
    }
    /**
     * Get the orderStatus value.
     *
     * @return an <code>OrderStatus</code> value
     */
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /**
     * Sets the orderStatus value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    public void setOrderStatus(OrderStatus inOrderStatus)
    {
        orderStatus = inOrderStatus;
    }
    /**
     * Get the orderType value.
     *
     * @return an <code>OrderType</code> value
     */
    public OrderType getOrderType()
    {
        return orderType;
    }
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    public Side getSide()
    {
        return side;
    }
    /**
     * Get the clOrdId value.
     *
     * @return a <code>String</code> value
     */
    public String getClOrdId()
    {
        return clOrdId;
    }
    /**
     * Sets the clOrdId value.
     *
     * @param inClOrdId a <code>String</code> value
     */
    public void setClOrdId(String inClOrdId)
    {
        clOrdId = inClOrdId;
    }
    /**
     * Get the initialOrder value.
     *
     * @return a <code>Message</code> value
     */
    public Message getInitialOrder()
    {
        return initialOrder;
    }
    /**
     * Get the mostRecentOrder value.
     *
     * @return a <code>Message</code> value
     */
    public Message getMostRecentOrder()
    {
        return mostRecentOrder;
    }
    /**
     * Sets the mostRecentOrder value.
     *
     * @param inMostRecentOrder a <code>Message</code> value
     */
    public void setMostRecentOrder(Message inMostRecentOrder)
    {
        mostRecentOrder = inMostRecentOrder;
        if(inMostRecentOrder.isSetField(quickfix.field.ClOrdID.FIELD)) {
            try {
                setClOrdId(mostRecentOrder.getString(quickfix.field.ClOrdID.FIELD));
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Get the mostRecentReport value.
     *
     * @return a <code>Message</code> value
     */
    public Message getMostRecentReport()
    {
        return mostRecentReport;
    }
    /**
     * Sets the mostRecentReport value.
     *
     * @param inMostRecentReport a <code>Message</code> value
     */
    public void setMostRecentReport(Message inMostRecentReport)
    {
        mostRecentReport = inMostRecentReport;
    }
    /**
     * Get the initialClOrdId value.
     *
     * @return a <code>String</code> value
     */
    public String getInitialClOrdId()
    {
        return initialClOrdId;
    }
    /**
     * Process the given message.
     *
     * @param inReport a <code>Message</code> value
     * @return a <code>boolean</code> value if the overall state of the order changed
     * @throws FieldNotFound if the report cannot be parsed
     */
    public boolean process(Message inReport)
            throws FieldNotFound
    {
        SLF4JLoggerProxy.debug(this,
                               "{} processing {}",
                               this,
                               inReport);
        if(FIXMessageUtil.isExecutionReport(inReport)) {
            mostRecentReport = inReport;
            // TODO need to check for corrections, keep a list of executions, regenerate tuples as needed
            OrderStatus orderStatus = OrderStatus.getInstanceForFIXMessage(inReport);
            boolean stateChanged = orderStatus != getOrderStatus();
            setOrderStatus(orderStatus);
            ExecutionType executionType = ExecutionType.getInstanceForFIXMessage(inReport);
            if(executionType.isFill()) {
                BigDecimal lastQty = inReport.getDecimal(quickfix.field.LastQty.FIELD);
                BigDecimal lastPrice = inReport.getDecimal(quickfix.field.LastPx.FIELD);
                add(lastPrice,lastQty);
                return true;
            }
            return stateChanged;
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "{} ignoring {}",
                                   this,
                                   inReport);
        }
        return false;
    }
    /**
     * Generate a replace message using the given order id using the most recent order message.
     *
     * @param inClOrdId a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the cancel could not be built
     */
    public Message generateReplace(String inClOrdId)
            throws FieldNotFound
    {
        Message replace = messageFactory.createMessage(MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        Message template = mostRecentOrder;
        FIXMessageUtil.fillFieldsFromExistingMessage(replace,
                                                     template,
                                                     dataDictionary,
                                                     false);
        replace.setField(new quickfix.field.OrigClOrdID(replace.getString(quickfix.field.ClOrdID.FIELD)));
        replace.setField(new quickfix.field.ClOrdID(inClOrdId));
        return replace;
    }
    /**
     * Generate a cancel message using the given order id using the most recent order message.
     *
     * @param inClOrdId a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the cancel could not be built
     */
    public Message generateCancel(String inClOrdId)
            throws FieldNotFound
    {
        Message cancel = messageFactory.createMessage(MsgType.ORDER_CANCEL_REQUEST);
        Message template = mostRecentOrder;
        FIXMessageUtil.fillFieldsFromExistingMessage(cancel,
                                                     template,
                                                     dataDictionary,
                                                     false);
        cancel.setField(new quickfix.field.OrigClOrdID(cancel.getString(quickfix.field.ClOrdID.FIELD)));
        cancel.setField(new quickfix.field.ClOrdID(inClOrdId));
        return cancel;
    }
    /**
     * Generate an execution report with the given attributes.
     *
     * @param inOrder a <code>Message</code> value
     * @param inExecutionType an <code>ExecutionType</code> value
     * @param inExecutionTransType an <code>ExecutionTransType</code> value
     * @return a <code>Message</code> value
     * @throws Exception if the message can not be generated
     */
    public Message generateExecutionReport(Message inOrder,
                                           ExecutionType inExecutionType,
                                           ExecutionTransType inExecutionTransType)
            throws Exception
    {
        Message executionReport = messageFactory.createMessage(MsgType.EXECUTION_REPORT);
        FIXMessageUtil.fillFieldsFromExistingMessage(executionReport,
                                                     inOrder,
                                                     dataDictionary,
                                                     false);
        if(inExecutionTransType != null) {
            if(dataDictionary.isField(quickfix.field.ExecTransType.FIELD)) {
                executionReport.setField(new ExecTransType(inExecutionTransType.getFIXValue()));
                if(inExecutionTransType == ExecutionTransType.Cancel || inExecutionTransType == ExecutionTransType.Correct) {
                    if(inOrder.isSetField(ClOrdID.FIELD)) {
                        executionReport.setField(new ExecRefID(inOrder.getString(ClOrdID.FIELD)));
                    } else {
                        executionReport.setField(new ExecRefID(PlatformServices.generateId()));
                    }
                }
            }
        }
        executionReport.setField(new quickfix.field.AvgPx(calculateAveragePrice()));
        executionReport.setField(new quickfix.field.CumQty(calculateCumQty()));
        executionReport.setField(new quickfix.field.LastPx(calculateLastPx()));
        executionReport.setField(new quickfix.field.LastQty(calculateLastQty()));
        executionReport.setField(new quickfix.field.LeavesQty(calculateLeavesQty()));
        executionReport.setField(new quickfix.field.ExecType(inExecutionType.getFIXValue()));
        OrderStatus orderStatus = getOrderStatus();
        executionReport.setField(new quickfix.field.OrdStatus(orderStatus.getFIXValue()));
        executionReport.setField(new quickfix.field.ExecID(PlatformServices.generateId()));
        executionReport.setField(new quickfix.field.OrderID(getOrderId()));
        executionReport.setField(new quickfix.field.TransactTime(new Date()));
        return executionReport;
    }
    /**
     * Create a new OrderData instance.
     *
     * @param inOrder a <code>Message</code> value
     * @throws FieldNotFound if the order data could not be constructed
     */
    public OrderData(Message inOrder)
            throws FieldNotFound
    {
        initialClOrdId = inOrder.getString(quickfix.field.ClOrdID.FIELD);
        clOrdId = initialClOrdId;
        orderQuantity = inOrder.getDecimal(quickfix.field.OrderQty.FIELD);
        side = Side.getInstanceForFIXValue(inOrder.getChar(quickfix.field.Side.FIELD));
        orderType = OrderType.getInstanceForFIXValue(inOrder.getChar(quickfix.field.OrdType.FIELD));
        orderStatus = OrderStatus.Unknown;
        if(orderType.isMarketOrder()) {
            orderPrice = null;
        } else {
            orderPrice = inOrder.getDecimal(quickfix.field.Price.FIELD);
        }
        FIXVersion fixVersion = FIXVersion.getFIXVersion(inOrder);
        dataDictionary = FIXMessageUtil.getDataDictionary(inOrder);
        messageFactory = fixVersion.getMessageFactory();
        initialOrder = inOrder;
        mostRecentOrder = initialOrder;
        mostRecentReport = null;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderData [").append(orderId).append("] at ").append(orderStatus);
        return builder.toString();
    }
    /**
     * holds initial order
     */
    private final Message initialOrder;
    /**
     * holds most recent order value (might be initial order single, cancel/replace, or cancel)
     */
    private volatile Message mostRecentOrder;
    /**
     * holds most recent report
     */
    private volatile Message mostRecentReport;
    /**
     * message factory to use for all messages
     */
    private final FIXMessageFactory messageFactory;
    /**
     * data dictionary to use for all messages
     */
    private final DataDictionary dataDictionary;
    /**
     * current order status
     */
    private volatile OrderStatus orderStatus;
    /**
     * collection of price/qty tuples
     */
    private final List<PriceQtyTuple> tuples = new ArrayList<>();
    /**
     * current order quantity value
     */
    private BigDecimal orderQuantity;
    /**
     * current order price value
     */
    private BigDecimal orderPrice;
    /**
     * current clordid value
     */
    private volatile String clOrdId;
    /**
     * initial clordid value, might or might not be the same as {@link #clOrdId current clordid}
     */
    private final String initialClOrdId;
    /**
     * order id to assign to execution reports, if appropriate
     */
    private final String orderId = UUID.randomUUID().toString();
    /**
     * order type value
     */
    private final OrderType orderType;
    /**
     * order side value
     */
    private final Side side;
}
