package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
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
    static final I18NMessage3P ORDER_SINGLE_SUGGESTION_TO_STRING =
            new I18NMessage3P(LOGGER, "order_single_suggestion_to_string");   //$NON-NLS-1$


}
