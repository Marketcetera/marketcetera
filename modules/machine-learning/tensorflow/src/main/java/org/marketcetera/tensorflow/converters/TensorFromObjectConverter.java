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
     * Convert the given object to a <code>Tensor</code>.
     *
     * @param inType a <code>T</code> value
     * @return a <code>Tensor</code> value
     */
    Tensor convert(T inType);
    /**
     * Get the type of object this converter can handle.
     *
     * @return a <code>Class&lt;T&gt</code> value
     */
    Class<T> getType();
}
