package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.*;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.MsgType;

import java.util.Map;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DefaultOrderModifierTest extends TestCase {
    private static final String HEADER_57_VAL = "asdf";
    private static final String FIELD_21_VAL = "qwer";
    private static final String TRAILER_28_VAL = "ppp";
    private static final String FIELD_42_VAL = "123456789101112";

    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();

    public DefaultOrderModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(DefaultOrderModifierTest.class);
    }

    public void testModifyOrder() throws Exception {
        String testValue = "A value";
        DefaultOrderModifier modifier = new DefaultOrderModifier();
        modifier.addDefaultField(111, testValue, DefaultOrderModifier.MessageFieldType.MESSAGE);

        Message aMessage = msgFactory.createNewMessage();

        modifier.modifyOrder(aMessage, null);
        StringField outField = new StringField(111);
        assertEquals(testValue, aMessage.getField(outField).getValue());
        final Message outerMessage = aMessage;
        new ExpectedTestFailure(FieldNotFound.class, null) {
                protected void execute() throws Throwable
                {
                    outerMessage.getField(new StringField(112));
                }
            }.run();
    }

    public void testIncorrectModiferListingFormat()
    {
        final DefaultOrderModifier mod = new DefaultOrderModifier();
        new ExpectedTestFailure(MarketceteraException.class,
                                MessageKey.ORDER_MODIFIER_WRONG_FIELD_FORMAT.getLocalizedMessage("27(app")) {
            protected void execute() throws Throwable {
                mod.setMsgFields(createFieldsMap(new String[][]{{"27(app", "malformed"}}));
            }
        }.run();

    }
    
    public void testModifyOrderValueExists() throws Exception {
        String replacementValue = "Replacement value";
        DefaultOrderModifier modifier = new DefaultOrderModifier();
        modifier.addDefaultField(111, replacementValue, DefaultOrderModifier.MessageFieldType.MESSAGE);

        String originalValue = "Original value";
        Message aMessage = msgFactory.createNewMessage();
        aMessage.setField(new StringField(111, originalValue));

        modifier.modifyOrder(aMessage, null);
        StringField outField = new StringField(111);
        assertEquals(originalValue, aMessage.getField(outField).getValue());

    }

    public void testModifyOrderWithPredicate() throws BackingStoreException, FieldNotFound, MarketceteraException {
        DefaultOrderModifier mod = new DefaultOrderModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57(*)", HEADER_57_VAL}}));
        mod.setMsgFields(createFieldsMap(new String[][]{{"21(d)", FIELD_21_VAL}, {"42(admin)", FIELD_42_VAL}}));
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28(app)", TRAILER_28_VAL}}));

        Message heartbeat = msgFactory.createNewMessage();
        heartbeat.getHeader().setField(new MsgType(MsgType.HEARTBEAT));

        Message newOrderSingle = msgFactory.createNewMessage();
        newOrderSingle.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));

        Message logon = msgFactory.createNewMessage();
        logon.getHeader().setField(new MsgType(MsgType.LOGON));

        assertTrue(mod.modifyOrder(heartbeat, null));
        assertTrue(mod.modifyOrder(newOrderSingle, null));
        assertTrue(mod.modifyOrder(logon, null));

        assertEquals(HEADER_57_VAL, heartbeat.getHeader().getString(57));
        assertFalse(heartbeat.isSetField(21));
        assertFalse(heartbeat.getTrailer().isSetField(28));
        assertEquals(FIELD_42_VAL, heartbeat.getString(42));

        assertEquals(HEADER_57_VAL, newOrderSingle.getHeader().getString(57));
        assertEquals(FIELD_21_VAL, newOrderSingle.getString(21));
        assertEquals(TRAILER_28_VAL, newOrderSingle.getTrailer().getString(28));
        assertFalse(newOrderSingle.isSetField(42));

        assertEquals(HEADER_57_VAL, logon.getHeader().getString(57));
        assertFalse(logon.isSetField(21));
        assertFalse(logon.getTrailer().isSetField(28));
        assertEquals(FIELD_42_VAL, logon.getString(42));
    }

    /** Put one extra field in the config and make sure that it appears
     * in the vanilla message after initializing the modifier and running the message
     * through it
     * @throws Exception
     */
    public void testModifyOrderWithAllMessageTypeModifiers() throws Exception
    {
        DefaultOrderModifier mod = new DefaultOrderModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57", HEADER_57_VAL}}));
        mod.setMsgFields(createFieldsMap(new String[][]{{"21", FIELD_21_VAL}}));
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28", TRAILER_28_VAL}}));

        Message msg = msgFactory.createNewMessage();
        assertTrue(mod.modifyOrder(msg, null));
        assertEquals(HEADER_57_VAL, msg.getHeader().getString(57));
        assertEquals(FIELD_21_VAL, msg.getString(21));
        assertEquals(TRAILER_28_VAL, msg.getTrailer().getString(28));
    }



    /** Given an array of key->value mappings, createsa a map out of them
     * Example:
     *  createFieldsMap(new String[][]{{"21(d)", FIELD_21_VAL}, {"42(admin)", FIELD_42_VAL}});
     */
    public static Map<String, String> createFieldsMap(String[][] mappings)
    {
        HashMap<String, String> result = new HashMap<String, String>();
        for (String[] mapping : mappings) {
            result.put(mapping[0], mapping[1]);
        }
        return result;
    }

}
