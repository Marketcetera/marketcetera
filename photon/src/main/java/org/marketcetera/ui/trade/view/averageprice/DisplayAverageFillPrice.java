package org.marketcetera.ui.trade.view.averageprice;

import java.math.BigDecimal;

import org.marketcetera.trade.AverageFillPrice;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.SimpleAverageFillPrice;
import org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/* $License$ */

/**
 * Provides a display POJO version of {@link AverageFillPrice}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayAverageFillPrice
        extends SimpleAverageFillPrice
        implements AverageFillPrice,FixMessageDisplayType
{
    /**
     * Create a new DisplayAverageFillPrice instance.
     *
     * @param inClientClazz an <code>AverageFillPrice</code> value
     */
    public DisplayAverageFillPrice(AverageFillPrice inClientClazz)
    {
        super(inClientClazz.getInstrument(),
              inClientClazz.getSide(),
              inClientClazz.getCumulativeQuantity(),
              inClientClazz.getAveragePrice());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasOrderId#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
    /**
     * Get the instrument symbol value.
     *
     * @return a <code>String<code> value
     */
    public String getSymbol()
    {
        return getInstrument() == null ? "" : getInstrument().getFullSymbol();
    }
    /**
     * Get the cumulativeQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumQty()
    {
        return getCumulativeQuantity();
    }
    /**
     * Get the averagePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAvgPx()
    {
        return getAveragePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType#isFillProperty()
     */
    @Override
    public BooleanProperty isFillProperty()
    {
        return fillProperty;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.trade.executionreport.view.FixMessageDisplayType#isCancelProperty()
     */
    @Override
    public BooleanProperty isCancelProperty()
    {
        return cancelProperty;
    }
    /**
     * tracks whether this report was just canceled
     */
    private final BooleanProperty cancelProperty = new SimpleBooleanProperty(false);
    /**
     * tracks whether this report was just filled
     */
    private final BooleanProperty fillProperty = new SimpleBooleanProperty(false);
}
