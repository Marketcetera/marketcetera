package org.marketcetera.tensorflow.converters;

import org.marketcetera.event.Event;
import org.marketcetera.tensorflow.TensorConverter;
import org.tensorflow.Tensor;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventConverter
        implements TensorConverter<Event>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#convert(java.lang.Object)
     */
    @Override
    public Tensor convert(Event inType)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#getType()
     */
    @Override
    public Class<Event> getType()
    {
        return Event.class;
    }
}
