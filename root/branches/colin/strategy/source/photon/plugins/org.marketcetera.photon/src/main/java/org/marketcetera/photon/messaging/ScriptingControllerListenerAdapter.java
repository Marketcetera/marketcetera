package org.marketcetera.photon.messaging;

import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;

public class ScriptingControllerListenerAdapter
    extends DirectMessageListenerAdapter
{
	private ScriptRegistry scriptRegistry;

	public ScriptingControllerListenerAdapter()
	{
		SLF4JLoggerProxy.debug(this,
		                       "constructor");  //$NON-NLS-1$
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
