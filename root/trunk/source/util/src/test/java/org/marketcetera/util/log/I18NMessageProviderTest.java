package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class I18NMessageProviderTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        I18NMessageProvider.class.getName();

    private static final class TestThread
        extends Thread
    {
        private Locale mLocale;

        Locale getLocale()
        {
            return mLocale;
        }

        @Override
        public void run()
        {
            I18NMessageProvider.setLocale(Locale.GERMAN);
            mLocale=I18NMessageProvider.getLocale();
        }
    }


    @Before
    public void setupI18NMessageProviderTest()
    {
        I18NMessageProvider.setLocale(Locale.US);
        setLevel(TEST_CATEGORY,Level.ERROR);
    }


    @Test
    public void localeSetPerThread()
        throws Exception
    {
        assertEquals(Locale.US,I18NMessageProvider.getLocale());
        I18NMessageProvider.setLocale(Locale.FRENCH);
        assertEquals(Locale.FRENCH,I18NMessageProvider.getLocale());
        TestThread t=new TestThread();
        t.start();
        t.join();
        assertEquals(Locale.GERMAN,t.getLocale());
        assertEquals(Locale.FRENCH,I18NMessageProvider.getLocale());
    }

    @Test
    public void idIsValid()
    {
        assertEquals("log_test",TestMessages.PROVIDER.getProviderId());
    }

    @Test
    public void retrievals()
    {
        assertEquals
            ("Hello",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_MSG));
        assertEquals
            ("Hello World!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_TITLE,"World"));
        assertEquals
            ("Bonjour",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.HELLO_MSG));
        assertEquals
            ("Bonjour Le Monde!",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.HELLO_TITLE,"Le Monde"));
        assertEquals
            ("Hello",TestMessages.PROVIDER.
             getText(Locale.GERMAN,TestMessages.HELLO_MSG));
        assertEquals
            ("Hello Welt!",TestMessages.PROVIDER.
             getText(Locale.GERMAN,TestMessages.HELLO_TITLE,"Welt"));

        assertEquals
            ("Hello {0}!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_TITLE,(Object[])null));
        assertEquals
            ("Hello null!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_TITLE,(Object)null));

        I18NMessageProvider.setLocale(Locale.FRENCH);
        assertEquals
            ("Bonjour",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_MSG));
        assertEquals
            ("Bonjour Le Monde!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_TITLE,"Le Monde"));
    }

    @Test
    public void nonexistentMappingFile()
    {
        I18NMessageProvider provider=new I18NMessageProvider("nonexistent_prv");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider 'nonexistent_prv'; file "+
             "'nonexistent_prv_messages.xml'");

        assertEquals
            ("provider 'nonexistent_prv'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ()",
             provider.getText(TestMessages.NONEXISTENT));
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message not found: "+
             "provider 'nonexistent_prv'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ()");
    }

    @Test
    public void nonexistentMessage()
    {
        assertEquals
            ("provider 'log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message not found: provider 'log_test'; id 'nonexistent_msg'; "+
             "entry 'msg'; parameters ('a')");

        I18NMessageProvider.setLocale(Locale.FRENCH);
        assertEquals
            ("provider 'log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message n'a pas \u00E9t\u00E9 trouv\u00E9e: fournisseur "+
             "'log_test'; identit\u00E9 'nonexistent_msg'; entr\u00E9e 'msg'; "+
             "param\u00E8tres ('a')");
    }

    /*
     * EXTREME TEST 1: run alone (no other tests in the same file,
     * and no other units test) after uncommenting sections in main
     * class.
    @Test
    public void nonexistentSystemMappingFile()
    {
        assertEquals
            ("provider 'log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider 'util_log'; "+
             "file 'util_log_message.xml'");
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider 'log_test'; "+
             "file 'log_test_message.xml'");
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Corrupted/unavailable message map");
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message missing: provider 'log_test'; id 'nonexistent_msg'; "+
             "entry 'msg'; parameters ('a')");
        assertFalse(events.hasNext());
    }
    */

    /*
     * EXTREME TEST 2: run alone (no other tests in the same file,
     * and no other units test) after uncommenting sections in main
     * class.
    @Test
    public void exceptionThrown()
    {
        assertEquals
            ("provider 'log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));

        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Corrupted/unavailable message map");
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace");
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: provider 'log_test'; id 'nonexistent_msg'; "+
             "entry 'msg'; parameters ('a')");
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace");
        assertFalse(events.hasNext());
    }
    */
}
