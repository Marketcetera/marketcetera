package org.marketcetera.persistence.hibernate;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.marketcetera.systemmodel.ReportSummary;
import org.marketcetera.systemmodel.User;
import org.marketcetera.systemmodel.persistence.PersistentEntity;
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
@Entity
@Table(name = "reportsummaries")
@ClassVersion("$Id$")
class PersistentReportSummary
        implements ReportSummary, PersistentEntity
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.PersistentEntity#getID()
     */
    @Override
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    public long getID()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.PersistentEntity#getLastUpdated()
     */
    @Override
    @Temporal(TemporalType.TIMESTAMP) //i18n_datetime when storing to / retrieving from database
    public Date getLastUpdated()
    {
        return lastUpdated;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.PersistentEntity#getUpdateCount()
     */
    @Override
    @Version
    public int getUpdateCount()
    {
        return updateCount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getRootID()
     */
    @Override
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name="value", column = @Column(name = "rootID", nullable = false))})
    @Column(nullable = false)
    public OrderID getRootID()
    {
        return rootId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getOrderID()
     */
    @Override
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name="value",column = @Column(name = "orderID", nullable = false))})
    public OrderID getOrderID()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getOrigOrderID()
     */
    @Override
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="value",column = @Column(name = "origOrderID", nullable = true))})
    public OrderID getOrigOrderID()
    {
        return origOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getSecurityType()
     */
    @Override
    @Column(nullable = false)
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getSymbol()
     */
    @Override
    @Column(nullable = false)
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getExpiry()
     */
    @Override
    @Column(nullable = true)
    public String getExpiry()
    {
        return expiry;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getStrikePrice()
     */
    @Override
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = true)
    public BigDecimal getStrikePrice()
    {
        return strikePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getOptionType()
     */
    @Override
    @Column(nullable = true)
    public OptionType getOptionType()
    {
        return optionType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getAccount()
     */
    @Override
    @Column(nullable = true)
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getSide()
     */
    @Override
    @Column(nullable = true)
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getCumulativeQuantity()
     */
    @Override
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    public BigDecimal getCumulativeQuantity()
    {
        return cumQty;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getAveragePrice()
     */
    @Override
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    public BigDecimal getAveragePrice()
    {
        return avgPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getLastQuantity()
     */
    @Override
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getLastPrice()
     */
    @Override
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getOrderStatus()
     */
    @Override
    @Column(nullable = false)
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getSendingTime()
     */
    @Override
    @Column(nullable = false)
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.ReportSummary#getOwner()
     */
    @Override
    @ManyToOne
    public PersistentUser getOwner()
    {
        return owner;
    }
    /**
     * Create a new PersistentReportSummary instance.
     *
     * @param inSummary
     */
    PersistentReportSummary(ReportSummary inSummary)
    {
        setAccount(inSummary.getAccount());
        setAveragePrice(inSummary.getAveragePrice());
        setCumulativeQuantity(inSummary.getCumulativeQuantity());
        setExpiry(inSummary.getExpiry());
        setLastPrice(inSummary.getLastPrice());
        setLastQuantity(inSummary.getLastQuantity());
        setOptionType(inSummary.getOptionType());
        setOrderID(inSummary.getOrderID());
        setOrderStatus(inSummary.getOrderStatus());
        setOrigOrderID(inSummary.getOrigOrderID());
        User owner = inSummary.getOwner();
        if(owner != null) {
            if(owner instanceof PersistentUser) {
                setOwner((PersistentUser)owner);
            } else {
                setOwner(new PersistentUser(owner));
            }
        }
        setRootID(inSummary.getRootID());
        setSecurityType(inSummary.getSecurityType());
        setSendingTime(inSummary.getSendingTime());
        setSide(inSummary.getSide());
        setStrikePrice(inSummary.getStrikePrice());
        setSymbol(inSummary.getSymbol());
    }
    /**
     * Sets the id value.
     *
     * @param a <code>long</code> value
     */
    @SuppressWarnings("unused")
    private void setID(long inId)
    {
        id = inId;
    }
    /**
     * Sets the updateCount value.
     *
     * @param an <code>int</code> value
     */
    @SuppressWarnings("unused")
    private void setUpdateCount(int inUpdateCount)
    {
        updateCount = inUpdateCount;
    }
    /**
     * Sets the lastUpdated value.
     *
     * @param a <code>Date</code> value
     */
    @SuppressWarnings("unused")
    private void setLastUpdated(Date inLastUpdated)
    {
        lastUpdated = inLastUpdated;
    }
    /**
     * Sets the rootId value.
     *
     * @param an <code>OrderID</code> value
     */
    private void setRootID(OrderID inRootId)
    {
        rootId = inRootId;
    }
    /**
     * Sets the orderId value.
     *
     * @param an <code>OrderID</code> value
     */
    private void setOrderID(OrderID inOrderId)
    {
        orderId = inOrderId;
    }
    /**
     * Sets the origOrderId value.
     *
     * @param an <code>OrderID</code> value
     */
    private void setOrigOrderID(OrderID inOrigOrderId)
    {
        origOrderId = inOrigOrderId;
    }
    /**
     * Sets the securityType value.
     *
     * @param a <code>SecurityType</code> value
     */
    private void setSecurityType(SecurityType inSecurityType)
    {
        securityType = inSecurityType;
    }
    /**
     * Sets the symbol value.
     *
     * @param a <code>String</code> value
     */
    private void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /**
     * Sets the expiry value.
     *
     * @param a <code>String</code> value
     */
    private void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /**
     * Sets the strikePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    private void setStrikePrice(BigDecimal inStrikePrice)
    {
        strikePrice = inStrikePrice;
    }
    /**
     * Sets the optionType value.
     *
     * @param an <code>OptionType</code> value
     */
    private void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /**
     * Sets the account value.
     *
     * @param a <code>String</code> value
     */
    private void setAccount(String inAccount)
    {
        account = inAccount;
    }
    /**
     * Sets the side value.
     *
     * @param a <code>Side</code> value
     */
    private void setSide(Side inSide)
    {
        side = inSide;
    }
    /**
     * Sets the cumQty value.
     *
     * @param a <code>BigDecimal</code> value
     */
    private void setCumulativeQuantity(BigDecimal inCumQty)
    {
        cumQty = inCumQty;
    }
    /**
     * Sets the avgPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    private void setAveragePrice(BigDecimal inAvgPrice)
    {
        avgPrice = inAvgPrice;
    }
    /**
     * Sets the lastQuantity value.
     *
     * @param a <code>BigDecimal</code> value
     */
    private void setLastQuantity(BigDecimal inLastQuantity)
    {
        lastQuantity = inLastQuantity;
    }
    /**
     * Sets the lastPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    private void setLastPrice(BigDecimal inLastPrice)
    {
        lastPrice = inLastPrice;
    }
    /**
     * Sets the orderStatus value.
     *
     * @param an <code>OrderStatus</code> value
     */
    private void setOrderStatus(OrderStatus inOrderStatus)
    {
        orderStatus = inOrderStatus;
    }
    /**
     * Sets the sendingTime value.
     *
     * @param a <code>Date</code> value
     */
    private void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /**
     * Sets the owner value.
     *
     * @param a <code>PersistentUser</code> value
     */
    private void setOwner(PersistentUser inOwner)
    {
        owner = inOwner;
    }
    /**
     * 
     */
    private long id;
    /**
     * 
     */
    private int updateCount;
    /**
     * 
     */
    private Date lastUpdated;
    /**
     * 
     */
    private OrderID rootId;
    /**
     * 
     */
    private OrderID orderId;
    /**
     * 
     */
    private OrderID origOrderId;
    /**
     * 
     */
    private SecurityType securityType;
    /**
     * 
     */
    private String symbol;
    /**
     * 
     */
    private String expiry;
    /**
     * 
     */
    private BigDecimal strikePrice;
    /**
     * 
     */
    private OptionType optionType;
    /**
     * 
     */
    private String account;
    /**
     * 
     */
    private Side side;
    /**
     * 
     */
    private BigDecimal cumQty;
    /**
     * 
     */
    private BigDecimal avgPrice;
    /**
     * 
     */
    private BigDecimal lastQuantity;
    /**
     * 
     */
    private BigDecimal lastPrice;
    /**
     * 
     */
    private OrderStatus orderStatus;
    /**
     * 
     */
    private Date sendingTime;
    /**
     * 
     */
    private PersistentUser owner;
    /**
     * The scale used for storing all decimal values.
     */
    private static final int DECIMAL_SCALE = 5;
    /**
     * The precision used for storing all decimal values.
     */
    private static final int DECIMAL_PRECISION = 15;
    private static final long serialVersionUID = 1L;
}
