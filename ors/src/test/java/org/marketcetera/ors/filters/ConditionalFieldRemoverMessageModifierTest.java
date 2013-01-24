package org.marketcetera.ors.filters;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.Price;
import quickfix.field.TargetStrategy;



public class ConditionalFieldRemoverMessageModifierTest extends TestCase {

	private FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();
	
	ConditionalFieldRemoverMessageModifier pm =new ConditionalFieldRemoverMessageModifier("44(D)");
	
	ConditionalFieldRemoverMessageModifier allMessage =new ConditionalFieldRemoverMessageModifier("44(*)");
	
	ConditionalFieldRemoverMessageModifier invalidMsgTypeMessage = null;
	
	ConditionalFieldRemoverMessageModifier invalidModifier =new ConditionalFieldRemoverMessageModifier("   ");
	
	ConditionalFieldRemoverMessageModifier noSkipFieldModifier =new ConditionalFieldRemoverMessageModifier("9999(D)");

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
    
    public void testMatchingModifyOrderWithoutSkipField() throws Exception {
    	List<String> matchCriteria = new ArrayList <String> ();
    	matchCriteria.add("502");
    	noSkipFieldModifier.setMatchCriteria(matchCriteria);
    	noSkipFieldModifier.setConditionalField(847);
    	initModifierParams();
        Message aMessage = msgFactory.newBasicOrder();
        Price price = new Price(1.6042);
        aMessage.setField(price);
        aMessage.setField(new TargetStrategy(502));
        assertFalse(noSkipFieldModifier.modifyMessage(aMessage, null, null));
    }
    
    public void testAllMatchingModifyOrder() throws Exception {
    	List<String> matchCriteria = new ArrayList <String> ();
    	matchCriteria.add("502");
    	allMessage.setMatchCriteria(matchCriteria);
    	allMessage.setConditionalField(847);
    	initModifierParams();
        Message aMessage = msgFactory.newBasicOrder();
        Price price = new Price(1.6042);
        aMessage.setField(price);
        aMessage.setField(new TargetStrategy(502));
        assertTrue(allMessage.modifyMessage(aMessage, null, null));
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

    
    public void testInvalidskipFieldModifyOrder() throws Exception {
    	List<String> matchCriteria = new ArrayList <String> ();
    	matchCriteria.add("502");
    	invalidModifier.setMatchCriteria(matchCriteria);
    	invalidModifier.setConditionalField(847);
        Message aMessage = msgFactory.newBasicOrder();
        Price price = new Price(1.6042);
        aMessage.setField(price);
        aMessage.setField(new TargetStrategy(501));
        assertFalse(invalidModifier.modifyMessage(aMessage, null, null));
    }
    
    public void testInvalidMsgTypeModifyOrder() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(org.marketcetera.ors.filters.Messages.NON_CONFORMING_FIELD_SPECIFICATION.getText()){
            @Override
            protected void run() throws Exception {
            	invalidMsgTypeMessage  = new ConditionalFieldRemoverMessageModifier("44(--J)");

            }
        };
    }

    public void initModifierParams() {
    	List<String> matchCriteria = new ArrayList <String> ();
    	matchCriteria.add("502");
    	pm.setMatchCriteria(matchCriteria);
    	pm.setConditionalField(847);
	}
}
