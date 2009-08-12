package org.marketcetera.event;

import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Has an <code>MSymbol</code> attribute.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface HasSymbol
{
    /**
     * Gets the symbol value.
     *
     * @return an <code>MSymbol</code> value
     */
    public MSymbol getSymbol();
}
