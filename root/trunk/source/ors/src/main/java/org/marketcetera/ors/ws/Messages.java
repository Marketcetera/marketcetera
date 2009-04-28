package org.marketcetera.ors.ws;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
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
        new I18NMessageProvider("ors_ws"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage3P VERSION_MISMATCH=
        new I18NMessage3P(LOGGER,"version_mismatch"); //$NON-NLS-1$
    static final I18NMessage1P BAD_CREDENTIALS=
        new I18NMessage1P(LOGGER,"bad_credentials"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CREATE_REPLY_TOPIC=
        new I18NMessage1P(LOGGER,"cannot_create_reply_topic"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_RETRIEVE_USER=
        new I18NMessage1P(LOGGER,"cannot_retrieve_user"); //$NON-NLS-1$
    static final I18NMessage2P CLIENT_SESSION_STRING=
        new I18NMessage2P(LOGGER,"client_session_string"); //$NON-NLS-1$
}
