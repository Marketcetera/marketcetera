package org.marketcetera.marketdata.csv;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Messages for CSVFeed module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Toli Kuznets</a>
 * @since $Release$
 * @version $Id: Messages.java 4348 2009-09-24 02:33:11Z toli $
 */
@ClassVersion("$Id: Messages.java 4348 2009-09-24 02:33:11Z toli $") //$NON-NLS-1$
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
    public static final I18NMessage0P END_OF_DATA_REACHED = new I18NMessage0P(LOGGER, "end_of_data_reached"); //$NON-NLS-1$
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
}
