package org.marketcetera.marketdata.exsim;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Provides messages for this package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdata_exsim", Messages.class.getClassLoader()); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"provider_description"); //$NON-NLS-1$
    static final I18NMessage0P FEED_OFFLINE = new I18NMessage0P(LOGGER,"feed_offline"); //$NON-NLS-1$
    static final I18NMessage0P DATA_REQUEST_PAYLOAD_REQUIRED = new I18NMessage0P(LOGGER,"data_request_payload_required"); //$NON-NLS-1$
    static final I18NMessage2P INVALID_DATA_REQUEST_PAYLOAD = new I18NMessage2P(LOGGER,"invalid_data_request_payload"); //$NON-NLS-1$
    static final I18NMessage1P UNSUPPORTED_DATA_REQUEST_PAYLOAD = new I18NMessage1P(LOGGER,"unsupported_data_request_payload"); //$NON-NLS-1$
    static final I18NMessage1P DATA_FLOW_ALREADY_CANCELED = new I18NMessage1P(LOGGER,"data_flow_already_canceled"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CANCEL_DATA_FLOW = new I18NMessage1P(LOGGER,"cannot_cancel_data_flow"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_RESOLVE_SYMBOL = new I18NMessage1P(LOGGER,"cannot_resolve_symbol"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_REQUEST_DATA = new I18NMessage1P(LOGGER,"cannot_request_data"); //$NON-NLS-1$
    static final I18NMessage2P CANNOT_CANCEL_DATA = new I18NMessage2P(LOGGER,"cannot_cancel_data"); //$NON-NLS-1$
    static final I18NMessage2P FEED_STATUS_UPDATE = new I18NMessage2P(LOGGER,"feed_status_update"); //$NON-NLS-1$
    static final I18NMessage1P UNSUPPORTED_UPDATE_ACTION = new I18NMessage1P(LOGGER,"unsupported_update_action"); //$NON-NLS-1$
    static final I18NMessage1P IGNORING_UNHANDLED_UPDATE_TYPE = new I18NMessage1P(LOGGER,"ignoring_unhandled_update_type"); //$NON-NLS-1$
    static final I18NMessage3P IGNORED_EXCEPTION_ON_SEND = new I18NMessage3P(LOGGER,"ignored_exception_on_send"); //$NON-NLS-1$
    static final I18NMessage1P MARKETDATA_REJECT_WITH_MESSAGE = new I18NMessage1P(LOGGER,"marketdata_reject_with_message"); //$NON-NLS-1$
    static final I18NMessage0P MARKETDATA_REJECT_WITHOUT_MESSAGE = new I18NMessage0P(LOGGER,"marketdata_reject_without_message"); //$NON-NLS-1$
    static final I18NMessage0P SENDER_COMPID_REQURED = new I18NMessage0P(LOGGER,"sender_compid_required"); //$NON-NLS-1$
    static final I18NMessage2P IGNORING_UNEXPECTED_MESSAGE = new I18NMessage2P(LOGGER,"ignoring_unexpected_message"); //$NON-NLS-1$
    static final I18NMessage3P UNABLE_TO_PROCESS_MESSAGE = new I18NMessage3P(LOGGER,"unable_to_process_message"); //$NON-NLS-1$
    static final I18NMessage2P MARKET_DATA_REQUEST_FAILED = new I18NMessage2P(LOGGER,"market_data_request_failed"); //$NON-NLS-1$
    static final I18NMessage0P FEED_CONFIG_REQUIRED = new I18NMessage0P(LOGGER,"feed_config_required"); //$NON-NLS-1$
}
