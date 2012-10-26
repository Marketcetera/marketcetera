package org.marketcetera.core.event;

import org.marketcetera.api.systemmodel.instruments.Equity;

/* $License$ */

/**
 * Has an {@link org.marketcetera.core.trade.EquityImpl} attribute.
 *
 * @version $Id: HasEquity.java 16063 2012-01-31 18:21:55Z colin $
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
