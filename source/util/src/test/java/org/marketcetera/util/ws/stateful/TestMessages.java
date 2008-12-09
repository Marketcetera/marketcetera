package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
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
