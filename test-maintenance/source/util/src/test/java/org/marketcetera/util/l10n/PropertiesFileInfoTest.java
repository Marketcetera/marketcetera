package org.marketcetera.util.l10n;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.CollectionAssert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class PropertiesFileInfoTest
    extends TestCaseBase
{
    @Test
    public void english()
        throws Exception
    {
        PropertiesFileInfo info=new PropertiesFileInfo(TestMessages.PROVIDER);
        assertEquals(TestMessages.PROVIDER,info.getProvider());
        assertEquals(Locale.ROOT,info.getLocale());
        assertArrayPermutation
            (new PropertyMessageInfo[] {
                new PropertyMessageInfo
                ("m0.msg",0,"Text"),
                new PropertyMessageInfo
                ("m1.msg",1,"Text {0}"),
                new PropertyMessageInfo
                ("m2.msg",2,"Text {0} {1}"),
                new PropertyMessageInfo
                ("m3.msg",3,"Text {0} {1} {2}"),
                new PropertyMessageInfo
                ("m4.msg",4,"Text {0} {1} {2} {3}"),
                new PropertyMessageInfo
                ("m5.msg",5,"Text {0} {1} {2} {3} {4}"),
                new PropertyMessageInfo
                ("m6.msg",6,"Text {0} {1} {2} {3} {4} {5}"),
                new PropertyMessageInfo
                ("m7.msg",7,"Text {0} {1} {2} {3} {4} {5} {6}"),
                new PropertyMessageInfo
                ("m8.msg",8,"Text {0} {1} {2} {3} {4} {5} {6} {7}"),
                new PropertyMessageInfo
                ("b1.ttl",0,"B Text"),
                new PropertyMessageInfo
                ("b2.ttl",1,"B Text {0,date,full}")
            },info.getMessageInfo());
    }

    @Test
    public void greek()
        throws Exception
    {
        Locale locale=new Locale("gr");
        PropertiesFileInfo info=
            new PropertiesFileInfo(TestMessages.PROVIDER,locale);
        assertEquals(TestMessages.PROVIDER,info.getProvider());
        assertEquals(locale,info.getLocale());
        assertArrayPermutation
            (new PropertyMessageInfo[] {
                new PropertyMessageInfo
                ("m2.msg",2,HELLO_GR+" {1} {1} {1}"),
                new PropertyMessageInfo
                ("m3.msg",5,"{0}: {1,choice,0#|1#{2}}{3,choice,0#|1#({4})}")
            },info.getMessageInfo());
    }

    @Test
    public void badText()
    {
        try {
            new PropertiesFileInfo(TestMessages.PROVIDER,Locale.GERMAN);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.BAD_TEXT,"{0"),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void missingFile()
    {
        try {
            new PropertiesFileInfo(TestMessages.PROVIDER,Locale.FRENCH);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P
                 (Messages.NONEXISTENT_RESOURCE,
                  "util_l10n_test"+I18NMessageProvider.MESSAGE_FILE_EXTENSION+
                  "_fr.properties"),
                 ex.getI18NBoundMessage());
        }
    }
}
