package org.marketcetera.util.ws.sample;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
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

@ClassVersion("$Id$") //$NON-NLS-1$
public interface SampleMessages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_ws_sample_test"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage2P SHORT_GREETING=
        new I18NMessage2P(LOGGER,"short_greeting"); //$NON-NLS-1$
    static final I18NMessage2P LONG_GREETING=
        new I18NMessage2P(LOGGER,"long_greeting"); //$NON-NLS-1$
    static final I18NMessage0P EXCEPTION_MESSAGE=
        new I18NMessage0P(LOGGER,"exception_message"); //$NON-NLS-1$
}