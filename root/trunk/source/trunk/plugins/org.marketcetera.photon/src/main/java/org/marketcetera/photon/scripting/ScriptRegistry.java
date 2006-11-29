package org.marketcetera.photon.scripting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.bsf.BSFManager;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.marketcetera.core.MMapEntry;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.MapEditorUtil;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.springframework.beans.factory.InitializingBean;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.matchers.Matcher;


/**
 * Script registry implementation.
 * 
 * @author andrei@lissovski.org
 * @author gmiller
 */
public class ScriptRegistry implements IPropertyChangeListener, IResourceChangeListener, InitializingBean {

	private BasicEventList<Entry<IScript, BSFManager>> quoteScripts = new BasicEventList<Entry<IScript, BSFManager>>();
	private BasicEventList<Entry<IScript, BSFManager>> tradeScripts = new BasicEventList<Entry<IScript, BSFManager>>();
	private String initialRegistryValueString;
	
	
	public ScriptRegistry() {
	}
	

	/**
	 * @return <code>null</code> if script couldn't be loaded.
	 */
	protected Script loadScript(String scriptWorkspacePath) {
		String path = workspacePathToAbsolutePath(scriptWorkspacePath);
		try {
			return new Script(readFileAsString(path), path, 1, 1);
		} catch (IOException e) {
			return null;
		}
	}

	private String workspacePathToAbsolutePath(String workspacePath) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResource resource = workspace.getRoot().findMember(workspacePath);
		URI resourceURI = resource.getRawLocationURI();

		return resourceURI.getPath();
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
			String newValue = event.getNewValue().toString();
			updateScripts(newValue);
		}
	}


	private void updateScripts(String registryValueString) {
		EventList<Entry<String, String>> completeEventScriptList = MapEditorUtil.parseString(registryValueString);

		FilterList<Entry<String, String>> quoteScriptList = new FilterList<Entry<String, String>>(
				completeEventScriptList, new Matcher<Entry<String, String>>() {
					public boolean matches(Entry<String, String> entry) {
						return entry.getKey().equalsIgnoreCase(ScriptingEventType.QUOTE.getName());
					}
				});
		unifyScriptLists(quoteScriptList, quoteScripts);

		FilterList<Entry<String, String>> tradeScriptList = new FilterList<Entry<String, String>>(
				completeEventScriptList, new Matcher<Entry<String, String>>() {
					public boolean matches(Entry<String, String> entry) {
						return entry.getKey().equalsIgnoreCase(ScriptingEventType.TRADE.getName());
					}
				});
		unifyScriptLists(tradeScriptList, tradeScripts);
	}

	protected void unifyScriptLists(EventList<Entry<String, String>> newScripts, EventList<Entry<IScript, BSFManager>> existingScripts) {

		//agl only (script path)'s
		EventList<String> scriptList = new FunctionList<Entry<String, String>, String>(
				newScripts,
		new FunctionList.Function<Entry<String, String>, String>() {
			public String evaluate(Entry<String, String> entry) {
				return entry.getValue();
			}
		});

		//agl actual (script)'s
		List<Entry<IScript, BSFManager>> scripts = new FunctionList<String, Entry<IScript, BSFManager>>(
		scriptList,
		new FunctionList.Function<String, Entry<IScript, BSFManager>>() {
			public Entry<IScript, BSFManager> evaluate(String scriptPath) {
				return new KeysEqualEntry<IScript, BSFManager>(loadScript(scriptPath), new BSFManager());
			}
		});

		synchronized (existingScripts.getReadWriteLock()){
			EventList<Entry<IScript, BSFManager>> toDelete = GlazedLists.eventList(existingScripts);
			toDelete.removeAll(scripts);
	
			EventList<Entry<IScript, BSFManager>> toAdd = GlazedLists.eventList(scripts);
			toAdd.removeAll(existingScripts);
			
			if (toDelete.size() > 0)
			{
				existingScripts.removeAll(toDelete);
			}
			if (toAdd.size() > 0)
			{
				existingScripts.addAll(toAdd);
			}
		}
	}
	
	public EventList<Entry<IScript, BSFManager>> getScriptList(ScriptingEventType type) {
		switch (type) {
		case QUOTE:
			return quoteScripts;
		case TRADE:
			return tradeScripts;
		default:
			return null;
		}
	}

	class KeysEqualEntry<K,V> extends MMapEntry<K,V>{


		public KeysEqualEntry(K key, V value) {
			super(key, value);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Map.Entry) {
				Map.Entry otherEntry = (Map.Entry) obj;
				return this.getKey().equals(otherEntry.getKey());
			}
			return false;
		}
		
	}

	public void afterPropertiesSet() throws Exception {
		if (initialRegistryValueString!=null && initialRegistryValueString.length()>0)
		{
			updateScripts(initialRegistryValueString);
		}
	}


	public String getInitialRegistryValueString() {
		return initialRegistryValueString;
	}


	public void setInitialRegistryValueString(String initialRegistryValueString) {
		this.initialRegistryValueString = initialRegistryValueString;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDeltaVisitor resourceDeltaVisitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) throws CoreException {
				if (delta.getResource().getType() == IResource.FILE
						&& (delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.CHANGED)) != 0) {  //agl the framework can fire off several resource change events for a single modification to a file (for example, a separate event for marker changes). we are only interested in the content changes here.

					String resourcePath = delta.getResource().getFullPath().toString();
					if (isScript(resourcePath))
					{
						refreshScript(resourcePath, tradeScripts);
						refreshScript(resourcePath, quoteScripts);
					}

					return false;  //agl skip children
				}

				return true;  //agl visit children
			}
		};
		
		try {
			event.getDelta().accept(resourceDeltaVisitor);
		} catch (CoreException e) {
			PhotonPlugin.getMainConsoleLogger().error("Could not process resource change", e);
		}
	}


	private boolean isScript(String resourcePath) {
		return resourcePath.toLowerCase().endsWith(".rb");  //$NON-NLS-1$
	}

	/**
	 * Reloads a script with the specified path if it's part of the specified list.
	 */
	private void refreshScript(String scriptWorkspacePath, BasicEventList<Entry<IScript, BSFManager>> scripts) {
		String scriptAbsolutePath = workspacePathToAbsolutePath(scriptWorkspacePath);
		synchronized (scripts.getReadWriteLock()) {
			Iterator<Entry<IScript, BSFManager>> iter = scripts.iterator();
			for (int i = 0; iter.hasNext(); i++) {
				Entry<IScript, BSFManager> entry = iter.next();
				if (entry.getKey().getID().equals(scriptAbsolutePath))
				{
					IScript newScript = loadScript(scriptWorkspacePath);
					Entry<IScript, BSFManager> newEntry = new KeysEqualEntry<IScript, BSFManager>(newScript, new BSFManager());
					
					scripts.set(i, newEntry);
				}
			}
		}
	}
}
