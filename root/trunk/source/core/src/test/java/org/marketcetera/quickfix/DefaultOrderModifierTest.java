package org.marketcetera.quickfix;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageKey;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.HandlInst;
import quickfix.field.MsgType;

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
        DefaultMessageModifier modifier = new DefaultMessageModifier();
        modifier.addDefaultField(111, testValue, DefaultMessageModifier.MessageFieldType.MESSAGE);

        Message aMessage = msgFactory.newBasicOrder();

        modifier.modifyMessage(aMessage, null);
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
        final DefaultMessageModifier mod = new DefaultMessageModifier();
        new ExpectedTestFailure(MarketceteraException.class,
                                MessageKey.ORDER_MODIFIER_WRONG_FIELD_FORMAT.getLocalizedMessage("27(app")) {
            protected void execute() throws Throwable {
                mod.setMsgFields(createFieldsMap(new String[][]{{"27(app", "malformed"}}));
            }
        }.run();

    }
    
    public void testModifyOrderValueExists() throws Exception {
        String replacementValue = "Replacement value";
        DefaultMessageModifier modifier = new DefaultMessageModifier();
        modifier.addDefaultField(111, replacementValue, DefaultMessageModifier.MessageFieldType.MESSAGE);

        String originalValue = "Original value";
        Message aMessage = msgFactory.newBasicOrder();
        aMessage.setField(new StringField(111, originalValue));

        modifier.modifyMessage(aMessage, null);
        StringField outField = new StringField(111);
        assertEquals(originalValue, aMessage.getField(outField).getValue());

    }

    public void testModifyOrderWithPredicate() throws BackingStoreException, FieldNotFound, MarketceteraException {
        DefaultMessageModifier mod = new DefaultMessageModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57(*)", HEADER_57_VAL}}));
        mod.setMsgFields(createFieldsMap(new String[][]{{"21(d)", FIELD_21_VAL}, {"42(admin)", FIELD_42_VAL}}));
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28(app)", TRAILER_28_VAL}}));

        Message heartbeat = msgFactory.createMessage(MsgType.HEARTBEAT);

        Message newOrderSingle = msgFactory.newBasicOrder();
        // taking this out explicitly to allow the order modifier to set it.
        newOrderSingle.removeField(HandlInst.FIELD);
        
        Message logon = msgFactory.createMessage(MsgType.LOGON);

        assertTrue(mod.modifyMessage(heartbeat, null));
        assertTrue(mod.modifyMessage(newOrderSingle, null));
        assertTrue(mod.modifyMessage(logon, null));

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
        DefaultMessageModifier mod = new DefaultMessageModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57", HEADER_57_VAL}}));
        mod.setMsgFields(createFieldsMap(new String[][]{{"21", FIELD_21_VAL}}));
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28", TRAILER_28_VAL}}));

        Message msg = msgFactory.newBasicOrder();
        // taking this out explicitly to allow the order modifier to set it.
        msg.removeField(HandlInst.FIELD);
        assertTrue(mod.modifyMessage(msg, null));
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
