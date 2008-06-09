package org.marketcetera.photon.messaging;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.photon.scripting.ScriptRegistry;

import quickfix.Message;

public class ScriptingControllerListenerAdapter extends DirectMessageListenerAdapter  {
	private ScriptRegistry scriptRegistry;

	public ScriptingControllerListenerAdapter()
	{
		if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("constructor", this);} 
	}
	
	@Override
	protected Object doOnMessage(Object convertedMessage) {
		if (scriptRegistry!= null) {
			scriptRegistry.onFIXEvent((Message) convertedMessage);
		}
		return null;
	}

	public ScriptRegistry getPhotonController() {
		return scriptRegistry;
	}

	public void setScriptRegistry(ScriptRegistry scriptRegistry) {
		this.scriptRegistry = scriptRegistry;
		
	}

}
