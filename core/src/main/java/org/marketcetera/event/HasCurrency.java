package org.marketcetera.event;

import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an {@link Equity} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasEquity.java 16154 2012-07-14 16:34:05Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: HasEquity.java 16154 2012-07-14 16:34:05Z colin $")
public interface HasCurrency
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return an <code>Equity</code> value
     */
    @Override
    public Currency getInstrument();
}
