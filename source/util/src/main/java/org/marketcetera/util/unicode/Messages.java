package org.marketcetera.util.unicode;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
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
        new I18NMessageProvider("util_unicode");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage1P UNKNOWN_CHARSET=
        new I18NMessage1P(LOGGER,"unknown_charset");
    static final I18NMessage0P STREAM_CLOSED=
        new I18NMessage0P(LOGGER,"stream_closed");
    static final I18NMessage0P STREAM_ACCESS_ERROR=
        new I18NMessage0P(LOGGER,"stream_access_error");
    static final I18NMessage1P CANNOT_GET_LENGTH=
        new I18NMessage1P(LOGGER,"cannot_get_length");
    static final I18NMessage0P NO_SIGNATURE_MATCHES=
        new I18NMessage0P(LOGGER,"no_signature_matches");
}
