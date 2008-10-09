package org.marketcetera.util.log;

import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.List;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class I18NMessageProviderTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        I18NMessageProvider.class.getName();
    private static final String TEST_LOCATION=
        TEST_CATEGORY;


    @Before
    public void setupI18NMessageProviderTest()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_CATEGORY,Level.ERROR);
    }


    @Test
    public void idIsValid()
    {
        assertEquals("util_log_test",TestMessages.PROVIDER.getProviderId());
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

        Locale saved=Locale.getDefault();
        try {
            Locale.setDefault(Locale.ROOT);
            assertEquals
                ("Hello",TestMessages.PROVIDER.
                 getText(Locale.GERMAN,TestMessages.HELLO_MSG));
            Locale.setDefault(Locale.JAPANESE);
            assertEquals
                ("Hello",TestMessages.PROVIDER.
                 getText(Locale.GERMAN,TestMessages.HELLO_MSG));
            Locale.setDefault(Locale.FRENCH);
            assertEquals
                ("Bonjour",TestMessages.PROVIDER.
                 getText(Locale.GERMAN,TestMessages.HELLO_MSG));
        } finally {
            Locale.setDefault(saved);
        }

        assertEquals
            ("Hello a {0} 'a' \"a\" b!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_ECHO,"a","b"));
        assertEquals
            ("Bonjour a {0} 'a' \"a\" {1}!",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.HELLO_ECHO,"a"));
        assertEquals
            ("Bonjour a {0} 'a' \"a\" b!",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.HELLO_ECHO,"a","b","c"));

        assertEquals
            ("There are no orders ma'am.",TestMessages.PROVIDER.
             getText(TestMessages.CHOICE_MSG,0));
        assertEquals
            ("There is just one order ma'am.",TestMessages.PROVIDER.
             getText(TestMessages.CHOICE_MSG,1));
        assertEquals
            ("There are 2 orders ma'am.",TestMessages.PROVIDER.
             getText(TestMessages.CHOICE_MSG,2));

        assertEquals
            ("Pas des ordres ma'am.",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.CHOICE_MSG,0));
        assertEquals
            ("Seulemont un ordre ma'am.",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.CHOICE_MSG,1));
        assertEquals
            ("Il y a 2 ordres ma'am.",TestMessages.PROVIDER.
             getText(Locale.FRENCH,TestMessages.CHOICE_MSG,2));

        assertEquals
            ("Hello {0}!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_TITLE,(Object[])null));
        assertEquals
            ("Hello null!",TestMessages.PROVIDER.
             getText(TestMessages.HELLO_TITLE,(Object)null));

        ActiveLocale.setProcessLocale(Locale.FRENCH);
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
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider 'nonexistent_prv'; base name "+
             "'nonexistent_prv_messages'",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace",TEST_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();

        assertEquals
            ("provider 'nonexistent_prv'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ()",
             provider.getText(TestMessages.NONEXISTENT));
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message not found: "+
             "provider 'nonexistent_prv'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ()",TEST_LOCATION);
    }

    @Test
    public void nonexistentMessage()
    {
        assertEquals
            ("provider 'util_log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message not found: provider 'util_log_test'; "+
             "id 'nonexistent_msg'; "+
             "entry 'msg'; parameters ('a')",TEST_LOCATION);

        ActiveLocale.setProcessLocale(Locale.FRENCH);
        assertEquals
            ("provider 'util_log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Message n'a pas \u00E9t\u00E9 trouv\u00E9e: fournisseur "+
             "'util_log_test'; "+
             "identit\u00E9 'nonexistent_msg'; entr\u00E9e 'msg'; "+
             "param\u00E8tres ('a')",TEST_LOCATION);
    }
    
    @Test
    public void classLoader() {
         //Verify that the resource is not available
        final String providerName = "loader_prv";
        I18NMessageProvider provider=new I18NMessageProvider(providerName);
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider '"+
                     providerName+
                     "'; base name '"+
                     providerName+
                     I18NMessageProvider.MESSAGE_FILE_EXTENSION+
                     "'", TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace",TEST_LOCATION);
        assertFalse(events.hasNext());
        getAppender().clear();
        final Properties messages=new Properties();
        messages.put("hello.msg","Hello");
        messages.put("hello.title","Hello {0}!");
        final String propertiesName=providerName+
                I18NMessageProvider.MESSAGE_FILE_EXTENSION+
                ".properties"; 
        //Create a provider with a custom classloader
        provider=new I18NMessageProvider(providerName,new ClassLoader(){
            @Override
            public InputStream getResourceAsStream(String name) {
                try {
                    if(propertiesName.equals(name)) {
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        messages.store(baos,"");
                        baos.close();
                        return new ByteArrayInputStream(baos.toByteArray());
                    }
                } catch (IOException ignore) {
                }
                return super.getResourceAsStream(name);
            }
        });
        //Verify that resource was found.
        final List<LoggingEvent> logs=getAppender().getEvents();
        assertTrue(logs.toString(),logs.isEmpty());
        I18NLoggerProxy logger=new I18NLoggerProxy(provider);
        I18NMessage0P helloMsg=new I18NMessage0P(logger,"hello");
        I18NMessage1P helloTitle=new I18NMessage1P(logger,"hello","title");
        //Verify that messages can now be translated
        Locale saved=Locale.getDefault();
        try {
            Locale.setDefault(Locale.ROOT);
            assertEquals("Hello",provider.getText(helloMsg));
            assertEquals("Hello World!",provider.getText(helloTitle,"World"));
        } finally {
            Locale.setDefault(saved);
        }
    }

    /*
     * EXTREME TEST 1: run alone (no other tests in the same file,
     * and no other units test) after uncommenting sections in main
     * class.
    @Test
    public void nonexistentSystemMappingFile()
    {
        assertEquals
            ("provider 'util_log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider 'util_log_test'; "+
             "base name 'util_log_test_message'",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message file missing: provider 'util_log'; "+
             "base name 'util_log_message'",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Corrupted/unavailable message map",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Message missing: provider 'util_log_test'; "+
             "id 'nonexistent_msg'; "+
             "entry 'msg'; parameters ('a')",TEST_LOCATION);
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
            ("provider 'util_log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));

        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Corrupted/unavailable message map",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: provider 'util_log_test'; "+
             "id 'nonexistent_msg'; "+
             "entry 'msg'; parameters ('a')",TEST_LOCATION);
        assertEvent
            (events.next(),Level.ERROR,TEST_CATEGORY,
             "Abnormal exception: stack trace",TEST_LOCATION);
        assertFalse(events.hasNext());
    }
    */
}
