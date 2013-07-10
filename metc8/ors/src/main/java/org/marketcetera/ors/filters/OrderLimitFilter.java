package org.marketcetera.ors.filters;

import java.math.BigDecimal;
import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Symbol;

@ClassVersion("$Id$$")
public class OrderLimitFilter
    implements OrderFilter
{
    private boolean mDisallowMarketOrders;
    private BigDecimal mMaxQuantityPerOrder;
    private BigDecimal mMaxNotionalPerOrder;
    private BigDecimal mMinPrice;
    private BigDecimal mMaxPrice;


    public void setDisallowMarketOrders
        (boolean disallowMarketOrders)
    {
        mDisallowMarketOrders=disallowMarketOrders;
    }

    public boolean getDisallowMarketOrders()
    {
        return mDisallowMarketOrders;
    }

    public void setMaxQuantityPerOrder
        (BigDecimal maxQuantityPerOrder)
    {
        mMaxQuantityPerOrder=maxQuantityPerOrder;
    }

    public BigDecimal getMaxQuantityPerOrder()
    {
        return mMaxQuantityPerOrder;
    }

    public void setMaxNotionalPerOrder
        (BigDecimal maxNotionalPerOrder)
    {
        mMaxNotionalPerOrder=maxNotionalPerOrder;
    }

    public BigDecimal getMaxNotionalPerOrder()
    {
        return mMaxNotionalPerOrder;
    }

    public void setMinPrice
        (BigDecimal minPrice)
    {
        mMinPrice=minPrice;
    }

    public BigDecimal getMinPrice()
    {
        return mMinPrice;
    }

    public void setMaxPrice
        (BigDecimal maxPrice)
    {
        mMaxPrice=maxPrice;
    }

    public BigDecimal getMaxPrice()
    {
        return mMaxPrice;
    }


    @Override
    public void assertAccepted
        (Message msg)
        throws CoreException
    {
        if (!FIXMessageUtil.isOrderSingle(msg) &&
            !FIXMessageUtil.isCancelReplaceRequest(msg)) {
            return;
        }
        String symbol=null;
        try {
            symbol=msg.getString(Symbol.FIELD);
        } catch (FieldNotFound ex) {
            Messages.NO_SYMBOL.warn(this,ex);
            // Later exceptions will use null symbol.
        }
        try {
            if (getDisallowMarketOrders() &&
                (OrdType.MARKET==msg.getChar(OrdType.FIELD))) {
                throw new CoreException
                    (new I18NBoundMessage1P
                     (Messages.MARKET_NOT_ALLOWED,symbol));
            }
        } catch (FieldNotFound ex) {
            Messages.NO_ORDER_TYPE.warn(this,ex);
        }
        BigDecimal p=null;
        try {
            p=new BigDecimal(msg.getString(Price.FIELD));
        } catch (FieldNotFound ex) {
            Messages.NO_PRICE.warn(this,ex);
        }
        BigDecimal q=null; 
        try {
            q=new BigDecimal(msg.getString(OrderQty.FIELD));
        } catch (FieldNotFound ex) {
            Messages.NO_QUANTITY.warn(this,ex);
        }
        if ((p==null) && (q==null)) {
            return;
        }
        if ((q!=null) &&
            (getMaxQuantityPerOrder()!=null) &&
            (getMaxQuantityPerOrder().compareTo(q)<0)) {
            throw new CoreException
                (new I18NBoundMessage3P
                 (Messages.MAX_QTY,q,getMaxQuantityPerOrder(),symbol));
        }
        if ((p!=null) &&
            (q!=null) &&
            (getMaxNotionalPerOrder()!=null)) {
            BigDecimal n=p.multiply(q);
            if (getMaxNotionalPerOrder().compareTo(n)<0) {
                throw new CoreException
                    (new I18NBoundMessage3P
                     (Messages.MAX_NOTIONAL,n,getMaxNotionalPerOrder(),symbol));
            }
        }
        if ((p!=null) &&
            (getMinPrice()!=null) &&
            (getMinPrice().compareTo(p)>0)) {
            throw new CoreException
                (new I18NBoundMessage3P
                 (Messages.MIN_PRICE,p,getMinPrice(),symbol));
        }
        if ((p!=null) &&
            (getMaxPrice()!=null) &&
            (getMaxPrice().compareTo(p)<0)) {
            throw new CoreException
                (new I18NBoundMessage3P
                 (Messages.MAX_PRICE,p,getMaxPrice(),symbol));
        }
    }
}
