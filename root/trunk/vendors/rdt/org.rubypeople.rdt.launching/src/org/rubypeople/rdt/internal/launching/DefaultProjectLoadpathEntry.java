/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.launching;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IRuntimeContainerComparator;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Default user classpath entries for a Ruby project
 */
public class DefaultProjectLoadpathEntry extends AbstractRuntimeLoadpathEntry {
	
	public static final String TYPE_ID = "org.eclipse.jdt.launching.classpathentry.defaultLoadpath"; //$NON-NLS-1$
	
	/**
	 * Whether only exported entries should be on the runtime classpath.
	 * By default all entries are on the runtime classpath.
	 */
	private boolean fExportedEntriesOnly = false;
	
	/**
	 * Default constructor need to instantiate extensions
	 */
	public DefaultProjectLoadpathEntry() {
	}
	
	/**
	 * Constructs a new classpath entry for the given project.
	 * 
	 * @param project Ruby project
	 */
	public DefaultProjectLoadpathEntry(IRubyProject project) {
		setRubyProject(project);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.launching.AbstractRuntimeLoadpathEntry#buildMemento(org.w3c.dom.Document, org.w3c.dom.Element)
	 */
	protected void buildMemento(Document document, Element memento) throws CoreException {
		memento.setAttribute("project", getRubyProject().getElementName()); //$NON-NLS-1$
		memento.setAttribute("exportedEntriesOnly", Boolean.toString(fExportedEntriesOnly)); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#initializeFrom(org.w3c.dom.Element)
	 */
	public void initializeFrom(Element memento) throws CoreException {
		String name = memento.getAttribute("project"); //$NON-NLS-1$
		if (name == null) {
			abort(LaunchingMessages.DefaultProjectLoadpathEntry_3, null); 
		}		
		IRubyProject project = RubyCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(name));
		setRubyProject(project);
		name = memento.getAttribute("exportedEntriesOnly"); //$NON-NLS-1$
		if (name == null) {
			fExportedEntriesOnly = false;
		} else {
			fExportedEntriesOnly = Boolean.valueOf(name).booleanValue();
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#getTypeId()
	 */
	public String getTypeId() {
		return TYPE_ID;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getType()
	 */
	public int getType() {
		return OTHER;
	}
	
	protected IProject getProject() {
		return getRubyProject().getProject();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getLocation()
	 */
	public String getLocation() {
		return getProject().getLocation().toOSString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getPath()
	 */
	public IPath getPath() {
		return getProject().getFullPath();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getResource()
	 */
	public IResource getResource() {
		return getProject();
	}
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.launching.IRuntimeLoadpathEntry2#getRuntimeLoadpathEntries(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public IRuntimeLoadpathEntry[] getRuntimeLoadpathEntries(ILaunchConfiguration configuration) throws CoreException {
		ILoadpathEntry entry = RubyCore.newProjectEntry(getRubyProject().getProject().getFullPath());
		List classpathEntries = new ArrayList(5);
		List<ILoadpathEntry> expanding = new ArrayList<ILoadpathEntry>(5);
		expandProject(entry, classpathEntries, expanding);
		IRuntimeLoadpathEntry[] runtimeEntries = new IRuntimeLoadpathEntry[classpathEntries.size()];
		for (int i = 0; i < runtimeEntries.length; i++) {
			Object e = classpathEntries.get(i);
			if (e instanceof ILoadpathEntry) {
				ILoadpathEntry cpe = (ILoadpathEntry)e;
				runtimeEntries[i] = new RuntimeLoadpathEntry(cpe);
			} else {
				runtimeEntries[i] = (IRuntimeLoadpathEntry)e;				
			}
		}
		// remove bootpath entries - this is a default user loadpath
		List<IRuntimeLoadpathEntry> ordered = new ArrayList<IRuntimeLoadpathEntry>(runtimeEntries.length);
		for (int i = 0; i < runtimeEntries.length; i++) {
			if (runtimeEntries[i].getLoadpathProperty() == IRuntimeLoadpathEntry.USER_CLASSES) {
				ordered.add(runtimeEntries[i]);
			} 
		}
		return ordered.toArray(new IRuntimeLoadpathEntry[ordered.size()]);		
	}
	
	/**
	 * Returns the transitive closure of classpath entries for the
	 * given project entry.
	 * 
	 * @param projectEntry project classpath entry
	 * @param expandedPath a list of entries already expanded, should be empty
	 * to begin, and contains the result
	 * @param expanding a list of projects that have been or are currently being
	 * expanded (to detect cycles)
	 * @exception CoreException if unable to expand the classpath
	 */
	private void expandProject(ILoadpathEntry projectEntry, List expandedPath, List<ILoadpathEntry> expanding) throws CoreException {
		expanding.add(projectEntry);
		// 1. Get the raw classpath
		// 2. Replace source folder entries with a project entry
		IPath projectPath = projectEntry.getPath();
		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(projectPath.lastSegment());
		if (res == null) {
			// add project entry and return
			expandedPath.add(projectEntry);
			return;
		}
		IRubyProject project = (IRubyProject)RubyCore.create(res);
		if (project == null || !project.getProject().isOpen() || !project.exists()) {
			// add project entry and return
			expandedPath.add(projectEntry);
			return;
		}
		
		ILoadpathEntry[] buildPath = project.getRawLoadpath();
		List unexpandedPath = new ArrayList(buildPath.length);
		boolean projectAdded = false;
		for (int i = 0; i < buildPath.length; i++) {
			ILoadpathEntry classpathEntry = buildPath[i];
			if (classpathEntry.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
				if (!projectAdded) {
					projectAdded = true;
					unexpandedPath.add(projectEntry);
				}
			} else {
				// add exported entires, as configured
				if (classpathEntry.isExported()) {
					unexpandedPath.add(classpathEntry);
				} else if (!isExportedEntriesOnly() || project.equals(getRubyProject())) {
					// add non exported entries from root project or if we are including all entries
					unexpandedPath.add(classpathEntry);
				}
			}
		}
		// 3. expand each project entry (except for the root project)
		// 4. replace each container entry with a runtime entry associated with the project
		Iterator iter = unexpandedPath.iterator();
		while (iter.hasNext()) {
			ILoadpathEntry entry = (ILoadpathEntry)iter.next();
			if (entry == projectEntry) {
				expandedPath.add(entry);
			} else {
				switch (entry.getEntryKind()) {
					case ILoadpathEntry.CPE_PROJECT:
						if (!expanding.contains(entry)) {
							expandProject(entry, expandedPath, expanding);
						}
						break;
					case ILoadpathEntry.CPE_CONTAINER:
						ILoadpathContainer container = RubyCore.getLoadpathContainer(entry.getPath(), project);
						int property = -1;
						if (container != null) {
							switch (container.getKind()) {
								case ILoadpathContainer.K_APPLICATION:
									property = IRuntimeLoadpathEntry.USER_CLASSES;
									break;
								case ILoadpathContainer.K_DEFAULT_SYSTEM:
									property = IRuntimeLoadpathEntry.STANDARD_CLASSES;
									break;	
								case ILoadpathContainer.K_SYSTEM:
									property = IRuntimeLoadpathEntry.BOOTSTRAP_CLASSES;
									break;
							}
							IRuntimeLoadpathEntry r = RubyRuntime.newRuntimeContainerLoadpathEntry(entry.getPath(), property, project);
							// check for duplicate/redundant entries 
							boolean duplicate = false;
							LoadpathContainerInitializer initializer = RubyCore.getLoadpathContainerInitializer(r.getPath().segment(0));
							for (int i = 0; i < expandedPath.size(); i++) {
								Object o = expandedPath.get(i);
								if (o instanceof IRuntimeLoadpathEntry) {
									IRuntimeLoadpathEntry re = (IRuntimeLoadpathEntry)o;
									if (re.getType() == IRuntimeLoadpathEntry.CONTAINER) {
										if (container instanceof IRuntimeContainerComparator) {
											duplicate = ((IRuntimeContainerComparator)container).isDuplicate(re.getPath());
										} else {
											LoadpathContainerInitializer initializer2 = RubyCore.getLoadpathContainerInitializer(re.getPath().segment(0));
											Object id1 = null;
											Object id2 = null;
											if (initializer == null) {
												id1 = r.getPath().segment(0);
											} else {
												id1 = initializer.getComparisonID(r.getPath(), project);
											}
											if (initializer2 == null) {
												id2 = re.getPath().segment(0);
											} else {
												IRubyProject context = re.getRubyProject();
												if (context == null) {
													context = project;
												}
												id2 = initializer2.getComparisonID(re.getPath(), context);
											}
											if (id1 == null) {
												duplicate = id2 == null;
											} else {
												duplicate = id1.equals(id2);
											}
										}
										if (duplicate) {
											break;
										}
									}
								}
							}
							if (!duplicate) {
								expandedPath.add(r);
							}	
						}
						break;
					case ILoadpathEntry.CPE_VARIABLE:
						if (entry.getPath().segment(0).equals(RubyRuntime.RUBYLIB_VARIABLE)) {
							IRuntimeLoadpathEntry r = RubyRuntime.newVariableRuntimeLoadpathEntry(entry.getPath());
							r.setLoadpathProperty(IRuntimeLoadpathEntry.STANDARD_CLASSES);
							if (!expandedPath.contains(r)) {
								expandedPath.add(r);
							}
							break;
						}
						// fall through if not the special RUBYLIB variable
					default:
						if (!expandedPath.contains(entry)) {
							expandedPath.add(entry);
						}
						break;
				}
			}
		}
		return;
	}	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#isComposite()
	 */
	public boolean isComposite() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2#getName()
	 */
	public String getName() {
		if (isExportedEntriesOnly()) {
			return MessageFormat.format(LaunchingMessages.DefaultProjectLoadpathEntry_2, getRubyProject().getElementName());
		}
		return MessageFormat.format(LaunchingMessages.DefaultProjectLoadpathEntry_4, getRubyProject().getElementName()); 
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof DefaultProjectLoadpathEntry) {
			DefaultProjectLoadpathEntry entry = (DefaultProjectLoadpathEntry) obj;
			return entry.getRubyProject().equals(getRubyProject()) &&
				entry.isExportedEntriesOnly() == isExportedEntriesOnly();
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getRubyProject().hashCode();
	}
	
	/**
	 * Sets whether the runtime classpath computaion should only
	 * include exported entries in referenced projects.
	 * 
	 * @param exportedOnly
	 * @since 3.2
	 */
	public void setExportedEntriesOnly(boolean exportedOnly) {
		fExportedEntriesOnly = exportedOnly;
	}
	
	/**
	 * Returns whether the classpath computation only includes exported
	 * entries in referenced projects.
	 * 
	 * @return
	 * @since 3.2
	 */
	public boolean isExportedEntriesOnly() {
		return fExportedEntriesOnly;
	}
}
