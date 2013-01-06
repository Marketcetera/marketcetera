package org.marketcetera.core.util.thread;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage1P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @version $Id$
 * @since $Release$
 */

/* $License$ */

public interface Messages
{
    /**
     * message provider value
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("util_thread",Messages.class.getClassLoader()); //$NON-NLS-1$
    /**
     * logger provider
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * The messages:
     */
    static final I18NMessage1P UNABLE_TO_START = new I18NMessage1P(LOGGER,"unable_to_start"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_DURING_STOP = new I18NMessage1P(LOGGER,"error_during_stop"); //$NON-NLS-1$
    static final I18NMessage1P STARTED = new I18NMessage1P(LOGGER,"started"); //$NON-NLS-1$
    static final I18NMessage1P STOPPED = new I18NMessage1P(LOGGER,"stopped"); //$NON-NLS-1$
    static final I18NMessage1P IGNORING_EXCEPTION = new I18NMessage1P(LOGGER,"ignoring_exception"); //$NON-NLS-1$
    static final I18NMessage1P INTERRUPTED = new I18NMessage1P(LOGGER,"interrupted"); //$NON-NLS-1$
    static final I18NMessage1P SHUTTING_DOWN_FROM_ERROR = new I18NMessage1P(LOGGER,"shutting_down_from_error"); //$NON-NLS-1$
}
