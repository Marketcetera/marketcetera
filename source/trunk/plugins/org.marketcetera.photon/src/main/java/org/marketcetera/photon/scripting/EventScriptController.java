package org.marketcetera.photon.scripting;

import java.util.Map;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.marketcetera.photon.PhotonPlugin;

import ca.odell.glazedlists.EventList;


public class EventScriptController {

	private ScriptingEventType eventType;

	
	public EventScriptController(ScriptingEventType eventType) {
		this.eventType = eventType;
	}
	
	public void onEvent(Object event) {
		ScriptRegistry scriptRegistry = PhotonPlugin.getDefault().getScriptRegistry();
		EventList<Map.Entry<IScript,BSFManager>> scripts = scriptRegistry.getScriptList(eventType);

		synchronized (scripts.getReadWriteLock()) {
			for (Map.Entry<IScript,BSFManager> entry : scripts) {
				IScript aScript = entry.getKey();
				BSFManager manager = entry.getValue();
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
