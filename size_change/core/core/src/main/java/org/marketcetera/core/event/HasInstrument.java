package org.marketcetera.core.event;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Has an {@link org.marketcetera.core.trade.Instrument} attribute.
 *
 * @version $Id$
 * @since 1.5.0
 */
public interface HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument();
    /**
     * Gets the instrument value as a string.
     *
     * @return a <code>String</code> value
     */
    public String getInstrumentAsString();
}
