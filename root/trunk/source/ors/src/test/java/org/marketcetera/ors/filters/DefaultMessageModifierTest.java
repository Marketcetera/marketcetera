package org.marketcetera.ors.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.HandlInst;
import quickfix.field.MsgType;

/**
 * @author Graham Miller
 * @version $Id$
 */

public class DefaultMessageModifierTest extends TestCase {
    private static final String HEADER_57_VAL = "asdf"; //$NON-NLS-1$
    private static final String FIELD_21_VAL = "qwer"; //$NON-NLS-1$
    private static final String TRAILER_28_VAL = "ppp"; //$NON-NLS-1$
    private static final String FIELD_42_VAL = "123456789101112"; //$NON-NLS-1$

    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();

    public DefaultMessageModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(DefaultMessageModifierTest.class);
    }

    public void testModifyOrder() throws Exception {
        String testValue = "A value"; //$NON-NLS-1$
        DefaultMessageModifier modifier = new DefaultMessageModifier();
        modifier.addDefaultField(111, testValue, DefaultMessageModifier.MessageFieldType.MESSAGE);

        Message aMessage = msgFactory.newBasicOrder();

        modifier.modifyMessage(aMessage, null, null);
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
        new ExpectedTestFailure(CoreException.class,
                                Messages.ORDER_MODIFIER_WRONG_FIELD_FORMAT.getText("27(app")) { //$NON-NLS-1$
            protected void execute() throws Throwable {
                mod.setMsgFields(createFieldsMap(new String[][]{{"27(app", "malformed"}})); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }.run();

    }
    
    public void testModifyOrderValueExists() throws Exception {
        String replacementValue = "Replacement value"; //$NON-NLS-1$
        DefaultMessageModifier modifier = new DefaultMessageModifier();
        modifier.addDefaultField(111, replacementValue, DefaultMessageModifier.MessageFieldType.MESSAGE);

        String originalValue = "Original value"; //$NON-NLS-1$
        Message aMessage = msgFactory.newBasicOrder();
        aMessage.setField(new StringField(111, originalValue));

        modifier.modifyMessage(aMessage, null, null);
        StringField outField = new StringField(111);
        assertEquals(originalValue, aMessage.getField(outField).getValue());

    }

    public void testModifyOrderWithPredicate() throws BackingStoreException, FieldNotFound, CoreException {
        DefaultMessageModifier mod = new DefaultMessageModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57(*)", HEADER_57_VAL}})); //$NON-NLS-1$
        mod.setMsgFields(createFieldsMap(new String[][]{{"21(d)", FIELD_21_VAL}, {"42(admin)", FIELD_42_VAL}})); //$NON-NLS-1$ //$NON-NLS-2$
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28(app)", TRAILER_28_VAL}})); //$NON-NLS-1$

        Message heartbeat = msgFactory.createMessage(MsgType.HEARTBEAT);

        Message newOrderSingle = msgFactory.newBasicOrder();
        // taking this out explicitly to allow the order modifier to set it.
        newOrderSingle.removeField(HandlInst.FIELD);
        
        Message logon = msgFactory.createMessage(MsgType.LOGON);

        assertTrue(mod.modifyMessage(heartbeat, null, null));
        assertTrue(mod.modifyMessage(newOrderSingle, null, null));
        assertTrue(mod.modifyMessage(logon, null, null));

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
     * in the vanilla message after initializing the modifier and RUNNING the message
     * through it
     * @throws Exception
     */
    public void testModifyOrderWithAllMessageTypeModifiers() throws Exception
    {
        DefaultMessageModifier mod = new DefaultMessageModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57", HEADER_57_VAL}})); //$NON-NLS-1$
        mod.setMsgFields(createFieldsMap(new String[][]{{"21", FIELD_21_VAL}})); //$NON-NLS-1$
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28", TRAILER_28_VAL}})); //$NON-NLS-1$

        Message msg = msgFactory.newBasicOrder();
        // taking this out explicitly to allow the order modifier to set it.
        msg.removeField(HandlInst.FIELD);
        assertTrue(mod.modifyMessage(msg, null, null));
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
