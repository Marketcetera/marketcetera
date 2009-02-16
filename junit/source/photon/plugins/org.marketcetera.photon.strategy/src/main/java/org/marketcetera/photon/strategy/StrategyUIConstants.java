package org.marketcetera.photon.strategy;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * UI related constants.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface StrategyUIConstants {

	/**
	 * The id of the Strategy perspective.
	 */
	static final String STRATEGY_PERSPECTIVE = "org.marketcetera.photon.StrategyPerspective"; //$NON-NLS-1$
	
	/**
	 * The id of the Strategies view, showing registered strategies and their state.
	 */
	static final String STRATEGIES_VIEW = "org.marketcetera.photon.strategy.StrategiesView"; //$NON-NLS-1$
	
	/**
	 * The id of the Trade Suggestions view, showing incoming trade suggestions.
	 */
	static final String TRADE_SUGGESTIONS_VIEW = "org.marketcetera.photon.strategy.TradeSuggestionsView"; //$NON-NLS-1$
	
	/**
	 * The id of the Project Explorer for the Strategy perspective.
	 */
	static final String PROJECT_EXPLORER_VIEW = "org.marketcetera.photon.strategy.ProjectExplorer"; //$NON-NLS-1$
	
	/**
	 * The extension used to identify ruby scripts.
	 */
	static final String RUBY_SCRIPT_EXTENSION = "rb"; //$NON-NLS-1$
	
}
