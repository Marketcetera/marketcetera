package org.marketcetera.photon.scripting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.preferences.MapEditorUtil;
import org.marketcetera.photon.preferences.ScriptRegistryPage;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.matchers.Matcher;


/**
 * Script registry implementation.
 * 
 * @author andrei@lissovski.org
 * @author gmiller
 */
public class ScriptRegistry implements IPropertyChangeListener {

	EventScriptController tradeScriptController;
	EventScriptController quoteScriptController;
	
	public ScriptRegistry() {
	}
	
	/* (non-Javadoc)
	 * @see org.marketcetera.photon.scripting.IScriptRegistry#listScriptsByEventType(org.marketcetera.photon.scripting.ScriptingEventType)
	 */
	public List<IScript> listScriptsByEventType(final ScriptingEventType eventType) {
		String encodedScriptList = Application.getPreferenceStore().getString(
				ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE);
		//agl (any event type, script path) tuples
		EventList<Entry<String, String>> completeEventScriptList = MapEditorUtil
				.parseString(encodedScriptList);
		//agl (specified event type, script path) tuples
		FilterList<Entry<String, String>> eventScriptList = new FilterList<Entry<String, String>>(
				completeEventScriptList, new Matcher<Entry<String, String>>() {
					public boolean matches(Entry<String, String> entry) {
						return entry.getKey().equalsIgnoreCase(eventType.getName());
					}
				});
		//agl only (script path)'s
		EventList<String> scriptList = new FunctionList<Entry<String, String>, String>(
				eventScriptList,
				new FunctionList.Function<Entry<String, String>, String>() {
					public String evaluate(Entry<String, String> entry) {
						return entry.getValue();
					}
				});

		//agl actual (script)'s
		List<IScript> scripts = new FunctionList<String, IScript>(
				scriptList,
				new FunctionList.Function<String, IScript>() {
					public IScript evaluate(String scriptPath) {
						return loadScript(scriptPath);
					}
				});
		return scripts;
	}

	/**
	 * @return <code>null</code> if script couldn't be loaded.
	 */
	private Script loadScript(String scriptWorkspacePath) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResource scriptResource = workspace.getRoot().findMember(scriptWorkspacePath);
		URI scriptResourceURI = scriptResource.getRawLocationURI();

		try {
			String path = scriptResourceURI.getPath();
			return new Script(readFileAsString(path), path, 1, 1);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * @param filePath
	 *            the name of the file to open. Not sure if it can accept URLs
	 *            or just filenames. Path handling could be better, and buffer
	 *            sizes are hardcoded
	 */
	private static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		} finally {
			if (reader != null ) reader.close();
		}
		return fileData.toString();
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE)){
			Object newValue = event.getNewValue();
			Object oldValue = event.getOldValue();
			if (newValue != null){
				//deleted
			}
		}
	}


}
