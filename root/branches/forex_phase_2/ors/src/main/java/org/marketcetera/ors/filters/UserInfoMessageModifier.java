package org.marketcetera.ors.filters;


import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.SenderSubID;

public class UserInfoMessageModifier implements MessageModifier {
	
	private Map<String,String> orsToBrokerUserMap;
	
	@Override
	public boolean modifyMessage(Message message,
			ReportHistoryServices historyServices, FIXMessageAugmentor augmentor)
			throws CoreException {
			String orsUserName = null;
			if(message.isSetField(SenderSubID.FIELD))
			{
				try {
					orsUserName = message.getString(SenderSubID.FIELD);
				} catch (FieldNotFound e) {
					SLF4JLoggerProxy.error(UserInfoMessageModifier.class,e);
				}
			}
			String brokerUserName = orsToBrokerUserMap.get(orsUserName);		
			if(brokerUserName !=null)
			{
				message.setField(new SenderSubID(brokerUserName));
			}
			return true;
	}

	public void setOrsToBrokerUserMap(Map<String,String> orsToBrokerUserMap) {
		this.orsToBrokerUserMap = orsToBrokerUserMap;
	}
	
}
