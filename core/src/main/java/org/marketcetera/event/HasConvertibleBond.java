package org.marketcetera.event;

import org.marketcetera.event.HasInstrument;
import org.marketcetera.trade.ConvertibleBond;

/* $License$ */

/**
 * Has a {@link org.marketcetera.trade.ConvertibleBond} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public interface HasConvertibleBond
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return a <code>ConvertibleBond</code> value
     */
    @Override
    public ConvertibleBond getInstrument();
}
