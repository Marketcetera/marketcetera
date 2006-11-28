package org.marketcetera.photon.scripting;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import ca.odell.glazedlists.EventList;

public class EventScriptController {

	BSFManager manager;
	EventList<Map.Entry<IScript,BSFManager>> scripts;
	
	
	public void setScriptList(EventList<Map.Entry<IScript, BSFManager>> scripts) {
		this.scripts = scripts;
	}

	public void onEvent(Object event) {
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
