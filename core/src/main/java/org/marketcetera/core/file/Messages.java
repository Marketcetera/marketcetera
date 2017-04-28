package org.marketcetera.core.file;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Provides messages for this package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 84991 2015-11-06 16:53:53Z colin $
 * @since $Release$
 */
public interface Messages
{

    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("core_file", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage2P SUBSCRIBER_FILE_PROCESSING_FAILED = new I18NMessage2P(LOGGER,"subscriber_file_processing_failed"); //$NON-NLS-1$
    static final I18NMessage1P FILE_DELETE_FAILURE = new I18NMessage1P(LOGGER,"file_delete_failure"); //$NON-NLS-1$
    static final I18NMessage1P MISSING_DIRECTORY = new I18NMessage1P(LOGGER,"missing_directory"); //$NON-NLS-1$
    static final I18NMessage1P DIRECTORY_ACCESS_DENIED = new I18NMessage1P(LOGGER,"directory_access_denied"); //$NON-NLS-1$
}
