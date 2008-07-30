package org.marketcetera.util.except;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public interface TestMessages
{
    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_except_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage0P BOT_EXCEPTION=
        new I18NMessage0P(LOGGER,"bot_exception");
    static final I18NMessage1P MID_EXCEPTION=
        new I18NMessage1P(LOGGER,"mid_exception");
    static final I18NMessage0P TOP_EXCEPTION=
        new I18NMessage0P(LOGGER,"top_exception");
}
