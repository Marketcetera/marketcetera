package org.marketcetera.tensorflow.converters;

import org.tensorflow.Tensor;

/* $License$ */

/**
 * Converts a given data type to a {@link Tensor}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TensorFromObjectConverter<T>
{
    /**
     * Get the type of object this converter can handle.
     *
     * @return a <code>Class&lt;T&gt</code> value
     */
    Class<T> getType();
    /**
     * Convert the given object to a <code>Tensor</code>.
     *
     * @param inData an <code>Object</code> value
     * @return a <code>Tensor</code> value
     */
    Tensor convert(Object inData);
}
