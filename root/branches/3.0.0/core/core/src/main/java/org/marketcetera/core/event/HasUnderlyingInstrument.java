package org.marketcetera.core.event;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Indicates that the implementing class has an underlying instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasUnderlyingInstrument.java 16063 2012-01-31 18:21:55Z colin $
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
