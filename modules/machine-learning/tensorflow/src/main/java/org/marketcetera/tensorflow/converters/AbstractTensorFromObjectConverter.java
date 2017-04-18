package org.marketcetera.tensorflow.converters;

import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.tensorflow.Messages;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Provides common be4
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractTensorFromObjectConverter<T>
        implements TensorFromObjectConverter<T>
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getType().getSimpleName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.TensorFromObjectConverter#convert(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Tensor convert(Object inData)
    {
        if(getType().isAssignableFrom(inData.getClass())) {
            return doConvert((T)inData);
        }
        throw new StopDataFlowException(new I18NBoundMessage2P(Messages.INVALID_DATA_TYPE,
                                                               inData.getClass().getSimpleName(),
                                                               toString()));
    }
    /**
     * Perform the conversion from the given input to a tensor.
     *
     * @param inData a <code>T</code> value
     * @return a <code>Tensor</code> value
     */
    protected abstract Tensor doConvert(T inData);
}
