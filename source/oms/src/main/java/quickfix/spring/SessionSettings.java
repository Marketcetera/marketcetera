package quickfix.spring;

import java.util.List;
import java.util.Map;

public class SessionSettings extends quickfix.SessionSettings {

	public void setDefaults(Map defaults) {
		super.set(defaults);
	}
	
	public void setSessionDescriptors(List<SessionDescriptor> sessionDescriptors){
		for (SessionDescriptor descriptor : sessionDescriptors) {
			Map<String, String> settings = descriptor.getSettings();
			for (String aKey : settings.keySet()) {
				setString(descriptor.getSessionID(), aKey, settings.get(aKey));
			}
		}
	}

}
