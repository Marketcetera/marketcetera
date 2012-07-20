package org.marketcetera.dao.impl;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Performs some initialization functions
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Initializer
{
    /**
     * Performs some initialization.
     */
    public void initialize();
}
