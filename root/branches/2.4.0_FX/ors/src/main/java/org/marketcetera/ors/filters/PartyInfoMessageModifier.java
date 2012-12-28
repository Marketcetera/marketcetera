package org.marketcetera.ors.filters;


import java.util.*;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.Message;
import quickfix.field.*;


public class PartyInfoMessageModifier implements SessionAwareMessageModifier {
	
    /**
     * Party information
     */
	Map<String,String> partyMap;	
	PartySubID partySubID;
	PartySubIDType partySubIDType;
	
    /**
     * session information value
     */
    private volatile SessionInfo sessionInfo;
    // Session Info can be added to Message depending upon broker requirement.
	
	@Override
	public boolean modifyMessage(Message message,
			ReportHistoryServices historyServices, FIXMessageAugmentor augmentor)
			throws CoreException {
		
		// Modifier works only on FIX 4.4	
       quickfix.fix44.NewOrderSingle.NoPartyIDs group = new quickfix.fix44.NewOrderSingle.NoPartyIDs();
       
       List<String> partyKeys = new ArrayList<String>();
       
       for(String party: partyMap.keySet())
       {
    	   if(!party.contains("Role"))	//$NON-NLS-1$
    	   {
    		   partyKeys.add(party);
    	   }
       }
       for(String key: partyKeys)
       {
    	   group.clear();
    	   group.setField(new PartyID(partyMap.get(key)));
    	   group.setField(new PartyRole(Integer.parseInt(partyMap.get(key+"Role"))));    	   //$NON-NLS-1$
    	   message.addGroup(group);
       }       
       quickfix.fix44.NewOrderSingle.NoPartyIDs.NoPartySubIDs subGroup = new quickfix.fix44.NewOrderSingle.NoPartyIDs.NoPartySubIDs();
       subGroup.setField(partySubID);
       subGroup.setField(partySubIDType);
       message.addGroup(subGroup);
       return true;
	}


	public void setPartyMap(Map<String, String> partyMap) {
		this.partyMap = partyMap;
	}

	public void setPartySubID(PartySubID partySubID) {
		this.partySubID = partySubID;
	}

	public void setPartySubIDType(PartySubIDType partySubIDType) {
		this.partySubIDType = partySubIDType;
	}

	/* (non-Javadoc)
     * @see org.marketcetera.ors.filters.SessionAwareMessageModifier#getSessionInfo()
     */
    @Override
    public SessionInfo getSessionInfo()
    {
        return sessionInfo;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.SessionAwareMessageModifier#setSessionInfo(org.marketcetera.ors.info.SessionInfo)
     */
    @Override
    public void setSessionInfo(SessionInfo inSessionInfo)
    {
        sessionInfo = inSessionInfo;
    }
}
