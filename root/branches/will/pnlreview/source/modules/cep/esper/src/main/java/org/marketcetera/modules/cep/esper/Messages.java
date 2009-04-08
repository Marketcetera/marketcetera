package org.marketcetera.modules.cep.esper;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages for this package. 
 *
 * @author anshul@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("modules_cep_esper",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_MODULE_NOT_STARTED =
            new I18NMessage0P(LOGGER, "error_module_not_started");  //$NON-NLS-1$
    static final I18NMessage0P ERROR_MODULE_ALREADY_STARTED =
            new I18NMessage0P(LOGGER, "error_module_already_started");  //$NON-NLS-1$
    static final I18NMessage1P ERROR_CREATING_STATEMENTS = new I18NMessage1P(LOGGER, "error_create_stmnt");  //$NON-NLS-1$
    static final I18NMessage0P ERROR_CONFIGURING_ESPER = new I18NMessage0P(LOGGER, "error_config_esper");  //$NON-NLS-1$
}
