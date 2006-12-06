package org.marketcetera.photon.scripting;


/**
 * todo:doc
 * 
 * @author andrei@lissovski.org
 */
public enum ScriptingEventType {
	QUOTE("quote"), TRADE("trade");


	private String name;
		
	private ScriptingEventType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return name;
	}
}
