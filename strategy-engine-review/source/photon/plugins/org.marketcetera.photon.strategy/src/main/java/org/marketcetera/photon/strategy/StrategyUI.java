package org.marketcetera.photon.strategy;

import org.eclipse.swt.widgets.Display;
import org.marketcetera.photon.internal.strategy.Activator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides API access to this bundle's functionality.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class StrategyUI {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "org.marketcetera.photon.strategy"; //$NON-NLS-1$

    /**
     * The id of the Strategy perspective.
     */
    public static final String STRATEGY_PERSPECTIVE = "org.marketcetera.photon.StrategyPerspective"; //$NON-NLS-1$

    /**
     * The id of the Trade Suggestions view, showing incoming trade suggestions.
     */
    public static final String TRADE_SUGGESTIONS_VIEW = "org.marketcetera.photon.strategy.TradeSuggestionsView"; //$NON-NLS-1$

    /**
     * The id of the Project Explorer for the Strategy perspective.
     */
    public static final String PROJECT_EXPLORER_VIEW = "org.marketcetera.photon.strategy.ProjectExplorer"; //$NON-NLS-1$

    /**
     * Initializes the list of strategy engines.
     * 
     * @param display
     *            the display on which to create the engines
     */
    public static void initializeStrategyEngines(final Display display) {
        Activator.initEngines(display);
    }
    
    private StrategyUI() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
