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

public class TI18NMessage0PTest
    extends I18NMessageTestBase
{
    private static final String TEST_MSG_EN=
        "P0 msg (expected) en";
    private static final String TEST_TTL_EN=
        "P0 ttl (expected) en";
    private static final String TEST_MSG_FR=
        "P0 msg (expected) fr";
    private static final String TEST_TTL_FR=
        "P0 ttl (expected) fr";
    private static final String TEST_LOCATION=
        TI18NMessage0PTest.class.getName();


    private static void castOverride
        (I18NMessage0P m) {}


    protected void boundTests0P
        (I18NBoundMessage msg,
         I18NBoundMessage msgCopy,
         I18NBoundMessage ttl,
         I18NBoundMessage ttlCopy)
    {
        boundTests(msg,msgCopy,
                   new I18NBoundMessage[] {
                       ttl,
                       new I18NBoundMessage1P(TestMessages.P1_MSG,TEST_P1)
                   },
                   I18NBoundMessage.EMPTY_PARAMS,
                   TestMessages.P0_MSG,TEST_MSG_EN,TEST_MSG_FR);
        boundTests(ttl,ttlCopy,
                   new I18NBoundMessage[] {
                       msg,
                       new I18NBoundMessage1P(TestMessages.P1_MSG,TEST_P1)
                   },
                   I18NBoundMessage.EMPTY_PARAMS,
                   TestMessages.P0_TTL,TEST_TTL_EN,TEST_TTL_FR);
    }


    @Test
    public void basic()
    {
        unboundTests
            (0,
             new I18NMessage0P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage0P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
             new I18NMessage[] {
                new I18NMessage0P
                (TEST_LOGGER_D,TEST_MSG_ID,TEST_ENTRY_ID),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID_D,TEST_ENTRY_ID),
                new I18NMessage0P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID_D),
                new I18NMessage1P
                (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID)
             },
             new I18NMessage0P(TestMessages.LOGGER,TEST_MSG_ID));
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            (TEST_MSG_EN,TestMessages.P0_MSG.getText());
        assertEquals
            (TEST_TTL_EN,TestMessages.P0_TTL.getText());
        assertEquals
            (TEST_MSG_FR,TestMessages.P0_MSG.getText(Locale.FRENCH));
        assertEquals
            (TEST_TTL_FR,TestMessages.P0_TTL.getText(Locale.FRENCH));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.P0_MSG.error(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P0_MSG.error(TEST_CATEGORY);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P0_TTL.error(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P0_TTL.error(TEST_CATEGORY);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P0_MSG.warn(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P0_MSG.warn(TEST_CATEGORY);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P0_TTL.warn(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P0_TTL.warn(TEST_CATEGORY);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P0_MSG.info(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P0_MSG.info(TEST_CATEGORY);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P0_TTL.info(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P0_TTL.info(TEST_CATEGORY);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P0_MSG.debug(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P0_MSG.debug(TEST_CATEGORY);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P0_TTL.debug(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P0_TTL.debug(TEST_CATEGORY);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);

        TestMessages.P0_MSG.trace(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);
        TestMessages.P0_MSG.trace(TEST_CATEGORY);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN,TEST_LOCATION);

        TestMessages.P0_TTL.trace(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
        TestMessages.P0_TTL.trace(TEST_CATEGORY);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN,TEST_LOCATION);
    }

    @Test
    public void bound()
    {
        boundTests0P(TestMessages.P0_MSG,TestMessages.P0_MSG_COPY,
                     TestMessages.P0_TTL,TestMessages.P0_TTL_COPY);
        I18NBoundMessage0P msg=new I18NBoundMessage0P(TestMessages.P0_MSG);
        I18NBoundMessage0P ttl=new I18NBoundMessage0P(TestMessages.P0_TTL);
        boundTests0P(msg,new I18NBoundMessage0P(TestMessages.P0_MSG),
                     ttl,new I18NBoundMessage0P(TestMessages.P0_TTL));

        assertFalse(TestMessages.P0_MSG.equals(msg));
        assertFalse(msg.equals(TestMessages.P0_MSG));
        assertFalse(TestMessages.P0_TTL.equals(ttl));
        assertFalse(ttl.equals(TestMessages.P0_TTL));

        castOverride(msg.getMessage());
    }
}
