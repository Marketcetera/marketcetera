package org.marketcetera.ors.filters;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.Price;
import quickfix.field.TargetStrategy;



public class ConditionalFieldRemoverMessageModifierTest extends TestCase {

	private FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();
	
	ConditionalFieldRemoverMessageModifier pm =new ConditionalFieldRemoverMessageModifier("44(D)");

    public ConditionalFieldRemoverMessageModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(ConditionalFieldRemoverMessageModifierTest.class);
    }

    public void testMatchingModifyOrder() throws Exception {
    	initModifierParams();
        Message aMessage = msgFactory.newBasicOrder();
        Price price = new Price(1.6042);
        aMessage.setField(price);
        aMessage.setField(new TargetStrategy(502));
        assertTrue(aMessage.isSetField(44));
        pm.modifyMessage(aMessage, null, null);
        assertFalse(aMessage.isSetField(44));
    }
    
    public void testNotMatchingModifyOrder() throws Exception {
    	initModifierParams();
        Message aMessage = msgFactory.newBasicOrder();
        Price price = new Price(1.6042);
        aMessage.setField(price);
        aMessage.setField(new TargetStrategy(501));
        assertTrue(aMessage.isSetField(44));
        pm.modifyMessage(aMessage, null, null);
        assertTrue(aMessage.isSetField(44));
    }


    public void initModifierParams() {
    	List<String> matchCriteria = new ArrayList <String> ();
    	matchCriteria.add("502");
    	pm.setMatchCriteria(matchCriteria);
    	pm.setConditionalField(847);
	}
}
