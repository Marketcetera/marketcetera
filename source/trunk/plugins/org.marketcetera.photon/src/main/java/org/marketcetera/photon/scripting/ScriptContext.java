package org.marketcetera.photon.scripting;

import java.util.HashMap;

import org.apache.bsf.BSFManager;

public class ScriptContext {

	/**
	 * Generated serialVersionUID;
	 */
	private static final long serialVersionUID = -8292514332941975200L;
	private BSFManager manager;
	private HashMap<String, Object> objects;

	public BSFManager getManager() {
		return manager;
	}

	public void setManager(BSFManager manager) {
		this.manager = manager;
		updateManager();
	}

	public void registerBeans(HashMap<String, Object> objects){
		this.objects = objects;
		updateManager();
	}

	private void updateManager() {
		if (objects!= null && manager!=null){
			for (String objectName : objects.keySet()) {
				manager.registerBean(objectName, objects.get(objectName));
			}
		}
	}
	
	
}
