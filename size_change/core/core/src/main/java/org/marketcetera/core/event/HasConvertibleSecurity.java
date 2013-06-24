package org.marketcetera.core.event;

import org.marketcetera.core.trade.ConvertibleSecurity;

/* $License$ */

/**
 * Has a {@link org.marketcetera.core.trade.ConvertibleSecurity} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasConvertibleSecurity
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return a <code>ConvertibleSecurity</code> value
     */
    @Override
    public ConvertibleSecurity getInstrument();
	}
