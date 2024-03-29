package org.marketcetera.marketdata.core;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;


/* $License$ */

/**
 * Provides messages for the <code>marketdata</code> packages.
 *
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdata_core", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * The messages.
     */
    static final I18NMessage1P UNKNOWN_MARKETDATA_CONTENT = new I18NMessage1P(LOGGER,"unknown_marketdata_content"); //$NON-NLS-1$
    static final I18NMessage2P UNSUPPORTED_MARKETDATA_CONTENT = new I18NMessage2P(LOGGER,"unsupported_marketdata_content"); //$NON-NLS-1$
    static final I18NMessage0P MARKETDATA_REQUEST_FAILED = new I18NMessage0P(LOGGER,"marketdata_request_failed"); //$NON-NLS-1$
    static final I18NMessage2P EVENT_NOTIFICATION_FAILED = new I18NMessage2P(LOGGER,"event_notification_failed"); //$NON-NLS-1$
    static final I18NMessage0P UNABLE_TO_ACQUIRE_LOCK = new I18NMessage0P(LOGGER,"unable_to_acquire_lock"); //$NON-NLS-1$
    static final I18NMessage0P NO_SYMBOLS_OR_UNDERLYING_SYMBOLS = new I18NMessage0P(LOGGER,"no_symbols_or_underlying_symbols"); //$NON-NLS-1$
    static final I18NMessage0P NO_CONTENT = new I18NMessage0P(LOGGER,"no_content"); //$NON-NLS-1$
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"provider_description"); //$NON-NLS-1$
    static final I18NMessage2P CONTENT_REQUIRES_QUOTE_EVENTS = new I18NMessage2P(LOGGER,"content_requires_quote_events"); //$NON-NLS-1$
    static final I18NMessage2P PROVIDER_REPORTS_STATUS = new I18NMessage2P(LOGGER,"provider_reports_status"); //$NON-NLS-1$
    static final I18NMessage1P JMX_REGISTRATION_ERROR = new I18NMessage1P(LOGGER,"jmx_registration_error"); //$NON-NLS-1$
    static final I18NMessage2P UNABLE_TO_REQUEST_MARKETDATA = new I18NMessage2P(LOGGER,"unable_to_request_marketdata"); //$NON-NLS-1$
    static final I18NMessage1P MARKETDATA_REQUEST_INTERRUPTED = new I18NMessage1P(LOGGER,"marketdata_request_interrupted"); //$NON-NLS-1$
    static final I18NMessage0P MARKETDATA_NEXUS_CONNECTION_LOST = new I18NMessage0P(LOGGER,"marketdata_nexus_connection_lost"); //$NON-NLS-1$
    static final I18NMessage1P MARKETDATA_ERROR_MESSAGE = new I18NMessage1P(LOGGER,"marketdata_error_message"); //$NON-NLS-1$
    static final I18NMessage0P MBEAN_SERVER_REQUIRED = new I18NMessage0P(LOGGER,"mbean_server_required"); //$NON-NLS-1$
    static final I18NMessage0P MODULE_NAME_REQUIRED = new I18NMessage0P(LOGGER,"module_name_required"); //$NON-NLS-1$
    static final I18NMessage2P BAD_FEED_STATUS = new I18NMessage2P(LOGGER,"bad_feed_status"); //$NON-NLS-1$
    static final I18NMessage2P CANNOT_RECONNECT_FEED = new I18NMessage2P(LOGGER,"cannot_reconnect_feed"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_DETERMINE_FEED_STATUS = new I18NMessage1P(LOGGER,"cannot_determine_feed_status"); //$NON-NLS-1$
    static final I18NMessage1P NO_SUBSCRIBER = new I18NMessage1P(LOGGER,"no_subscriber"); //$NON-NLS-1$
    static final I18NMessage2P PUBLISHING_ERROR = new I18NMessage2P(LOGGER,"publishing_error"); //$NON-NLS-1$
    static final I18NMessage2P NO_INSTRUMENT = new I18NMessage2P(LOGGER,"no_instrument"); //$NON-NLS-1$
    static final I18NMessage0P EVENT_MODULE_CONNECTOR_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"event_module_connector_provider_description");//$NON-NLS-1$
}
