package org.marketcetera.photon.marketdata.ui;

import org.marketcetera.photon.internal.marketdata.ui.MarketDepthView;

/* $License$ */

/**
 * UI constants for this plugin.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketDataUI {

	/**
	 * The ID of the Market Depth view
	 */
	public static final String MARKET_DEPTH_VIEW_ID = MarketDepthView.ID; //$NON-NLS-1$
	
	/**
	 * The ID of the Show Market Depth command
	 */
	public static final String SHOW_MARKET_DEPTH_COMMAND_ID = "org.marketcetera.photon.marketdata.ui.commands.ShowMarketDepth"; //$NON-NLS-1$
	
	/**
	 * The symbol parameter for the Show Market Depth command
	 */
	public static final String SHOW_MARKET_DEPTH_COMMAND_SYMBOL_PARAMETER = "symbol"; //$NON-NLS-1$
	
	/**
	 * The source parameter for the Show Market Depth command
	 */
	public static final String SHOW_MARKET_DEPTH_COMMAND_SOURCE_PARAMETER = "source"; //$NON-NLS-1$
}
