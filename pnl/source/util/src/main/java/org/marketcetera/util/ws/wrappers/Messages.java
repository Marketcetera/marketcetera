package org.marketcetera.util.ws.wrappers;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
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
        new I18NMessageProvider("util_ws_wrappers"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P SERIALIZATION_ERROR=
        new I18NMessage0P(LOGGER,"serialization_error"); //$NON-NLS-1$
    static final I18NMessage0P RECEIVER_WRAPS_NULL=
        new I18NMessage0P(LOGGER,"receiver_wraps_null"); //$NON-NLS-1$
    static final I18NMessage0P ARGUMENT_IS_NULL=
        new I18NMessage0P(LOGGER,"argument_is_null"); //$NON-NLS-1$
    static final I18NMessage0P ARGUMENT_WRAPS_NULL=
        new I18NMessage0P(LOGGER,"argument_wraps_null"); //$NON-NLS-1$
    static final I18NMessage0P REMOTE_EXCEPTION=
        new I18NMessage0P(LOGGER,"remote_exception"); //$NON-NLS-1$
}
