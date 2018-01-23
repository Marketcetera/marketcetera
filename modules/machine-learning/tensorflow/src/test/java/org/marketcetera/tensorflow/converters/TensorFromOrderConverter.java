package org.marketcetera.tensorflow.converters;

import java.util.List;

import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderSingle;
import org.tensorflow.Tensor;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Translates {@link Order} objects to tensors. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFromOrderConverter
        extends AbstractTensorFromObjectConverter<Order>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#getType()
     */
    @Override
    public Class<Order> getType()
    {
        return Order.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.AbstractTensorFromObjectConverter#doConvert(java.lang.Object)
     */
    @Override
    protected Tensor doConvert(Order inType)
    {
        List<Float> primatives = Lists.newArrayList();
        if(inType instanceof OrderSingle) {
            OrderSingle order = (OrderSingle)inType;
            primatives.add((float)order.getInstrument().getFullSymbol().hashCode());
            primatives.add(order.getOrderType()==null?0f:order.getOrderType().ordinal());
            primatives.add(order.getTimeInForce()==null?0f:order.getTimeInForce().ordinal());
            primatives.add(order.getSide()==null?0f:order.getSide().ordinal());
            primatives.add(order.getPrice()==null?0f:order.getPrice().floatValue());
            primatives.add(order.getQuantity().floatValue());
        } else {
            throw new UnsupportedOperationException();
        }
        return Tensor.create(primatives.toArray(new Float[primatives.size()]));
    }
}
