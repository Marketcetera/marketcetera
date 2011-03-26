package org.marketcetera.systemmodel.persistence.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.persistence.*;

import org.marketcetera.systemmodel.HasOwner;
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
@Table(name = "orders")
@ClassVersion("$Id$")
public class PersistentOrder
        implements OrderBase, PersistentEntity, HasOwner
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getID()
     */
    @Override
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    public long getID()
    {
        return id;
    }
    /**
     * 
     *
     *
     * @param inId
     */
    @SuppressWarnings("unused")
    private void setID(long inId)
    {
        id = inId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getLastUpdated()
     */
    @Override
    @Temporal(TemporalType.TIMESTAMP) //i18n_datetime when storing to / retrieving from database
    public Date getLastUpdated()
    {
        return lastUpdated;
    }
    /**
     * 
     *
     *
     * @param inLastUpdated
     */
    @SuppressWarnings("unused")
    private void setLastUpdated(Date inLastUpdated)
    {
        lastUpdated = inLastUpdated;
    }
    /*
     * (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getUpdateCount()
     */
    @Override
    @Version
    public int getUpdateCount()
    {
        return updateCount;
    }
    /**
     * 
     *
     *
     * @param inUpdateCount
     */
    @SuppressWarnings("unused")
    private void setUpdateCount(int inUpdateCount)
    {
        updateCount = inUpdateCount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Order#getSecurityType()
     */
    @Override
    @Column(nullable = false)
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /**
     * 
     *
     *
     * @param inSecurityType
     */
    @SuppressWarnings("unused")
    private void setSecurityType(SecurityType inSecurityType)
    {
        securityType = inSecurityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Order#getBrokerID()
     */
    @Override
    @Transient
    public BrokerID getBrokerID()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Order#setBrokerID(org.marketcetera.trade.BrokerID)
     */
    @Override
    public void setBrokerID(BrokerID inBrokerID)
    {
        brokerId = inBrokerID;
    }
    /**
     * 
     *
     *
     * @return
     */
    @SuppressWarnings("unused")
    @Column(name = "brokerID", nullable = false)
    private String getBrokerIdAsString()
    {
        return getBrokerID() == null ? null : getBrokerID().toString();
    }
    /**
     * 
     *
     *
     * @param inValue
     */
    @SuppressWarnings("unused")
    private void setBrokerIdAsString(String inValue)
    {
        setBrokerID(inValue == null ? null : new BrokerID(inValue));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getOrderID()
     */
    @Override
    @Transient
    public OrderID getOrderID()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setOrderID(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderID(OrderID inOrderID)
    {
        orderId = inOrderID;
    }
    /**
     * 
     *
     *
     * @return
     */
    @SuppressWarnings("unused")
    @Column(name = "orderID", nullable = false, unique = true)
    private String getOrderIDAsString()
    {
        return getOrderID() == null ? null : getOrderID().getValue();
    }
    /**
     * 
     *
     *
     * @param inValue
     */
    @SuppressWarnings("unused")
    private void setOrderIDAsString(String inValue)
    {
        setOrderID(inValue == null ? null : new OrderID(inValue));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getSide()
     */
    @Override
    @Column(nullable = false)
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setSide(org.marketcetera.trade.Side)
     */
    @Override
    public void setSide(Side inSide)
    {
        side = inSide;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getInstrument()
     */
    @Override
    @Transient
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setInstrument(org.marketcetera.trade.Instrument)
     */
    @Override
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * 
     *
     *
     * @return
     */
    @SuppressWarnings("unused")
    @Column(name = "instrument", nullable = false)
    private String getInstrumentAsString()
    {
        return getInstrument() == null ? null : getInstrument().getSymbol();
    }
    /**
     * 
     *
     *
     * @param inInstrument
     */
    @SuppressWarnings("unused")
    private void setInstrumentAsString(String inInstrument)
    {
        setInstrument(inInstrument == null ? null : new Equity(inInstrument)); // TODO add symbol resolver
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getQuantity()
     */
    @Override
    @Column(precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE, nullable = false)
    public BigDecimal getQuantity()
    {
        return quantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setQuantity(java.math.BigDecimal)
     */
    @Override
    public void setQuantity(BigDecimal inQuantity)
    {
        quantity = inQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getCustomFields()
     */
    @Override
    @Transient // TODO
    public Map<String,String> getCustomFields()
    {
        return customFields;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setCustomFields(java.util.Map)
     */
    @Override
    public void setCustomFields(Map<String,String> inCustomFields)
    {
        customFields = inCustomFields;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getAccount()
     */
    @Override
    @Column(nullable = true)
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setAccount(java.lang.String)
     */
    @Override
    public void setAccount(String inAccount)
    {
        account = inAccount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#getText()
     */
    @Override
    @Column(nullable = true)
    public String getText()
    {
        return text;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderBase#setText(java.lang.String)
     */
    @Override
    public void setText(String inText)
    {
        text = inText;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.HasOwner#getOwner()
     */
    @Override
    @ManyToOne
    public PersistentUser getOwner()
    {
        return owner;
    }
    /**
     * 
     *
     *
     * @param inOwner
     */
    @SuppressWarnings("unused")
    private void setOwner(PersistentUser inOwner)
    {
        owner = inOwner;
    }
    /**
     * Create a new PersistentOrder instance.
     *
     * @param inOrder an <code>Order</code> value
     */
    PersistentOrder(Order inOrder)
    {
        securityType = inOrder.getSecurityType();
        brokerId = inOrder.getBrokerID();
        if(inOrder instanceof OrderBase) {
            OrderBase order = (OrderBase)inOrder;
            orderId = order.getOrderID();
            side = order.getSide();
            instrument = order.getInstrument();
            quantity = order.getQuantity();
            customFields = order.getCustomFields();
            account = order.getAccount();
            text = order.getText();
            owner = null; // TODO figure out how to set the owner of the order - this is necessary for the owner of the ERs that come back
        } else {
            throw new UnsupportedOperationException("Order must be an OrderBase");
        }
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
    private SecurityType securityType;
    /**
     * 
     */
    private BrokerID brokerId;
    /**
     * 
     */
    private OrderID orderId;
    /**
     * 
     */
    private Side side;
    /**
     * 
     */
    private Instrument instrument;
    /**
     * 
     */
    private BigDecimal quantity;
    /**
     * 
     */
    private Map<String,String> customFields;
    /**
     * 
     */
    private String account;
    /**
     * 
     */
    private String text;
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
