package org.marketcetera.event.beans;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.Messages;
import org.marketcetera.event.util.EventValidationServices;
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
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instrument == null) ? 0 : instrument.getSymbol().hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InstrumentBean other = (InstrumentBean) obj;
        if (instrument == null) {
            if (other.instrument != null)
                return false;
        } else if (!instrument.equals(other.instrument))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("InstrumentBean [instrument=").append(instrument).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        return builder.toString();
    }
    /**
     * the instrument value 
     */
    private Instrument instrument;
    private static final long serialVersionUID = 1L;
}
