package org.marketcetera.ors.filters;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.MsgType;

/**
 * @author Sameer Patil
 * @version $Id$
 */

public class FieldOverrideMessageModifierTest extends TestCase {
    private static final String HEADER_57_VAL_OLD = "asdf"; //$NON-NLS-1$
    private static final String FIELD_21_VAL_OLD = "qwer"; //$NON-NLS-1$
    private static final String TRAILER_28_VAL_OLD = "ppp"; //$NON-NLS-1$
    private static final String FIELD_42_VAL_OLD = "123456789101112"; //$NON-NLS-1$
    private static final String FIELD_63_VAL_OLD = "field63val"; //$NON-NLS-1$
    
    private static final String HEADER_57_VAL_NEW = "asdf_new"; //$NON-NLS-1$
    private static final String FIELD_21_VAL_NEW = "qwer_new"; //$NON-NLS-1$
    private static final String TRAILER_28_VAL_NEW = "ppp_new"; //$NON-NLS-1$
    private static final String FIELD_42_VAL_NEW = "123456789101112_new"; //$NON-NLS-1$
    private static final String FIELD_63_VAL_NEW = "field63val_new"; //$NON-NLS-1$

    private FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();

    public FieldOverrideMessageModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FieldOverrideMessageModifierTest.class);
    }

    public void testModifyOrder() throws Exception {
        String testValue = "A value"; //$NON-NLS-1$
        FieldOverrideMessageModifier modifier = new FieldOverrideMessageModifier();
        modifier.setMsgFields(createFieldsMap(new String[][]{{"111", testValue}}));

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
        final FieldOverrideMessageModifier mod = new FieldOverrideMessageModifier();
        new ExpectedTestFailure(NumberFormatException.class) {
            protected void execute() throws Throwable {
                mod.setMsgFields(createFieldsMap(new String[][]{{"27(app", "malformed"}})); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }.run();

    }
    

    public void testModifyOrderWithPredicate() throws BackingStoreException, FieldNotFound, CoreException {
        FieldOverrideMessageModifier mod = new FieldOverrideMessageModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57", HEADER_57_VAL_NEW}})); //$NON-NLS-1$
        mod.setMsgFields(createFieldsMap(new String[][]{{"21", FIELD_21_VAL_NEW}, {"42", FIELD_42_VAL_NEW}, {"63", FIELD_63_VAL_NEW}})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28", TRAILER_28_VAL_NEW}})); //$NON-NLS-1$

        Message heartbeat = msgFactory.createMessage(MsgType.HEARTBEAT);

        Message newOrderSingle = msgFactory.newBasicOrder();
        // taking this out explicitly to allow the order modifier to set it.
        //newOrderSingle.removeField(HandlInst.FIELD);
        
        Message logon = msgFactory.createMessage(MsgType.LOGON);

        Message confRequest = msgFactory.createMessage(MsgType.CONFIRMATION_REQUEST);
        
 
        
        setHeaderFieldsMap(new String[][]{{"57", HEADER_57_VAL_OLD}}, heartbeat);
        setMsgFieldsMap(new String[][]{{"21", FIELD_21_VAL_OLD}, {"42", FIELD_42_VAL_OLD}, {"63", FIELD_63_VAL_OLD}}, heartbeat);
        setTrailerFieldsMap(new String[][]{{"28", TRAILER_28_VAL_OLD}}, heartbeat);
        
        setHeaderFieldsMap(new String[][]{{"57", HEADER_57_VAL_OLD}}, newOrderSingle);
        setMsgFieldsMap(new String[][]{{"21", FIELD_21_VAL_OLD}, {"42", FIELD_42_VAL_OLD}, {"63", FIELD_63_VAL_OLD}}, newOrderSingle);
        setTrailerFieldsMap(new String[][]{{"28", TRAILER_28_VAL_OLD}}, newOrderSingle);
        
        setHeaderFieldsMap(new String[][]{{"57", HEADER_57_VAL_OLD}}, confRequest);
        setMsgFieldsMap(new String[][]{{"21", FIELD_21_VAL_OLD}, {"42", FIELD_42_VAL_OLD}, {"63", FIELD_63_VAL_OLD}}, confRequest);
        setTrailerFieldsMap(new String[][]{{"28", TRAILER_28_VAL_OLD}}, confRequest);
        
        setHeaderFieldsMap(new String[][]{{"57", HEADER_57_VAL_OLD}}, logon);
        setMsgFieldsMap(new String[][]{{"21", FIELD_21_VAL_OLD}, {"42", FIELD_42_VAL_OLD}, {"63", FIELD_63_VAL_OLD}}, logon);
        setTrailerFieldsMap(new String[][]{{"28", TRAILER_28_VAL_OLD}}, logon);        
        
        assertTrue(heartbeat.getHeader().isSetField(57));
        assertTrue(heartbeat.isSetField(21));
        assertTrue(heartbeat.isSetField(42));
        assertTrue(heartbeat.isSetField(63));
        assertTrue(heartbeat.getTrailer().isSetField(28));
        
        assertTrue(newOrderSingle.getHeader().isSetField(57));
        assertTrue(newOrderSingle.isSetField(21));
        assertTrue(newOrderSingle.isSetField(42));
        assertTrue(newOrderSingle.isSetField(63));
        assertTrue(newOrderSingle.getTrailer().isSetField(28));
        
        assertTrue(confRequest.getHeader().isSetField(57));
        assertTrue(confRequest.isSetField(21));
        assertTrue(confRequest.isSetField(42));
        assertTrue(confRequest.isSetField(63));
        assertTrue(confRequest.getTrailer().isSetField(28));    
        
        assertTrue(logon.getHeader().isSetField(57));
        assertTrue(logon.isSetField(21));
        assertTrue(logon.isSetField(42));
        assertTrue(logon.isSetField(63));
        assertTrue(logon.getTrailer().isSetField(28)); 

        assertTrue(mod.modifyMessage(heartbeat, null, null));
        assertTrue(mod.modifyMessage(newOrderSingle, null, null));
        assertTrue(mod.modifyMessage(logon, null, null));
        assertTrue(mod.modifyMessage(confRequest, null, null));

        assertEquals(HEADER_57_VAL_NEW, heartbeat.getHeader().getString(57));
        assertTrue(heartbeat.isSetField(21));
        assertTrue(heartbeat.getTrailer().isSetField(28));
        assertEquals(FIELD_42_VAL_NEW, heartbeat.getString(42));
        assertTrue(heartbeat.isSetField(63));

        assertEquals(HEADER_57_VAL_NEW, newOrderSingle.getHeader().getString(57));
        assertEquals(FIELD_21_VAL_NEW, newOrderSingle.getString(21));
        assertEquals(TRAILER_28_VAL_NEW, newOrderSingle.getTrailer().getString(28));
        assertTrue(newOrderSingle.isSetField(42));
        assertTrue(newOrderSingle.isSetField(63));

        assertEquals(HEADER_57_VAL_NEW, logon.getHeader().getString(57));
        assertTrue(logon.isSetField(21));
        assertTrue(logon.getTrailer().isSetField(28));
        assertEquals(FIELD_42_VAL_NEW, logon.getString(42));
        assertTrue(logon.isSetField(63));

        assertEquals(HEADER_57_VAL_NEW, confRequest.getHeader().getString(57));
        assertTrue(confRequest.isSetField(21));
        assertEquals(TRAILER_28_VAL_NEW, confRequest.getTrailer().getString(28));
        assertTrue(confRequest.isSetField(42));
        assertEquals(FIELD_63_VAL_NEW, confRequest.getString(63));
    }

    /** Put one extra field in the config and make sure that it appears
     * in the vanilla message after initializing the modifier and RUNNING the message
     * through it
     * @throws Exception
     */
    public void testModifyOrderWithAllMessageTypeModifiers() throws Exception
    {
        FieldOverrideMessageModifier mod = new FieldOverrideMessageModifier();
        mod.setHeaderFields(createFieldsMap(new String[][]{{"57", HEADER_57_VAL_OLD}})); //$NON-NLS-1$
        mod.setMsgFields(createFieldsMap(new String[][]{{"21", FIELD_21_VAL_OLD}})); //$NON-NLS-1$
        mod.setTrailerFields(createFieldsMap(new String[][]{{"28", TRAILER_28_VAL_OLD}})); //$NON-NLS-1$

        Message msg = msgFactory.newBasicOrder();
        
        assertTrue(mod.modifyMessage(msg, null, null));
        assertEquals(HEADER_57_VAL_OLD, msg.getHeader().getString(57));
        assertEquals(FIELD_21_VAL_OLD, msg.getString(21));
        assertEquals(TRAILER_28_VAL_OLD, msg.getTrailer().getString(28));
    }

    public static Map<Integer, String> createFieldsMap(String[][] mappings)
    {
        HashMap<Integer, String> result = new HashMap<Integer, String>();
        for (String[] mapping : mappings) {
            result.put(Integer.parseInt(mapping[0]), mapping[1]);
        }
        return result;
    }
    
    public static void setMsgFieldsMap(String[][] mappings, Message message)
    {
        for (String[] mapping : mappings) {
            message.setField(new StringField(Integer.parseInt(mapping[0]), mapping[1]));
        }
    }
    
    public static void setHeaderFieldsMap(String[][] mappings, Message message)
    {
        for (String[] mapping : mappings) {
            message.getHeader().setField(new StringField(Integer.parseInt(mapping[0]), mapping[1]));
        }
    }

    public static void setTrailerFieldsMap(String[][] mappings, Message message)
    {
        for (String[] mapping : mappings) {
            message.getTrailer().setField(new StringField(Integer.parseInt(mapping[0]), mapping[1]));
        }
    }
}
