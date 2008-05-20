package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Level;
import org.junit.Test;

import static org.junit.Assert.*;

public class I18NMessage0PTest
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


    private static void castOverride
        (I18NMessage0P m) {}


    protected void boundTests0P
        (I18NBoundMessage msg,
         I18NBoundMessage ttl)
    {
        boundTests(msg,ArrayUtils.EMPTY_OBJECT_ARRAY,
                   TestMessages.P0_MSG,TEST_MSG_EN,TEST_MSG_FR);
        boundTests(ttl,ArrayUtils.EMPTY_OBJECT_ARRAY,
                   TestMessages.P0_TTL,TEST_TTL_EN,TEST_TTL_FR);
    }


    @Test
    public void basic()
    {
        unboundTests
            (new I18NMessage0P(TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID),
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
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P0_MSG.error(TEST_CATEGORY);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P0_TTL.error(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P0_TTL.error(TEST_CATEGORY);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P0_MSG.warn(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P0_MSG.warn(TEST_CATEGORY);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P0_TTL.warn(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P0_TTL.warn(TEST_CATEGORY);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P0_MSG.info(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P0_MSG.info(TEST_CATEGORY);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P0_TTL.info(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P0_TTL.info(TEST_CATEGORY);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P0_MSG.debug(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P0_MSG.debug(TEST_CATEGORY);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P0_TTL.debug(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P0_TTL.debug(TEST_CATEGORY);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_TTL_EN);

        TestMessages.P0_MSG.trace(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN);
        TestMessages.P0_MSG.trace(TEST_CATEGORY);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG_EN);

        TestMessages.P0_TTL.trace(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN);
        TestMessages.P0_TTL.trace(TEST_CATEGORY);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_TTL_EN);
    }

    @Test
    public void bound()
    {
        boundTests0P(TestMessages.P0_MSG,TestMessages.P0_TTL);
        I18NBoundMessage0P msg=new I18NBoundMessage0P(TestMessages.P0_MSG);
        I18NBoundMessage0P ttl=new I18NBoundMessage0P(TestMessages.P0_TTL);
        boundTests0P(msg,ttl);
        castOverride(msg.getMessage());
    }
}
