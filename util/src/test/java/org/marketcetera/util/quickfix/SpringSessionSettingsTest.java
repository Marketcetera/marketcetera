package org.marketcetera.util.quickfix;

import java.io.File;
import java.util.Iterator;
import org.junit.Test;
import org.marketcetera.util.except.I18NRuntimeException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import quickfix.ConfigError;
import quickfix.FileStoreFactory;
import quickfix.JdbcLogFactory;
import quickfix.JdbcStoreFactory;
import quickfix.SLF4JLogFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SpringSessionSettingsTest
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"quickfix"+File.separator;
    private static final String TEST_BEAN_GOOD_SETTINGS=
        "goodSettings";
    private static final String TEST_BEAN_EMPTY_SETTINGS=
        "emptySettings";
    private static final String TEST_BEAN_GOOD_CUSTOM_FACTORIES=
        "goodCustomFactories";
    private static final String TEST_BEAN_BAD_CUSTOM_LOG_FACTORY=
        "badCustomLogFactory";
    private static final String TEST_BEAN_BAD_CUSTOM_STORE_FACTORY=
        "badCustomStoreFactory";
    private static final String TEST_BAD_LOG_FACTORY=
        "BadLogFactory";
    private static final String TEST_BAD_STORE_FACTORY=
        "BadStoreFactory";
    private static final String TEST_KEY=
        "testKey";
    private static final String TEST_VALUE_SUFFIX=
        "Value";
    private static final String TEST_VALUE1_SUFFIX=
        TEST_VALUE_SUFFIX+"1";
    private static final String TEST_VALUE2_SUFFIX=
        TEST_VALUE_SUFFIX+"2";
    private static final String TEST_FILE=
        TEST_ROOT+"settings.xml";


    @Test
    public void all()
        throws Exception
    {
        FileSystemXmlApplicationContext context=
            new FileSystemXmlApplicationContext(TEST_FILE);

        assertNotNull(SpringSessionSettings.LOG_FACTORY_CLASS_PARAM);
        assertNotNull(SpringSessionSettings.MESSAGE_STORE_FACTORY_CLASS_PARAM);

        // GOOD SETTINGS.

        SpringSessionSettings settings=(SpringSessionSettings)
            (context.getBean(TEST_BEAN_GOOD_SETTINGS));

        SessionSettings qSettings=settings.getQSettings();
        assertNotNull(qSettings);
        assertEquals(SLF4JLogFactory.class,
                     settings.getQLogFactory().getClass());
        assertEquals(FileStoreFactory.class,
                     settings.getQMessageStoreFactory().getClass());

        // Lazy bean failures.
        try {
            settings.setDefaults(settings.getDefaults());
            fail();
        } catch (I18NRuntimeException ex) {
            // Desired.
        }
        assertNotNull(settings.getDefaults());
        try {
            settings.setDescriptors(settings.getDescriptors());
            fail();
        } catch (I18NRuntimeException ex) {
            // Desired.
        }
        assertNotNull(settings.getDescriptors());

        // Settings.
        assertEquals(SessionSettings.SENDERCOMPID+TEST_VALUE_SUFFIX,
                     qSettings.getString(SessionSettings.SENDERCOMPID));
        assertEquals(TEST_KEY+TEST_VALUE_SUFFIX,
                     qSettings.getString(TEST_KEY));

        // Descriptors.
        Iterator<SpringSessionDescriptor> descriptors=
            settings.getDescriptors().iterator();
        SessionID qID=descriptors.next().getQSessionID();
        assertEquals(SessionSettings.BEGINSTRING+TEST_VALUE1_SUFFIX,
                     qSettings.getString(qID,SessionSettings.BEGINSTRING));
        assertEquals(SessionSettings.SENDERCOMPID+TEST_VALUE1_SUFFIX,
                     qSettings.getString(qID,SessionSettings.SENDERCOMPID));
        assertEquals(SessionSettings.TARGETCOMPID+TEST_VALUE1_SUFFIX,
                     qSettings.getString(qID,SessionSettings.TARGETCOMPID));
        assertEquals(TEST_KEY+TEST_VALUE_SUFFIX,
                     qSettings.getString(qID,TEST_KEY));
        qID=descriptors.next().getQSessionID();
        assertEquals(SessionSettings.BEGINSTRING+TEST_VALUE2_SUFFIX,
                     qSettings.getString(qID,SessionSettings.BEGINSTRING));
        assertEquals(SessionSettings.SENDERCOMPID+TEST_VALUE_SUFFIX,
                     qSettings.getString(qID,SessionSettings.SENDERCOMPID));
        assertEquals(SessionSettings.TARGETCOMPID+TEST_VALUE2_SUFFIX,
                     qSettings.getString(qID,SessionSettings.TARGETCOMPID));
        assertEquals(TEST_KEY+TEST_VALUE_SUFFIX,
                     qSettings.getString(qID,TEST_KEY));

        // EMPTY SETTINGS.

        settings=(SpringSessionSettings)
            (context.getBean(TEST_BEAN_EMPTY_SETTINGS));

        assertNotNull(settings.getQSettings());
        assertEquals(SLF4JLogFactory.class,
                     settings.getQLogFactory().getClass());
        assertEquals(FileStoreFactory.class,
                     settings.getQMessageStoreFactory().getClass());
        assertNull(settings.getDefaults());
        assertNull(settings.getDescriptors());

        // GOOD CUSTOM FACTORIES.

        settings=(SpringSessionSettings)
            (context.getBean(TEST_BEAN_GOOD_CUSTOM_FACTORIES));

        qSettings=settings.getQSettings();
        assertNotNull(qSettings);
        assertEquals(JdbcLogFactory.class,
                     settings.getQLogFactory().getClass());
        assertEquals(JdbcStoreFactory.class,
                     settings.getQMessageStoreFactory().getClass());
        assertNotNull(settings.getDefaults());
        assertEquals(JdbcLogFactory.class.getName(),
                     settings.getDefaults().get
                     (SpringSessionSettings.
                      LOG_FACTORY_CLASS_PARAM));
        assertEquals(JdbcStoreFactory.class.getName(),
                     settings.getDefaults().get
                     (SpringSessionSettings.
                      MESSAGE_STORE_FACTORY_CLASS_PARAM));
        assertNull(settings.getDescriptors());
        try {
            qSettings.getString
                (SpringSessionSettings.LOG_FACTORY_CLASS_PARAM);
            fail();
        } catch (ConfigError ex) {
            // Desired.
        }
        try {
            qSettings.getString
                (SpringSessionSettings.MESSAGE_STORE_FACTORY_CLASS_PARAM);
            fail();
        } catch (ConfigError ex) {
            // Desired.
        }

        // BAD CUSTOM LOG FACTORY.

        settings=(SpringSessionSettings)
            (context.getBean(TEST_BEAN_BAD_CUSTOM_LOG_FACTORY));

        try {
            settings.getQSettings();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals(I18NRuntimeException.class,ex.getClass());
            assertEquals
                (new I18NBoundMessage1P(Messages.BAD_LOG_FACTORY,
                                        TEST_BAD_LOG_FACTORY),
                 ex.getI18NBoundMessage());
            assertEquals(ClassNotFoundException.class,ex.getCause().getClass());
        }
        assertNotNull(settings.getDefaults());
        assertNull(settings.getDescriptors());

        // BAD CUSTOM STORE FACTORY.

        settings=(SpringSessionSettings)
            (context.getBean(TEST_BEAN_BAD_CUSTOM_STORE_FACTORY));

        try {
            settings.getQSettings();
            fail();
        } catch (I18NRuntimeException ex) {
            assertEquals(I18NRuntimeException.class,ex.getClass());
            assertEquals
                (new I18NBoundMessage1P(Messages.BAD_MESSAGE_STORE_FACTORY,
                                        TEST_BAD_STORE_FACTORY),
                 ex.getI18NBoundMessage());
            assertEquals(ClassNotFoundException.class,ex.getCause().getClass());
        }
        assertNotNull(settings.getDefaults());
        assertNull(settings.getDescriptors());

        // As of QuickFIX/J 1.3.3, it is impossible for a ConfigError
        // to occur during processing of SpringSessionSettings: the
        // signature of a method used during processing includes a
        // ConfigError in its throws clause, but the method body can
        // never throw it. Hence it is impossible to test it.
    }
}
