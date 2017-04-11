package org.marketcetera.tensorflow.converters;

import org.marketcetera.tensorflow.TensorConverter;
import org.marketcetera.trade.Order;
import org.tensorflow.Tensor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderConverter
        implements TensorConverter<Order>
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
