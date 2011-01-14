package org.marketcetera.server;

import org.marketcetera.util.log.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since $Release$
 * @version $Id: Messages.java 10936 2010-04-20 22:54:11Z colin $
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * provides messages for this package
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("server");  //$NON-NLS-1$
    /**
     * logger object used to server messages
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * The messages.
     */
    static final I18NMessage0P APP_COPYRIGHT = new I18NMessage0P(LOGGER,
                                                                 "app_copyright"); //$NON-NLS-1$
    static final I18NMessage2P APP_VERSION_BUILD = new I18NMessage2P(LOGGER,
                                                                     "app_version_build"); //$NON-NLS-1$
    static final I18NMessage0P APP_START = new I18NMessage0P(LOGGER,
                                                             "app_start"); //$NON-NLS-1$
    static final I18NMessage0P APP_STARTED = new I18NMessage0P(LOGGER,
                                                               "app_started"); //$NON-NLS-1$
    static final I18NMessage0P APP_STOP = new I18NMessage0P(LOGGER,
                                                            "app_stop"); //$NON-NLS-1$
    static final I18NMessage0P APP_STOP_SUCCESS = new I18NMessage0P(LOGGER,
                                                                    "app_stop_success"); //$NON-NLS-1$
    static final I18NMessage0P APP_STOP_ERROR = new I18NMessage0P(LOGGER,
                                                                  "app_stop_error"); //$NON-NLS-1$
    static final I18NMessage1P APP_USAGE = new I18NMessage1P(LOGGER,
                                                             "app_usage"); //$NON-NLS-1$
    static final I18NMessage0P APP_AUTH_OPTIONS = new I18NMessage0P(LOGGER,
                                                                    "app_auth_options"); //$NON-NLS-1$
    static final I18NMessage0P APP_MISSING_CREDENTIALS = new I18NMessage0P(LOGGER,
                                                                           "app_missing_credentials"); //$NON-NLS-1$
    static final I18NMessage0P APP_NO_ARGS_ALLOWED = new I18NMessage0P(LOGGER,
                                                                       "app_no_args_allowed"); //$NON-NLS-1$
    static final I18NMessage0P APP_NO_CONFIGURATION = new I18NMessage0P(LOGGER,
                                                                        "app_no_configuration"); //$NON-NLS-1$
}
