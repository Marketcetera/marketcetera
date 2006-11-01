package org.marketcetera.photon.scripting;

import java.util.List;


/**
 * Script registry. Resolves mappings of scripting events to particular scripts.
 * 
 * @author andrei@lissovski.org
 */
public interface IScriptRegistry {
	List<IScript> listScriptsByEventType(ScriptingEventType eventType);
}
