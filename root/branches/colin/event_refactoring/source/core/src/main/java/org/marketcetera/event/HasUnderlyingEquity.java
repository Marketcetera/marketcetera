package org.marketcetera.event;

import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementing class has an underlying equity.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface HasUnderlyingEquity
{
    /**
     * Gets the underlying equity.
     *
     * @return an <code>Equity</code> value
     */
    public Equity getUnderlyingEquity();
}
