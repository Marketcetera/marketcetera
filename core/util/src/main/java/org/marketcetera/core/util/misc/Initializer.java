package org.marketcetera.core.util.misc;

import org.marketcetera.api.attributes.ClassVersion;

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
