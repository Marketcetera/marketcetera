package org.marketcetera.photon.scripting;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.marketcetera.core.MMapEntry;

public class EventScriptController {

	BSFManager manager;
	List<Map.Entry<IScript,ScriptContext>> scripts = Collections.synchronizedList(new LinkedList<Map.Entry<IScript,ScriptContext>>());
	
	public void addScript(IScript aScript, ScriptContext context){
		scripts.add(new MMapEntry<IScript, ScriptContext>(aScript, context));
	}
	
	public void removeScript(IScript aScript){
		removeScript(aScript.getID());
	}
	
	public void removeScript(String scriptID){
		synchronized (scripts) {
			for (Map.Entry<IScript,ScriptContext> entry : scripts) {
				if (scriptID.equals(entry.getKey().getID())){
					scripts.remove(entry);
					return;
				}
			}
		}
	}
	
	public void setScripts(List<Map.Entry<IScript,ScriptContext>> theScripts){
		List<Entry<IScript, ScriptContext>> newScripts =
			new LinkedList<Map.Entry<IScript,ScriptContext>>(theScripts);
		newScripts = Collections.synchronizedList(newScripts);
		scripts = newScripts;
	}
	
	public void onEvent(Object event) {
		synchronized (scripts) {
			for (Map.Entry<IScript,ScriptContext> entry : scripts) {
				IScript aScript = entry.getKey();
				ScriptContext context = entry.getValue();
				try {
					aScript.exec(context.getManager());
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
