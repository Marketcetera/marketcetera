package org.marketcetera.core.event;

import org.marketcetera.core.trade.Option;

/* $License$ */

/**
 * Has an {@link Option} attribute.
 *
 * @version $Id$
 * @since 2.0.0
 */
public interface HasOption
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Option</code> value
     */
    @Override
    public Option getInstrument();
}
