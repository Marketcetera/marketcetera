package org.marketcetera.photon.scripting;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.bsf.BSFException;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.ListEditorUtil;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.springframework.beans.factory.InitializingBean;

import ca.odell.glazedlists.BasicEventList;

public class ScriptChangesAdapter implements IPropertyChangeListener, IResourceChangeListener, InitializingBean {
	private String initialRegistryValueString;
	private BasicEventList<String> currentScripts = new BasicEventList<String>();
	private ScriptRegistry registry;

	public String getInitialRegistryValueString() {
		return initialRegistryValueString;
	}


	public void setInitialRegistryValueString(String initialRegistryValueString) {
		this.initialRegistryValueString = initialRegistryValueString;
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE)){
			String newValue = event.getNewValue().toString();
			updateScripts(newValue);
		}
	}

	private void updateScripts(String registryValueString) {
		String[] registryStringArray = ListEditorUtil.parseString(registryValueString);

		List<String> toAdd = new LinkedList<String>(Arrays.asList(registryStringArray));
		toAdd.removeAll(currentScripts);

		List<String> toDelete = new LinkedList<String>(currentScripts);
		toDelete.removeAll(Arrays.asList(registryStringArray));
		
		Logger mainConsoleLogger = PhotonPlugin.getMainConsoleLogger();
		for (String aScript : toDelete) {
			aScript = normalizeName(aScript);
			currentScripts.remove(aScript);
			try {
				registry.unregister(aScript);
			} catch (BSFException e) {
				mainConsoleLogger.error("Error unregistering script "+aScript, e);
			}
		}
		for (String aScript : toAdd) {
			aScript = normalizeName(aScript);
			currentScripts.add(aScript);
			try {
				registry.register(aScript);
			} catch (BSFException e) {
				mainConsoleLogger.error("Error registering script "+aScript);
				ScriptLoggingUtil.error(mainConsoleLogger, e);
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		if (initialRegistryValueString!=null && initialRegistryValueString.length()>0)
		{
			updateScripts(initialRegistryValueString);
		}
	}
	
	
	
	private boolean isScript(IPath resourcePath) {
		return resourcePath.getFileExtension().equalsIgnoreCase("rb");  //$NON-NLS-1$
	}


	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDeltaVisitor resourceDeltaVisitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) throws CoreException {
				if (delta.getResource().getType() == IResource.FILE
						&& (delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.CHANGED)) != 0) {  //agl the framework can fire off several resource change events for a single modification to a file (for example, a separate event for marker changes). we are only interested in the content changes here.

					IPath resourcePath = delta.getResource().getLocation();
					if (isScript(resourcePath))
					{
						
						try {
							registry.scriptChanged(normalizeName(resourcePath.toOSString()));
						} catch (BSFException e) {
							CoreException ex = new CoreException(Status.CANCEL_STATUS);
							throw ex;
						}
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

	protected String normalizeName(String string) {
		if (string.endsWith(".rb")){
			int length = string.length();
			int end = length - 3;
			int start = 0;
			if (string.startsWith("/")){
				start = 1;
			}
			return string.substring(start, end);
		}
		return string;
	}

	/**
	 * @return the registry
	 */
	public ScriptRegistry getRegistry() {
		return registry;
	}


	/**
	 * @param registry the registry to set
	 */
	public void setRegistry(ScriptRegistry registry) {
		this.registry = registry;
	}



	
}
