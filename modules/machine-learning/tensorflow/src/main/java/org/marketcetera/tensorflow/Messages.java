package org.marketcetera.tensorflow;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("tensorflow", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"provider_description"); //$NON-NLS-1$
    static final I18NMessage1P FEED_NOT_AVAILABLE = new I18NMessage1P(LOGGER,"feed_not_available"); //$NON-NLS-1$
    static final I18NMessage1P DATA_PROCESSING_ERROR = new I18NMessage1P(LOGGER,"data_processing_error"); //$NON-NLS-1$
}
