package org.marketcetera.util.quickfix;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
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
        new I18NMessageProvider("util_quickfix"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P MISSING_SETTINGS=
        new I18NMessage0P(LOGGER,"missing_settings"); //$NON-NLS-1$
    static final I18NMessage1P FIELD_REQUIRED=
        new I18NMessage1P(LOGGER,"field_required"); //$NON-NLS-1$
    static final I18NMessage1P NO_DEFAULT_DATA_DICTIONARY=
        new I18NMessage1P(LOGGER,"no_default_data_dictionary"); //$NON-NLS-1$
    static final I18NMessage0P CONFIG_ERROR=
        new I18NMessage0P(LOGGER,"config_error"); //$NON-NLS-1$
    static final I18NMessage1P BAD_LOG_FACTORY=
        new I18NMessage1P(LOGGER,"bad_log_factory"); //$NON-NLS-1$
    static final I18NMessage1P BAD_MESSAGE_STORE_FACTORY=
        new I18NMessage1P(LOGGER,"bad_message_store_factory"); //$NON-NLS-1$
}
