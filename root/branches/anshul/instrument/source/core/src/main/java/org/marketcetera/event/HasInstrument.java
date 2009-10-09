package org.marketcetera.event;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an {@link Instrument} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument();
}
