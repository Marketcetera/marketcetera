package org.marketcetera.systemmodel.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.systemmodel.Report;
import org.marketcetera.systemmodel.ReportSummary;
import org.marketcetera.systemmodel.User;
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
    /**
     * Create a new ReportSummaryImpl instance.
     *
     * @param inOrderId
     * @param inOrigOrderId
     * @param inSecurityType
     * @param inSymbol
     * @param inExpiry
     * @param inStrikePrice
     * @param inOptionType
     * @param inAccount
     * @param inSide
     * @param inCumQuantity
     * @param inAvgPrice
     * @param inLastQuantity
     * @param inLastPrice
     * @param inOrderStatus
     * @param inSendingTime
     * @param inOwner
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
     * 
     */
    private final OrderID rootID;
    /**
     * 
     */
    private final OrderID orderID;
    /**
     * 
     */
    private final OrderID origOrderID;
    /**
     * 
     */
    private final SecurityType securityType;
    /**
     * 
     */
    private final String symbol;
    /**
     * 
     */
    private final String expiry;
    /**
     * 
     */
    private final BigDecimal strikePrice;
    /**
     * 
     */
    private final OptionType optionType;
    /**
     * 
     */
    private final String account;
    /**
     * 
     */
    private final Side side;
    /**
     * 
     */
    private final BigDecimal cumQuantity;
    /**
     * 
     */
    private final BigDecimal avgPrice;
    /**
     * 
     */
    private final BigDecimal lastQuantity;
    /**
     * 
     */
    private final BigDecimal lastPrice;
    /**
     * 
     */
    private final OrderStatus orderStatus;
    /**
     * 
     */
    private final Date sendingTime;
    /**
     * 
     */
    private final User owner;
}
