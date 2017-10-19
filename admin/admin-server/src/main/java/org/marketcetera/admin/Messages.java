package org.marketcetera.admin;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */
/**
 * Messages
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: Messages.java 16468 2014-05-12 00:36:56Z colin $")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("admin_server"); //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage1P CANNOT_SET_PASSWORD = new I18NMessage1P(LOGGER, "cannot_set_password"); //$NON-NLS-1$
    static final I18NMessage0P EMPTY_PASSWORD = new I18NMessage0P(LOGGER, "empty_password"); //$NON-NLS-1$
    static final I18NMessage0P INVALID_PASSWORD = new I18NMessage0P(LOGGER, "invalid_password"); //$NON-NLS-1$
    static final I18NMessage0P SIMPLE_USER_NAME = new I18NMessage0P(LOGGER, "simple_user_name"); //$NON-NLS-1$
    static final I18NMessage1P BAD_CREDENTIALS = new I18NMessage1P(LOGGER,"bad_credentials"); //$NON-NLS-1$
    static final I18NMessage3P VERSION_MISMATCH = new I18NMessage3P(LOGGER,"version_mismatch"); //$NON-NLS-1$
    static final I18NMessage2P APP_MISMATCH = new I18NMessage2P(LOGGER, "app_mismatch");   //$NON-NLS-1$
}
