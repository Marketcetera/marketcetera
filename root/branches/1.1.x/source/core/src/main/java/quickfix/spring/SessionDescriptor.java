package quickfix.spring;

import java.util.Map;
import java.util.HashMap;

import quickfix.SessionID;
import org.springframework.beans.factory.InitializingBean;
import org.marketcetera.core.Messages;

public class SessionDescriptor implements InitializingBean {

	private static final String TARGET_COMP_ID_KEY = "TargetCompID"; //$NON-NLS-1$

	private static final String SENDER_COMP_ID_KEY = "SenderCompID"; //$NON-NLS-1$

	private static final String BEGIN_STRING_KEY = "BeginString"; //$NON-NLS-1$

	private SessionID cachedSessionID;
	
	private String senderCompID;
	private String targetCompID;
	private String beginString;

	private Map<String, String> settings = new HashMap<String, String>();
	
	public String getBeginString() {
		return beginString;
	}
	
	public void setBeginString(String beginString) {
		this.beginString = beginString;
	}
	
	public String getSenderCompID() {
		return senderCompID;
	}
	
	public void setSenderCompID(String senderCompID) {
		this.senderCompID = senderCompID;
	}
	
	public String getTargetCompID() {
		return targetCompID;
	}
	
	public void setTargetCompID(String targetCompID) {
		this.targetCompID = targetCompID;
	}

	public void setSessionID(SessionID id){
		cachedSessionID = id;
	}
	
	public SessionID getSessionID(){
		return cachedSessionID;
	}
	
	public void setSettings(Map<String, String> settings){
		this.settings = settings;
	}
	
	private void updateSessionIDFields() {
		if (!settings.containsKey(BEGIN_STRING_KEY)){
			settings.put(BEGIN_STRING_KEY, cachedSessionID.getBeginString());
		}
		if (!settings.containsKey(SENDER_COMP_ID_KEY)){
			settings.put(SENDER_COMP_ID_KEY, cachedSessionID.getSenderCompID());
		}
		if (!settings.containsKey(TARGET_COMP_ID_KEY)){
			settings.put(TARGET_COMP_ID_KEY, cachedSessionID.getTargetCompID());
		}
	}

	public Map<String,String> getSettings()
	{
		return settings;
	}

    public void afterPropertiesSet() throws Exception {
        if((cachedSessionID != null) && ((beginString != null ) || (senderCompID != null) || (targetCompID != null))) {
            throw new IllegalStateException(Messages.ERROR_CONFIG_SESSIONID_INDIVIDUAL_PROPS_SET.getText());
        }
        if(cachedSessionID == null) {
            cachedSessionID = new SessionID(beginString, senderCompID, targetCompID);
        }
        updateSessionIDFields();
    }
}
