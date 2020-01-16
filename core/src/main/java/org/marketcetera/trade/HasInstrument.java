package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementer has an {@link Instrument} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasInstrument
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    Instrument getInstrument();
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    void setInstrument(Instrument inInstrument);
}
