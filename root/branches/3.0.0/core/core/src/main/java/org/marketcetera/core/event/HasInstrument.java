package org.marketcetera.core.event;

import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Has an {@link org.marketcetera.core.trade.Instrument} attribute.
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
