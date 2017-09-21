package org.marketcetera.dataflow;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Provides messages for the package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Messages
{
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("dataflow_core",  //$NON-NLS-1$ 
                                                                        Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P DATA_RECEIVER_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER, "data_receiver_provider_description");   //$NON-NLS-1$
    static final I18NMessage0P DATA_SENDER_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER, "data_sender_provider_description");   //$NON-NLS-1$
}
