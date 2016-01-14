package org.marketcetera.marketdata.recorder;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
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
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdatarecorder",Messages.class.getClassLoader());  //$NON-NLS-1$ 
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P FILERECORDER_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER, "filerecorder_provider_description");   //$NON-NLS-1$
    static final I18NMessage0P PARAMETER_COUNT_ERROR = new I18NMessage0P(LOGGER, "filerecorder_parameter_count_error");   //$NON-NLS-1$
    static final I18NMessage1P FILERECORDER_IGNORING_UNEXPECTED_DATA = new I18NMessage1P(LOGGER,"filerecorder_ignoring_unexpected_data");   //$NON-NLS-1$
    static final I18NMessage0P TIMESTAMP_GENERATOR_REQUIRED = new I18NMessage0P(LOGGER, "timestamp_generator_required");   //$NON-NLS-1$
    static final I18NMessage0P SESSION_RESET_REQUIRED = new I18NMessage0P(LOGGER, "session_reset_required");   //$NON-NLS-1$
    static final I18NMessage3P STOPPING_DATA_FLOW = new I18NMessage3P(LOGGER,"stopping_data_flow");   //$NON-NLS-1$
    static final I18NMessage1P NOT_A_DIRECTORY = new I18NMessage1P(LOGGER,"not_a_directory");   //$NON-NLS-1$
    static final I18NMessage0P EVENT_BOUNDARY_CAPABILITY_REQUIRED = new I18NMessage0P(LOGGER,"event_boundary_capability_required");   //$NON-NLS-1$
}
