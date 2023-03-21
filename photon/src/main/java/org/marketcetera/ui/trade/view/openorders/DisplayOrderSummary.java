package org.marketcetera.ui.trade.view.openorders;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.HasBrokerID;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.Side;
import org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType;

import quickfix.FieldNotFound;
import quickfix.InvalidMessage;

/* $License$ */

/**
 * Provides a flattened display model of {@link OrderSummary}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayOrderSummary
        implements FixMessageDisplayType,OrderSummary,HasBrokerID
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return fixMessage;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        return orderSummary.getOrderId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#setOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderId(OrderID inOrderId)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderStatus#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return orderSummary.getOrderStatus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getReport()
     */
    @Override
    public Report getReport()
    {
        return orderSummary.getReport();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getRootOrderId()
     */
    @Override
    public OrderID getRootOrderId()
    {
        return orderSummary.getRootOrderId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return orderSummary.getCumulativeQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getAccount()
     */
    @Override
    public String getAccount()
    {
        return orderSummary.getAccount();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return orderSummary.getBrokerId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getSide()
     */
    @Override
    public Side getSide()
    {
        return orderSummary.getSide();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return orderSummary.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getAveragePrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return orderSummary.getAveragePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return orderSummary.getLastQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return orderSummary.getLeavesQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return orderSummary.getOrderQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return orderSummary.getLastPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getOrderPrice()
     */
    @Override
    public BigDecimal getOrderPrice()
    {
        return orderSummary.getOrderPrice();
    }
    /*
     * @see org.marketcetera.trade.OrderSummary#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return orderSummary.getSendingTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getTransactTime()
     */
    @Override
    public Date getTransactTime()
    {
        return orderSummary.getTransactTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getActor()
     */
    @Override
    public User getActor()
    {
        return orderSummary.getActor();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getViewer()
     */
    @Override
    public User getViewer()
    {
        return orderSummary.getViewer();
    }
    /**
     * Get the trader name value.
     *
     * @return a <code>String</code> value
     */
    public String getTrader()
    {
        return orderSummary.getViewer().getName();
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /**
     * Get the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOriginalOrderId()
    {
        return originalOrderId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayOrderSummary [orderSummary=").append(orderSummary).append(", fixMessage=")
                .append(fixMessage).append("]");
        return builder.toString();
    }
    /**
     * Create a new DisplayOrderSummary instance.
     *
     * @param inOrderSummary an <code>OrderSummary</code> value
     */
    public DisplayOrderSummary(OrderSummary inOrderSummary)
    {
        orderSummary = inOrderSummary;
        try {
            fixMessage = new quickfix.Message(orderSummary.getReport().getFixMessage());
            if(fixMessage.isSetField(quickfix.field.SecurityExchange.FIELD)) {
                exchange = fixMessage.getString(quickfix.field.SecurityExchange.FIELD);
            } else {
                exchange = null;
            }
            if(fixMessage.isSetField(quickfix.field.OrigClOrdID.FIELD)) {
                originalOrderId = new OrderID(fixMessage.getString(quickfix.field.OrigClOrdID.FIELD));
            } else {
                originalOrderId = null;
            }
        } catch (InvalidMessage | FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * original order id value, may be <code>null</code>
     */
    private final OrderID originalOrderId;
    /**
     * exchange value, if available
     */
    private final String exchange;
    /**
     * order summary value
     */
    private final OrderSummary orderSummary;
    /**
     * fix message value
     */
    private final quickfix.Message fixMessage;
}
