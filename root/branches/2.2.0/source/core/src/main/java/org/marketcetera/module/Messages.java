package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * The message constants for messages used within the module framework.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_module");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage3P INVALID_URN_SCHEME =
            new I18NMessage3P(LOGGER, "invalid_urn_scheme");  //$NON-NLS-1$
    static final I18NMessage1P INCOMPLETE_PROVIDER_URN =
            new I18NMessage1P(LOGGER,"incomplete_provider_urn");  //$NON-NLS-1$
    static final I18NMessage2P INVALID_PROVIDER_TYPE =
            new I18NMessage2P(LOGGER, "invalid_provider_type");  //$NON-NLS-1$
    static final I18NMessage2P INVALID_PROVIDER_NAME =
            new I18NMessage2P(LOGGER, "invalid_provider_name");  //$NON-NLS-1$
    static final I18NMessage3P UNABLE_SET_ATTRIBUTE =
            new I18NMessage3P(LOGGER,"unable_set_attribute");  //$NON-NLS-1$
    static final I18NMessage1P BEAN_REGISTRATION_ERROR =
            new I18NMessage1P(LOGGER,"bean_registration_error");  //$NON-NLS-1$
    static final I18NMessage1P BEAN_ATTRIB_DISCOVERY_ERROR =
            new I18NMessage1P(LOGGER, "bean_attrib_discovery_error");  //$NON-NLS-1$
    static final I18NMessage0P MODULE_CONFIGURATION_ERROR =
            new I18NMessage0P(LOGGER, "module_configuration_error");  //$NON-NLS-1$
    static final I18NMessage1P INCOMPLETE_INSTANCE_URN =
            new I18NMessage1P(LOGGER, "incomplete_instance_urn");  //$NON-NLS-1$
    static final I18NMessage2P INVALID_INSTANCE_URN =
            new I18NMessage2P(LOGGER,"invalid_instance_urn");  //$NON-NLS-1$
    static final I18NMessage2P INSTANCE_PROVIDER_URN_MISMATCH = 
            new I18NMessage2P(LOGGER,"instance_provider_urn_mismatch");  //$NON-NLS-1$
    static final I18NMessage1P DUPLICATE_MODULE_URN =
            new I18NMessage1P(LOGGER, "duplicate_module_urn");  //$NON-NLS-1$
    static final I18NMessage3P MODULE_NOT_STARTED_STATE_INCORRECT =
            new I18NMessage3P(LOGGER, "module_not_started_state_incorrect");  //$NON-NLS-1$
    static final I18NMessage1P MODULE_NOT_FOUND = 
            new I18NMessage1P(LOGGER, "module_not_found");  //$NON-NLS-1$
    static final I18NMessage1P BEAN_OBJECT_NAME_ERROR = 
            new I18NMessage1P(LOGGER, "bean_object_name_error");  //$NON-NLS-1$
    static final I18NMessage3P MODULE_NOT_STOPPED_STATE_INCORRECT =
            new I18NMessage3P(LOGGER, "module_not_stopped_state_incorrect");  //$NON-NLS-1$
    static final I18NMessage4P CANCEL_FAILED_MODULE_STATE_INCORRECT =
            new I18NMessage4P(LOGGER, "cancel_failed_module_state_incorrect");  //$NON-NLS-1$
    static final I18NMessage1P EMPTY_URN =
            new I18NMessage1P(LOGGER, "empty_urn");  //$NON-NLS-1$
    static final I18NMessage1P PROVIDER_NOT_FOUND =
            new I18NMessage1P(LOGGER, "provider_not_found");  //$NON-NLS-1$
    static final I18NMessage3P DATAFLOW_FAILED_REQ_MODULE_STATE_INCORRECT =
            new I18NMessage3P(LOGGER,"dataflow_failed_req_module_state_incorrect");  //$NON-NLS-1$
    static final I18NMessage3P DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT =
            new I18NMessage3P(LOGGER,"dataflow_failed_pcpt_module_state_incorrect");  //$NON-NLS-1$
    static final I18NMessage1P DATA_REQUEST_TOO_SHORT =
            new I18NMessage1P(LOGGER, "data_request_too_short");  //$NON-NLS-1$
    static final I18NMessage1P MODULE_NOT_EMITTER =
            new I18NMessage1P(LOGGER, "module_not_emitter");  //$NON-NLS-1$
    static final I18NMessage1P MODULE_NOT_RECEIVER =
            new I18NMessage1P(LOGGER, "module_not_receiver");  //$NON-NLS-1$
    static final I18NMessage1P DATA_FLOW_NOT_FOUND =
            new I18NMessage1P(LOGGER, "data_flow_not_found");  //$NON-NLS-1$
    static final I18NMessage0P SINK_MODULE_FACTORY_DESC =
            new I18NMessage0P(LOGGER, "sink_module_factory_desc");  //$NON-NLS-1$
    static final I18NMessage1P PROVIDER_URN_HAS_INSTANCE =
            new I18NMessage1P(LOGGER, "provider_urn_has_instance");  //$NON-NLS-1$
    static final I18NMessage0P UNABLE_GENERATE_FLOW_ID =
            new I18NMessage0P(LOGGER, "unable_generate_flow_id");  //$NON-NLS-1$
    static final I18NMessage1P CANNOT_DELETE_SINGLETON =
            new I18NMessage1P(LOGGER, "cannot_delete_singleton");  //$NON-NLS-1$
    static final I18NMessage2P CANNOT_STOP_MODULE_DATAFLOWS =
            new I18NMessage2P(LOGGER, "cannot_stop_module_dataflows");  //$NON-NLS-1$
    static final I18NMessage2P CANNOT_CREATE_SINGLETON =
            new I18NMessage2P(LOGGER, "cannot_create_singleton");  //$NON-NLS-1$
    static final I18NMessage3P CANNOT_CREATE_MODULE_WRONG_PARAM_NUM =
            new I18NMessage3P(LOGGER, "cannot_create_module_wrong_param_num");  //$NON-NLS-1$
    static final I18NMessage4P CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE =
            new I18NMessage4P(LOGGER, "cannot_create_module_wrong_param_type");  //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CONVERT_TO_MODULE_URN =
            new I18NMessage1P(LOGGER, "cannot_convert_to_module_urn");  //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CONVERT_TO_PROPERTIES =
            new I18NMessage1P(LOGGER, "cannot_convert_to_properties");  //$NON-NLS-1$
    static final I18NMessage0P EMPTY_STRING_DATA_REQUEST =
            new I18NMessage0P(LOGGER, "empty_string_data_request");  //$NON-NLS-1$
    static final I18NMessage3P STRING_CONVERSION_ERROR =
            new I18NMessage3P(LOGGER, "string_conversion_error");  //$NON-NLS-1$
    static final I18NMessage2P CANNOT_CREATE_MODULE_PARAM_CONVERT_ERROR =
            new I18NMessage2P(LOGGER, "cannot_create_module_param_convert_error");  //$NON-NLS-1$
    static final I18NMessage1P MODULE_DELETE_ERROR_MXBEAN_UNREG =
            new I18NMessage1P(LOGGER, "module_delete_error_mxbean_unreg");  //$NON-NLS-1$
    static final I18NMessage1P ERROR_READ_PROPERTIES =
            new I18NMessage1P(LOGGER, "error_read_properties");  //$NON-NLS-1$
    static final I18NMessage0P UNABLE_GENERATE_REQUEST_ID =
            new I18NMessage0P(LOGGER, "unable_generate_request_id");  //$NON-NLS-1$
    static final I18NMessage2P ILLEGAL_REQ_PARM_VALUE =
            new I18NMessage2P(LOGGER, "illegal_req_parm_value");  //$NON-NLS-1$
    static final I18NMessage2P UNSUPPORTED_REQ_PARM_TYPE =
            new I18NMessage2P(LOGGER, "unsupported_req_parm_type");  //$NON-NLS-1$
    static final I18NMessage2P LOG_MODULE_EMIT_ERROR =
            new I18NMessage2P(LOGGER, "log_module_emit_error");  //$NON-NLS-1$
    static final I18NMessage2P LOG_DATA_RECEIVE_ERROR =
            new I18NMessage2P(LOGGER, "log_data_receive_error");   //$NON-NLS-1$
    static final I18NMessage2P LOG_CANCELING_DATA_FLOW =
            new I18NMessage2P(LOGGER, "log_canceling_data_flow");   //$NON-NLS-1$
    static final I18NMessage1P LOG_UNEXPECTED_ERROR_CANCELING_REQ =
            new I18NMessage1P(LOGGER, "log_unexpected_error_canceling_req");   //$NON-NLS-1$
    static final I18NMessage2P LOG_UNEXPECTED_ERROR_CANCELING_FLOW =
            new I18NMessage2P(LOGGER, "log_unexpected_error_canceling_flow");   //$NON-NLS-1$
    static final I18NMessage1P INCORRECT_FACTORY_AUTO_INSTANTIATE =
            new I18NMessage1P(LOGGER, "incorrect_factory_auto_instantiate");  //$NON-NLS-1$
    static final I18NMessage3P MULTIPLE_MODULES_MATCH_URN =
            new I18NMessage3P(LOGGER, "multiple_modules_match_urn");  //$NON-NLS-1$
    static final I18NMessage1P LOG_INIT_FACTORY =
            new I18NMessage1P(LOGGER, "log_init_factory");   //$NON-NLS-1$
    static final I18NMessage1P LOG_INIT_FACTORY_IGNORE =
            new I18NMessage1P(LOGGER, "log_init_factory_ignore");   //$NON-NLS-1$
    static final I18NMessage2P LOG_REGISTERED_FACTORY_BEAN =
            new I18NMessage2P(LOGGER, "log_registered_factory_bean");   //$NON-NLS-1$
    static final I18NMessage1P LOG_CREATED_MODULE_INSTANCE =
            new I18NMessage1P(LOGGER, "log_created_module_instance");   //$NON-NLS-1$
    static final I18NMessage2P LOG_REGISTERED_MODULE_BEAN =
            new I18NMessage2P(LOGGER, "log_registered_module_bean");   //$NON-NLS-1$
    static final I18NMessage1P LOG_START_MODULE_FAILED =
            new I18NMessage1P(LOGGER, "log_start_module_failed");   //$NON-NLS-1$
    static final I18NMessage1P LOG_MODULE_STARTED =
            new I18NMessage1P(LOGGER, "log_module_started");   //$NON-NLS-1$
    static final I18NMessage1P LOG_MODULE_STOPPED =
            new I18NMessage1P(LOGGER, "log_module_stopped");   //$NON-NLS-1$
    static final I18NMessage1P LOG_STOP_MODULE_FAILED =
            new I18NMessage1P(LOGGER, "log_stop_module_failed");   //$NON-NLS-1$
    static final I18NMessage0P LOG_SINK_LISTENER_RECEIVE_ERROR =
            new I18NMessage0P(LOGGER, "log_sink_listener_receive_error");   //$NON-NLS-1$
    static final I18NMessage1P LOG_MODULE_DELETED =
            new I18NMessage1P(LOGGER, "log_module_deleted");   //$NON-NLS-1$
    static final I18NMessage3P LOG_ERROR_READ_DEFAULT_CONFIG =
            new I18NMessage3P(LOGGER, "log_error_read_default_config");   //$NON-NLS-1$
    static final I18NMessage0P LOG_SINK_MODULE_MISCONFIGURED =
            new I18NMessage0P(LOGGER, "log_sink_module_misconfigured");   //$NON-NLS-1$
    static final I18NMessage0P CANNOT_STOP_SINK_MODULE =
            new I18NMessage0P(LOGGER, "cannot_stop_sink_module");   //$NON-NLS-1$
    static final I18NMessage2P LOG_DELETE_AUTO_CREATED_MODULE =
            new I18NMessage2P(LOGGER, "log_delete_auto_created_module");   //$NON-NLS-1$
    static final I18NMessage2P LOG_CANCEL_DATA_FLOW_START_FAILED =
            new I18NMessage2P(LOGGER, "log_cancel_data_flow_start_failed");   //$NON-NLS-1$
    static final I18NMessage0P BEAN_UNREGISTRATION_ERROR =
            new I18NMessage0P(LOGGER, "bean_unregistration_error");  //$NON-NLS-1$
    static final I18NMessage0P LOG_STOP_FAILURE =
            new I18NMessage0P(LOGGER, "log_stop_failure");  //$NON-NLS-1$
    static final I18NMessage0P ERROR_REFRESH =
            new I18NMessage0P(LOGGER, "error_refresh");  //$NON-NLS-1$
    static final I18NMessage1P REFRESH_LISTENER_ALREADY_SETUP =
            new I18NMessage1P(LOGGER, "refresh_listener_already_setup");  //$NON-NLS-1$
    static final I18NMessage0P LOG_STOPPING_MODULE_MANAGER =
            new I18NMessage0P(LOGGER, "log_stopping_module_manager");  //$NON-NLS-1$
    static final I18NMessage3P DELETE_FAILED_MODULE_STATE_INCORRECT =
            new I18NMessage3P(LOGGER, "delete_failed_module_state_incorrect");   //$NON-NLS-1$
    static final I18NMessage1P LOG_DELETE_AUTO_CREATED_MODULE_FAIL =
            new I18NMessage1P(LOGGER, "log_delete_auto_created_module_fail");   //$NON-NLS-1$
    static final I18NMessage1P DATA_FLOW_ALREADY_CANCELING =
            new I18NMessage1P(LOGGER, "data_flow_already_canceling");   //$NON-NLS-1$
    static final I18NMessage0P INCORRECT_NESTED_FLOW_REQUEST =
            new I18NMessage0P(LOGGER, "incorrect_nested_flow_request");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_CLEANING_UP_INIT_FAILURE =
            new I18NMessage0P(LOGGER, "error_cleaning_up_init_failure");   //$NON-NLS-1$

}
