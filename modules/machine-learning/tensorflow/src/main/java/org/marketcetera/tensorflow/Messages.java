package org.marketcetera.tensorflow;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Provides messages for the tensorflow package.
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

    static final I18NMessage0P CONVERTER_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"converter_provider_description"); //$NON-NLS-1$
    static final I18NMessage1P NO_MODEL_ERROR = new I18NMessage1P(LOGGER,"no_model_error"); //$NON-NLS-1$
    static final I18NMessage1P UNKNOWN_REQUEST_TYPE = new I18NMessage1P(LOGGER,"unknown_request_type"); //$NON-NLS-1$
    static final I18NMessage0P MODEL_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"model_provider_description"); //$NON-NLS-1$
    static final I18NMessage2P INVALID_DATA_TYPE = new I18NMessage2P(LOGGER,"invalid_data_type"); //$NON-NLS-1$
}
