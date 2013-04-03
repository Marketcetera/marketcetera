package org.marketcetera.ors.filters;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;

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

public class OrderTagRecorderMessageModifierTest extends TestCase {


    private FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();
    
    protected String testAccount = "testAccount";//$NON-NLS-1$
    protected String testSymbol = "testSymbol";//$NON-NLS-1$

    public OrderTagRecorderMessageModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(OrderTagRecorderMessageModifierTest.class);
    }


    public void testModifyOrder() throws Exception {
        Message aMessage = msgFactory.newBasicOrder();
        aMessage.setField(new StringField(1, testAccount));//$NON-NLS-1$
        aMessage.setField(new StringField(55, testSymbol));//$NON-NLS-1$
        aMessage.setField(new ClOrdID("1000001"));//$NON-NLS-1$
        Set<Integer> inTagsToWatch =  new LinkedHashSet<Integer>();
        inTagsToWatch.add(1);
        inTagsToWatch.add(55);
        Set<Integer> inTagsToRemove =  new LinkedHashSet<Integer>();
        inTagsToRemove.add(1);
        OrderTagRecorder modifier = new OrderTagRecorder();
        modifier.setTagsToWatch(inTagsToWatch);
        modifier.setTagsToRemove(inTagsToRemove);
        modifier.modifyMessage(aMessage, null, null);
        assertFalse(aMessage.isSetField(1));
        assertTrue(aMessage.isSetField(55));
    }

 
   
    

}
