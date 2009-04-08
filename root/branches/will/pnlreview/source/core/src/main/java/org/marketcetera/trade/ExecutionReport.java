package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Date;
import java.math.BigDecimal;

/* $License$ */
/**
 * Represents an execution report of an order. Instances of this
 * message can be created via
 * {@link Factory#createExecutionReport(quickfix.Message, BrokerID, Originator)}.
 * <p>
 * The enum attributes of this type have a null value, in case a value
 * is not specified for that attribute / field in the underlying FIX Message.
 * However, if the attribute / field has a value that does not have a
 * corresponding value in the Enum type, the sentinel value <code>Unknown</code>
 * is returned to indicate that the value is set but is not currently
 * expressible through the current API.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface ExecutionReport extends TradeMessage, ReportBase {

    /**
     * Time of execution / order creation in UTC.
     *
     * @return the time of execution / order creation.
     */
    public Date getTransactTime();

    /**
     * The status of this execution report.
     *
     * @return status of this execution report.
     */
    public ExecutionType getExecutionType();

    /**
     * Unique identifier of execution as assigned by the
     * Broker.
     *
     * @return the unique identifier of the execution.
     */
    public String getExecutionID();

    /**
     * The Side for this report.
     *
     * @return the side for the report.
     */
    public Side getSide();

    /**
     * Returns the symbol of the security being traded.
     *
     * @return the symbol of the security being traded.
     */
    public MSymbol getSymbol();

    /**
     * Quantity of shares bought or sold.
     *
     * @return quantity of shares bought or sold.
     */
    public BigDecimal getLastQuantity();

    /**
     * Price of last fill.
     *
     * @return the price of last fill.
     */
    public BigDecimal getLastPrice();

    /**
     * Market of execution for the last fill.
     *
     * @return Market of execution for the last fill.
     */
    public String getLastMarket();

    /**
     * Number of shares ordered.
     *
     * @return number of shares ordered.
     */
    public BigDecimal getOrderQuantity();

    /**
     * The quantity open for further execution.
     *  
     * @return the quantity open for further execution.
     */
    public BigDecimal getLeavesQuantity();

    /**
     * Total number of shares filled.
     *
     * @return total number of shares filled.
     */
    public BigDecimal getCumulativeQuantity();

    /**
     * Calculated average price of all fills on this order.
     *
     * @return calculated average price of all fills in this order.
     */
    public BigDecimal getAveragePrice();

    /**
     * The account for this report.
     *
     * @return the account for this report.
     */
    public String getAccount();

    /**
     * The order type of the order.
     *
     * @return the order type.
     */
    public OrderType getOrderType();

    /**
     * The Time in Force value for the order.
     *
     * @return the time in force value for the order.
     */
    public TimeInForce getTimeInForce();

    /**
     * Gets the order capacity value for this order.
     *
     * @return the order capacity value.
     */
    public OrderCapacity getOrderCapacity();
    
    /**
     * Gets the position effect for this order.
     *
     * @return the position effect value.
     */
    public PositionEffect getPositionEffect();
    
    /**
     * Returns whether this message is cancelable.
     * 
     * @return whether the message is cancelable.
     */
    public boolean isCancelable();
}
