package org.marketcetera.event;

import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an {@link Equity} attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface HasEquity
        extends HasInstrument
{
    /**
     * Gets the Equity value.
     *
     * @return an <code>Equity</code> value
     */
    public Equity getEquity();
}
