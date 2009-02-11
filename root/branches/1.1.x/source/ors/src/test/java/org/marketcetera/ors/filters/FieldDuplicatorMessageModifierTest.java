package org.marketcetera.ors.filters;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.messagefactory.NoOpFIXMessageAugmentor;
import quickfix.Message;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

public class FieldDuplicatorMessageModifierTest extends TestCase {
    private FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();

    public FieldDuplicatorMessageModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FieldDuplicatorMessageModifierTest.class);
    }

    public void testFieldExists() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        FieldDuplicatorMessageModifier mmod = new FieldDuplicatorMessageModifier(Symbol.FIELD, 7632);
        assertTrue(mmod.modifyMessage(msg, null, new NoOpFIXMessageAugmentor()));
        assertEquals("ABC", msg.getString(7632)); //$NON-NLS-1$
    }

    public void testFieldDNE() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        FieldDuplicatorMessageModifier mmod = new FieldDuplicatorMessageModifier(7631, 7632);
        assertFalse(mmod.modifyMessage(msg, null, new NoOpFIXMessageAugmentor()));
        assertFalse(msg.isSetField(7632));
    }

    public void testNotStringField() throws Exception {
        Message msg = FIXMessageUtilTest.createNOS("ABC", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        FieldDuplicatorMessageModifier mmod = new FieldDuplicatorMessageModifier(Price.FIELD, 7632);
        assertTrue(mmod.modifyMessage(msg, null, new NoOpFIXMessageAugmentor()));
        assertEquals("23.33", msg.getString(7632)); //$NON-NLS-1$
    }
}
