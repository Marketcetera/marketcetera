package org.marketcetera.dao.impl;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Performs some initialization functions
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Initializer.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Initializer.java 82384 2012-07-20 19:09:59Z colin $")
public interface Initializer
{
    /**
     * Performs some initialization.
     */
    public void initialize();
}
