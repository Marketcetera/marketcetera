package org.marketcetera.util.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.marketcetera.util.test.EqualityAssert.assertEquality;
import static org.marketcetera.util.test.SerializableAssert.assertSerializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.SerializationException;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NMessageProviderTest.java 16994 2015-03-09 21:18:25Z colin $
 */

/* $License$ */

public class I18NMessageProviderTest
    extends TestCaseBase
{
    private static final String TEST_MEM_PROVIDER=
        "classloader_prv";


    @Before
    public void setupI18NMessageProviderTest()
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    @Test
    public void idIsValid()
    {
        assertEquals("a",(new I18NMessageProvider("a")).getProviderId());
    }

    @Test
    public void equality()
    {
        assertEquality(new I18NMessageProvider("a"),
                       new I18NMessageProvider("a"),
                       new I18NMessageProvider("b"));
        assertSerializable(TestMessages.PROVIDER);
    }

    @Test
    public void deserialization()
    {
        byte[] serialized=SerializationUtils.serialize
            (new I18NMessageProvider("nonexistent_prv"));
        try {
            SerializationUtils.deserialize(serialized);
            fail();
        } catch (SerializationException ex) {
            assertEquals(IOException.class,ex.getCause().getClass());
        }
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

        assertEquals
            ("provider 'nonexistent_prv'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ()",
             provider.getText(TestMessages.NONEXISTENT));
    }

    @Test
    public void nonexistentMessage()
    {
        assertEquals
            ("provider 'util_log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));

        ActiveLocale.setProcessLocale(Locale.FRENCH);
        assertEquals
            ("provider 'util_log_test'; id 'nonexistent_msg'; entry 'msg'; "+
             "parameters ('a')",
             TestMessages.PROVIDER.getText(TestMessages.NONEXISTENT,"a"));
    }
    
    @Test@Ignore
    // it is no longer possible to use custom ClassLoaders in this way as of Java9
    public void customClassLoader()
        throws Exception
    {
        // Verify that the resource is not available.

        I18NMessageProvider provider=new I18NMessageProvider(TEST_MEM_PROVIDER);

        // Create a provider with a custom classloader.

        Properties messages=new Properties();
        messages.put("hello.msg","Hello");
        messages.put("hello.title","Hello {0}!");
        ByteArrayOutputStream output=new ByteArrayOutputStream();
        try {
            messages.store(output,StringUtils.EMPTY);
        } finally {
            output.close();
        }
        final byte[] inputStream=output.toByteArray();
        final String propertiesName=TEST_MEM_PROVIDER+
            I18NMessageProvider.MESSAGE_FILE_EXTENSION+".properties"; 
        provider=new I18NMessageProvider(TEST_MEM_PROVIDER,new ClassLoader() {
            @Override
            public InputStream getResourceAsStream
                (String name)
            {
                if (propertiesName.equals(name)) {
                    return new ByteArrayInputStream(inputStream);
                }
                return super.getResourceAsStream(name);
            }
        });
        
        // Messages can now be translated.

        I18NLoggerProxy logger=new I18NLoggerProxy(provider);
        I18NMessage0P helloMsg=new I18NMessage0P(logger,"hello");
        I18NMessage1P helloTitle=new I18NMessage1P(logger,"hello","title");
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
