package quickfix.spring;

import java.util.List;
import java.util.Map;

public class SessionSettings extends quickfix.SessionSettings {

	public void setDefaults(Map<Object, Object> defaults) {
		super.set(defaults);
	}
	
	public void setSessionDescriptors(List<SessionDescriptor> sessionDescriptors){
        for (SessionDescriptor descriptor : sessionDescriptors) {
            // first, copy the defaults
            Map defaults = get().toMap();
            for (Object oneDefault : defaults.keySet()) {
                setString(descriptor.getSessionID(), (String) oneDefault, defaults.get(oneDefault).toString());
            }

            // then copy the specific ones
            Map<String, String> settings = descriptor.getSettings();
			for (String aKey : settings.keySet()) {
				setString(descriptor.getSessionID(), aKey, settings.get(aKey));
			}
		}
	}

}
