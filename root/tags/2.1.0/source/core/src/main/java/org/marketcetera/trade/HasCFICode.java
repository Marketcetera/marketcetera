package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the implementer can produce a CFI code.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@ClassVersion("$Id$")
public interface HasCFICode
{
    /**
     * Gets the CFI Code.
     *
     * @return a <code>char</code> value
     */
    public char getCfiCode();
}
