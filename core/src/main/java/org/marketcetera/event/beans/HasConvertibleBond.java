package org.marketcetera.event.beans;

import org.marketcetera.event.HasInstrument;
import org.marketcetera.trade.ConvertibleBond;

/* $License$ */

/**
 * Has a {@link org.marketcetera.trade.ConvertibleBond} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasConvertibleSecurity.java 16598 2013-06-25 13:27:58Z colin $
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
