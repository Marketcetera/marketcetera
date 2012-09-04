package org.marketcetera.core.ws.stateless;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage2P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/**
 * @since 1.0.0
 * @version $Id: TestMessages.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public interface TestMessages
{
    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_ws_stateless_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage2P MESSAGE=
        new I18NMessage2P(LOGGER,"message");
}
