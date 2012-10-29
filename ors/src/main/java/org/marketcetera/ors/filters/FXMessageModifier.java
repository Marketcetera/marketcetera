package org.marketcetera.ors.filters;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.Message;
import quickfix.field.NoPartyIDs;
import quickfix.field.NoPartySubIDs;
import quickfix.field.PartySubID;

public class FXMessageModifier implements MessageModifier {
	
	Map<String,String> partyMap;
	
	@Override
	public boolean modifyMessage(Message message,
			ReportHistoryServices historyServices, FIXMessageAugmentor augmentor)
			throws CoreException {

	   // Currently works only on FIX 4.4
       quickfix.fix44.NewOrderSingle.NoPartyIDs group = new quickfix.fix44.NewOrderSingle.NoPartyIDs();
        
       List<String> partyKeys = new ArrayList<String>();
       
       for(String party: partyMap.keySet())
       {
    	   if(!party.contains("Role"))
    	   {
    		   partyKeys.add(party);
    	   }
       }   
       
       for(String key: partyKeys)
       {
    	   group.setString(448, (String)partyMap.get(key));
    	   group.setString(452, partyMap.get(key+"Role"));
    	   message.addGroup(group);
       }
       return true;
	}


	public void setPartyMap(Map<String, String> partyMap) {
		this.partyMap = partyMap;
	}
	


}
