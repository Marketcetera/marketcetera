package org.marketcetera.ors.filters;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.StringField;
import quickfix.field.PartySubID;
import quickfix.field.PartySubIDType;



public class PartyInfoMessageModifierTest extends TestCase {

	private FIXMessageFactory msgFactory = FIXVersion.FIX44.getMessageFactory();
	
	PartyInfoMessageModifier pm =new PartyInfoMessageModifier();

    public PartyInfoMessageModifierTest(String inName) {
        super(inName);
        initModifierParams();
    }

    public static Test suite() {
        return new MarketceteraTestSuite(PartyInfoMessageModifierTest.class);
    }

    public void testModifyOrder() throws Exception {
        Message aMessage = msgFactory.newBasicOrder();
        pm.modifyMessage(aMessage, null, null);
        assertEquals("4", aMessage.getField(new StringField(453)).getValue());
        assertEquals("1", aMessage.getField(new StringField(802)).getValue());
        for(String val: pm.partyMap.values())
        {
        	assertTrue(aMessage.toString().contains(val));
        }
    }


    public void initModifierParams() {
    	Map<String,String> partyMap = new HashMap <String,String> ();
    	partyMap.put("firm", "Ramius");
    	partyMap.put("firmRole", "13");
    	
    	partyMap.put("trader", "ramius-uat-user");
    	partyMap.put("traderRole", "11");
    	
    	partyMap.put("sessionID", "CITIFX-ABC_ALGO");
    	partyMap.put("sessionIDRole", "55");
    	
    	partyMap.put("account", "ramius-uat-k1");
    	partyMap.put("accountRole", "38");

    	pm.setPartyMap(partyMap);
    	pm.setPartySubID(new PartySubID("K1"));
    	pm.setPartySubIDType(new PartySubIDType(26));
	}
}
