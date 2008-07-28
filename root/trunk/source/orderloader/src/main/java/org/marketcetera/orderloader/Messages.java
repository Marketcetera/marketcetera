package org.marketcetera.orderloader;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Message constants for {@link OrderLoader}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: $")//$NON-NLS-1$
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("orderloader"); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER); //$NON-NLS-1$

    static final I18NMessage2P PARSING_ORDER_GEN_ERROR = new I18NMessage2P(LOGGER,
                                                                           "parsing_order_gen_error"); //$NON-NLS-1$
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
    static final I18NMessage0P PARSING_WRONG_NUM_FIELDS = new I18NMessage0P(LOGGER,
                                                                            "parsing_wrong_num_fields"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_USAGE = new I18NMessage1P(LOGGER,
                                                               "error_usage"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_EXAMPLE = new I18NMessage0P(LOGGER,
                                                                 "error_example"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_AUTHENTICATION = new I18NMessage0P(LOGGER,
                                                                        "error_authentication"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MISSING_FILE = new I18NMessage0P(LOGGER,
                                                                      "error_missing_file"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_TOO_MANY_ARGUMENTS = new I18NMessage0P(LOGGER,
                                                                            "error_too_many_arguments"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_NO_ORDERS = new I18NMessage0P(LOGGER,
                                                                   "error_no_orders"); //$NON-NLS-1$
    static final I18NMessage0P REPORT_SUMMARY = new I18NMessage0P(LOGGER,
                                                                  "report_summary"); //$NON-NLS-1$
    static final I18NMessage1P REPORT_PROCESSED_LINES = new I18NMessage1P(LOGGER,
                                                                          "report_processed_lines"); //$NON-NLS-1$
    static final I18NMessage1P REPORT_BLANK_LINES = new I18NMessage1P(LOGGER,
                                                                      "report_blank_lines"); //$NON-NLS-1$
    static final I18NMessage1P FAILED_MESSAGES = new I18NMessage1P(LOGGER,
                                                                   "failed_messages"); //$NON-NLS-1$
    static final I18NMessage2P ERROR_PARSING_MESSAGE = new I18NMessage2P(LOGGER,
                                                                         "error_parsing_message"); //$NON-NLS-1$
}
