package org.marketcetera.ors.brokers;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
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
        new I18NMessageProvider("ors_brokers"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P NO_DEFAULT_BROKER=
        new I18NMessage0P(LOGGER,"no_default_broker"); //$NON-NLS-1$
    static final I18NMessage0P NO_BROKER=
        new I18NMessage0P(LOGGER,"no_broker"); //$NON-NLS-1$
    static final I18NMessage0P NO_TARGET_TYPE=
        new I18NMessage0P(LOGGER,"no_target_type"); //$NON-NLS-1$
    static final I18NMessage0P NO_DESCRIPTOR=
        new I18NMessage0P(LOGGER,"no_descriptor"); //$NON-NLS-1$
    static final I18NMessage0P NO_NAME=
        new I18NMessage0P(LOGGER,"no_name"); //$NON-NLS-1$
    static final I18NMessage0P NO_ID=
        new I18NMessage0P(LOGGER,"no_id"); //$NON-NLS-1$
    static final I18NMessage0P NO_SETTINGS=
        new I18NMessage0P(LOGGER,"no_settings"); //$NON-NLS-1$
    static final I18NMessage0P NO_BROKERS=
        new I18NMessage0P(LOGGER,"no_brokers"); //$NON-NLS-1$

    static final I18NMessage1P UNKNOWN_SECURITY_TYPE=
        new I18NMessage1P(LOGGER,"unknown_security_type"); //$NON-NLS-1$

    static final I18NMessage1P INVALID_SESSION_ID=
        new I18NMessage1P(LOGGER,"invalid_session_id"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_BROKER_ID=
        new I18NMessage1P(LOGGER,"invalid_broker_id"); //$NON-NLS-1$
    static final I18NMessage1P ANALYZED_MESSAGE=
        new I18NMessage1P(LOGGER,"analyzed_message"); //$NON-NLS-1$
    static final I18NMessage3P BROKER_STRING=
        new I18NMessage3P(LOGGER,"broker_string"); //$NON-NLS-1$
}
