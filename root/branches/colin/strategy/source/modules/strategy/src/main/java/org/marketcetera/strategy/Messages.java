package org.marketcetera.strategy;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
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
}
