package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_trade");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage1P NON_SYSTEM_FIX_MESSAGE =
            new I18NMessage1P(LOGGER, "non_system_fix_message");   //$NON-NLS-1$
    static final I18NMessage1P SYSTEM_FIX_MESSAGE_NO_BEGIN_STRING =
            new I18NMessage1P(LOGGER, "system_fix_message_no_begin_string");  //$NON-NLS-1$
    static final I18NMessage1P NOT_SINGLE_ORDER =
            new I18NMessage1P(LOGGER, "not_single_order");  //$NON-NLS-1$
    static final I18NMessage1P NOT_CANCEL_ORDER =
            new I18NMessage1P(LOGGER, "not_cancel_order");  //$NON-NLS-1$
    static final I18NMessage1P NOT_CANCEL_REPLACE_ORDER =
            new I18NMessage1P(LOGGER, "not_cancel_replace_order");  //$NON-NLS-1$
    static final I18NMessage2P MESSAGE_HAS_GROUPS =
            new I18NMessage2P(LOGGER, "message_has_groups");  //$NON-NLS-1$
    static final I18NMessage1P NOT_EXECUTION_REPORT =
            new I18NMessage1P(LOGGER, "not_execution_report");  //$NON-NLS-1$
    static final I18NMessage1P NOT_CANCEL_REJECT =
            new I18NMessage1P(LOGGER, "not_cancel_reject");  //$NON-NLS-1$
    static final I18NMessage0P UNABLE_TO_GENERATE_IDS =
            new I18NMessage0P(LOGGER, "unable_to_generate_ids");   //$NON-NLS-1$
    static final I18NMessage2P FIX_ORDER_TO_STRING =
            new I18NMessage2P(LOGGER, "fix_order_to_string");   //$NON-NLS-1$
    static final I18NMessageNP ORDER_SINGLE_TO_STRING =
            new I18NMessageNP(LOGGER, "order_single_to_string");   //$NON-NLS-1$
    static final I18NMessageNP ORDER_REPLACE_TO_STRING =
            new I18NMessageNP(LOGGER, "order_replace_to_string");   //$NON-NLS-1$
    static final I18NMessageNP ORDER_CANCEL_TO_STRING =
            new I18NMessageNP(LOGGER, "order_cancel_to_string");   //$NON-NLS-1$
    static final I18NMessageNP EXECUTION_REPORT_TO_STRING =
            new I18NMessageNP(LOGGER, "execution_report_to_string");   //$NON-NLS-1$
    static final I18NMessageNP ORDER_CANCEL_REJECT_TO_STRING =
            new I18NMessageNP(LOGGER, "order_cancel_reject_to_string");   //$NON-NLS-1$
    static final I18NMessage5P FIX_RESPONSE_TO_STRING =
            new I18NMessage5P(LOGGER, "fix_response_to_string");   //$NON-NLS-1$
    static final I18NMessage3P ORDER_SINGLE_SUGGESTION_TO_STRING =
            new I18NMessage3P(LOGGER, "order_single_suggestion_to_string");   //$NON-NLS-1$
    static final I18NMessage2P INVALID_ID_START_VALUE =
            new I18NMessage2P(LOGGER, "invalid_id_start_value");   //$NON-NLS-1$


    static final I18NMessage1P NO_INSTRUMENT=
        new I18NMessage1P(LOGGER,"no_instrument"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_INSTRUMENT=
        new I18NMessage0P(LOGGER,"unsupported_instrument"); //$NON-NLS-1$
    static final I18NMessage0P NO_PRICE=
        new I18NMessage0P(LOGGER,"no_price"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_PRICE=
        new I18NMessage0P(LOGGER,"unsupported_price"); //$NON-NLS-1$
    static final I18NMessage0P NO_QUANTITY=
        new I18NMessage0P(LOGGER,"no_quantity"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_QUANTITY=
        new I18NMessage0P(LOGGER,"unsupported_quantity"); //$NON-NLS-1$
    static final I18NMessage0P NO_ACCOUNT=
        new I18NMessage0P(LOGGER,"no_account"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_ACCOUNT=
        new I18NMessage0P(LOGGER,"unsupported_account"); //$NON-NLS-1$
    static final I18NMessage0P NO_TEXT=
        new I18NMessage0P(LOGGER,"no_text"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_TEXT=
        new I18NMessage0P(LOGGER,"unsupported_text"); //$NON-NLS-1$
    static final I18NMessage0P NO_ORDER_ID=
        new I18NMessage0P(LOGGER,"no_order_id"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_ORDER_ID=
        new I18NMessage0P(LOGGER,"unsupported_order_id"); //$NON-NLS-1$
    static final I18NMessage0P NO_ORIGINAL_ORDER_ID=
        new I18NMessage0P(LOGGER,"no_original_order_id"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_ORIGINAL_ORDER_ID=
        new I18NMessage0P(LOGGER,"unsupported_original_order_id"); //$NON-NLS-1$
    static final I18NMessage0P NO_BROKER_ORDER_ID=
        new I18NMessage0P(LOGGER,"no_broker_order_id"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_BROKER_ORDER_ID=
        new I18NMessage0P(LOGGER,"unsupported_broker_order_id"); //$NON-NLS-1$
    static final I18NMessage1P NO_SIDE=
        new I18NMessage1P(LOGGER,"no_side"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_SIDE=
        new I18NMessage0P(LOGGER,"unsupported_side"); //$NON-NLS-1$
    static final I18NMessage1P NO_SECURITY_TYPE=
        new I18NMessage1P(LOGGER,"no_security_type"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_SECURITY_TYPE=
        new I18NMessage0P(LOGGER,"unsupported_security_type"); //$NON-NLS-1$
    static final I18NMessage1P NO_TIME_IN_FORCE=
        new I18NMessage1P(LOGGER,"no_time_in_force"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_TIME_IN_FORCE=
        new I18NMessage0P(LOGGER,"unsupported_time_in_force"); //$NON-NLS-1$
    static final I18NMessage1P NO_POSITION_EFFECT=
        new I18NMessage1P(LOGGER,"no_position_effect"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_POSITION_EFFECT=
        new I18NMessage0P(LOGGER,"unsupported_position_effect"); //$NON-NLS-1$
    static final I18NMessage1P NO_ORDER_CAPACITY=
        new I18NMessage1P(LOGGER,"no_order_capacity"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_ORDER_CAPACITY=
        new I18NMessage0P(LOGGER,"unsupported_order_capacity"); //$NON-NLS-1$
    static final I18NMessage1P NO_ORDER_TYPE=
        new I18NMessage1P(LOGGER,"no_order_type"); //$NON-NLS-1$
    static final I18NMessage0P UNSUPPORTED_ORDER_TYPE=
        new I18NMessage0P(LOGGER,"unsupported_order_type"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CONVERT=
        new I18NMessage1P(LOGGER,"cannot_convert"); //$NON-NLS-1$
    static final I18NMessage0P NULL_SYMBOL=
        new I18NMessage0P(LOGGER,"null_symbol"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_SYMBOL=
        new I18NMessage1P(LOGGER,"invalid_symbol"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_LEFT_CURRENCY=
            new I18NMessage0P(LOGGER,"missing_left_currency"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_RIGHT_CURRENCY=
            new I18NMessage0P(LOGGER,"missing_right_currency"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_NEAR_TENOR=
        new I18NMessage0P(LOGGER,"missing_near_tenor"); //$NON-NLS-1$
    static final I18NMessage0P NULL_MONTH=
        new I18NMessage0P(LOGGER,"null_month"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_MONTH=
        new I18NMessage1P(LOGGER,"invalid_month"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_DAY = new I18NMessage1P(LOGGER,
                                                               "invalid_day"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_YEAR=
        new I18NMessage1P(LOGGER,"invalid_year"); //$NON-NLS-1$
    static final I18NMessage0P NULL_EXPIRY=
        new I18NMessage0P(LOGGER,"null_expiry"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_EXPIRY=
        new I18NMessage1P(LOGGER,"invalid_expiry"); //$NON-NLS-1$
    static final I18NMessage1P SKIPPNG_MALFORMED_REPORT = new I18NMessage1P(LOGGER,
                                                                            "skipping_malformed_report"); //$NON-NLS-1$
}
