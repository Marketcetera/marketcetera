package org.marketcetera.core.options;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage0P;
import org.marketcetera.core.util.log.I18NMessage1P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/* $License$ */
/**
 * Internationalized messages for this package. 
 *
 * @version $Id: Messages.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_options");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    /**
     * The messages.
     */
    static final I18NMessage0P LOG_ERROR_LOADING_OPTION_EXPIRY_NORMALIZER =
            new I18NMessage0P(LOGGER, "log_error_loading_option_expiry_normalizer");   //$NON-NLS-1$
    static final I18NMessage1P LOG_OPTION_EXPIRY_NORMALIZER_CUSTOMIZED =
            new I18NMessage1P(LOGGER, "log_option_expiry_normalizer_customized");   //$NON-NLS-1$
}