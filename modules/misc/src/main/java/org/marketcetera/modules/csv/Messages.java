package org.marketcetera.modules.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("modules_csv",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$

    static final I18NMessage1P INSUFFICIENT_DATA =
            new I18NMessage1P(LOGGER, "insufficient_data");  //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_ERROR =
            new I18NMessage1P(LOGGER, "unexpected_error");  //$NON-NLS-1$
    static final I18NMessage0P NO_MORE_DATA =
            new I18NMessage0P(LOGGER, "no_more_data");  //$NON-NLS-1$
}
