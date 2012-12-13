package org.marketcetera.core.event;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Indicates that the implementing class has an underlying instrument.
 *
 * @version $Id$
 * @since 2.0.0
 */
public interface HasUnderlyingInstrument
{
    /**
     * Gets the underlying instrument.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getUnderlyingInstrument();
}
