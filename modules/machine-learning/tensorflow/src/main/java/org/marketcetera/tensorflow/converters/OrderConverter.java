package org.marketcetera.tensorflow.converters;

import org.marketcetera.trade.Order;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Translates {@link Order} objects to tensors. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderConverter
        extends AbstractTensorConverter<Order>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#convert(java.lang.Object)
     */
    @Override
    public Tensor convert(Order inType)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#getType()
     */
    @Override
    public Class<Order> getType()
    {
        return Order.class;
    }
}
