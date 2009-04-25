package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages used by this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("client",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage0P PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage2P UNSUPPORTED_DATA_TYPE =
            new I18NMessage2P(LOGGER, "unsupported_data_type");   //$NON-NLS-1$
    static final I18NMessage1P SEND_ORDER_FAIL_NO_CONNECT =
            new I18NMessage1P(LOGGER, "send_order_fail_no_connect");   //$NON-NLS-1$
    static final I18NMessage4P ERROR_CONNECT_TO_SERVER =
            new I18NMessage4P(LOGGER, "error_connect_to_server");   //$NON-NLS-1$
    static final I18NMessage0P CREATE_MODULE_ERROR =
            new I18NMessage0P(LOGGER, "create_module_error");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_ALREADY_INITIALIZED =
            new I18NMessage0P(LOGGER, "client_already_initialized");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER, "client_not_initialized");   //$NON-NLS-1$

    static final I18NMessage1P ERROR_SEND_MESSAGE =
            new I18NMessage1P(LOGGER, "error_send_message");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_REMOTE_EXECUTION =
            new I18NMessage0P(LOGGER, "error_remote_execution");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_HEARTBEAT_FAILED =
            new I18NMessage0P(LOGGER, "error_heartbeat_failed");   //$NON-NLS-1$
    static final I18NMessage0P CONNECT_ERROR_NO_URL =
            new I18NMessage0P(LOGGER, "connect_error_no_url");   //$NON-NLS-1$
    static final I18NMessage0P CONNECT_ERROR_NO_USERNAME =
            new I18NMessage0P(LOGGER, "connect_error_no_username");   //$NON-NLS-1$
    static final I18NMessage0P CONNECT_ERROR_NO_HOSTNAME =
            new I18NMessage0P(LOGGER, "connect_error_no_hostname");   //$NON-NLS-1$
    static final I18NMessage1P CONNECT_ERROR_INVALID_PORT =
            new I18NMessage1P(LOGGER, "connect_error_invalid_port");   //$NON-NLS-1$
    static final I18NMessage0P CONNECT_ERROR_NO_CONFIGURATION=
            new I18NMessage0P(LOGGER, "connect_error_no_configuration"); //$NON-NLS-1$
    static final I18NMessage0P NOT_CONNECTED_TO_SERVER =
            new I18NMessage0P(LOGGER, "not_connected_to_server");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_RECEIVING_JMS_MESSAGE =
            new I18NMessage0P(LOGGER, "error_receiving_jms_message");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_CREATING_JMS_CONNECTION =
            new I18NMessage0P(LOGGER, "error_creating_jms_connection");   //$NON-NLS-1$
    static final I18NMessage0P REQUEST_PARAMETER_SPECIFIED =
            new I18NMessage0P(LOGGER, "request_parameter_specified");   //$NON-NLS-1$
    static final I18NMessage0P REQUEST_CLIENT_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER, "request_client_not_initialized");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_CLOSED =
            new I18NMessage0P(LOGGER, "client_closed");   //$NON-NLS-1$
    static final I18NMessage0P SERVER_CONNECTION_DEAD =
            new I18NMessage0P(LOGGER, "server_connection_dead");   //$NON-NLS-1$
    static final I18NMessage0P NO_ORDER_SUPPLIED =
            new I18NMessage0P(LOGGER, "no_order_supplied");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_ORDERID =
            new I18NMessage0P(LOGGER, "validation_orderid");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_ORDER_TYPE =
            new I18NMessage0P(LOGGER, "validation_order_type");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_ORDER_QUANTITY =
            new I18NMessage0P(LOGGER, "validation_order_quantity");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_ORDER_SIDE =
            new I18NMessage0P(LOGGER, "validation_order_side");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_ORDER_SYMBOL =
            new I18NMessage0P(LOGGER, "validation_order_symbol");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_ORIG_ORDERID =
            new I18NMessage0P(LOGGER, "validation_orig_orderid");   //$NON-NLS-1$
    static final I18NMessage2P SEND_ORDER_VALIDATION_FAILED =
            new I18NMessage2P(LOGGER, "send_order_validation_failed");   //$NON-NLS-1$
    static final I18NMessage0P NO_SUGGEST_SUPPLIED =
            new I18NMessage0P(LOGGER, "no_suggest_supplied");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_SUGGEST_IDENTIFIER =
            new I18NMessage0P(LOGGER, "validation_suggest_identifier");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_SUGGEST_SCORE =
            new I18NMessage0P(LOGGER, "validation_suggest_score");   //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_SUGGEST_ORDER =
            new I18NMessage0P(LOGGER, "validation_suggest_order");   //$NON-NLS-1$
    static final I18NMessage0P UNABLE_FETCH_ID_SERVER =
            new I18NMessage0P(LOGGER, "unable_fetch_id_server");   //$NON-NLS-1$

    static final I18NMessage1P LOG_ERROR_RECEIVE_EXEC_REPORT =
            new I18NMessage1P(LOGGER, "log_error_receive_exec_report");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_RECEIVE_CANCEL_REJECT =
            new I18NMessage1P(LOGGER, "log_error_receive_cancel_reject");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_RECEIVE_BROKER_STATUS =
            new I18NMessage1P(LOGGER, "log_error_receive_broker_status");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_RECEIVE_SERVER_STATUS =
            new I18NMessage1P(LOGGER, "log_error_receive_server_status");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_NOTIFY_EXCEPTION =
            new I18NMessage1P(LOGGER, "log_error_notify_exception");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_SEND_EXCEPTION =
            new I18NMessage1P(LOGGER, "log_error_send_exception");   //$NON-NLS-1$
    static final I18NMessage1P LOG_CLIENT_NOT_INIT_CANCEL_REQUEST =
            new I18NMessage1P(LOGGER, "log_client_not_init_cancel_request");   //$NON-NLS-1$
    static final I18NMessage0P LOG_UNABLE_FETCH_ID_SERVER =
            new I18NMessage0P(LOGGER, "log_unable_fetch_id_server");   //$NON-NLS-1$
    static final I18NMessage0P HEARTBEAT_THREAD_NAME =
            new I18NMessage0P(LOGGER, "heartbeat_thread_name");   //$NON-NLS-1$
}