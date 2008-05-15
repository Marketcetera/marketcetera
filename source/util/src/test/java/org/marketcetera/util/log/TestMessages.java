package org.marketcetera.util.log;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;

public interface TestMessages
{
    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("log_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage HELLO_MSG=
        new I18NMessage(LOGGER,"hello");
    static final I18NMessage HELLO_TITLE=
        new I18NMessage(LOGGER,"hello","title");
    static final I18NMessage LOG_MSG=
        new I18NMessage(LOGGER,"log");

    static final I18NMessage NONEXISTENT=
        new I18NMessage(LOGGER,"nonexistent_msg");
}
