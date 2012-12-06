package org.marketcetera.ors.filters;


import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;
import quickfix.field.SenderSubID;

public class UserInfoMessageModifier implements SessionAwareMessageModifier {
	
	private Map<String,String> orsToBrokerUserMap;
	
	@Override
	public boolean modifyMessage(Message message,
			ReportHistoryServices historyServices, FIXMessageAugmentor augmentor)
			throws CoreException {
			String orsUserName = null;
			if(sessionInfo != null) {
			    SimpleUser currentUser = (SimpleUser)sessionInfo.getValue(SessionInfo.ACTOR);
			    if(currentUser == null) {
	                SLF4JLoggerProxy.debug(this,
	                                       "No session username, quitting");
	                return false;
			    }
			    orsUserName = currentUser.getName();
			}
			String brokerUserName = orsToBrokerUserMap.get(orsUserName);		
			if(brokerUserName ==null)
			{
			    SLF4JLoggerProxy.debug(this,
			                           "No broker username for {}",
			                           orsUserName);
			    return false;
			}
			message.setField(new SenderSubID(brokerUserName));
			return true;
	}

	public void setOrsToBrokerUserMap(Map<String,String> orsToBrokerUserMap) {
		this.orsToBrokerUserMap = orsToBrokerUserMap;
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
    /**
     * session information value
     */
    private volatile SessionInfo sessionInfo;
}
