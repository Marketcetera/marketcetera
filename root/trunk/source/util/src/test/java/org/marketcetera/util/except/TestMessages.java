package org.marketcetera.util.except;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;

public interface TestMessages
{
    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("except_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage BOT_EXCEPTION=
        new I18NMessage(LOGGER,"bot_exception");
    static final I18NMessage MID_EXCEPTION=
        new I18NMessage(LOGGER,"mid_exception");
    static final I18NMessage TOP_EXCEPTION=
        new I18NMessage(LOGGER,"top_exception");
}
