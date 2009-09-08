package org.marketcetera.photon.strategy.engine;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StrategyEnginesCore {

    public static final String PLUGIN_ID = "org.marketcetera.photon.strategy.engine"; //$NON-NLS-1$

    private StrategyEnginesCore() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
