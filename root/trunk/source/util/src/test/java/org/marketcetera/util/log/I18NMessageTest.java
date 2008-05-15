package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class I18NMessageTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        I18NMessageTest.class.getName();
    private static final String TEST_MSG_ID=
        "Test message ID";
    private static final String TEST_ENTRY_ID=
        "Test entry ID";
    private static final String TEST_MSG=
        "Test here (expected): 'a'";
    private static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");


    @Before
    public void setupI18NMessageProviderTest()
    {
        Messages.PROVIDER.setLocale(Locale.US);
        setLevel(TEST_CATEGORY,Level.TRACE);
    }


    @Test
    public void basic()
    {
        I18NMessage m=new I18NMessage
            (TestMessages.LOGGER,TEST_MSG_ID,TEST_ENTRY_ID);
        assertEquals(TestMessages.LOGGER,m.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,m.getMessageProvider());
        assertEquals(TEST_MSG_ID,m.getMessageId());
        assertEquals(TEST_ENTRY_ID,m.getEntryId());

        m=new I18NMessage(TestMessages.LOGGER,TEST_MSG_ID);
        assertEquals(TestMessages.LOGGER,m.getLoggerProxy());
        assertEquals(TestMessages.PROVIDER,m.getMessageProvider());
        assertEquals(TEST_MSG_ID,m.getMessageId());
        assertEquals(I18NMessage.UNKNOWN_ENTRY_ID,m.getEntryId());
    }

    @Test
    public void messageProvider()
    {
        assertEquals
            ("Hello",
             TestMessages.HELLO_MSG.getText());
        assertEquals
            ("Hello World!",
             TestMessages.HELLO_TITLE.getText("World"));
        assertEquals
            ("Bonjour",
             TestMessages.HELLO_MSG.getText(Locale.FRENCH));
        assertEquals
            ("Bonjour Le Monde!",
             TestMessages.HELLO_TITLE.getText(Locale.FRENCH,"Le Monde"));
    }

    @Test
    public void loggerProxy()
    {
        TestMessages.LOG_MSG.error(TEST_CATEGORY,TEST_THROWABLE,"a");
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG);
        TestMessages.LOG_MSG.error(TEST_CATEGORY,"a");
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MSG);

        TestMessages.LOG_MSG.warn(TEST_CATEGORY,TEST_THROWABLE,"a");
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG);
        TestMessages.LOG_MSG.warn(TEST_CATEGORY,"a");
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MSG);

        TestMessages.LOG_MSG.info(TEST_CATEGORY,TEST_THROWABLE,"a");
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG);
        TestMessages.LOG_MSG.info(TEST_CATEGORY,"a");
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MSG);

        TestMessages.LOG_MSG.debug(TEST_CATEGORY,TEST_THROWABLE,"a");
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG);
        TestMessages.LOG_MSG.debug(TEST_CATEGORY,"a");
        assertSingleEvent(Level.DEBUG,TEST_CATEGORY,TEST_MSG);

        TestMessages.LOG_MSG.trace(TEST_CATEGORY,TEST_THROWABLE,"a");
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG);
        TestMessages.LOG_MSG.trace(TEST_CATEGORY,"a");
        assertSingleEvent(Level.TRACE,TEST_CATEGORY,TEST_MSG);
    }
}
