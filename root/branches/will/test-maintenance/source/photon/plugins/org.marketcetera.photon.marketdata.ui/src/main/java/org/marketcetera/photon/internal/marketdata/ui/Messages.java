package org.marketcetera.photon.internal.marketdata.ui;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@ClassVersion("$Id$")
public interface Messages {
	/**
	 * The message provider.
	 */
	static final I18NMessageProvider PROVIDER = new I18NMessageProvider(
			"photon_marketdata_ui"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	public static I18NMessage0P MARKET_DATA_FEEDS_PREFERENCE_PAGE_DESCRIPTION = new I18NMessage0P(
			LOGGER, "market_data_feeds_preferences_page.description"); //$NON-NLS-1$
	public static I18NMessage0P ACTIVE_MARKET_DATA_FEED_LABEL = new I18NMessage0P(
			LOGGER, "active_market_data_feed.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_SYMBOL_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.symbol.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_SOURCE_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.source.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_TIME_COLUMN_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.time_column.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_MPID_COLUMN_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.mpid_column.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_SIZE_COLUMN_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.size_column.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_ASK_PRICE_COLUMN_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.ask_price_column.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_BID_PRICE_COLUMN_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.bid_price_column.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_LEVEL_2_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.level_2.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_TOTAL_VIEW_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.total_view.label"); //$NON-NLS-1$
	public static I18NMessage0P MARKET_DEPTH_VIEW_OPEN_BOOK_LABEL = new I18NMessage0P(
			LOGGER, "market_depth_view.open_book.label"); //$NON-NLS-1$

	/*
	 * Log Messages
	 */
	public static I18NMessage1P FEED_PREFERENCE_PAGE_INITIALIZER_MODULE_ERROR = new I18NMessage1P(
			LOGGER, "feed_preference_page_initializer.module_error"); //$NON-NLS-1$
	public static I18NMessage2P SHOW_MARKET_DEPTH_HANDLER_UNABLE_TO_SHOW_VIEW = new I18NMessage2P(
			LOGGER, "show_market_depth_handler.unable_to_show_view"); //$NON-NLS-1$
}
