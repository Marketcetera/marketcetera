package org.marketcetera.modules.async;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages for this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("modules_async",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage1P JMX_ATTRIBUTE_FLOW_CNT_DESCRIPTION =
            new I18NMessage1P(LOGGER, "jmx_attribute_flow_cnt_description");   //$NON-NLS-1$
    static final I18NMessage0P JMX_MXBEAN_DESCRIPTION =
            new I18NMessage0P(LOGGER, "jmx_mxbean_description");   //$NON-NLS-1$
    static final I18NMessage1P DATA_RECVD_UNKNOWN_FLOW =
            new I18NMessage1P(LOGGER, "data_recvd_unknown_flow");   //$NON-NLS-1$
    static final I18NMessage1P MXBEAN_ATTRIB_NOT_WRITABLE =
            new I18NMessage1P(LOGGER, "mxbean_attrib_not_writable");   //$NON-NLS-1$

}