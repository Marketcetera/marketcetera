package org.marketcetera.persist.example;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;

/* $License$ */
/**
 * Messages used for unit testing
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_persist_test");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    /**
     * The localized name for users
     */
    static final I18NMessage0P NAME_USER = new I18NMessage0P(LOGGER,"name_user"); //$NON-NLS-1$
    /**
     * The localized name for groups
     */
    static final I18NMessage0P NAME_GROUP = new I18NMessage0P(LOGGER,"name_group"); //$NON-NLS-1$
}
