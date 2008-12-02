package org.marketcetera.ors.dest;

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
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("ors_dest"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P NO_DEFAULT_DESTINATION=
        new I18NMessage0P(LOGGER,"no_default_destination"); //$NON-NLS-1$
    static final I18NMessage0P NO_DESTINATION=
        new I18NMessage0P(LOGGER,"no_destination"); //$NON-NLS-1$
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
    static final I18NMessage0P NO_DESTINATIONS=
        new I18NMessage0P(LOGGER,"no_destinations"); //$NON-NLS-1$

    static final I18NMessage1P UNKNOWN_SECURITY_TYPE=
        new I18NMessage1P(LOGGER,"unknown_security_type"); //$NON-NLS-1$

    static final I18NMessage1P INVALID_SESSION_ID=
        new I18NMessage1P(LOGGER,"invalid_session_id"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_DESTINATION_ID=
        new I18NMessage1P(LOGGER,"invalid_destination_id"); //$NON-NLS-1$
    static final I18NMessage1P ANALYZED_MESSAGE=
        new I18NMessage1P(LOGGER,"analyzed_message"); //$NON-NLS-1$
    static final I18NMessage3P DESTINATION_STRING=
        new I18NMessage3P(LOGGER,"destination_string"); //$NON-NLS-1$
}
