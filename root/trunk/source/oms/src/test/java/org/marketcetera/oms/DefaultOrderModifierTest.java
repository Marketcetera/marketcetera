package org.marketcetera.oms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.OrderModifierFactory;
import org.marketcetera.quickfix.DefaultOrderModifier;
import org.marketcetera.quickfix.FIXMessageUtil;

import java.util.Properties;
import java.util.prefs.BackingStoreException;

import quickfix.Message;
import quickfix.FieldNotFound;
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

    public DefaultOrderModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new TestSuite(DefaultOrderModifierTest.class);
    }

    /** Put one extra field in the config and make sure that it appears
     * in the vanilla message after initializing the modifier and running the message
     * through it
     * @throws Exception
     */
    public void testModifyOrder() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(OrderModifierFactory.FIX_HEADER_PREFIX+"57", HEADER_57_VAL);
        props.setProperty(OrderModifierFactory.FIX_FIELDS_PREFIX+"21", FIELD_21_VAL);
        props.setProperty(OrderModifierFactory.FIX_TRAILER_PREFIX+"28", TRAILER_28_VAL);
        PropertiesConfigData config = new PropertiesConfigData(props);

        DefaultOrderModifier mod = OrderModifierFactory.defaultsModifierInstance(config);

        Message msg = FIXMessageUtil.createNewMessage();
        assertTrue(mod.modifyOrder(msg));
        assertEquals(HEADER_57_VAL, msg.getHeader().getString(57));
        assertEquals(FIELD_21_VAL, msg.getString(21));
        assertEquals(TRAILER_28_VAL, msg.getTrailer().getString(28));
    }

    public void testModifyOrderWithPredicate() throws BackingStoreException, FieldNotFound, MarketceteraException {
        Properties props = new Properties();
        props.setProperty(OrderModifierFactory.FIX_HEADER_PREFIX+"57(*)", HEADER_57_VAL);
        props.setProperty(OrderModifierFactory.FIX_FIELDS_PREFIX+"21(d)", FIELD_21_VAL);
        props.setProperty(OrderModifierFactory.FIX_TRAILER_PREFIX+"28(app)", TRAILER_28_VAL);
        props.setProperty(OrderModifierFactory.FIX_FIELDS_PREFIX+"42(admin)", FIELD_42_VAL);
        PropertiesConfigData config = new PropertiesConfigData(props);

        DefaultOrderModifier mod = OrderModifierFactory.defaultsModifierInstance(config);

        Message heartbeat = FIXMessageUtil.createNewMessage();
        heartbeat.getHeader().setField(new MsgType(MsgType.HEARTBEAT));

        Message newOrderSingle = FIXMessageUtil.createNewMessage();
        newOrderSingle.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));

        Message logon = FIXMessageUtil.createNewMessage();
        logon.getHeader().setField(new MsgType(MsgType.LOGON));

        assertTrue(mod.modifyOrder(heartbeat));
        assertTrue(mod.modifyOrder(newOrderSingle));
        assertTrue(mod.modifyOrder(logon));

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
}
