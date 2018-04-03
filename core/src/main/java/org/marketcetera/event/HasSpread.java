package org.marketcetera.event;

import org.marketcetera.trade.Spread;

/* $License$ */

/**
 * Has a {@link Spread} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSpread
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return a <code>Spread</code> value
     */
    @Override
    Spread getInstrument();
}
