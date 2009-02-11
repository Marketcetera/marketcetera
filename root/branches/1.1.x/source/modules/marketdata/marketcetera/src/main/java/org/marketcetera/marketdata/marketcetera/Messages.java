package org.marketcetera.marketdata.marketcetera;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Internationalization messages for the Marketcetera <em>OpenTick</em> Connector.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketcetera", Messages.class.getClassLoader()); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    static final I18NMessage1P UNKNOWN_EVENT_TYPE = new I18NMessage1P(LOGGER,
                                                                      "unknown_event_type"); //$NON-NLS-1$
    static final I18NMessage1P UNKNOWN_MESSAGE_ENTRY_TYPE = new I18NMessage1P(LOGGER,
                                                                              "unknown_message_entry_type"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_EQUITY_OPTION_SYMBOL = new I18NMessage1P(LOGGER,
                                                                                "invalid_equity_option_symbol"); //$NON-NLS-1$
    static final I18NMessage0P SESSION_NOT_FOUND = new I18NMessage0P(LOGGER,
                                                                     "session_not_found"); //$NON-NLS-1$
    static final I18NMessage1P CONNECTION_STARTED = new I18NMessage1P(LOGGER,
                                                                      "connection_started"); //$NON-NLS-1$
    static final I18NMessage0P CANNOT_START_FEED = new I18NMessage0P(LOGGER,
                                                                     "cannot_start_feed"); //$NON-NLS-1$
    static final I18NMessage1P CONNECTION_STOPPED = new I18NMessage1P(LOGGER,
                                                                      "connection_stopped"); //$NON-NLS-1$
    static final I18NMessage0P URI_MISSING_PORT = new I18NMessage0P(LOGGER,
                                                                    "uri_missing_port"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_FIX_VERSION = new I18NMessage0P(LOGGER,
                                                                           "unsupported_fix_version"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_EXECUTE_QUERY = new I18NMessage1P(LOGGER,
                                                                        "cannot_execute_query"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_FIND_REQID = new I18NMessage1P(LOGGER,
                                                                     "cannot_find_reqid"); //$NON-NLS-1$
    static final I18NMessage0P CANNOT_ACQUIRE_ID = new I18NMessage0P(LOGGER,
                                                                     "cannot_acquire_id"); //$NON-NLS-1$
    static final I18NMessage0P EXCHANGE_ERROR = new I18NMessage0P(LOGGER,
                                                                  "exchange_error"); //$NON-NLS-1$
    static final I18NMessage0P URL_LABEL = new I18NMessage0P(LOGGER,
                                                             "url_label"); //$NON-NLS-1$
    static final I18NMessage0P SENDER_COMP_LABEL = new I18NMessage0P(LOGGER,
                                                                     "sender_comp_label"); //$NON-NLS-1$
    static final I18NMessage0P TARGET_COMP_LABEL = new I18NMessage0P(LOGGER,
                                                                     "target_comp_label"); //$NON-NLS-1$
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                        "provider_description"); //$NON-NLS-1$
    static final I18NMessage0P TARGET_COMP_ID_REQUIRED = new I18NMessage0P(LOGGER,
                                                                           "target_comp_id_required"); //$NON-NLS-1$
    static final I18NMessage0P URL_REQUIRED = new I18NMessage0P(LOGGER,
                                                                "url_required"); //$NON-NLS-1$
}
