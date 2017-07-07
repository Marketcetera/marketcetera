package org.marketcetera.tensorflow.converters;

import org.marketcetera.event.Event;
import org.tensorflow.Tensor;

/* $License$ */

/**
 * Translates {@link Event} objects to tensors. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TensorFromEventConverter
        extends AbstractTensorFromObjectConverter<Event>
{
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.TensorConverter#getType()
     */
    @Override
    public Class<Event> getType()
    {
        return Event.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.tensorflow.converters.AbstractTensorFromObjectConverter#doConvert(java.lang.Object)
     */
    @Override
    protected Tensor doConvert(Event inData)
    {
        throw new UnsupportedOperationException(); // TODO
    }
}
