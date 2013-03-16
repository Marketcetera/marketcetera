package org.marketcetera.core.event;

import org.marketcetera.core.trade.ConvertibleBond;

/* $License$ */

/**
 * Has a {@link org.marketcetera.core.trade.ConvertibleBond} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
