package org.marketcetera.photon.commons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNotNull;

import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.test.SerializableAssert;

/* $License$ */

/**
 * Tests {@link ReflectiveMessages}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ReflectiveMessagesTest extends PhotonTestBase {

    @Override
    public void setupTestCaseBase() {
        super.setupTestCaseBase();
        setLevel(ReflectiveMessages.class.getName(), Level.ALL);
    }

    @Test
    public void testNonStaticFieldIgnored() {
        ReflectiveMessages.init(Messages1.class);
        assertMessage(Messages1.GOOD, 0, "good");
        assertSomeEvent(
                Level.INFO,
                ReflectiveMessages.class.getName(),
                "Non-static field 'NON_STATIC' on class 'org.marketcetera.photon.commons.ReflectiveMessagesTest$Messages1' was ignored.",
                ReflectiveMessages.class.getName());
    }

    public static class Messages1 {
        I18NMessage0P NON_STATIC;
        static I18NMessage0P GOOD;
    }

    @Test
    public void testNonMessageFieldIgnored() {
        ReflectiveMessages.init(Messages2.class);
        assertMessage(Messages2.GOOD, 1, "good");
        assertSomeEvent(
                Level.INFO,
                ReflectiveMessages.class.getName(),
                "Unsupported type 'java.lang.String' for field 'NON_MESSAGE' on class 'org.marketcetera.photon.commons.ReflectiveMessagesTest$Messages2' was ignored. Field types must either be assignable to I18NMessage or must have a static 'initReflectiveMessages' method.",
                ReflectiveMessages.class.getName());
    }

    public static class Messages2 {
        static String NON_MESSAGE;
        static I18NMessage1P GOOD;
    }

    @Test
    public void testSerialization() throws Exception {
        ReflectiveMessages.init(Messages3.class);
        SerializableAssert.assertSerializable(Messages3.TO_SERIALIZE);
    }

    public static class Messages3 {
        static I18NMessage0P TO_SERIALIZE;
    }

    @Test
    public void testEntryId() {
        ReflectiveMessages.init(Messages4.class);
        assertMessage(Messages4.MESSAGE__FOO, 0, "message", "foo");
        assertMessage(Messages4.MESSAGE__, 0, "message__");
        assertMessage(Messages4.MESSAGE____, 0, "message____");
        assertMessage(Messages4.MESSAGE__ABC__XYZ, 0, "message__abc", "xyz");
        assertMessage(Messages4.__XYZ, 0, "", "xyz");
        assertMessage(Messages4.__, 0, "__");
    }

    public static class Messages4 {
        static I18NMessage0P MESSAGE__FOO;
        static I18NMessage0P MESSAGE__;
        static I18NMessage0P MESSAGE____;
        static I18NMessage0P MESSAGE__ABC__XYZ;
        static I18NMessage0P __XYZ;
        static I18NMessage0P __;
    }

    @Test
    public void testExtension() {
        ReflectiveMessages.init(Messages5.class);
        assertThat(Messages5.EXTENSION, sameInstance(Extension.sInstance));
    }

    public static class Messages5 {
        static Extension EXTENSION;
    }

    public static class Extension {
        static final Extension sInstance = new Extension();

        static Extension initReflectiveMessages(String fieldName,
                I18NLoggerProxy logger) {
            return sInstance;
        }
    }

    @Test
    public void testFieldInitializationFailure() {
        ReflectiveMessages.init(Messages6.class);
        assertSomeEvent(
                Level.ERROR,
                ReflectiveMessages.class.getName(),
                "Unable to initialize message field 'BOGUS' on class 'org.marketcetera.photon.commons.ReflectiveMessagesTest$Messages6'.",
                ReflectiveMessages.class.getName());
    }

    public static class Messages6 {
        static class Bogus extends I18NMessage0P {
            private static final long serialVersionUID = 1L;

            public Bogus(I18NLoggerProxy loggerProxy, String messageId) {
                super(loggerProxy, messageId);
                throw new RuntimeException();
            }
        }

        static Bogus BOGUS;
    }

    @Test
    public void testExtensionFailure() {
        ReflectiveMessages.init(Messages7.class);
        assertSomeEvent(
                Level.ERROR,
                ReflectiveMessages.class.getName(),
                "Unable to initialize message field 'EXT2' on class 'org.marketcetera.photon.commons.ReflectiveMessagesTest$Messages7'.",
                ReflectiveMessages.class.getName());
    }

    public static class Extension2 {
        static Extension2 initReflectiveMessages(String fieldName,
                I18NLoggerProxy logger) throws Exception {
            throw new Exception();
        }
    }

    public static class Messages7 {
        static Extension2 EXT2;
    }

    private void assertMessage(I18NMessage message, int paramCount,
            String messageId) {
        assertMessage(message, paramCount, messageId,
                I18NMessage.UNKNOWN_ENTRY_ID);
    }

    private void assertMessage(I18NMessage message, int paramCount,
            String messageId, String entryId) {
        assertNotNull(message);
        assertNotNull(message.getMessageProvider());
        assertNotNull(message.getLoggerProxy());
        assertThat(message.getParamCount(), is(paramCount));
        assertThat(message.getMessageId(), is(messageId));
        assertThat(message.getEntryId(), is(entryId));
    }

}
