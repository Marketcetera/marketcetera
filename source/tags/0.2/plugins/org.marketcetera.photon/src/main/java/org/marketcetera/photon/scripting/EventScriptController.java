package org.marketcetera.photon.scripting;

import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;
import ca.odell.glazedlists.EventList;


public class EventScriptController {

	private static final String TRADE_BEAN_NAME = "trade";
	private static final String QUOTE_BEAN_NAME = "quote";
	private ScriptingEventType eventType;

	
	public EventScriptController(ScriptingEventType eventType) {
		this.eventType = eventType;
	}
	
	public void onEvent(Message event) throws BSFException {
		ScriptRegistry scriptRegistry = PhotonPlugin.getDefault().getScriptRegistry();
		EventList<Map.Entry<IScript,BSFManager>> scripts = scriptRegistry.getScriptList(eventType);

		synchronized (scripts.getReadWriteLock()) {
			for (Map.Entry<IScript,BSFManager> entry : scripts) {
				IScript aScript = entry.getKey();
				BSFManager manager = entry.getValue();
				
				String beanName;
				switch (eventType){
				case QUOTE:
					manager.undeclareBean(QUOTE_BEAN_NAME);
					manager.declareBean(QUOTE_BEAN_NAME, event, event.getClass());
					break;
				case TRADE:
					manager.undeclareBean(TRADE_BEAN_NAME);
					manager.declareBean(TRADE_BEAN_NAME, event, event.getClass());
					break;
				}
				try {
					aScript.exec(manager);
				} catch (BSFException e) {
					handleException(e);
				}
			}
		}
	}
	
	protected void handleException(Exception ex){
		// TODO Auto-generated catch block
		ex.printStackTrace();
	}

	
}
