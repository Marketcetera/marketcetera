package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Ignore;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public class I18NMessageTestBase
    extends TestCaseBase
{
    protected static final String TEST_P1=
        "1";
    protected static final String TEST_P2=
        "2";
    protected static final String TEST_P3=
        "3";
    protected static final String TEST_P4=
        "4";
    protected static final String TEST_P5=
        "5";
    protected static final String TEST_P6=
        "6";
    protected static final String TEST_P7=
        "7";
    protected static final String TEST_CATEGORY=
        "TestCategory";
    protected static final String TEST_MSG_ID=
        "base";
    protected static final String TEST_ENTRY_ID=
        "ttl";
    protected static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");

    private static final String TEST_LOCATION=
        I18NMessageTestBase.class.getName();


    @Before
    public void setupI18NMessageXPTestBase()
    {
        I18NMessageProvider.setLocale(Locale.US);
        setLevel(TEST_CATEGORY,Level.TRACE);
    }


    protected static void unboundTests
        (int paramCount,
         I18NMessage withEntry,
         I18NMessage withoutEntry)
    {
        assertEquals(paramCount,withEntry.getParamCount());
        assertEquals(paramCount,withoutEntry.getParamCount());

        assertEquals(TestMessages.LOGGER,withoutEntry.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,withoutEntry.getMessageProvider());
        assertEquals(TEST_MSG_ID,withoutEntry.getMessageId());
        assertEquals(I18NMessage.UNKNOWN_ENTRY_ID,withoutEntry.getEntryId());

        assertEquals(TestMessages.LOGGER,withEntry.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,withEntry.getMessageProvider());
        assertEquals(TEST_MSG_ID,withEntry.getMessageId());
        assertEquals(TEST_ENTRY_ID,withEntry.getEntryId());
    }

    protected void boundTests
        (I18NBoundMessage m,
         Object[] params,
         I18NMessage unbound,
         String textEn,
         String testFr)
    {
        assertEquals(TestMessages.LOGGER,m.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,m.getMessageProvider());

        assertEquals(unbound,m.getMessage());
        assertArrayEquals(params,m.getParams());

        assertEquals(textEn,m.getText());
        assertEquals(testFr,m.getText(Locale.FRENCH));

        assertEquals(textEn,m.toString());

        m.error(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,textEn,TEST_LOCATION);
        m.error(TEST_CATEGORY);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,textEn,TEST_LOCATION);

        m.warn(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,textEn,TEST_LOCATION);
        m.warn(TEST_CATEGORY);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,textEn,TEST_LOCATION);

        m.info(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,textEn,TEST_LOCATION);
        m.info(TEST_CATEGORY);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,textEn,TEST_LOCATION);

        m.debug(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,textEn,TEST_LOCATION);
        m.debug(TEST_CATEGORY);
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,textEn,TEST_LOCATION);

        m.trace(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,textEn,TEST_LOCATION);
        m.trace(TEST_CATEGORY);
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,textEn,TEST_LOCATION);
    }
}
