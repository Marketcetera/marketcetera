package org.marketcetera.core.event;

import org.marketcetera.core.trade.Option;

/* $License$ */

/**
 * Has an {@link Option} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HasOption.java 16063 2012-01-31 18:21:55Z colin $
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
