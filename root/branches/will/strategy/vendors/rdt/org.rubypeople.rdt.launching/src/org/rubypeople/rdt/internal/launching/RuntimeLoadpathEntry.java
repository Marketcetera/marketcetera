/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     BEA - Daniel R Somerfield - Bug 88939
 *******************************************************************************/
package org.rubypeople.rdt.internal.launching;


import java.io.IOException;
import java.text.MessageFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An entry on the runtime loadpath that the user can manipulate
 * and share in a launch configuration.
 * 
 * @see org.rubypeople.rdt.launching.IRuntimeLoadpathEntry
 * @since 0.9.0
 */
public class RuntimeLoadpathEntry implements IRuntimeLoadpathEntry {

	/**
	 * This entry's type - must be set on creation.
	 */
	private int fType = -1;
	
	/**
	 * This entry's loadpath property.
	 */
	private int fLoadpathProperty = -1;
	
	/**
	 * This entry's associated build path entry.
	 */
	private ILoadpathEntry fLoadpathEntry = null;
	
	/**
	 * The entry's resolved entry (lazily initialized)
	 */
	private ILoadpathEntry fResolvedEntry = null;
	
	/**
	 * Associated Ruby project, or <code>null</code>
	 */
	private IRubyProject fRubyProject = null;
	
	/**
	 * The path if the entry was invalid and fLoadpathEntry is null
	 */
	private IPath fInvalidPath;
	
	/**
	 * Constructs a new runtime classpath entry based on the
	 * (build) classpath entry.
	 * 
	 * @param entry the associated classpath entry
	 */
	public RuntimeLoadpathEntry(ILoadpathEntry entry) {
		switch (entry.getEntryKind()) {
			case ILoadpathEntry.CPE_PROJECT:
				setType(PROJECT);
				break;
			case ILoadpathEntry.CPE_LIBRARY:
				setType(ARCHIVE);
				break;
			case ILoadpathEntry.CPE_VARIABLE:
				setType(VARIABLE);
				break;
			default:
				throw new IllegalArgumentException(MessageFormat.format(LaunchingMessages.RuntimeLoadpathEntry_Illegal_classpath_entry__0__1, entry.toString())); 
		}
		setLoadpathEntry(entry);
		initializeLoadpathProperty();
	}
	
	/**
	 * Constructs a new container entry in the context of the given project
	 * 
	 * @param entry classpath entry
	 * @param classpathProperty this entry's classpath property
	 */
	public RuntimeLoadpathEntry(ILoadpathEntry entry, int classpathProperty) {
		switch (entry.getEntryKind()) {
			case ILoadpathEntry.CPE_CONTAINER:
				setType(CONTAINER);
				break;
			default:
				throw new IllegalArgumentException(MessageFormat.format(LaunchingMessages.RuntimeLoadpathEntry_Illegal_classpath_entry__0__1,entry.toString())); 
		}
		setLoadpathEntry(entry);
		setLoadpathProperty(classpathProperty);
	}	

	/**
	 * Reconstructs a runtime classpath entry from the given
	 * XML document root not.
	 * 
	 * @param root a memento root doc element created by this class
	 * @exception CoreException if unable to restore from the given memento
	 */
	public RuntimeLoadpathEntry(Element root) throws CoreException {									
		try {
			setType(Integer.parseInt(root.getAttribute("type"))); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			abort(LaunchingMessages.RuntimeLoadpathEntry_Unable_to_recover_runtime_class_path_entry_type_2, e); 
		}
		try {
			setLoadpathProperty(Integer.parseInt(root.getAttribute("path"))); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			abort(LaunchingMessages.RuntimeLoadpathEntry_Unable_to_recover_runtime_class_path_entry_location_3, e); 
		}			

		// source attachment
		IPath sourcePath = null;
		IPath rootPath = null;
		String path = root.getAttribute("sourceAttachmentPath"); //$NON-NLS-1$
		if (path != null && path.length() > 0) {
			sourcePath = new Path(path);
		}
		path = root.getAttribute("sourceRootPath"); //$NON-NLS-1$
		if (path != null && path.length() > 0) {
			rootPath = new Path(path);
		}			

		switch (getType()) {
			case PROJECT :
				String name = root.getAttribute("projectName"); //$NON-NLS-1$
				if (isEmpty(name)) {
					abort(LaunchingMessages.RuntimeLoadpathEntry_Unable_to_recover_runtime_class_path_entry___missing_project_name_4, null); 
				} else {
					IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
					setLoadpathEntry(RubyCore.newProjectEntry(proj.getFullPath()));
				}
				break;
			case ARCHIVE :
				path = root.getAttribute("externalArchive"); //$NON-NLS-1$
				if (isEmpty(path)) {
					// internal
					path = root.getAttribute("internalArchive"); //$NON-NLS-1$
					if (isEmpty(path)) {
						abort(LaunchingMessages.RuntimeLoadpathEntry_Unable_to_recover_runtime_class_path_entry___missing_archive_path_5, null); 
					} else {
						setLoadpathEntry(createLibraryEntry(sourcePath, rootPath, path));
					}
				} else {
					// external
					setLoadpathEntry(createLibraryEntry(sourcePath, rootPath, path));
				}
				break;
			case VARIABLE :
				String var = root.getAttribute("containerPath"); //$NON-NLS-1$
				if (isEmpty(var)) {
					abort(LaunchingMessages.RuntimeLoadpathEntry_Unable_to_recover_runtime_class_path_entry___missing_variable_name_6, null); 
				} else {
					setLoadpathEntry(RubyCore.newVariableEntry(new Path(var)));
				}
				break;
			case CONTAINER :
				var = root.getAttribute("containerPath"); //$NON-NLS-1$
				if (isEmpty(var)) {
					abort(LaunchingMessages.RuntimeLoadpathEntry_Unable_to_recover_runtime_class_path_entry___missing_variable_name_6, null); 
				} else {
					setLoadpathEntry(RubyCore.newContainerEntry(new Path(var)));
				}
				break;
		}	
		
		String name = root.getAttribute("rubyProject"); //$NON-NLS-1$
		if (isEmpty(name)) {
			fRubyProject = null;
		} else {
			IProject project2 = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
			fRubyProject = RubyCore.create(project2);
		}
	}

	private ILoadpathEntry createLibraryEntry(IPath sourcePath, IPath rootPath, String path) {
		Path p = new Path(path);
		if (!p.isAbsolute())
		{
			fInvalidPath = p;
			return null;
			//abort("There was a problem with path \" " + path + "\": paths must be absolute.", null);			
		}
		return RubyCore.newLibraryEntry(p);
	}
	
	/**
	 * Throws an internal error exception
	 */
	protected void abort(String message, Throwable e)	throws CoreException {
		IStatus s = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, message, e);
		throw new CoreException(s);		
	}

	/**
	 * @see IRuntimeLoadpathEntry#getType()
	 */
	public int getType() {
		return fType;
	}

	/**
	 * Sets this entry's type
	 * 
	 * @param type this entry's type
	 */
	private void setType(int type) {
		fType = type;
	}

	/**
	 * Sets the classpath entry associated with this runtime classpath entry.
	 * Clears the cache of the resolved entry.
	 *
	 * @param entry the classpath entry associated with this runtime classpath entry
	 */
	private void setLoadpathEntry(ILoadpathEntry entry) {
		fLoadpathEntry = entry;
		fResolvedEntry = null;
	}

	/**
	 * @see IRuntimeLoadpathEntry#getLoadpathEntry()
	 */
	public ILoadpathEntry getLoadpathEntry() {
		return fLoadpathEntry;
	}

	/**
	 * @see IRuntimeLoadpathEntry#getMemento()
	 */
	public String getMemento() throws CoreException {
	
		Document doc;
		try {
			doc = LaunchingPlugin.getDocument();
		} catch (ParserConfigurationException e) {
			IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, LaunchingMessages.RuntimeLoadpathEntry_An_exception_occurred_generating_runtime_classpath_memento_8, e); 
			throw new CoreException(status);
		}
		Element node = doc.createElement("runtimeLoadpathEntry"); //$NON-NLS-1$
		doc.appendChild(node);
		node.setAttribute("type", (new Integer(getType())).toString()); //$NON-NLS-1$
		node.setAttribute("path", (new Integer(getLoadpathProperty())).toString()); //$NON-NLS-1$
		switch (getType()) {
			case PROJECT :
				node.setAttribute("projectName", getPath().lastSegment()); //$NON-NLS-1$
				break;
			case ARCHIVE :
				IResource res = getResource();
				if (res == null) {
					node.setAttribute("externalArchive", getPath().toString()); //$NON-NLS-1$
				} else {
					node.setAttribute("internalArchive", res.getFullPath().toString()); //$NON-NLS-1$
				}
				break;
			case VARIABLE :
			case CONTAINER :
				node.setAttribute("containerPath", getPath().toString()); //$NON-NLS-1$
				break;
		}		
		if (getRubyProject() != null) {
			node.setAttribute("rubyProject", getRubyProject().getElementName()); //$NON-NLS-1$
		}
		try {
			return LaunchingPlugin.serializeDocument(doc);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, LaunchingMessages.RuntimeLoadpathEntry_An_exception_occurred_generating_runtime_classpath_memento_8, e); 
			throw new CoreException(status);
		} catch (TransformerException e) {
			IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, LaunchingMessages.RuntimeLoadpathEntry_An_exception_occurred_generating_runtime_classpath_memento_8, e); 
			throw new CoreException(status);
		}
	}

	/**
	 * @see IRuntimeLoadpathEntry#getPath()
	 */
	public IPath getPath() {
		ILoadpathEntry entry = getLoadpathEntry();
		return entry != null ? entry.getPath() : fInvalidPath;
	}

	/**
	 * @see IRuntimeLoadpathEntry#getResource()
	 */
	public IResource getResource() {
		switch (getType()) {
			case CONTAINER:
			case VARIABLE:
				return null;
			default:
				return getResource(getPath());
		}
	}
	
	/**
	 * Returns the resource in the workspace assciated with the given
	 * absolute path, or <code>null</code> if none. The path may have
	 * a device.
	 * 
	 * @param path absolute path, or <code>null</code>
	 * @return resource or <code>null</code>
	 */
	protected IResource getResource(IPath path) {
		if (path != null) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			if (path.getDevice() == null) {
				// search relative to the workspace if no device present
				return root.findMember(path);
			} 
			// look for files or folders with the given path
			IFile[] files = root.findFilesForLocation(path);
			if (files.length > 0) {
				return files[0];
			}
			IContainer[] containers = root.findContainersForLocation(path);
			if (containers.length > 0) {
				return containers[0];
			}
		}		
		return null;
	}

	/**
	 * Initlaizes the classpath property based on this entry's type.
	 */
	private void initializeLoadpathProperty() {
		switch (getType()) {
			case VARIABLE:
				if (getVariableName().equals(RubyRuntime.RUBYLIB_VARIABLE) || getVariableName().equals("GEM_LIB")) {
					setLoadpathProperty(STANDARD_CLASSES);
				} else {
					setLoadpathProperty(USER_CLASSES);
				}
				break;
			case PROJECT:
				setLoadpathProperty(USER_CLASSES);
				break;
			case ARCHIVE:
				if (isGem()) { // FIXME This is a huge hack. We should integrate the idea of gems into our loadpath infrastructure much more!
					setLoadpathProperty(STANDARD_CLASSES);
				} else {
					setLoadpathProperty(USER_CLASSES);
				}
				break;
			default:
				break;
		}
	}
	
	
	private boolean isGem() {
		String[] segments = fLoadpathEntry.getPath().segments();
		if (segments == null) return false;
		for (int i = 0; i < segments.length; i++) {
			if (segments[i].equals("gems")) return true;
		}
		return false;
	}

	/**
	 * @see IRuntimeLoadpathEntry#setLoadpathProperty(int)
	 */
	public void setLoadpathProperty(int location) {
		fLoadpathProperty = location;
	}

	/**
	 * @see IRuntimeLoadpathEntry#setLoadpathProperty(int)
	 */
	public int getLoadpathProperty() {
		return fLoadpathProperty;
	}

	/**
	 * @see IRuntimeLoadpathEntry#getLocation()
	 */
	public String getLocation() {

		IPath path = null;
		switch (getType()) {
			case PROJECT :
				IRubyProject pro = (IRubyProject) RubyCore.create(getResource());
				if (pro != null) {
					path = pro.getPath();
				}
				break;
			case ARCHIVE :
				path = getPath();
				break;
			case VARIABLE :
				ILoadpathEntry resolved = getResolvedLoadpathEntry();
				if (resolved != null) {
					path = resolved.getPath();
				}
				break;
			case CONTAINER :
				break;
		}
		return resolveToOSPath(path);
	}
	
	/**
	 * Returns the OS path for the given aboslute or workspace relative path
	 */
	protected String resolveToOSPath(IPath path) {
		if (path != null) {
			IResource res = null;
			if (path.getDevice() == null) {
				// if there is no device specified, find the resource
				res = getResource(path);
			}
			if (res == null) {
				return path.toOSString();
			} 
			IPath location = res.getLocation();
			if (location != null) {
				return location.toOSString();
			}
		}
		return null;		
	}

	/**
	 * @see IRuntimeLoadpathEntry#getVariableName()
	 */
	public String getVariableName() {
		if (getType() == IRuntimeLoadpathEntry.VARIABLE || getType() == IRuntimeLoadpathEntry.CONTAINER) {
			return getPath().segment(0);
		}
		return null;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof IRuntimeLoadpathEntry) {
			IRuntimeLoadpathEntry r = (IRuntimeLoadpathEntry)obj;
			if (getType() == r.getType() && getLoadpathProperty() == r.getLoadpathProperty()) {
				if (getType() == IRuntimeLoadpathEntry.CONTAINER) {
					String id = getPath().segment(0);
					LoadpathContainerInitializer initializer = RubyCore.getLoadpathContainerInitializer(id);
					IRubyProject javaProject1 = getRubyProject();
					IRubyProject javaProject2 = r.getRubyProject();
					if (initializer == null || javaProject1 == null || javaProject2 == null) {
						// containers are equal if their ID is equal by default
						return getPath().equals(r.getPath());
					}
					Object comparisonID1 = initializer.getComparisonID(getPath(), javaProject1);
					Object comparisonID2 = initializer.getComparisonID(r.getPath(), javaProject2);
					return comparisonID1.equals(comparisonID2);
				} else  {
					return getPath() != null && getPath().equals(r.getPath());
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether the given objects are equal, accounting for null
	 */
	protected boolean equal(Object one, Object two) {
		if (one == null) {
			return two == null;
		}
		return one.equals(two);
	}
	
	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		if (getType() == CONTAINER) {
			return getPath().segment(0).hashCode() + getType();
		}
		return getPath().hashCode() + getType();
	}

	/**
	 * Creates a new underlying classpath entry for this runtime classpath entry
	 * with the given paths, due to a change in source attachment.
	 */
	protected void updateLoadpathEntry(IPath path, IPath sourcePath, IPath rootPath) {
		ILoadpathEntry entry = null;
		ILoadpathEntry original = getLoadpathEntry();
		switch (getType()) {
			case ARCHIVE:
				entry = RubyCore.newLibraryEntry(path, original.isExported());
				break;
			case VARIABLE:
				entry = RubyCore.newVariableEntry(path);
				break;
			default:
				return;
		}		
		setLoadpathEntry(entry);		
	}
	
	/**
	 * Returns the resolved classpath entry associated with this runtime
	 * entry, resolving if required.
	 */
	protected ILoadpathEntry getResolvedLoadpathEntry() {
		if (fResolvedEntry == null) {
			fResolvedEntry = RubyCore.getResolvedLoadpathEntry(getLoadpathEntry());
		}
		return fResolvedEntry;
	}
		
	protected boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}
	
	public String toString() {
		if (fLoadpathEntry != null) {
			return fLoadpathEntry.toString();
		}
		return super.toString();
		
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getRubyProject()
	 */
	public IRubyProject getRubyProject() {
		return fRubyProject;
	}
	
	/**
	 * Sets the Ruby project associated with this classpath entry.
	 * 
	 * @param project Ruby project
	 */
	public void setRubyProject(IRubyProject project) {
		fRubyProject = project;
	}
}
