package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_ws_stateful"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage2P CLIENT_CONTEXT=
        new I18NMessage2P(LOGGER,"client_context"); //$NON-NLS-1$
    static final I18NMessage0P BAD_CREDENTIALS=
        new I18NMessage0P(LOGGER,"bad_credentials"); //$NON-NLS-1$
    static final I18NMessage0P ALREADY_LOGGED_IN=
        new I18NMessage0P(LOGGER,"already_logged_in"); //$NON-NLS-1$
    static final I18NMessage0P NOT_LOGGED_IN=
        new I18NMessage0P(LOGGER,"not_logged_in"); //$NON-NLS-1$
    static final I18NMessage0P REAPER_THREAD_NAME=
        new I18NMessage0P(LOGGER,"reaper_thread_name"); //$NON-NLS-1$
    static final I18NMessage2P REAPER_EXPIRED_SESSION=
        new I18NMessage2P(LOGGER,"reaper_expired_session"); //$NON-NLS-1$
    static final I18NMessage1P REAPER_TERMINATED=
        new I18NMessage1P(LOGGER,"reaper_terminated"); //$NON-NLS-1$
}
