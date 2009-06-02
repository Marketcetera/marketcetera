package org.marketcetera.metrics;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * The message constants for internationalized messages used in this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_metrics");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage1P STDERR_STREAM_SUMMARY_HEADER =
            new I18NMessage1P(LOGGER, "stderr_stream_summary_header");   //$NON-NLS-1$
    
    static final I18NMessage3P LOG_NON_NUMERIC_PROPERTY =
            new I18NMessage3P(LOGGER, "log_non_numeric_property");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERR_LOADING_PROPERTIES =
            new I18NMessage1P(LOGGER, "log_err_loading_properties");   //$NON-NLS-1$
    static final I18NMessage2P LOG_CREATED_METRICS_FILE =
            new I18NMessage2P(LOGGER, "log_created_metrics_file");   //$NON-NLS-1$
    static final I18NMessage1P LOG_REGISTERED_MXBEAN =
            new I18NMessage1P(LOGGER, "log_registered_mxbean");   //$NON-NLS-1$
    static final I18NMessage1P LOG_UNREGISTERED_MXBEAN =
            new I18NMessage1P(LOGGER, "log_unregistered_mxbean");   //$NON-NLS-1$
    static final I18NMessage0P LOG_ERROR_SUMMARIZING =
            new I18NMessage0P(LOGGER, "log_error_summarizing");   //$NON-NLS-1$
    static final I18NMessage0P LOG_ERROR_REGISTER_MXBEAN =
            new I18NMessage0P(LOGGER, "log_error_register_mxbean");   //$NON-NLS-1$
    static final I18NMessage0P LOG_ERROR_UNREGISTER_MXBEAN =
            new I18NMessage0P(LOGGER, "log_error_unregister_mxbean");   //$NON-NLS-1$


}