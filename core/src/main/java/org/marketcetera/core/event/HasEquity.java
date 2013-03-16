package org.marketcetera.core.event;

import org.marketcetera.core.trade.Equity;

/* $License$ */

/**
 * Has an {@link org.marketcetera.core.trade.Equity} attribute.
 *
 * @version $Id$
 * @since 2.0.0
 */
public interface HasEquity
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Equity</code> value
     */
    @Override
    public Equity getInstrument();
}
