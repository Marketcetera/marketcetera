package org.marketcetera.util.log;

import java.io.Serializable;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TI18NMessage1PTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "P1 msg (expected) en "+TEST_P1;
    private static final String TEST_TTL_EN=
        "P1 ttl (expected) en "+TEST_P1;
    private static final String TEST_MSG_FR=
        "P1 msg (expected) fr "+TEST_P1;
    private static final String TEST_TTL_FR=
        "P1 ttl (expected) fr "+TEST_P1;
    private static final String TEST_LOCATION=
        TI18NMessage1PTest.class.getName();


    private static void castOverride
        (I18NMessage1P m) {}


    @Test
    public void basic()
    {
        unboundTests
            (1,
             new I18NMessage1P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage1P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage[] {
                new I18NMessage1P
                (TEST_LOGGER_D,TEST_MSG_ID,TEST_ENTRY_ID),
                new I18NMessage1P
                (TestMessages.LOGGER,TEST_MSG_ID_D,TEST_ENTRY_ID),
                new I18NMessage1P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID_D),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID)
             },
             new I18NMessage1P(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.P1_MSG.getText
             (TEST_P1));
        assertEquals
            (TEST_TTL_EN,TestMessages.P1_TTL.getText
             (TEST_P1));
        assertEquals
            (TEST_MSG_FR,TestMessages.P1_MSG.getText
             (Locale.FRENCH,TEST_P1));
        assertEquals
            (TEST_TTL_FR,TestMessages.P1_TTL.getText
             (Locale.FRENCH,TEST_P1));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.P1_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P1_MSG.error
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P1_TTL.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P1_TTL.error
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P1_MSG.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P1_MSG.warn
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P1_TTL.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P1_TTL.warn
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P1_MSG.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P1_MSG.info
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P1_TTL.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P1_TTL.info
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P1_MSG.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P1_MSG.debug
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P1_TTL.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P1_TTL.debug
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P1_MSG.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P1_MSG.trace
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P1_TTL.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P1_TTL.trace
            (TEST_CATEGORY,TEST_P1);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
    }

    @Test
    public void bound()
    {
        Serializable[] params=new Serializable[]
            {TEST_P1};
        I18NBoundMessage1P m=new I18NBoundMessage1P
            (TestMessages.P1_MSG,TEST_P1);
        boundTests(m,new I18NBoundMessage1P
                   (TestMessages.P1_MSG,TEST_P1),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage1P
                       (TestMessages.P1_MSG,TEST_P2),
                       new I18NBoundMessage1P
                       (TestMessages.P1_TTL,TEST_P1),
                       TestMessages.P0_MSG
                   },params,TestMessages.P1_MSG,TEST_MSG_EN,TEST_MSG_FR);
        castOverride(m.getMessage());
        boundTests(new I18NBoundMessage1P
                   (TestMessages.P1_TTL,TEST_P1),
                   new I18NBoundMessage1P
                   (TestMessages.P1_TTL,TEST_P1),
                   new I18NBoundMessage[] {
                       new I18NBoundMessage1P
                       (TestMessages.P1_TTL,TEST_P2),
                       new I18NBoundMessage1P
                       (TestMessages.P1_MSG,TEST_P1),
                       TestMessages.P0_TTL
                   },params,TestMessages.P1_TTL,
                   TEST_TTL_EN,TEST_TTL_FR);
        assertEquals(TEST_P1,m.getParam1());
    }
}
