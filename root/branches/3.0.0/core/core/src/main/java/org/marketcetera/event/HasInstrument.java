package org.marketcetera.event;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an {@link Instrument} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasInstrument.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: HasInstrument.java 16063 2012-01-31 18:21:55Z colin $")
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
