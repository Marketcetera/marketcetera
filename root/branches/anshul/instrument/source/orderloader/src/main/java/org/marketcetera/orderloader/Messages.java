package org.marketcetera.orderloader;

import org.marketcetera.util.log.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Message constants for classes in this package and its subpackages.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("orderloader"); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER); //$NON-NLS-1$

    static final I18NMessage1P PARSING_PRICE_VALID_NUM = new I18NMessage1P(LOGGER,
                                                                           "parsing_price_valid_num"); //$NON-NLS-1$
    static final I18NMessage1P PARSING_PRICE_POSITIVE = new I18NMessage1P(LOGGER,
                                                                          "parsing_price_positive"); //$NON-NLS-1$
    static final I18NMessage1P PARSING_QTY_INT = new I18NMessage1P(LOGGER,
                                                                   "parsing_qty_int"); //$NON-NLS-1$
    static final I18NMessage1P PARSING_QTY_POS_INT = new I18NMessage1P(LOGGER,
                                                                       "parsing_qty_pos_int"); //$NON-NLS-1$
    static final I18NMessage2P PARSING_FIELD_NOT_IN_DICT = new I18NMessage2P(LOGGER,
                                                                             "parsing_field_not_in_dict"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_USAGE = new I18NMessage0P(LOGGER,
                                                               "error_usage"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_EXAMPLE = new I18NMessage0P(LOGGER,
                                                                 "error_example"); //$NON-NLS-1$
    static final I18NMessage0P USAGE_LOADER_OPTIONS =
            new I18NMessage0P(LOGGER, "usage_loader_options");   //$NON-NLS-1$
    static final I18NMessage0P USAGE_MODE =
            new I18NMessage0P(LOGGER, "usage_mode");   //$NON-NLS-1$
    static final I18NMessage0P USAGE_BROKER_ID =
            new I18NMessage0P(LOGGER, "usage_broker_id");   //$NON-NLS-1$


    static final I18NMessage0P ERROR_AUTHENTICATION = new I18NMessage0P(LOGGER,
                                                                        "error_authentication"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MISSING_FILE = new I18NMessage0P(LOGGER,
                                                                      "error_missing_file"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_TOO_MANY_ARGUMENTS = new I18NMessage0P(LOGGER,
                                                                            "error_too_many_arguments"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_NO_ORDERS = new I18NMessage0P(LOGGER,
                                                                   "error_no_orders"); //$NON-NLS-1$
    static final I18NMessage2P ERROR_PARSING_MESSAGE = new I18NMessage2P(LOGGER,
                                                                         "error_parsing_message"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_PARSING_UNKNOWN = new I18NMessage1P(LOGGER,
                                                                         "error_parsing_unknown"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_PARSING_NUMBER_FORMAT = new I18NMessage1P(LOGGER,
                                                                               "error_parsing_number_format"); //$NON-NLS-1$
    static final I18NMessage1P PARSED_MESSAGE_FAILED_VALIDATION =
            new I18NMessage1P(LOGGER, "parsed_message_failed_validation");   //$NON-NLS-1$
    static final I18NMessage0P BROKER_ID_REQUIRED =
            new I18NMessage0P(LOGGER, "broker_id_required");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_PROCESS_FIX_VERSION =
            new I18NMessage1P(LOGGER, "error_process_fix_version");   //$NON-NLS-1$
    static final I18NMessage2P HEADER_ROW_MISMATCH =
            new I18NMessage2P(LOGGER, "header_row_mismatch");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_ORDER_CAPACITY =
            new I18NMessage2P(LOGGER, "invalid_order_capacity");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_SECURITY_TYPE =
            new I18NMessage2P(LOGGER, "invalid_security_type");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_ORDER_TYPE =
            new I18NMessage2P(LOGGER, "invalid_order_type");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_POSITION_EFFECT =
            new I18NMessage2P(LOGGER, "invalid_position_effect");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_SIDE =
            new I18NMessage2P(LOGGER, "invalid_side");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_TIME_IN_FORCE =
            new I18NMessage2P(LOGGER, "invalid_time_in_force");   //$NON-NLS-1$

    static final I18NMessage1P INVALID_PRICE_VALUE =
            new I18NMessage1P(LOGGER, "invalid_price_value");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_QUANTITY_VALUE =
            new I18NMessage1P(LOGGER, "invalid_quantity_value");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_CUSTOM_HEADER =
            new I18NMessage2P(LOGGER, "invalid_custom_header");   //$NON-NLS-1$
    static final I18NMessage3P DUPLICATE_HEADER =
            new I18NMessage3P(LOGGER, "duplicate_header");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_FIX_VERSION =
            new I18NMessage2P(LOGGER, "invalid_fix_version");   //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_ORDER_TYPE =
            new I18NMessage1P(LOGGER, "unexpected_order_type");   //$NON-NLS-1$
    static final I18NMessage0P ARG_MODE_VALUE =
            new I18NMessage0P(LOGGER, "arg_mode_value");   //$NON-NLS-1$
    static final I18NMessage0P ARG_MODE_DESCRIPTION =
            new I18NMessage0P(LOGGER, "arg_mode_description");   //$NON-NLS-1$
    static final I18NMessage0P ARG_BROKER_VALUE =
            new I18NMessage0P(LOGGER, "arg_broker_value");   //$NON-NLS-1$
    static final I18NMessage0P ARG_BROKER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "arg_broker_description");   //$NON-NLS-1$
    static final I18NMessage3P LINE_SUMMARY =
            new I18NMessage3P(LOGGER, "line_summary");   //$NON-NLS-1$
    static final I18NMessage2P ORDER_SUMMARY =
            new I18NMessage2P(LOGGER, "order_summary");   //$NON-NLS-1$
    static final I18NMessage0P FAILED_ORDERS =
            new I18NMessage0P(LOGGER, "failed_orders");   //$NON-NLS-1$
    static final I18NMessage3P FAILED_ORDER =
            new I18NMessage3P(LOGGER, "failed_order");   //$NON-NLS-1$
    static final I18NMessage1P MISSING_REQUIRED_FIELD =
            new I18NMessage1P(LOGGER, "missing_required_field");   //$NON-NLS-1$

    static final I18NMessage2P LOG_FAILED_ORDER =
            new I18NMessage2P(LOGGER, "log_failed_order");   //$NON-NLS-1$
    static final I18NMessage0P LOG_APP_COPYRIGHT =
            new I18NMessage0P(LOGGER, "log_app_copyright");   //$NON-NLS-1$
    static final I18NMessage2P LOG_APP_VERSION_BUILD =
            new I18NMessage2P(LOGGER, "log_app_version_build");   //$NON-NLS-1$

}
