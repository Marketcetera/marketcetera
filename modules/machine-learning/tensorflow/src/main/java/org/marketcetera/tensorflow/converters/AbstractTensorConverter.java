package org.marketcetera.tensorflow.converters;

/* $License$ */

/**
 * Provides common be4
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractTensorConverter<T>
        implements TensorConverter<T>
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getType().getSimpleName();
    }
}
