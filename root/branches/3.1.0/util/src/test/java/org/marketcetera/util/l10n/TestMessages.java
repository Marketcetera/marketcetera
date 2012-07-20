package org.marketcetera.util.l10n;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessage6P;
import org.marketcetera.util.log.I18NMessageNP;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public interface TestMessages
{
    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_l10n_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage0P M0_MSG=
        new I18NMessage0P(LOGGER,"m0");
    static final I18NMessage1P M1_MSG=
        new I18NMessage1P(LOGGER,"m1");
    static final I18NMessage2P M2_MSG=
        new I18NMessage2P(LOGGER,"m2");
    static final I18NMessage3P M3_MSG=
        new I18NMessage3P(LOGGER,"m3");
    static final I18NMessage4P M4_MSG=
        new I18NMessage4P(LOGGER,"m4");
    static final I18NMessage5P M5_MSG=
        new I18NMessage5P(LOGGER,"m5");
    static final I18NMessage6P M6_MSG=
        new I18NMessage6P(LOGGER,"m6");
    static final I18NMessageNP M7_MSG=
        new I18NMessageNP(LOGGER,"m7");
    static final I18NMessageNP M8_MSG=
        new I18NMessageNP(LOGGER,"m8");
}
