package org.marketcetera.photon.strategy;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * UI related constants.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface StrategyUIConstants {

	/**
	 * The Photon Strategy perspective.
	 */
	static final String STRATEGY_PERSPECTIVE = "org.marketcetera.photon.StrategyPerspective"; //$NON-NLS-1$
	
	/**
	 * The Photon Strategies view, showing registered strategies and their state.
	 */
	static final String STRATEGIES_VIEW = "org.marketcetera.photon.strategy.StrategiesView"; //$NON-NLS-1$
	
	/**
	 * The Project Explorer for the Strategy perspective.
	 */
	static final String PROJECT_EXPLORER_VIEW = "org.marketcetera.photon.strategy.ProjectExplorer"; //$NON-NLS-1$
}
