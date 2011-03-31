package org.marketcetera.systemmodel;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ReportSummary
{
    /**
     * Get the rootID value.
     *
     * @return a <code>OrderID</code> value
     */
    public OrderID getRootID();
    /**
     * Get the orderID value.
     *
     * @return a <code>OrderID</code> value
     */
    public OrderID getOrderID();
    /**
     * Get the origOrderID value.
     *
     * @return a <code>OrderID</code> value
     */
    public OrderID getOrigOrderID();
    /**
     * Get the securityType value.
     *
     * @return a <code>SecurityType</code> value
     */
    public SecurityType getSecurityType();
    /**
     * Get the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol();
    /**
     * Get the expiry value.
     *
     * @return a <code>String</code> value
     */
    public String getExpiry();
    /**
     * Get the strikePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getStrikePrice();
    /**
     * Get the optionType value.
     *
     * @return a <code>OptionType</code> value
     */
    public OptionType getOptionType();
    /**
     * Get the account value.
     *
     * @return a <code>String</code> value
     */
    public String getAccount();
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    public Side getSide();
    /**
     * Get the cumQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumulativeQuantity();
    /**
     * Get the avgPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAveragePrice();
    /**
     * Get the lastQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastQuantity();
    /**
     * Get the lastPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastPrice();
    /**
     * Get the orderStatus value.
     *
     * @return a <code>OrderStatus</code> value
     */
    public OrderStatus getOrderStatus();
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    public Date getSendingTime();
    /**
     * Get the owner value.
     *
     * @return a <code>User</code> value
     */
    public User getOwner();
}