package org.marketcetera.core.container;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage0P;
import org.marketcetera.core.util.log.I18NMessage1P;
import org.marketcetera.core.util.log.I18NMessage3P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Provides messages for the CORE package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 82324 2012-04-09 20:56:08Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Messages.java 82324 2012-04-09 20:56:08Z colin $")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("container", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    // the messages
    static final I18NMessage0P APP_COPYRIGHT = new I18NMessage0P(LOGGER,
                                                                 "app_copyright"); //$NON-NLS-1$
    static final I18NMessage3P APP_VERSION_BUILD = new I18NMessage3P(LOGGER,
                                                                     "app_version_build");   //$NON-NLS-1$
    static final I18NMessage1P APP_STARTING = new I18NMessage1P(LOGGER,
                                                                "app_starting"); //$NON-NLS-1$
    static final I18NMessage1P APP_STARTED = new I18NMessage1P(LOGGER,
                                                               "app_started"); //$NON-NLS-1$
    static final I18NMessage1P APP_STOPPING = new I18NMessage1P(LOGGER,
                                                                "app_stopping"); //$NON-NLS-1$
    static final I18NMessage1P APP_STOPPED = new I18NMessage1P(LOGGER,
                                                               "app_stopped"); //$NON-NLS-1$
    static final I18NMessage1P NULL_CONTEXT = new I18NMessage1P(LOGGER,
                                                                "null_context");   //$NON-NLS-1$
    static final I18NMessage1P APP_START_ERROR = new I18NMessage1P(LOGGER,
                                                                   "app_start_error");   //$NON-NLS-1$

    I18NMessage0P LAZY_ALREADY_PROCESSED=
        new I18NMessage0P(LOGGER,"lazy_already_processed"); //$NON-NLS-1$
    I18NMessage0P LAZY_IN_PROCESS=
        new I18NMessage0P(LOGGER,"lazy_in_process"); //$NON-NLS-1$
}
