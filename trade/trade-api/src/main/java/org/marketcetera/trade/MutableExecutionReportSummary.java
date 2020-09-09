package org.marketcetera.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Represents an execution report received as part of a trade flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ExecutionReportSummary.java 17962 2019-11-22 00:34:21Z colin $
 * @since $Release$
 */
public interface MutableExecutionReportSummary
        extends ExecutionReportSummary
{
    /**
     * Set the order id value.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    void setOrderID(OrderID inOrderId);
    /**
     * Set the original order id value.
     *
     * @param inOrigOrderId an <code>OrderID</code> value
     */
    void setOriginalOrderID(OrderID inOrigOrderId);
    /**
     * Set the security type value.
     *
     * @param inSecurityType a <code>SecurityType</code> value
     */
    void setSecurityType(SecurityType inSecurityType);
    /**
     * Set the symbol value.
     *
     * @param inSymbol a <code>String</code> value
     */
    void setSymbol(String inSymbol);
    /**
     * Set the expiry value.
     *
     * @param inExpiry a <code>String</code> value
     */
    void setExpiry(String inExpiry);
    /**
     * Set the strike price value.
     *
     * @param inStrikePrice a <code>BigDecimal</code> value
     */
    void setStrikePrice(BigDecimal inStrikePrice);
    /**
     * Set the option type value.
     *
     * @param inOptionType an <code>OptionType</code> value
     */
    void setOptionType(OptionType inOptionType);
    /**
     * Set the account value.
     *
     * @param inAccount a <code>String</code> value
     */
    void setAccount(String inAccount);
    /**
     * Set the rootOrderId value.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     */
    void setRootOrderID(OrderID inRootOrderId);
    /**
     * Set the side value.
     *
     * @param inSide a <code>Side</code> value
     */
    void setSide(Side inSide);
    /**
     * Set the cumQuantity value.
     *
     * @param inCumQuantity a <code>BigDecimal</code> value
     */
    void setCumulativeQuantity(BigDecimal inCumQuantity);
    /**
     * Set the effectiveCumQuantity value.
     *
     * @param inEffectiveCumQuantity a <code>BigDecimal</code> value
     */
    void setEffectiveCumulativeQuantity(BigDecimal inEffectiveCumQuantity);
    /**
     * Set the avgPrice value.
     *
     * @param inAvgPrice a <code>BigDecimal</code> value
     */
    void setAveragePrice(BigDecimal inAvgPrice);
    /**
     * Set the lastQuantity value.
     *
     * @param inLastQuantity a <code>BigDecimal</code> value
     */
    void setLastQuantity(BigDecimal inLastQuantity);
    /**
     * Set the lastPrice value.
     *
     * @param inLastPrice a <code>BigDecimal</code> value
     */
    void setLastPrice(BigDecimal inLastPrice);
    /**
     * Set the orderStatus value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    void setOrderStatus(OrderStatus inOrderStatus);
    /**
     * Set the execType value.
     *
     * @param inExecType an <code>ExecutionType</code> value
     */
    void setExecutionType(ExecutionType inExecType);
    /**
     * Set the sendingTime value.
     *
     * @param inSendingTime a <code>LocalDateTime</code> value
     */
    void setSendingTime(LocalDateTime inSendingTime);
    /**
     * Set the viewer value.
     *
     * @param inViewer a <code>User</code> value
     */
    void setViewer(User inViewer);
    /**
     * Set the actor value.
     *
     * @param inActor a <code>User</code> value
     */
    void setActor(User inActor);
    /**
     * Set the report value.
     *
     * @param inReport a <code>Report</code> value
     */
    void setReport(Report inReport);
    /**
     * Set the broker order id value.
     *
     * @param inBrokerOrderId an <code>OrderID</code> value
     */
    void setBrokerOrderId(OrderID inBrokerOrderId);
    /**
     * Set the execution id value.
     *
     * @param inExecutionId a <code>String</code> value
     */
    void setExecutionId(String inExecutionId);
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    void setInstrument(Instrument inInstrument);
    /**
     * Set the leavesQuantity value.
     *
     * @param inLeavesQuantity a <code>BigDecimal</code> value
     */
    void setLeavesQuantity(BigDecimal inLeavesQuantity);
    /**
     * Set the orderQuantity value.
     *
     * @param inOrderQuantity a <code>BigDecimal</code> value
     */
    void setOrderQuantity(BigDecimal inOrderQuantity);
    /**
     * Set the orderType value.
     *
     * @param inOrderType an <code>OrderType</code> value
     */
    void setOrderType(OrderType inOrderType);
    /**
     * Set the price value.
     *
     * @param inPrice a <code>BigDecimal</code> value
     */
    void setPrice(BigDecimal inPrice);
    /**
     * Set the time-in-force value.
     *
     * @param inTimeInForce a <code>TimeInForce</code> value
     */
    void setTimeInForce(TimeInForce inTimeInForce);
}
