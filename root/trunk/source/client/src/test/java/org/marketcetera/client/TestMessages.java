package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages used by test code.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public interface TestMessages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("client_test");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_ORDER_SENDER =
            new I18NMessage0P(LOGGER, "provider_order_sender");   //$NON-NLS-1$
}