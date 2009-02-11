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

public class TI18NMessageNPTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "PN msg (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6+" "+TEST_P7;
    private static final String TEST_TTL_EN=
        "PN ttl (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6+" "+TEST_P7;
    private static final String TEST_MSG_FR=
        "PN msg (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6+" "+TEST_P7;
    private static final String TEST_TTL_FR=
        "PN ttl (expected) fr "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6+" "+TEST_P7;

    private static final String TEST_MSG_MISMATCH_EN=
        "PN msg (expected) en "+TEST_P1+" "+TEST_P2+" "+TEST_P3+" "+TEST_P4+
        " "+TEST_P5+" "+TEST_P6+" {6}";

    private static final String TEST_MSG_EN_NULL=
        "PN msg (expected) en null null null null null null null";
    private static final String TEST_TTL_EN_NULL=
        "PN ttl (expected) en null null null null null null null";
    private static final String TEST_MSG_FR_NULL=
        "PN msg (expected) fr null null null null null null null";
    private static final String TEST_TTL_FR_NULL=
        "PN ttl (expected) fr null null null null null null null";

    private static final String TEST_MSG_EN_NOSUB=
        "PN msg (expected) en {0} {1} {2} {3} {4} {5} {6}";
    private static final String TEST_TTL_EN_NOSUB=
        "PN ttl (expected) en {0} {1} {2} {3} {4} {5} {6}";
    private static final String TEST_MSG_FR_NOSUB=
        "PN msg (expected) fr {0} {1} {2} {3} {4} {5} {6}";
    private static final String TEST_TTL_FR_NOSUB=
        "PN ttl (expected) fr {0} {1} {2} {3} {4} {5} {6}";

    private static final String TEST_LOCATION=
        TI18NMessageNPTest.class.getName();


    private static void castOverride
        (I18NMessageNP m) {}


    @Test
    public void basic()
    {
        unboundTests
            (-1,
             new I18NMessageNP(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessageNP(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage[] {
                new I18NMessageNP
                (TEST_LOGGER_D,TEST_MSG_ID,TEST_ENTRY_ID),
                new I18NMessageNP
                (TestMessages.LOGGER,TEST_MSG_ID_D,TEST_ENTRY_ID),
                new I18NMessageNP
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID_D),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID)
             },
             new I18NMessageNP(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.PN_MSG.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,TEST_P7));
        assertEquals
            (TEST_TTL_EN,TestMessages.PN_TTL.getText
             (TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,TEST_P7));
        assertEquals
            (TEST_MSG_FR,TestMessages.PN_MSG.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
              TEST_P7));
        assertEquals
            (TEST_TTL_FR,TestMessages.PN_TTL.getText
             (Locale.FRENCH,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
              TEST_P7));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.PN_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.PN_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.PN_TTL.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.PN_TTL.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.PN_MSG.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.PN_MSG.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.PN_TTL.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.PN_TTL.warn
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.PN_MSG.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.PN_MSG.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.PN_TTL.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.PN_TTL.info
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
 
        TestMessages.PN_MSG.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.PN_MSG.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.PN_TTL.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.PN_TTL.debug
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
 
        TestMessages.PN_MSG.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.PN_MSG.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.PN_TTL.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.PN_TTL.trace
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
    }

    @Test
    public void countMismatch()
    {
        TestMessages.PN_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MSG_MISMATCH_EN,TEST_LOCATION);
        TestMessages.PN_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MSG_MISMATCH_EN,TEST_LOCATION);

        TestMessages.PN_MSG.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7,TEST_P7);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.PN_MSG.error
            (TEST_CATEGORY,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,TEST_P6,
             TEST_P7,TEST_P7);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
    }

    @Test
    public void bound()
    {
        Serializable[] params=new Serializable[]
            {TEST_P1,TEST_P2,TEST_P3,TEST_P4,
             TEST_P5,TEST_P6,TEST_P7};
        I18NBoundMessageNP m=new I18NBoundMessageNP
            (TestMessages.PN_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,
             TEST_P6,TEST_P7);
        boundTests(m,new I18NBoundMessageNP
                   (TestMessages.PN_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                    TEST_P5,TEST_P6,TEST_P7),
                   new I18NBoundMessage[] {
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6,TEST_P7,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6,TEST_P7),
                       TestMessages.P0_MSG
                   },params,TestMessages.PN_MSG,TEST_MSG_EN,TEST_MSG_FR);
        castOverride(m.getMessage());
        boundTests(new I18NBoundMessageNP
                   (TestMessages.PN_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                    TEST_P5,TEST_P6,TEST_P7),
                   new I18NBoundMessageNP
                   (TestMessages.PN_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,TEST_P5,
                    TEST_P6,TEST_P7),
                   new I18NBoundMessage[] {
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6,TEST_P7,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,TEST_P1,TEST_P2,TEST_P3,TEST_P4,
                        TEST_P5,TEST_P6,TEST_P7),
                       TestMessages.P0_TTL
                   },params,TestMessages.PN_TTL,
                   TEST_TTL_EN,TEST_TTL_FR);

        params=new Serializable[] {null,null,null,null,null,null,null};
        boundTests(new I18NBoundMessageNP
                   (TestMessages.PN_MSG,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null),
                   new I18NBoundMessageNP
                   (TestMessages.PN_MSG,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null),
                   new I18NBoundMessage[] {
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null),
                       TestMessages.P0_MSG
                   },params,
                   TestMessages.PN_MSG,TEST_MSG_EN_NULL,TEST_MSG_FR_NULL);
        boundTests(new I18NBoundMessageNP
                   (TestMessages.PN_TTL,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null),
                   new I18NBoundMessageNP
                   (TestMessages.PN_TTL,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null,
                    (Serializable)null,(Serializable)null),
                   new I18NBoundMessage[] {
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null,
                        (Serializable)null,(Serializable)null),
                       TestMessages.P0_TTL
                   },params,
                   TestMessages.PN_TTL,TEST_TTL_EN_NULL,TEST_TTL_FR_NULL);

        boundTests(new I18NBoundMessageNP
                   (TestMessages.PN_MSG,(Serializable[])null),
                   new I18NBoundMessageNP
                   (TestMessages.PN_MSG,I18NBoundMessage.EMPTY_PARAMS),
                   new I18NBoundMessage[] {
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,(Serializable[])null),
                       TestMessages.P0_MSG
                   },I18NBoundMessage.EMPTY_PARAMS,
                   TestMessages.PN_MSG,TEST_MSG_EN_NOSUB,TEST_MSG_FR_NOSUB);
        boundTests(new I18NBoundMessageNP
                   (TestMessages.PN_TTL,(Serializable[])null),
                   new I18NBoundMessageNP
                   (TestMessages.PN_TTL,I18NBoundMessage.EMPTY_PARAMS),
                   new I18NBoundMessage[] {
                       new I18NBoundMessageNP
                       (TestMessages.PN_TTL,TEST_P1),
                       new I18NBoundMessageNP
                       (TestMessages.PN_MSG,(Serializable[])null),
                       TestMessages.P0_TTL
                   },I18NBoundMessage.EMPTY_PARAMS,
                   TestMessages.PN_TTL,TEST_TTL_EN_NOSUB,TEST_TTL_FR_NOSUB);
    }
}
