package org.marketcetera.util.quickfix;

import java.io.File;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import quickfix.Dictionary;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SpringSessionDescriptorTest
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"quickfix"+File.separator;
    private static final String TEST_BEAN_GOOD_DESCRIPTOR=
        "goodDescriptor";
    private static final String TEST_BEAN_MISSING_BEGIN_STRING=
        "missingBeginString";
    private static final String TEST_BEAN_MISSING_SENDER_COMP_ID=
        "missingSenderCompId";
    private static final String TEST_BEAN_MISSING_TARGET_COMP_ID=
        "missingTargetCompId";
    private static final String TEST_BEAN_GOOD_DEFAULT_DICTIONARY=
        "goodDefaultDictionary";
    private static final String TEST_BEAN_BAD_DEFAULT_DICTIONARY=
        "badDefaultDictionary";
    private static final String TEST_BEAN_INHERITS_DEFAULTS=
        "inheritsDefaults";
    private static final String TEST_DEFAULT_DICTIONARY_BEGIN_STRING=
        "FIX.4.1";
    private static final String TEST_DEFAULT_DICTIONARY=
        "FIX41.xml";
    private static final String TEST_BAD_BEGIN_STRING=
        "FIX.3.0";
    private static final String TEST_KEY=
        "testKey";
    private static final String TEST_VALUE_SUFFIX=
        "Value";
    private static final String TEST_VALUE_OVERRIDE_SUFFIX=
        TEST_VALUE_SUFFIX+"Override";
    private static final String TEST_FILE=
        TEST_ROOT+"descriptor.xml";
    private static final String TEST_CATEGORY=
        SpringSessionDescriptor.class.getName();
    private static final String TEST_MESSAGE=
        "The descriptor has no settings assigned to it";


    @Test
    public void all()
        throws Exception
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_CATEGORY,Level.WARN);
        FileSystemXmlApplicationContext context=
            new FileSystemXmlApplicationContext(TEST_FILE);

        assertNotNull(SpringSessionDescriptor.DEFAULT_DATA_DICTIONARY);

        // GOOD DESCRIPTOR.

        SpringSessionDescriptor descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_GOOD_DESCRIPTOR));
        getAppender().clear();

        SessionID qID=descriptor.getQSessionID();
        assertNotNull(qID);
        Dictionary qDictionary=descriptor.getQDictionary();
        assertNotNull(qDictionary);

        // Lazy bean failures.
        try {
            descriptor.setDictionary(descriptor.getDictionary());
            fail();
        } catch (I18NRuntimeException ex) {
            // Desired.
        }
        assertNotNull(descriptor.getDictionary());
        try {
            descriptor.setSettings(descriptor.getSettings());
            fail();
        } catch (I18NRuntimeException ex) {
            // Desired.
        }
        assertNull(descriptor.getSettings());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_CATEGORY);

        // Dictionary check.
        assertEquals(SessionSettings.BEGINSTRING+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.BEGINSTRING));
        assertEquals(SessionSettings.SENDERCOMPID+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.SENDERCOMPID));
        assertEquals(SessionSettings.SENDERSUBID+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.SENDERSUBID));
        assertEquals(SessionSettings.SENDERLOCID+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.SENDERLOCID));
        assertEquals(SessionSettings.TARGETCOMPID+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.TARGETCOMPID));
        assertEquals(SessionSettings.TARGETSUBID+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.TARGETSUBID));
        assertEquals(SessionSettings.TARGETLOCID+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.TARGETLOCID));
        assertEquals(SessionSettings.SESSION_QUALIFIER+TEST_VALUE_SUFFIX,
                     qDictionary.getString(SessionSettings.SESSION_QUALIFIER));
        assertEquals(Session.SETTING_DATA_DICTIONARY+TEST_VALUE_SUFFIX,
                     qDictionary.getString(Session.SETTING_DATA_DICTIONARY));
        assertEquals(TEST_KEY+TEST_VALUE_SUFFIX,
                     qDictionary.getString(TEST_KEY));

        // Session ID check.
        assertEquals(SessionSettings.BEGINSTRING+TEST_VALUE_SUFFIX,
                     qID.getBeginString());
        assertEquals(SessionSettings.SENDERCOMPID+TEST_VALUE_SUFFIX,
                     qID.getSenderCompID());
        assertEquals(SessionSettings.SENDERSUBID+TEST_VALUE_SUFFIX,
                     qID.getSenderSubID());
        assertEquals(SessionSettings.SENDERLOCID+TEST_VALUE_SUFFIX,
                     qID.getSenderLocationID());
        assertEquals(SessionSettings.TARGETCOMPID+TEST_VALUE_SUFFIX,
                     qID.getTargetCompID());
        assertEquals(SessionSettings.TARGETSUBID+TEST_VALUE_SUFFIX,
                     qID.getTargetSubID());
        assertEquals(SessionSettings.TARGETLOCID+TEST_VALUE_SUFFIX,
                     qID.getTargetLocationID());
        assertEquals(SessionSettings.SESSION_QUALIFIER+TEST_VALUE_SUFFIX,
                     qID.getSessionQualifier());

        // DESCRIPTOR WITH MISSING BEGINSTRING.

        descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_MISSING_BEGIN_STRING));
        getAppender().clear();

        try {
            descriptor.getQDictionary();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals
                (new I18NRuntimeException
                 (new I18NBoundMessage1P(Messages.FIELD_REQUIRED,
                                         SessionSettings.BEGINSTRING)),ex);
        }
        assertNull(descriptor.getDictionary());
        assertNull(descriptor.getSettings());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_CATEGORY);

        // DESCRIPTOR WITH MISSING SENDERCOMPID.

        descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_MISSING_SENDER_COMP_ID));
        getAppender().clear();

        try {
            descriptor.getQDictionary();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals
                (new I18NRuntimeException
                 (new I18NBoundMessage1P(Messages.FIELD_REQUIRED,
                                         SessionSettings.SENDERCOMPID)),ex);
        }
        assertNotNull(descriptor.getDictionary());
        assertNotNull(descriptor.getSettings());
        assertNoEvents();

        // DESCRIPTOR WITH MISSING TARGETCOMPID.

        descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_MISSING_TARGET_COMP_ID));
        getAppender().clear();

        try {
            descriptor.getQDictionary();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals
                (new I18NRuntimeException
                 (new I18NBoundMessage1P(Messages.FIELD_REQUIRED,
                                         SessionSettings.TARGETCOMPID)),ex);
        }
        assertNotNull(descriptor.getDictionary());
        assertNotNull(descriptor.getSettings());
        assertNoEvents();

        // DESCRIPTOR WITH GOOD DEFAULT DICTIONARY.

        descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_GOOD_DEFAULT_DICTIONARY));
        getAppender().clear();

        qID=descriptor.getQSessionID();
        assertNotNull(qID);
        qDictionary=descriptor.getQDictionary();
        assertNotNull(qDictionary);
        assertNull(descriptor.getDictionary());
        assertNotNull(descriptor.getSettings());
        assertNoEvents();

        assertEquals(TEST_DEFAULT_DICTIONARY_BEGIN_STRING,
                     qID.getBeginString());
        assertEquals(TEST_DEFAULT_DICTIONARY,
                     qDictionary.getString(Session.SETTING_DATA_DICTIONARY));

        // DESCRIPTOR WITH BAD DEFAULT DICTIONARY.

        descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_BAD_DEFAULT_DICTIONARY));
        getAppender().clear();

        try {
            descriptor.getQDictionary();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals
                (new I18NRuntimeException
                 (new I18NBoundMessage1P(Messages.NO_DEFAULT_DATA_DICTIONARY,
                                         TEST_BAD_BEGIN_STRING)),ex);
        }
        assertNotNull(descriptor.getDictionary());
        assertNull(descriptor.getSettings());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_CATEGORY);

        // DESCRIPTOR WHICH INHERITS DEFAULTS.

        descriptor=(SpringSessionDescriptor)
            (context.getBean(TEST_BEAN_INHERITS_DEFAULTS));
        getAppender().clear();

        qID=descriptor.getQSessionID();
        assertNotNull(qID);
        qDictionary=descriptor.getQDictionary();
        assertNotNull(qDictionary);
        assertNotNull(descriptor.getDictionary());
        assertNotNull(descriptor.getSettings());
        assertNoEvents();

        assertEquals(TEST_DEFAULT_DICTIONARY_BEGIN_STRING,
                     qID.getBeginString());
        assertEquals(SessionSettings.SENDERCOMPID+TEST_VALUE_SUFFIX,
                     qID.getSenderCompID());
        assertEquals(SessionSettings.TARGETCOMPID+TEST_VALUE_OVERRIDE_SUFFIX,
                     qID.getTargetCompID());
        assertEquals(TEST_DEFAULT_DICTIONARY,
                     qDictionary.getString(Session.SETTING_DATA_DICTIONARY));
    }
}
