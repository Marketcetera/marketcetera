package org.marketcetera.tensorflow;

import org.tensorflow.Tensor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TensorConverter<T>
{
    /**
     * 
     *
     * @param inType
     * @return
     */
    Tensor convert(T inType);
    /**
     * 
     *
     *
     * @return
     */
    Class<T> getType();
}
