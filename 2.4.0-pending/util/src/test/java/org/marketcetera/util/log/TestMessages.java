package org.marketcetera.util.log;

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
        new I18NMessageProvider("util_log_test");
    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage0P HELLO_MSG=
        new I18NMessage0P(LOGGER,"hello");
    static final I18NMessage1P HELLO_TITLE=
        new I18NMessage1P(LOGGER,"hello","title");
    static final I18NMessage1P HELLO_ECHO=
        new I18NMessage1P(LOGGER,"hello","echo");
    static final I18NMessage1P CHOICE_MSG=
        new I18NMessage1P(LOGGER,"choice");
    static final I18NMessage1P LOG_MSG=
        new I18NMessage1P(LOGGER,"log");

    static final I18NMessageNP BASE_MSG=
        new I18NMessageNP(LOGGER,"base");
    static final I18NMessageNP BASE_TTL=
        new I18NMessageNP(LOGGER,"base","ttl");

    static final I18NMessage0P P0_MSG=
        new I18NMessage0P(LOGGER,"p0");
    static final I18NMessage0P P0_MSG_COPY=
        new I18NMessage0P(LOGGER,"p0");
    static final I18NMessage0P P0_TTL=
        new I18NMessage0P(LOGGER,"p0","ttl");
    static final I18NMessage0P P0_TTL_COPY=
        new I18NMessage0P(LOGGER,"p0","ttl");
    static final I18NMessage1P P1_MSG=
        new I18NMessage1P(LOGGER,"p1");
    static final I18NMessage1P P1_TTL=
        new I18NMessage1P(LOGGER,"p1","ttl");
    static final I18NMessage2P P2_MSG=
        new I18NMessage2P(LOGGER,"p2");
    static final I18NMessage2P P2_TTL=
        new I18NMessage2P(LOGGER,"p2","ttl");
    static final I18NMessage3P P3_MSG=
        new I18NMessage3P(LOGGER,"p3");
    static final I18NMessage3P P3_TTL=
        new I18NMessage3P(LOGGER,"p3","ttl");
    static final I18NMessage4P P4_MSG=
        new I18NMessage4P(LOGGER,"p4");
    static final I18NMessage4P P4_TTL=
        new I18NMessage4P(LOGGER,"p4","ttl");
    static final I18NMessage5P P5_MSG=
        new I18NMessage5P(LOGGER,"p5");
    static final I18NMessage5P P5_TTL=
        new I18NMessage5P(LOGGER,"p5","ttl");
    static final I18NMessage6P P6_MSG=
        new I18NMessage6P(LOGGER,"p6");
    static final I18NMessage6P P6_TTL=
        new I18NMessage6P(LOGGER,"p6","ttl");
    static final I18NMessageNP PN_MSG=
        new I18NMessageNP(LOGGER,"pn");
    static final I18NMessageNP PN_TTL=
        new I18NMessageNP(LOGGER,"pn","ttl");

    static final I18NMessage0P NONEXISTENT=
        new I18NMessage0P(LOGGER,"nonexistent_msg");
}
