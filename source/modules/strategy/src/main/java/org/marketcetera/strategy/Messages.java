package org.marketcetera.strategy;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Message keys for the strategy module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("strategy");  //$NON-NLS-1$
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P COMPILATION_ERROR = new I18NMessage0P(LOGGER,
                                                                     "compilation_error"); //$NON-NLS-1$
    static final I18NMessage1P STRATEGY_NOT_FOUND = new I18NMessage1P(LOGGER,
                                                                      "strategy_not_found"); //$NON-NLS-1$
    static final I18NMessage1P STRATEGY_ALREADY_REGISTERED = new I18NMessage1P(LOGGER,
                                                                               "strategy_already_registered"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_STRATEGY_SUPERCLASS = new I18NMessage1P(LOGGER,
                                                                               "invalid_strategy_superclass"); //$NON-NLS-1$
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                        "provider_description"); //$NON-NLS-1$
    static final I18NMessage0P PARAMETER_COUNT_ERROR = new I18NMessage0P(LOGGER,
                                                                         "parameter_count_error"); //$NON-NLS-1$
    static final I18NMessage3P PARAMETER_TYPE_ERROR = new I18NMessage3P(LOGGER,
                                                                        "parameter_type_error"); //$NON-NLS-1$
    static final I18NMessage2P NULL_PARAMETER_ERROR = new I18NMessage2P(LOGGER,
                                                                        "null_parameter_error"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_LANGUAGE_ERROR = new I18NMessage1P(LOGGER,
                                                                          "invalid_language_error"); //$NON-NLS-1$
    static final I18NMessage0P EMPTY_NAME_ERROR = new I18NMessage0P(LOGGER,
                                                                    "empty_name_error"); //$NON-NLS-1$
    static final I18NMessage1P FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE = new I18NMessage1P(LOGGER,
                                                                                          "file_does_not_exist_or_is_not_readable"); //$NON-NLS-1$
}
