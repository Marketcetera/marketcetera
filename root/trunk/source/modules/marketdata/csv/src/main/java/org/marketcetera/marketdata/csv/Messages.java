package org.marketcetera.marketdata.csv;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Messages for CSVFeed plug-in.
 *
 * @author <a href="mailto:colin@marketcetera.com">Toli Kuznets</a>
 * @version $Id: Messages.java 4348 2009-09-24 02:33:11Z toli $
 * @since 1.0
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
    public static final I18NMessage1P CANCEL_NOT_IMPL = new I18NMessage1P(LOGGER, "cancel_not_impl"); //$NON-NLS-1$
    public static final I18NMessage0P END_OF_DATA_REACHED = new I18NMessage0P(LOGGER, "end_of_data_reached"); //$NON-NLS-1$

}
