package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages for classes in this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("modules_object",  //$NON-NLS-1$
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P RECORDER_PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "recorder_provider_description");   //$NON-NLS-1$
    static final I18NMessage0P EMITTER_PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "emitter_provider_description");   //$NON-NLS-1$

    static final I18NMessage1P LOG_CLOSE_FILE_ERROR =
            new I18NMessage1P(LOGGER, "log_close_file_error");   //$NON-NLS-1$
    static final I18NMessage0P NO_MORE_DATA =
            new I18NMessage0P(LOGGER, "no_more_data");   //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_ERROR =
            new I18NMessage1P(LOGGER, "unexpected_error");   //$NON-NLS-1$

}