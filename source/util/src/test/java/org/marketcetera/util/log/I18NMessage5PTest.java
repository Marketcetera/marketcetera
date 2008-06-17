package org.marketcetera.util.log;

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

public class I18NMessage5PTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "P5 msg (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5;
    private static final String TEST_TTL_EN=
        "P5 ttl (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5;
    private static final String TEST_MSG_FR=
        "P5 msg (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5;
    private static final String TEST_TTL_FR=
        "P5 ttl (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5;


    private static void castOverride
        (I18NMessage5P m) {}


    @Test
    public void basic()
    {
        unboundTests
            (new I18NMessage5P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage5P(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.P5_MSG.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5));
        assertEquals
            (TEST_TTL_EN,TestMessages.P5_TTL.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5));
        assertEquals
            (TEST_MSG_FR,TestMessages.P5_MSG.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5));
        assertEquals
            (TEST_TTL_FR,TestMessages.P5_TTL.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.P5_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P5_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P5_TTL.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P5_TTL.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P5_MSG.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P5_MSG.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P5_TTL.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P5_TTL.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P5_MSG.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P5_MSG.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P5_TTL.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P5_TTL.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P5_MSG.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P5_MSG.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P5_TTL.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P5_TTL.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P5_MSG.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P5_MSG.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P5_TTL.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P5_TTL.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN);
    }

    @Test
    public void bound()
    {
        Object[] params=new Object[]
            {TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5};
        I18NBoundMessage5P m=new I18NBoundMessage5P
            (TestMessages.P5_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5);
        boundTests(m,params,TestMessages.P5_MSG,TEST_MSG_EN,TEST_MSG_FR);
        castOverride(m.getMessage());
        boundTests(new I18NBoundMessage5P
                   (TestMessages.P5_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                    TEST_P5),params,TestMessages.P5_TTL,
                   TEST_TTL_EN,TEST_TTL_FR);
        assertEquals(TEST_P1,m.getParam1());
        assertEquals(TEST_P2,m.getParam2());
        assertEquals(TEST_P3,m.getParam3());
        assertEquals(TEST_P4,m.getParam4());
        assertEquals(TEST_P5,m.getParam5());
    }
}
