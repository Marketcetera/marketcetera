package quickfix.spring;

import java.util.Map;

import quickfix.SessionID;

public class SessionDescriptor {

	private static final String TARGET_COMP_ID_KEY = "TargetCompID";

	private static final String SENDER_COMP_ID_KEY = "SenderCompID";

	private static final String BEGIN_STRING_KEY = "BeginString";

	private SessionID cachedSessionID;
	
	private String senderCompID;
	private String targetCompID;
	private String beginString;

	private Map<String, String> settings;
	
	public String getBeginString() {
		return beginString;
	}
	
	public void setBeginString(String beginString) {
		this.beginString = beginString;
		cachedSessionID = new SessionID(beginString, senderCompID, targetCompID);
	}
	
	public String getSenderCompID() {
		return senderCompID;
	}
	
	public void setSenderCompID(String senderCompID) {
		this.senderCompID = senderCompID;
		cachedSessionID = new SessionID(beginString, senderCompID, targetCompID);
	}
	
	public String getTargetCompID() {
		return targetCompID;
	}
	
	public void setTargetCompID(String targetCompID) {
		this.targetCompID = targetCompID;
		cachedSessionID = new SessionID(beginString, senderCompID, targetCompID);
	}

	public void setSessionID(SessionID id){
		cachedSessionID = id;
	}
	
	public SessionID getSessionID(){
		return cachedSessionID;
	}
	
	public void setSettings(Map<String, String> settings){
		this.settings = settings;
		updateSessionIDFields();
	}
	
	private void updateSessionIDFields() {
		if (!settings.containsKey(BEGIN_STRING_KEY)){
			settings.put(BEGIN_STRING_KEY, cachedSessionID.getBeginString());
		}
		if (!settings.containsKey(SENDER_COMP_ID_KEY)){
			settings.put(SENDER_COMP_ID_KEY, cachedSessionID.getBeginString());
		}
		if (!settings.containsKey(TARGET_COMP_ID_KEY)){
			settings.put(TARGET_COMP_ID_KEY, cachedSessionID.getBeginString());
		}
	}

	public Map<String,String> getSettings()
	{
		return settings;
	}
}
