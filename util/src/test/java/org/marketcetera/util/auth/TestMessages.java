package org.marketcetera.util.auth;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
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
        new I18NMessageProvider("util_auth_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage0P TEST_MESSAGE=
        new I18NMessage0P(LOGGER,"test_message");
    static final I18NMessage0P TEST_CONTEXT=
        new I18NMessage0P(LOGGER,"test_context");
    static final I18NMessage0P TEST_USAGE=
        new I18NMessage0P(LOGGER,"test_usage");
    static final I18NMessage0P TEST_PROMPT=
        new I18NMessage0P(LOGGER,"test_prompt");
    static final I18NMessage0P TEST_DESCRIPTION=
        new I18NMessage0P(LOGGER,"test_description");
}
