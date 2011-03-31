package org.marketcetera.systemmodel.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.systemmodel.ReportSummary;
import org.marketcetera.systemmodel.User;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link ReportSummary} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class ReportSummaryImpl
        implements ReportSummary
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getRootID()
     */
    @Override
    public OrderID getRootID()
    {
        return rootID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return orderID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getOrigOrderID()
     */
    @Override
    public OrderID getOrigOrderID()
    {
        return origOrderID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return expiry;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getStrikePrice()
     */
    @Override
    public BigDecimal getStrikePrice()
    {
        return strikePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return optionType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getAccount()
     */
    @Override
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getSide()
     */
    @Override
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getCumQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return cumQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getAvgPrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return avgPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.impl.ReportSummary#getOwner()
     */
    @Override
    public User getOwner()
    {
        return owner;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("ReportSummary [rootID=%s, orderID=%s, origOrderID=%s, securityType=%s, symbol=%s, expiry=%s, strikePrice=%s, optionType=%s, account=%s, side=%s, cumQuantity=%s, avgPrice=%s, lastQuantity=%s, lastPrice=%s, orderStatus=%s, sendingTime=%s, owner=%s]",
                             rootID,
                             orderID,
                             origOrderID,
                             securityType,
                             symbol,
                             expiry,
                             strikePrice,
                             optionType,
                             account,
                             side,
                             cumQuantity,
                             avgPrice,
                             lastQuantity,
                             lastPrice,
                             orderStatus,
                             sendingTime,
                             owner);
    }
    /**
     * Create a new ReportSummaryImpl instance.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @param inOrigOrderId an <code>OrderID</code> value
     * @param inSecurityType a <code>SecurityType</code> value
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>BigDecimal</code> value
     * @param inOptionType an <code>OptionType</code> value
     * @param inAccount a <code>String</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumQuantity a <code>BigDecimal</code> value
     * @param inAvgPrice a <code>BigDecimal</code> value
     * @param inLastQuantity a <code>BigDecimal</code> value
     * @param inLastPrice a <code>BigDecimal</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inSendingTime a <code>Date</code> value
     * @param inOwner a <code>User</code> value
     */
    ReportSummaryImpl(OrderID inOrderId,
                      OrderID inOrigOrderId,
                      SecurityType inSecurityType,
                      String inSymbol,
                      String inExpiry,
                      BigDecimal inStrikePrice,
                      OptionType inOptionType,
                      String inAccount,
                      Side inSide,
                      BigDecimal inCumQuantity,
                      BigDecimal inAvgPrice,
                      BigDecimal inLastQuantity,
                      BigDecimal inLastPrice,
                      OrderStatus inOrderStatus,
                      Date inSendingTime,
                      User inOwner)
    {
        rootID = inOrderId; // TODO
        orderID = inOrderId;
        origOrderID = inOrigOrderId;
        securityType = inSecurityType;
        symbol = inSymbol;
        expiry = inExpiry;
        strikePrice = inStrikePrice;
        optionType = inOptionType;
        account = inAccount;
        side = inSide;
        cumQuantity = inCumQuantity;
        avgPrice = inAvgPrice;
        lastQuantity = inLastQuantity;
        lastPrice = inLastPrice;
        orderStatus = inOrderStatus;
        sendingTime = inSendingTime;
        owner = inOwner;
    }
    /**
     * Create a new ReportSummaryImpl instance.
     */
    @SuppressWarnings("unused")
    private ReportSummaryImpl()
    {
        rootID = null;
        orderID = null;
        origOrderID = null;
        securityType = null;
        symbol = null;
        expiry = null;
        strikePrice = null;
        optionType = null;
        account = null;
        side = null;
        cumQuantity = null;
        avgPrice = null;
        lastQuantity = null;
        lastPrice = null;
        orderStatus = null;
        sendingTime = null;
        owner = null;
    }
    /**
     * the rootID of the execution report family
     */
    private final OrderID rootID;
    /**
     * the underlying orderID of the execution report
     */
    private final OrderID orderID;
    /**
     * the original orderID of the execution report or <code>null</code>
     */
    private final OrderID origOrderID;
    /**
     * the security type of the execution report
     */
    private final SecurityType securityType;
    /**
     * the symbol of the execution report
     */
    private final String symbol;
    /**
     * the expiry of the execution report
     */
    private final String expiry;
    /**
     * the strike price of the execution report
     */
    private final BigDecimal strikePrice;
    /**
     * the option type of the execution report
     */
    private final OptionType optionType;
    /**
     * the account of the execution report
     */
    private final String account;
    /**
     * the side of the execution report
     */
    private final Side side;
    /**
     * the cumulative quantity of the execution report
     */
    private final BigDecimal cumQuantity;
    /**
     * the average price of the execution report
     */
    private final BigDecimal avgPrice;
    /**
     * the last quantity of the execution report
     */
    private final BigDecimal lastQuantity;
    /**
     * the last price of the execution report
     */
    private final BigDecimal lastPrice;
    /**
     * the order status of the execution report
     */
    private final OrderStatus orderStatus;
    /**
     * the sending time of the execution report
     */
    private final Date sendingTime;
    /**
     * the owner of the execution report
     */
    private final User owner;
}
