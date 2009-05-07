package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
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
	 * Log Messages
	 */
	static final I18NMessage0P MARKET_DATA_FEED_INVALID_PROVIDER_TYPE = new I18NMessage0P(LOGGER,
			"market_data_feed.invalid_provider_type"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_NOT_SINGLETON = new I18NMessage0P(
			LOGGER, "market_data_feed.not_singleton"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_NOT_EMITTER = new I18NMessage0P(
			LOGGER, "market_data_feed.not_emitter"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_INVALID_OBJECT_NAME = new I18NMessage0P(
			LOGGER, "market_data_feed.invalid_object_name"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_FEED_INVALID_INTERFACE = new I18NMessage0P(
			LOGGER, "market_data_feed.invalid_interface"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_FEED_INVALID_STATUS_NOTIFICATION = new I18NMessage1P(
			LOGGER, "market_data_feed.invalid_status_notification"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_FEED_FAILED_TO_DETERMINE_CAPABILITY = new I18NMessage1P(
			LOGGER, "market_data_feed.failed_to_determine_capability"); //$NON-NLS-1$
	
	static final I18NMessage1P MARKET_DATA_MANAGER_IGNORING_PROVIDER = new I18NMessage1P(
			LOGGER, "market_data_manager.ignoring_provider"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_FEED_START_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.feed_start_failed"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_MANAGER_FEED_RECONNECT_FAILED = new I18NMessage1P(
			LOGGER, "market_data_manager.feed_reconnect_failed"); //$NON-NLS-1$
	
	static final I18NMessage0P MARKET_DATA_RECEIVER_FACTORY_DESCRIPTION = new I18NMessage0P(
			LOGGER, "market_data_receiver_factory.description"); //$NON-NLS-1$
		
	static final I18NMessage0P MARKET_DATA_RECEIVER_NO_SUBSCRIBER = new I18NMessage0P(
			LOGGER, "market_data_receiver.no_subscriber"); //$NON-NLS-1$
	static final I18NMessage0P MARKET_DATA_RECEIVER_NO_REQUEST = new I18NMessage0P(
			LOGGER, "market_data_receiver.no_request"); //$NON-NLS-1$
	static final I18NMessage1P MARKET_DATA_RECEIVER_NO_SOURCE = new I18NMessage1P(
			LOGGER, "market_data_receiver.no_source"); //$NON-NLS-1$
	
	static final I18NMessage2P DATA_FLOW_MANAGER_UNEXPECTED_DATA = new I18NMessage2P(LOGGER,
			"data_flow_manager.unexpected_data"); //$NON-NLS-1$
	static final I18NMessage2P DATA_FLOW_MANAGER_UNEXPECTED_MESSAGE_ID = new I18NMessage2P(LOGGER,
			"data_flow_manager.unexpected_message_id"); //$NON-NLS-1$
	static final I18NMessage2P DATA_FLOW_MANAGER_EVENT_SYMBOL_MISMATCH = new I18NMessage2P(LOGGER,
			"data_flow_manager.event_symbol_mismatch"); //$NON-NLS-1$
	static final I18NMessage3P DATA_FLOW_MANAGER_CAPABILITY_UNSUPPORTED = new I18NMessage3P(LOGGER,
			"data_flow_manager.capability_unsupported"); //$NON-NLS-1$

}
