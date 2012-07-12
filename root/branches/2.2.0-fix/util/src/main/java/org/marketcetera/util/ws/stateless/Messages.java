package org.marketcetera.util.ws.stateless;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage4P;
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
        new I18NMessageProvider("util_ws_stateless"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage2P VERSION_MISMATCH=
        new I18NMessage2P(LOGGER,"version_mismatch"); //$NON-NLS-1$
    static final I18NMessage4P STATELESS_CLIENT_CONTEXT=
        new I18NMessage4P(LOGGER,"stateless_client_context"); //$NON-NLS-1$
    static final I18NMessage1P CALL_START=
        new I18NMessage1P(LOGGER,"call_start"); //$NON-NLS-1$
    static final I18NMessage1P CALL_SUCCESS=
        new I18NMessage1P(LOGGER,"call_success"); //$NON-NLS-1$
    static final I18NMessage1P CALL_FAILURE=
        new I18NMessage1P(LOGGER,"call_failure"); //$NON-NLS-1$
}
