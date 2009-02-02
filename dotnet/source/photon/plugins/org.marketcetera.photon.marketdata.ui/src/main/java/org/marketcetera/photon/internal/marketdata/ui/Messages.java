package org.marketcetera.photon.internal.marketdata.ui;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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

	/*
	 * Log Messages
	 */
	public static I18NMessage1P FEED_PREFERENCE_PAGE_INITIALIZER_MODULE_ERROR = new I18NMessage1P(
			LOGGER, "feed_preference_page_initializer.module_error"); //$NON-NLS-1$
}
