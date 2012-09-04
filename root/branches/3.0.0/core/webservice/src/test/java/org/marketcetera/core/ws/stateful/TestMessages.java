package org.marketcetera.core.ws.stateful;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage2P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/**
 * @since 1.0.0
 * @version $Id: TestMessages.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public interface TestMessages
{
    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_ws_stateful_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage2P MESSAGE=
        new I18NMessage2P(LOGGER,"message");
}
