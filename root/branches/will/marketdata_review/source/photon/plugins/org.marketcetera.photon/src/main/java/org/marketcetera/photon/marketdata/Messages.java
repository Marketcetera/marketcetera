package org.marketcetera.photon.marketdata;

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
@ClassVersion("$Id$")//$NON-NLS-1$
public interface Messages {
	/**
	 * The message provider.
	 */
	static final I18NMessageProvider PROVIDER = new I18NMessageProvider(
			"photon_marketdata"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

	/*
	 * UI Text
	 */
	public static I18NMessage0P MARKET_DATA_STATUS_NO_FEED_TOOLTIP = new I18NMessage0P(
			LOGGER, "market_data_status.no_feed.tooltip"); //$NON-NLS-1$
    public static I18NMessage1P MARKET_DATA_STATUS_ON_TOOLTIP = new I18NMessage1P(
			LOGGER, "market_data_status.on.tooltip"); //$NON-NLS-1$
    public static I18NMessage1P MARKET_DATA_STATUS_OFF_TOOLTIP = new I18NMessage1P(
			LOGGER, "market_data_status.off.tooltip"); //$NON-NLS-1$
    public static I18NMessage1P MARKET_DATA_STATUS_ERROR_TOOLTIP = new I18NMessage1P(
			LOGGER, "market_data_status.error.tooltip"); //$NON-NLS-1$

	/*
	 * Log Messages
	 */
	static final I18NMessage0P MARKET_DATA_FEED_INVALID_PROVIDER_TYPE = new I18NMessage0P(
			LOGGER, "market_data_feed.invalid_provider_type"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_NOT_SINGLETON = new I18NMessage0P(
			LOGGER, "market_data_feed.not_singleton"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_NOT_EMITTER = new I18NMessage0P(
			LOGGER, "market_data_feed.not_emitter"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_INVALID_OBJECT_NAME = new I18NMessage0P(
			LOGGER, "market_data_feed.invalid_object_name"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_INVALID_INTERFACE = new I18NMessage0P(
			LOGGER, "market_data_feed.invalid_interface"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_FEED_INVALID_STATUS = new I18NMessage1P(
			LOGGER, "market_data_feed.invalid_status"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_FEED_INVALID_STATUS_NOTIFICATION = new I18NMessage1P(
			LOGGER, "market_data_feed.invalid_status_notification"); //$NON-NLS-1$
	
	static final I18NMessage1P MARKET_DATA_MANAGER_IGNORING_PROVIDER = new I18NMessage1P(
			LOGGER, "market_data_manager.ignoring_provider"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_SUBSCRIBE_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.subscribe_failed"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_RECEIVER_START_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.receiver_start_failed"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_RECEIVER_STOP_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.receiver_stop_failed"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_DELETE_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.delete_failed"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_FEED_START_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.feed_start_failed"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.feed_reconnect_failed"); //$NON-NLS-1$
	
	static final I18NMessage0P MARKET_DATA_RECEIVER_FACTORY_DESCRIPTION = new I18NMessage0P(
			LOGGER, "market_data_receiver_factory.description"); //$NON-NLS-1$
		
	static final I18NMessage0P MARKET_DATA_RECEIVER_NO_CONFIG = new I18NMessage0P(
			LOGGER, "market_data_receiver.no_config"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_RECEIVER_NO_SUBSCRIBER = new I18NMessage0P(
			LOGGER, "market_data_receiver.no_subscriber"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_RECEIVER_NO_SYMBOL = new I18NMessage0P(
			LOGGER, "market_data_receiver.no_symbol"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_RECEIVER_NO_SOURCE = new I18NMessage1P(
			LOGGER, "market_data_receiver.no_source"); //$NON-NLS-1$
}
