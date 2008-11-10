/*
 * Created on Jan 29, 2005
 *
 */
package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;
import org.rubypeople.rdt.internal.core.util.Messages;

/**
 * @author Chris
 * 
 */
public class RubyModel extends Openable implements IRubyModel {

	/**
	 * A set of java.io.Files used as a cache of external jars that 
	 * are known to be existing.
	 * Note this cache is kept for the whole session.
	 */ 
	public static HashSet existingExternalFiles = new HashSet();
	
	/**
	 * A set of external files ({@link #existingExternalFiles}) which have
	 * been confirmed as file (ie. which returns true to {@link java.io.File#isFile()}.
	 * Note this cache is kept for the whole session.
	 */ 
	public static HashSet existingExternalConfirmedFolders = new HashSet();
	
	protected RubyModel() {
		super(null);
	}
	
	/**
	 * Flushes the cache of external files known to be existing.
	 */
	public static void flushExternalFileCache() {
		existingExternalFiles = new HashSet();
		existingExternalConfirmedFolders = new HashSet();
	}

	/**
	 * Returns a new element info for this element.
	 */
	protected Object createElementInfo() {
		return new RubyModelInfo();
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyModel)) return false;
		return super.equals(o);
	}

	/**
	 * @see IRubyModel
	 */
	public Object[] getNonRubyResources() throws RubyModelException {
		return ((RubyModelInfo) getElementInfo()).getNonRubyResources();
	}

	/**
	 * Finds the given project in the list of the java model's children. Returns
	 * null if not found.
	 */
	public IRubyProject findRubyProject(IProject project) {
		try {
			IRubyProject[] projects = this.getRubyProjects();
			for (int i = 0, length = projects.length; i < length; i++) {
				IRubyProject rubyProject = projects[i];
				if (project.equals(rubyProject.getProject())) { return rubyProject; }
			}
		} catch (RubyModelException e) {
			// ruby model doesn't exist: cannot find any project
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#getElementType()
	 */
	public int getElementType() {
		return IRubyElement.RUBY_MODEL;
	}

	/*
	 * @see IRubyElement
	 */
	public IPath getPath() {
		return Path.ROOT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#getResource()
	 */
	public IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * @see IOpenable
	 */
	public IResource getUnderlyingResource() {
		return null;
	}

	/**
	 * Returns the workbench associated with this object.
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * @see IRubyModel
	 */
	public IRubyProject[] getRubyProjects() throws RubyModelException {
		ArrayList list = getChildrenOfType(RUBY_PROJECT);
		IRubyProject[] array = new IRubyProject[list.size()];
		list.toArray(array);
		return array;

	}

	protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) {
		// determine my children
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0, max = projects.length; i < max; i++) {
			IProject project = projects[i];
			if (RubyProject.hasRubyNature(project)) {
				info.addChild(getRubyProject(project));
			}
		}
		newElements.put(this, info);
		return true;
	}

	/**
	 * Returns the active Ruby project associated with the specified resource,
	 * or <code>null</code> if no Ruby project yet exists for the resource.
	 * 
	 * @exception IllegalArgumentException
	 *                if the given resource is not one of an IProject, IFolder,
	 *                or IFile.
	 */
	public IRubyProject getRubyProject(IResource resource) {
		switch (resource.getType()) {
		case IResource.FOLDER:
			return new RubyProject(((IFolder) resource).getProject(), this);
		case IResource.FILE:
			return new RubyProject(((IFile) resource).getProject(), this);
		case IResource.PROJECT:
			return new RubyProject((IProject) resource, this);
		default:
			throw new IllegalArgumentException(Messages.bind(Messages.element_invalidResourceForProject));
		}
	}

	/**
	 * @see IRubyModel
	 */
	public IRubyProject getRubyProject(String projectName) {
		return new RubyProject(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName), this);
	}

	/**
 * Helper method - returns the targeted item (IResource if internal or java.io.File if external), 
 * or null if unbound
 * Internal items must be referred to using container relative paths.
 */
public static Object getTarget(IContainer container, IPath path, boolean checkResourceExistence) {

	if (path == null) return null;
	
	// lookup - inside the container
	if (path.getDevice() == null) { // container relative paths should not contain a device 
												// (see http://dev.eclipse.org/bugs/show_bug.cgi?id=18684)
												// (case of a workspace rooted at d:\ )
		IResource resource = container.findMember(path);
		if (resource != null){
			if (!checkResourceExistence ||resource.exists()) return resource;
			return null;
		}
	}
	
	// if path is relative, it cannot be an external path
	// (see http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
	if (!path.isAbsolute()) return null; 

	// lookup - outside the container
	return getTargetAsExternalFile(path, checkResourceExistence);	
}
private synchronized static Object getTargetAsExternalFile(IPath path, boolean checkResourceExistence) {
	File externalFile = new File(path.toOSString());
	if (!checkResourceExistence) {
		return externalFile;
	} else if (existingExternalFiles.contains(externalFile)) {
		return externalFile;
	} else { 
		if (RubyModelManager.ZIP_ACCESS_VERBOSE) {
			System.out.println("(" + Thread.currentThread() + ") [RubyModel.getTarget(...)] Checking existence of " + path.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (externalFile.exists()) {
			// cache external file
			existingExternalFiles.add(externalFile);
			return externalFile;
		}
	}
	return null;
}

/**
 * Helper method - returns whether an object is a file (ie. which returns true to {@link java.io.File#isFile()}.
 */
public static boolean isFolder(Object target) {
	return getFolder(target) != null;
}

/**
 * Helper method - returns the file item (ie. which returns true to {@link java.io.File#isFile()},
 * or null if unbound
 */
public static synchronized File getFolder(Object target) {
	if (existingExternalConfirmedFolders.contains(target))
		return (File) target;
	if (target instanceof File) {
		File f = (File) target;
		if (f.isDirectory()) {
			existingExternalConfirmedFolders.add(f);
			return f;
		}
	}
	
	return null;
}

/*
 * @see RubyElement
 */
public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
	switch (token.charAt(0)) {
		case JEM_RUBYPROJECT:
			if (!memento.hasMoreTokens()) return this;
			String projectName = memento.nextToken();
			RubyElement project = (RubyElement)getRubyProject(projectName);
			return project.getHandleFromMemento(memento, owner);
	}
	return null;
}
/**
 * @see RubyElement#getHandleMemento(StringBuffer)
 */
protected void getHandleMemento(StringBuffer buff) {
	buff.append(getElementName());
}
/**
 * Returns the <code>char</code> that marks the start of this handles
 * contribution to a memento.
 */
protected char getHandleMementoDelimiter(){
	Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
	return 0;
}

/*
 * @see IRubyModel
 */
public boolean contains(IResource resource) {
	switch (resource.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
			return true;
	}
	// file or folder
	IRubyProject[] projects;
	try {
		projects = this.getRubyProjects();
	} catch (RubyModelException e) {
		return false;
	}
	for (int i = 0, length = projects.length; i < length; i++) {
		RubyProject project = (RubyProject)projects[i];
		if (!project.contains(resource)) {
			return false;
		}
	}
	return true;
}

	/**
	 * @see IRubyModel#refreshExternalArchives(IRubyElement[], IProgressMonitor)
	 */
	public void refreshExternalArchives(IRubyElement[] elementsScope, IProgressMonitor monitor) throws RubyModelException {
		if (elementsScope == null){
			elementsScope = new IRubyElement[] { this };
		}
		RubyModelManager.getRubyModelManager().getDeltaProcessor().checkExternalArchiveChanges(elementsScope, monitor);
	}
}
