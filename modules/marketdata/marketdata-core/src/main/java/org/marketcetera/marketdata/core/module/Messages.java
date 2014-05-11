package org.marketcetera.marketdata.core.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */

/**
 * Internationalized messages for this package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id: Messages.java 16154 2012-07-14 16:34:05Z colin $")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("modules_receiver",Messages.class.getClassLoader());  //$NON-NLS-1$ 
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage0P INCORRECT_PARAMETER_COUNT = new I18NMessage0P(LOGGER, "incorrect_parameter_count");   //$NON-NLS-1$
}
