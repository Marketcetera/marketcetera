package org.marketcetera.event;

import org.marketcetera.trade.Currency;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an {@link Currency} attribute.
 *
 */
@ClassVersion("$Id: HasCurrency.java")
public interface HasCurrency
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Currency</code> value
     */
    @Override
    public Currency getInstrument();
}
