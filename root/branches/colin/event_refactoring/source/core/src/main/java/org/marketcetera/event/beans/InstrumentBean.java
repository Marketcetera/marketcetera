package org.marketcetera.event.beans;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.Messages;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link HasInstrument}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public final class InstrumentBean
        implements Serializable, Messages
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Sets the instrument value.
     *
     * @param an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Performs validation of the attributes.
     *
     * @throws IllegalArgumentException if {@link #instrument} is <code>null</code>
     */
    public void validate()
    {
        if(instrument == null) {
            EventValidationServices.error(VALIDATION_NULL_INSTRUMENT);
        }
    }
    /**
     * the instrument value 
     */
    private Instrument instrument;
    private static final long serialVersionUID = 1L;
}
