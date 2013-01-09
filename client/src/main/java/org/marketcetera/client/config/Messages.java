package org.marketcetera.client.config;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 1.5.0
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
        new I18NMessageProvider("client_config",  //$NON-NLS-1$ 
                                Messages.class.getClassLoader());

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P NO_INCOMING_CONNECTION_FACTORY=
        new I18NMessage0P
        (LOGGER,"no_incoming_connection_factory"); //$NON-NLS-1$
    static final I18NMessage0P NO_OUTGOING_CONNECTION_FACTORY=
        new I18NMessage0P
        (LOGGER,"no_outgoing_connection_factory"); //$NON-NLS-1$
}
