package org.marketcetera.photon.scripting;

import java.util.LinkedList;
import java.util.List;


/**
 * Mock script registry.
 * 
 * @author andrei@lissovski.org
 */
public class MockScriptRegistry implements IScriptRegistry {
	List<IScript> onQuoteScriptList = new LinkedList<IScript>();
	List<IScript> onTradeScriptList = new LinkedList<IScript>();
	
	
	public MockScript createAndRegisterMockScript(ScriptingEventType eventType) {
		MockScript mockScript = new MockScript();
		
		if (eventType == ScriptingEventType.QUOTE)
			onQuoteScriptList.add(mockScript);
		else if (eventType == ScriptingEventType.TRADE)
			onTradeScriptList.add(mockScript);
		else
			throw new IllegalArgumentException("Unknown event type");
			
		return mockScript;
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.scripting.IScriptRegistry#listScriptsByEventType(org.marketcetera.photon.scripting.ScriptingEventType)
	 */
	public List<IScript> listScriptsByEventType(ScriptingEventType eventType) {
		if (eventType == ScriptingEventType.QUOTE)
			return onQuoteScriptList;
		else if (eventType == ScriptingEventType.TRADE)
			return onTradeScriptList;
		else
			throw new IllegalArgumentException("Unknown event type");
	}

}
