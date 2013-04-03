package org.marketcetera.ors.filters;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Test;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.StringField;
import quickfix.field.ClOrdID;

/**
 * @author Sameer Patil
 * @version $Id$
 */

public class OrderTagRemapperMessageModifierTest extends OrderTagRecorderMessageModifierTest {


    private FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();

    public OrderTagRemapperMessageModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(OrderTagRemapperMessageModifierTest.class);
    }


    public void testModifyER() throws Exception {
        Set<Integer> inTagsToWatch =  new LinkedHashSet<Integer>();
        inTagsToWatch.add(1);
        inTagsToWatch.add(55);
        Set<Integer> inTagsToRemove =  new LinkedHashSet<Integer>();
        inTagsToRemove.add(1);
        OrderTagRemapper modifier = new OrderTagRemapper();
        Message aMessage = msgFactory.newBasicOrder();
        aMessage.setField(new StringField(1, "testAccountER"));//$NON-NLS-1$
        aMessage.setField(new StringField(58, "testText"));//$NON-NLS-1$
        aMessage.setField(new ClOrdID("1000001"));//$NON-NLS-1$
        super.testModifyOrder();
        modifier.modifyMessage(aMessage, null, null);
        assertEquals(super.testAccount, aMessage.getField(new StringField(1)).getValue());
        assertTrue(aMessage.isSetField(55));
        assertEquals(super.testSymbol, aMessage.getField(new StringField(55)).getValue());
        assertEquals("testText", aMessage.getField(new StringField(58)).getValue());
    }
    
    public void testModifyNonMatchingER() throws Exception {
        Set<Integer> inTagsToWatch =  new LinkedHashSet<Integer>();
        inTagsToWatch.add(1);
        inTagsToWatch.add(55);
        Set<Integer> inTagsToRemove =  new LinkedHashSet<Integer>();
        inTagsToRemove.add(1);
        OrderTagRemapper modifier = new OrderTagRemapper();
        Message aMessage = msgFactory.newBasicOrder();
        aMessage.setField(new StringField(1, "testAccountER"));//$NON-NLS-1$
        aMessage.setField(new ClOrdID("9999999"));//$NON-NLS-1$
        super.testModifyOrder();
        modifier.modifyMessage(aMessage, null, null);
        assertEquals("testAccountER", aMessage.getField(new StringField(1)).getValue());
        assertFalse(aMessage.isSetField(55));
    }

 
   
    

}
