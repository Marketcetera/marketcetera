package org.marketcetera.systemmodel;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.MSymbol;

import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class OrderImpl
        implements Order
{
    private final String account;
    private final Map<String,String> customFields;
    private final OrderID orderID;
    private final OrdType orderType;
    private final BigDecimal price;
    private final BigDecimal quantity;
    private final Side side;
    private final MSymbol symbol;
    private final TimeInForce timeInForce;
    private final Date transactTime;
    /**
     * Create a new OrderImpl instance.
     *
     */
    public OrderImpl(MSymbol inSymbol,
                     Side inSide,
                     OrdType inOrderType,
                     BigDecimal inQuantity,
                     BigDecimal inPrice,
                     TimeInForce inTimeInForce,
                     String inAccount)
    {
        symbol = inSymbol;
        side = inSide;
        orderType = inOrderType;
        price = inPrice;
        quantity = inQuantity;
        transactTime = new Date();
        timeInForce = inTimeInForce;
        customFields = new HashMap<String,String>();
        account = inAccount;
        orderID = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getAccount()
     */
    @Override
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getCustomFields()
     */
    @Override
    public Map<String, String> getCustomFields()
    {
        return new HashMap<String,String>(customFields);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getID()
     */
    @Override
    public OrderID getID()
    {
        return orderID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getOrderType()
     */
    @Override
    public OrdType getOrderType()
    {
        return orderType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return price;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getQuantity()
     */
    @Override
    public BigDecimal getQuantity()
    {
        return quantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getSide()
     */
    @Override
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getSymbol()
     */
    @Override
    public MSymbol getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getTimeInForce()
     */
    @Override
    public TimeInForce getTimeInForce()
    {
        return timeInForce;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Order#getTransactTime()
     */
    @Override
    public Date getTransactTime()
    {
        return transactTime;
    }
}
