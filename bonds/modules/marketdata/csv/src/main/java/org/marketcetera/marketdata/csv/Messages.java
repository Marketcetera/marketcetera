package org.marketcetera.marketdata.csv;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */

/**
 * Messages for CSVFeed module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Toli Kuznets</a>
 * @since 2.1.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("csv", Messages.class.getClassLoader());  //$NON-NLS-1$

    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    public static final I18NMessage1P UNKNOWN_EVENT_TYPE = new I18NMessage1P(LOGGER,
                                                                             "unknown_event_type"); //$NON-NLS-1$
    public static final I18NMessage1P UNKNOWN_ENTRY_TYPE = new I18NMessage1P(LOGGER,
                                                                             "unknown_entry_type"); //$NON-NLS-1$
    public static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                               "provider_description"); //$NON-NLS-1$
    public static final I18NMessage1P INVALID_REQUEST_DATA_TYPE = new I18NMessage1P(LOGGER,
                                                                                    "invalid_request_data_type"); //$NON-NLS-1$
    public static final I18NMessage0P CSV_FILE_DNE = new I18NMessage0P(LOGGER, "csv_file_dne"); //$NON-NLS-1$
    public static final I18NMessage1P UNABLE_PARSE_CSV = new I18NMessage1P(LOGGER, "unable_parse_csv"); //$NON-NLS-1$
    public static final I18NMessage1P UNABLE_PARSE_CSV_LINE = new I18NMessage1P(LOGGER, "unable_parse_csv_line"); //$NON-NLS-1$
    public static final I18NMessage2P END_OF_DATA_REACHED = new I18NMessage2P(LOGGER, "end_of_data_reached"); //$NON-NLS-1$
    public static final I18NMessage1P CANCEL_REQUEST_FAILED_HANDLE_NOT_FOUND = new I18NMessage1P(LOGGER,
                                                                                                 "cancel_request_failed_handle_not_found"); //$NON-NLS-1$
    public static final I18NMessage0P FAILED_TO_START_REQUEST = new I18NMessage0P(LOGGER,
                                                                                  "failed_to_start_request"); //$NON-NLS-1$
    public static final I18NMessage1P REQUEST_FAILED = new I18NMessage1P(LOGGER,
                                                                         "request_failed"); //$NON-NLS-1$
    public static final I18NMessage0P INVALID_EVENT_DELAY = new I18NMessage0P(LOGGER,
                                                                              "invalid_event_delay"); //$NON-NLS-1$
    public static final I18NMessage1P INVALID_EVENT_TRANSLATOR = new I18NMessage1P(LOGGER,
                                                                                   "invalid_event_translator"); //$NON-NLS-1$
    public static final I18NMessage2P UNKNOWN_BASIC_EVENT_TYPE = new I18NMessage2P(LOGGER,
                                                                                   "unknown_basic_event_type"); //$NON-NLS-1$
    public static final I18NMessage1P CANNOT_GUESS_BIG_DECIMAL = new I18NMessage1P(LOGGER,
                                                                                   "cannot_guess_big_decimal"); //$NON-NLS-1$
    public static final I18NMessage1P CANNOT_GUESS_DATE = new I18NMessage1P(LOGGER,
                                                                            "cannot_guess_date"); //$NON-NLS-1$
    public static final I18NMessage2P CANNOT_INTERPRET_DIVIDEND_FREQUENCY = new I18NMessage2P(LOGGER,
                                                                                              "cannot_interpret_dividend_frequency"); //$NON-NLS-1$
    public static final I18NMessage2P CANNOT_INTERPRET_DIVIDEND_STATUS = new I18NMessage2P(LOGGER,
                                                                                           "cannot_interpret_dividend_status"); //$NON-NLS-1$
    public static final I18NMessage2P CANNOT_INTERPRET_DIVIDEND_TYPE = new I18NMessage2P(LOGGER,
                                                                                         "cannot_interpret_dividend_type"); //$NON-NLS-1$
    public static final I18NMessage3P UNSUPPORTED_CFI_CODE = new I18NMessage3P(LOGGER,
                                                                               "unsupported_cfi_code"); //$NON-NLS-1$
    public static final I18NMessage3P INVALID_CFI_CODE = new I18NMessage3P(LOGGER,
                                                                           "invalid_cfi_code"); //$NON-NLS-1$
    public static final I18NMessage2P NOT_OSI_COMPLIANT = new I18NMessage2P(LOGGER,
                                                                            "not_osi_compliant"); //$NON-NLS-1$
    public static final I18NMessage1P UNKNOWN_SYMBOL_FORMAT = new I18NMessage1P(LOGGER,
                                                                                "unknown_symbol_format"); //$NON-NLS-1$
    public static final I18NMessage2P LINE_MISSING_REQUIRED_FIELDS = new I18NMessage2P(LOGGER,
                                                                                       "line_missing_required_fields"); //$NON-NLS-1$
    public static final I18NMessage1P UNABLE_TO_CONSTRUCT_DIVIDEND = new I18NMessage1P(LOGGER,
                                                                                       "unable_to_construct_dividend"); //$NON-NLS-1$
    public static final I18NMessage1P UNABLE_TO_CONSTRUCT_QUOTE = new I18NMessage1P(LOGGER,
                                                                                    "unable_to_construct_quote"); //$NON-NLS-1$
    public static final I18NMessage1P UNABLE_TO_CONSTRUCT_TRADE = new I18NMessage1P(LOGGER,
                                                                                    "unable_to_construct_trade"); //$NON-NLS-1$
    public static final I18NMessage1P UNABLE_TO_CONSTRUCT_MARKETSTAT = new I18NMessage1P(LOGGER,
                                                                                         "unable_to_construct_marketstat"); //$NON-NLS-1$
    public static final I18NMessage0P EMPTY_LINE = new I18NMessage0P(LOGGER,
                                                                     "empty_line"); //$NON-NLS-1$
}
