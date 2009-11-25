package org.marketcetera.photon.strategy.engine.sa.ui.workbench;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Exposes services offered by this plug-in.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyAgentEngineWorkbenchUI {

    /**
     * The unique plug-in ID.
     */
    public final static String PLUGIN_ID = "org.marketcetera.photon.strategy.engine.sa.ui.workbench"; //$NON-NLS-1$

    /**
     * The connection details property page id.
     */
    public final static String STRATEGY_AGENT_CONNECTION_PROPERTY_PAGE_ID = "org.marketcetera.photon.strategy.engine.sa.ui.workbench.strategyAgentConnectionPropertyPage"; //$NON-NLS-1$

    private StrategyAgentEngineWorkbenchUI() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
