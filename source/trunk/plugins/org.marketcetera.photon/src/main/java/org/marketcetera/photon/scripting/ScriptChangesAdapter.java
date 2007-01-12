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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
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
				IResource resource = delta.getResource();
				int resourceType = resource.getType();
				int resourceFlags = delta.getFlags();
				IPath resourcePath = resource.getFullPath();
				if (resourceType == IResource.FILE
						&& (resourceFlags & (IResourceDelta.CONTENT | IResourceDelta.CHANGED)) != 0) {  //agl the framework can fire off several resource change events for a single modification to a file (for example, a separate event for marker changes). we are only interested in the content changes here.
 
 					if (isScript(resourcePath))
 					{
 						try {
 							registry.scriptChanged(
 									normalizeName(getProjectRelativePath(resourcePath))
 									);
 						} catch (BSFException bsfe) {
							// why oh wy didn't they just allow a cause parameter
							// to the CoreException constructor?
							CoreException ex = new CausedCoreException(Status.CANCEL_STATUS, bsfe);
							throw ex;
						}
 					}
 					 
 					return false;  //agl skip children
				} else if (resourceType == IResource.FILE && resourcePath.lastSegment().equals(".project")){
					String absolutePath = resource.getLocation().removeLastSegments(1).toOSString();
					if ((resourceFlags & (IResourceDelta.ADDED))!=0) 
					{
						// project added
						registry.projectAdded(absolutePath);
					}
					else if ((resourceFlags & IResourceDelta.REMOVED)!=0)
					{
						// project removed
						registry.projectRemoved(absolutePath);
					}
					return false;  //gjm skip children
 				}
 
 				return true;  //agl visit children
			}

		};
		
		try {
			event.getDelta().accept(resourceDeltaVisitor);
		} catch (CoreException e) {
			Logger mainConsoleLogger = PhotonPlugin.getMainConsoleLogger();
			mainConsoleLogger.error("Could not process resource change", e);
			Throwable cause = e.getCause();
			if (cause instanceof BSFException) {
				BSFException bsfe = (BSFException) cause;
				ScriptLoggingUtil.error(mainConsoleLogger, bsfe);
			}
		}
	}
 	
 	String getProjectRelativePath(IPath path){
 		return path.removeFirstSegments(1).toString();
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

	class CausedCoreException extends CoreException{

		private Throwable mCause;

		public CausedCoreException(IStatus status, Throwable cause) {
			super(status);
			mCause = cause;
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 9043840065856991021L;

		@Override
		public Throwable getCause() {
			return mCause;
		}

		
		
	}


	
}
