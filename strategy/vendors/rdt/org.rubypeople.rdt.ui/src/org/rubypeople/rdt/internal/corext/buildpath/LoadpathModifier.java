/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 *******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/
package org.rubypeople.rdt.internal.corext.buildpath;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.packageview.LoadPathContainer;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathBasePage;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierQueries;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierQueries.IRemoveLinkedFolderQuery;

public class LoadpathModifier {

	/**
	 * Interface for listeners that want to receive a notification about 
	 * changes on <code>ILoadpathEntry</code>. For example, if a source 
	 * folder changes one of it's inclusion/exclusion filters, then 
	 * this event will be fired.
	 */
	public static interface ILoadpathModifierListener {
		/**
		 * The new build path entry that was generated upon calling a method of 
		 * <code>LoadpathModifier</code>. The type indicates which kind of 
		 * interaction was executed on this entry.
		 * 
		 * Note that the list does not contain elements of type 
		 * <code>ILoadpathEntry</code>, but <code>CPListElement</code>
		 * 
		 * @param newEntries list of <code>CPListElement</code>
		 */
		public void classpathEntryChanged(List newEntries); // XXX Rename!
	}

	private ILoadpathModifierListener fListener;

	public LoadpathModifier() {
		this(null);
	}

	protected LoadpathModifier(ILoadpathModifierListener listener) {
		fListener= listener;
	}
	
	/**
	 * Remove <code>path</code> from inclusion/exlusion filters in all <code>existingEntries</code>
	 * 
	 * @param path the path to remove
	 * @param project the Java project
	 * @param existingEntries a list of <code>CPListElement</code> representing the build path
	 * entries of the project.
	 * @return returns a <code>List</code> of <code>CPListElement</code> of modified elements, not null.
	 */
	public static List removeFilters(IPath path, IRubyProject project, List existingEntries) {
		if (path == null)
			return Collections.EMPTY_LIST;
		
		IPath projPath= project.getPath();
		if (projPath.isPrefixOf(path)) {
			path= path.removeFirstSegments(projPath.segmentCount()).addTrailingSeparator();
		}
		
		List result= new ArrayList();
		for (Iterator iter= existingEntries.iterator(); iter.hasNext();) {
			CPListElement element= (CPListElement)iter.next();
			boolean hasChange= false;
			IPath[] exlusions= (IPath[])element.getAttribute(CPListElement.EXCLUSION);
			if (exlusions != null) {
				List exlusionList= new ArrayList(exlusions.length);
				for (int i= 0; i < exlusions.length; i++) {
					if (!exlusions[i].equals(path)) {
						exlusionList.add(exlusions[i]);
					} else {
						hasChange= true;
					}
				}
				element.setAttribute(CPListElement.EXCLUSION, exlusionList.toArray(new IPath[exlusionList.size()]));
			}
			
			IPath[] inclusion= (IPath[])element.getAttribute(CPListElement.INCLUSION);
			if (inclusion != null) {
				List inclusionList= new ArrayList(inclusion.length);
				for (int i= 0; i < inclusion.length; i++) {
					if (!inclusion[i].equals(path)) {
						inclusionList.add(inclusion[i]);
					} else {
						hasChange= true;
					}
				}
				element.setAttribute(CPListElement.INCLUSION, inclusionList.toArray(new IPath[inclusionList.size()]));
			}
			if (hasChange) {
				result.add(element);
			}
		}
		return result;
	}

	/**
	 * Get the <code>ILoadpathEntry</code> from the project and 
	 * convert it into a list of <code>CPListElement</code>s.
	 * 
	 * @param project the Ruby project to get it's build path entries from
	 * @return a list of <code>CPListElement</code>s corresponding to the 
	 * build path entries of the project
	 * @throws RubyModelException
	 */
	public static List getExistingEntries(IRubyProject project) throws RubyModelException {
		ILoadpathEntry[] classpathEntries= project.getRawLoadpath();
		ArrayList newClassPath= new ArrayList();
		for (int i= 0; i < classpathEntries.length; i++) {
			ILoadpathEntry curr= classpathEntries[i];
			newClassPath.add(CPListElement.createFromExisting(curr, project));
		}
		return newClassPath;
	}

	/**
	 * Try to find the corresponding and modified <code>CPListElement</code> for the root 
	 * in the list of elements and return it.
	 * If no one can be found, the roots <code>ClasspathEntry</code> is converted to a 
	 * <code>CPListElement</code> and returned.
	 * 
	 * @param elements a list of <code>CPListElements</code>
	 * @param root the root to find the <code>ClasspathEntry</code> for represented by 
	 * a <code>CPListElement</code>
	 * @return the <code>CPListElement</code> found in the list (matching by using the path) or 
	 * the roots own <code>IClasspathEntry</code> converted to a <code>CPListElement</code>.
	 * @throws RubyModelException
	 */
	public static CPListElement getLoadpathEntry(List elements, ISourceFolderRoot root) throws RubyModelException {
		ILoadpathEntry entry= root.getRawLoadpathEntry();
		for (int i= 0; i < elements.size(); i++) {
			CPListElement element= (CPListElement) elements.get(i);
			if (element.getPath().equals(root.getPath()) && element.getEntryKind() == entry.getEntryKind())
				return (CPListElement) elements.get(i);
		}
		CPListElement newElement= CPListElement.createFromExisting(entry, root.getRubyProject());
		elements.add(newElement);
		return newElement;
	}

	/**
	 * Get the <code>ILoadpathEntry</code> for the
	 * given path by looking up all
	 * build path entries on the project
	 * 
	 * @param path the path to find a build path entry for
	 * @param project the Ruby project
	 * @return the <code>ILoadpathEntry</code> corresponding
	 * to the <code>path</code> or <code>null</code> if there
	 * is no such entry
	 * @throws RubyModelException
	 */
	public static ILoadpathEntry getLoadpathEntryFor(IPath path, IRubyProject project, int entryKind) throws RubyModelException {
		ILoadpathEntry[] entries= project.getRawLoadpath();
		for (int i= 0; i < entries.length; i++) {
			ILoadpathEntry entry= entries[i];
			if (entry.getPath().equals(path) && equalEntryKind(entry, entryKind))
				return entry;
		}
		return null;
	}

	/**
	 * Test if the provided kind is of type
	 * <code>ILoadpathEntry.CPE_SOURCE</code>
	 * 
	 * @param entry the classpath entry to be compared with the provided type
	 * @param kind the kind to be checked
	 * @return <code>true</code> if kind equals
	 * <code>ILoadpathEntry.CPE_SOURCE</code>, 
	 * <code>false</code> otherwise
	 */
	private static boolean equalEntryKind(ILoadpathEntry entry, int kind) {
		return entry.getEntryKind() == kind;
	}

	/**
	 * Check whether the input paramenter of type <code>
	 * ISourceFolderRoot</code> has either it's inclusion or
	 * exclusion filter or both set (that means they are
	 * not empty).
	 * 
	 * @param root the fragment root to be inspected
	 * @return <code>true</code> inclusion or exclusion filter set,
	 * <code>false</code> otherwise.
	 */
	public static boolean filtersSet(ISourceFolderRoot root) throws RubyModelException {
		if (root == null)
			return false;
		ILoadpathEntry entry= root.getRawLoadpathEntry();
		IPath[] inclusions= entry.getInclusionPatterns();
		IPath[] exclusions= entry.getExclusionPatterns();
		if (inclusions != null && inclusions.length > 0)
			return true;
		if (exclusions != null && exclusions.length > 0)
			return true;
		return false;
	}

	/**
	 * Find out whether the <code>IResource</code> excluded or not.
	 * 
	 * @param resource the resource to be checked
	 * @param project the Ruby project
	 * @return <code>true</code> if the resource is excluded, <code>
	 * false</code> otherwise
	 * @throws RubyModelException
	 */
	public static boolean isExcluded(IResource resource, IRubyProject project) throws RubyModelException {
		ISourceFolderRoot root= getFolderRoot(resource, project, null);
		if (root == null)
			return false;
		String fragmentName= getName(resource.getFullPath(), root.getPath());
		fragmentName= completeName(fragmentName);
		ILoadpathEntry entry= root.getRawLoadpathEntry();
		return entry != null && contains(new Path(fragmentName), entry.getExclusionPatterns(), null);
	}
	
	/**
	 * Find out whether the provided path equals to one
	 * in the array.
	 * 
	 * @param path path to find an equivalent for
	 * @param paths set of paths to compare with
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return <code>true</code> if there is an occurrence, <code>
	 * false</code> otherwise
	 */
	private static boolean contains(IPath path, IPath[] paths, IProgressMonitor monitor) {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		if (path == null)
			return false;
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_ComparePaths, paths.length); 
			if (path.getFileExtension() == null)
				path= new Path(completeName(path.toString())); 
			for (int i= 0; i < paths.length; i++) {
				if (paths[i].equals(path))
					return true;
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
		return false;
	}

	/**
	 * Add a '/' at the end of the name if
	 * it does not end with '.rb', or other Ruby-like extension.
	 * 
	 * @param name append '/' at the end if
	 * necessary
	 * @return modified string
	 */
	private static String completeName(String name) {
		if (!RubyCore.isRubyLikeFileName(name)) {
			name= name + "/"; //$NON-NLS-1$
			name= name.replace('.', '/');
			return name;
		}
		return name;
	}
	
	/**
	 * Returns a string corresponding to the <code>path</code>
	 * with the <code>rootPath<code>'s number of segments
	 * removed
	 * 
	 * @param path path to remove segments
	 * @param rootPath provides the number of segments to
	 * be removed
	 * @return a string corresponding to the mentioned
	 * action
	 */
	private static String getName(IPath path, IPath rootPath) {
		return path.removeFirstSegments(rootPath.segmentCount()).toString();
	}
	
	/**
	 * Get the source folder of a given <code>IResource</code> element,
	 * starting with the resource's parent.
	 * 
	 * @param resource the resource to get the fragment root from
	 * @param project the Ruby project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return resolved fragment root
	 * @throws RubyModelException
	 */
	public static ISourceFolderRoot getFolderRoot(IResource resource, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		IRubyElement javaElem= null;
		if (resource.getFullPath().equals(project.getPath()))
			return project.getSourceFolderRoot(resource);
		IContainer container= resource.getParent();
		do {
			if (container instanceof IFolder)
				javaElem= RubyCore.create((IFolder) container);
			if (container.getFullPath().equals(project.getPath())) {
				javaElem= project;
				break;
			}
			container= container.getParent();
			if (container == null)
				return null;
		} while (javaElem == null || !(javaElem instanceof ISourceFolderRoot));
		if (javaElem instanceof IRubyProject)
			javaElem= project.getSourceFolderRoot(project.getResource());
		return (ISourceFolderRoot) javaElem;
	}
	
	protected static String escapeSpecialChars(String value) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			switch (c) {
			case '&':
				buf.append("&amp;"); //$NON-NLS-1$
				break;
			case '<':
				buf.append("&lt;"); //$NON-NLS-1$
				break;
			case '>':
				buf.append("&gt;"); //$NON-NLS-1$
				break;
			case '\'':
				buf.append("&apos;"); //$NON-NLS-1$
				break;
			case '\"':
				buf.append("&quot;"); //$NON-NLS-1$
				break;
			case 160:
				buf.append(" "); //$NON-NLS-1$
				break;
			default:
				buf.append(c);
				break;
			}
		}
		return buf.toString();
	}
	
	/**
	 * Check whether the <code>IRubyProject</code>
	 * is a source folder 
	 * 
	 * @param project the project to test
	 * @return <code>true</code> if <code>project</code> is a source folder
	 * <code>false</code> otherwise.
	 */
	public static boolean isSourceFolder(IRubyProject project) throws RubyModelException {
		return LoadpathModifier.getLoadpathEntryFor(project.getPath(), project, ILoadpathEntry.CPE_SOURCE) != null;
	}

	/**
	 * Add a list of elements to the build path.
	 * 
	 * @param elements a list of elements to be added to the build path. An element 
	 * must either be of type <code>IFolder</code>, <code>IRubyElement</code> or 
	 * <code>IFile</code> (only allowed if the file is a .jar or .zip file!).
	 * @param project the Ruby project
	 * @param query for information about whether the project should be removed as
	 * source folder and update build folder
	 * @param monitor progress monitor, can be <code>null</code> 
	 * @return returns a list of elements of type <code>IPackageFragmentRoot</code> or 
	 * <code>IRubyProject</code> that have been added to the build path or an 
	 * empty list if the operation was aborted
	 * @throws CoreException 
	 * @throws OperationCanceledException 
	 * @see LoadpathModifierQueries.OutputFolderQuery
	 */
	protected List addToLoadpath(List elements, IRubyProject project, IProgressMonitor monitor) throws OperationCanceledException, CoreException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.LoadpathModifier_Monitor_AddToBuildpath, 2 * elements.size() + 3); 
			IWorkspaceRoot workspaceRoot= RubyPlugin.getWorkspace().getRoot();

			if (project.getProject().hasNature(RubyCore.NATURE_ID)) {
				
				IPath projPath= project.getProject().getFullPath();
				List existingEntries= getExistingEntries(project);

				List newEntries= new ArrayList();
				for (int i= 0; i < elements.size(); i++) {
					Object element= elements.get(i);
					CPListElement entry;
					if (element instanceof IResource)
						entry= addToLoadpath((IResource) element, existingEntries, newEntries, project, monitor);
					else
						entry= addToLoadpath((IRubyElement) element, existingEntries, newEntries, project, monitor);
					newEntries.add(entry);
				}
				
				Set modifiedSourceEntries= new HashSet();
				BuildPathBasePage.fixNestingConflicts((CPListElement[])newEntries.toArray(new CPListElement[newEntries.size()]), (CPListElement[])existingEntries.toArray(new CPListElement[existingEntries.size()]), modifiedSourceEntries);

				setNewEntry(existingEntries, newEntries, project, new SubProgressMonitor(monitor, 1));

				updateLoadpath(existingEntries, project, new SubProgressMonitor(monitor, 1));

				List result= new ArrayList();
				for (int i= 0; i < newEntries.size(); i++) {
					ILoadpathEntry entry= ((CPListElement) newEntries.get(i)).getLoadpathEntry();
					IRubyElement root;
					if (entry.getPath().equals(project.getPath()))
						root= project;
					else
						root= project.findSourceFolderRoot(entry.getPath());
					if (root != null) {
						result.add(root);
					}
				}

				return result;
			} else {
				StatusInfo rootStatus= new StatusInfo();
				rootStatus.setError(NewWizardMessages.LoadpathModifier_Error_NoNatures); 
				throw new CoreException(rootStatus);
			}
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Updates the build path if changes have been applied to a
	 * build path entry. For example, this can be necessary after
	 * having edited some filters on a build path entry, which can happen
	 * when including or excluding an object.
	 * 
	 * @param newEntries a list of <code>CPListElements</code> that should be used 
	 * as build path entries for the project.
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @throws JavaModelException in case that validation for the new entries fails
	 */
	private void updateLoadpath(List newEntries, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			ILoadpathEntry[] entries= convert(newEntries);
			IRubyModelStatus status= RubyConventions.validateLoadpath(project, entries, null);
			if (!status.isOK())
				throw new RubyModelException(status);

			project.setRawLoadpath(entries, null, new SubProgressMonitor(monitor, 2));
			fireEvent(newEntries);
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Convert a list of <code>CPListElement</code>s to 
	 * an array of <code>IClasspathEntry</code>.
	 * 
	 * @param list the list to be converted
	 * @return an array containing build path entries 
	 * corresponding to the list
	 */
	private static ILoadpathEntry[] convert(List list) {
		ILoadpathEntry[] entries= new ILoadpathEntry[list.size()];
		for (int i= 0; i < list.size(); i++) {
			CPListElement element= (CPListElement) list.get(i);
			entries[i]= element.getLoadpathEntry();
		}
		return entries;
	}
	
	/**
	 * Event fired whenever build pathentries changed.
	 * The event parameter corresponds to the 
	 * a <code>List</code> of <code>CPListElement</code>s
	 * 
	 * @param newEntries
	 * 
	 * @see #addToClasspath(List, IJavaProject, OutputFolderQuery, IProgressMonitor)
	 * @see #removeFromClasspath(IRemoveLinkedFolderQuery, List, IJavaProject, IProgressMonitor)
	 */
	private void fireEvent(List newEntries) {
		if (fListener != null)
			fListener.classpathEntryChanged(newEntries);
	}
	
	/**
	 * Add a resource to the build path.
	 * 
	 * @param resource the resource to be added to the build path
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code> 
	 * @return returns the new element of type <code>IPackageFragmentRoot</code> that has been added to the build path
	 * @throws CoreException 
	 * @throws OperationCanceledException 
	 */
	public static CPListElement addToLoadpath(IResource resource, List existingEntries, List newEntries, IRubyProject project, IProgressMonitor monitor) throws OperationCanceledException, CoreException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_AddToBuildpath, 2); 
			exclude(resource.getFullPath(), existingEntries, newEntries, project, new SubProgressMonitor(monitor, 1));
			CPListElement entry= new CPListElement(project, ILoadpathEntry.CPE_SOURCE, resource.getFullPath(), resource);
			return entry;
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Exclude an object at a given path.
	 * This means that the exclusion filter for the
	 * corresponding <code>IPackageFragmentRoot</code> needs to be modified.
	 * 
	 * First, the fragment root needs to be found. To do so, the new entries 
	 * are and the existing entries are traversed for a match and the entry 
	 * with the path is removed from one of those lists.
	 * 
	 * Note: the <code>IJavaElement</code>'s fragment (if there is one)
	 * is not allowed to be excluded! However, inclusion (or simply no
	 * filter) on the parent fragment is allowed.
	 * 
	 * @param path absolute path of an object to be excluded
	 * @param existingEntries a list of existing build path entries
	 * @param newEntries a list of new build path entries
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code>
	 */
	public static void exclude(IPath path, List existingEntries, List newEntries, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_Excluding, 1); 
			CPListElement elem= null;
			CPListElement existingElem= null;
			int i= 0;
			do {
				i++;
				IPath rootPath= path.removeLastSegments(i);

				if (rootPath.segmentCount() == 0)
					return;

				elem= getListElement(rootPath, newEntries);
				existingElem= getListElement(rootPath, existingEntries);
			} while (existingElem == null && elem == null);
			if (elem == null) {
				elem= existingElem;
			}
			exclude(path.removeFirstSegments(path.segmentCount() - i).toString(), null, elem, project, new SubProgressMonitor(monitor, 1)); 
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * For a given path, find the corresponding element in the list.
	 * 
	 * @param path the path to found an entry for
	 * @param elements a list of <code>CPListElement</code>s
	 * @return the mathed <code>CPListElement</code> or <code>null</code> if 
	 * no match could be found
	 */
	private static CPListElement getListElement(IPath path, List elements) {
		for (int i= 0; i < elements.size(); i++) {
			CPListElement element= (CPListElement) elements.get(i);
			if (element.getEntryKind() == ILoadpathEntry.CPE_SOURCE && element.getPath().equals(path)) {
				return element;
			}
		}
		return null;
	}
	
	/**
	 * Removes <code>path</code> out of the set of given <code>
	 * paths</code>. If the path is not contained, then the 
	 * initially provided array of paths is returned.
	 * 
	 * Only the first occurrence will be removed.
	 * 
	 * @param path path to be removed
	 * @param paths array of path to apply the removal on
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return array which does not contain <code>path</code>
	 */
	private static IPath[] remove(IPath path, IPath[] paths, IProgressMonitor monitor) {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_RemovePath, paths.length + 5); 
			if (!contains(path, paths, new SubProgressMonitor(monitor, 5)))
				return paths;

			ArrayList newPaths= new ArrayList();
			for (int i= 0; i < paths.length; i++) {
				monitor.worked(1);
				if (!paths[i].equals(path))
					newPaths.add(paths[i]);
			}
			
			return (IPath[]) newPaths.toArray(new IPath[newPaths.size()]);
		} finally {
			monitor.done();
		}

	}
	
	/**
	 * Exclude an element with a given name and absolute path
	 * from the build path.
	 * 
	 * @param name the name of the element to be excluded
	 * @param fullPath the absolute path of the element
	 * @param entry the build path entry to be modified
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return a <code>IResource</code> corresponding to the excluded element
	 * @throws JavaModelException 
	 */
	private static IResource exclude(String name, IPath fullPath, CPListElement entry, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		IResource result;
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_Excluding, 6); 
			IPath[] excludedPath= (IPath[]) entry.getAttribute(CPListElement.EXCLUSION);
			IPath[] newExcludedPath= new IPath[excludedPath.length + 1];
			name= completeName(name);
			IPath path= new Path(name);
			if (!contains(path, excludedPath, new SubProgressMonitor(monitor, 2))) {
				System.arraycopy(excludedPath, 0, newExcludedPath, 0, excludedPath.length);
				newExcludedPath[excludedPath.length]= path;
				entry.setAttribute(CPListElement.EXCLUSION, newExcludedPath);
				entry.setAttribute(CPListElement.INCLUSION, remove(path, (IPath[]) entry.getAttribute(CPListElement.INCLUSION), new SubProgressMonitor(monitor, 4)));
			}
			result= fullPath == null ? null : getResource(fullPath, project);
		} finally {
			monitor.done();
		}
		return result;
	}
	
	/**
	 * Returns for the given absolute path the corresponding
	 * resource, this is either element of type <code>IFile</code>
	 * or <code>IFolder</code>.
	 *  
	 * @param path an absolute path to a resource
	 * @param project the Ruby project
	 * @return the resource matching to the path. Can be
	 * either an <code>IFile</code> or an <code>IFolder</code>.
	 */
	private static IResource getResource(IPath path, IRubyProject project) {
		return project.getProject().getWorkspace().getRoot().findMember(path);
	}
	
	/**
	 * Add a Ruby element to the build path.
	 * 
	 * @param javaElement element to be added to the build path
	 * @param project the Ruby project
	 * @param monitor progress monitor, can be <code>null</code> 
	 * @return returns the new element of type <code>IPackageFragmentRoot</code> that has been added to the build path
	 * @throws CoreException 
	 * @throws OperationCanceledException 
	 */
	public static CPListElement addToLoadpath(IRubyElement javaElement, List existingEntries, List newEntries, IRubyProject project, IProgressMonitor monitor) throws OperationCanceledException, CoreException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_AddToBuildpath, 10); 
			CPListElement entry= new CPListElement(project, ILoadpathEntry.CPE_SOURCE, javaElement.getPath(), javaElement.getResource());
			return entry;
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Sets and validates the new entries. Note that the elments of 
	 * the list containing the new entries will be added to the list of 
	 * existing entries (therefore, there is no return list for this method).
	 * 
	 * @param existingEntries a list of existing classpath entries
	 * @param newEntries a list of entries to be added to the existing ones
	 * @param project the Java project
	 * @param monitor a progress monitor, can be <code>null</code>
	 * @throws CoreException in case that validation on one of the new entries fails
	 */
	public static void setNewEntry(List existingEntries, List newEntries, IRubyProject project, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_SetNewEntry, existingEntries.size()); 
			for (int i= 0; i < newEntries.size(); i++) {
				CPListElement entry= (CPListElement) newEntries.get(i);
				validateAndAddEntry(entry, existingEntries, project);
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Validate the new entry in the context of the existing entries. Furthermore, 
	 * check if exclusion filters need to be applied and do so if necessary.
	 * 
	 * If validation was successfull, add the new entry to the list of existing entries.
	 * 
	 * @param entry the entry to be validated and added to the list of existing entries.
	 * @param existingEntries a list of existing entries representing the build path
	 * @param project the Java project
	 * @throws CoreException in case that validation fails
	 */
	private static void validateAndAddEntry(CPListElement entry, List existingEntries, IRubyProject project) throws CoreException {
		IPath path= entry.getPath();
		IPath projPath= project.getProject().getFullPath();
		IWorkspaceRoot workspaceRoot= ResourcesPlugin.getWorkspace().getRoot();
		IStatus validate= workspaceRoot.getWorkspace().validatePath(path.toString(), IResource.FOLDER);
		StatusInfo rootStatus= new StatusInfo();
		rootStatus.setOK();
		boolean isExternal= isExternalArchiveOrLibrary(entry, project);
		if (!isExternal && validate.matches(IStatus.ERROR) && !project.getPath().equals(path)) {
			rootStatus.setError(Messages.format(NewWizardMessages.NewSourceFolderWizardPage_error_InvalidRootName, validate.getMessage())); 
			throw new CoreException(rootStatus);
		} else {
			if (!isExternal && !project.getPath().equals(path)) {
				IResource res= workspaceRoot.findMember(path);
				if (res != null) {
					if (res.getType() != IResource.FOLDER && res.getType() != IResource.FILE) {
						rootStatus.setError(NewWizardMessages.NewSourceFolderWizardPage_error_NotAFolder); 
						throw new CoreException(rootStatus);
					}
				} else {
					URI projLocation= project.getProject().getLocationURI();
					if (projLocation != null) {
						IFileStore store= EFS.getStore(projLocation).getChild(path);
						if (store.fetchInfo().exists()) {
							rootStatus.setError(NewWizardMessages.NewSourceFolderWizardPage_error_AlreadyExistingDifferentCase); 
							throw new CoreException(rootStatus);
						}
					}
				}
			}

			for (int i= 0; i < existingEntries.size(); i++) {
				CPListElement curr= (CPListElement) existingEntries.get(i);
				if (curr.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
					if (path.equals(curr.getPath()) && !project.getPath().equals(path)) {
						rootStatus.setError(NewWizardMessages.NewSourceFolderWizardPage_error_AlreadyExisting); 
						throw new CoreException(rootStatus);
					}
				}
			}

			if (!isExternal && !entry.getPath().equals(project.getPath()))
				exclude(entry.getPath(), existingEntries, new ArrayList(), project, null);

			insertAtEndOfCategory(entry, existingEntries);

			ILoadpathEntry[] entries= convert(existingEntries);

			IRubyModelStatus status= RubyConventions.validateLoadpath(project, entries, null);
			if (!status.isOK()) {
				rootStatus.setError(status.getMessage());
				throw new CoreException(rootStatus);
			}

			if (isSourceFolder(project) || project.getPath().equals(path)) {
				rootStatus.setWarning(NewWizardMessages.NewSourceFolderWizardPage_warning_ReplaceSF); 
				return;
			}

			rootStatus.setOK();
			return;
		}
	}
	
	private static boolean isExternalArchiveOrLibrary(CPListElement entry, IRubyProject project) {
		if (entry.getEntryKind() == ILoadpathEntry.CPE_LIBRARY || entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER) {
			if (entry.getResource() instanceof IFolder) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private static void insertAtEndOfCategory(CPListElement entry, List existingEntries) {
		int length= existingEntries.size();
		CPListElement[] elements= (CPListElement[])existingEntries.toArray(new CPListElement[length]);
		int i= 0;
		while (i < length && elements[i].getLoadpathEntry().getEntryKind() != entry.getLoadpathEntry().getEntryKind()) {
			i++;
		}
		if (i < length) {
			i++;
			while (i < length && elements[i].getLoadpathEntry().getEntryKind() == entry.getLoadpathEntry().getEntryKind()) {
				i++;
			}
			existingEntries.add(i, entry);
			return;
		}
		
		switch (entry.getLoadpathEntry().getEntryKind()) {
		case ILoadpathEntry.CPE_SOURCE:
			existingEntries.add(0, entry);
			break;
		case ILoadpathEntry.CPE_CONTAINER:
		case ILoadpathEntry.CPE_LIBRARY:
		case ILoadpathEntry.CPE_PROJECT:
		case ILoadpathEntry.CPE_VARIABLE:
		default:
			existingEntries.add(entry);
			break;
		}
	}
	
	/**
	 * Remove a list of elements to the build path.
	 * 
	 * @param query query to remove unused linked folders from the project
	 * @param elements a list of elements to be removed from the build path. An element 
	 * must either be of type <code>IJavaProject</code>, <code>IPackageFragmentRoot</code> or 
	 * <code>ClassPathContainer</code>
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code> 
	 * @return returns a list of elements of type <code>IFile</code> (in case of removed archives) or 
	 * <code>IFolder</code> that have been removed from the build path
	 * @throws CoreException 
	 * @throws OperationCanceledException 
	 */
	protected List removeFromLoadpath(IRemoveLinkedFolderQuery query, List elements, IRubyProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_RemoveFromBuildpath, elements.size() + 1); 
			List existingEntries= getExistingEntries(project);
			List resultElements= new ArrayList();

			boolean archiveRemoved= false;
			for (int i= 0; i < elements.size(); i++) {
				Object element= elements.get(i);
				Object res= null;
				if (element instanceof IRubyProject) {
					res= removeFromLoadpath(project, existingEntries, new SubProgressMonitor(monitor, 1));
				} else {
					if (element instanceof ISourceFolderRoot) {
						ISourceFolderRoot root= (ISourceFolderRoot) element;
							final IResource resource= root.getCorrespondingResource();
							if (resource instanceof IFolder) {
								final IFolder folder= (IFolder) resource;
								if (folder.isLinked()) {
									final int result= query.doQuery(folder);
									if (result != IRemoveLinkedFolderQuery.REMOVE_CANCEL) {
										if (result == IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH) {
											res= removeFromLoadpath(root, existingEntries, project, new SubProgressMonitor(monitor, 1));
										} else if (result == IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH_AND_FOLDER) {
											res= removeFromLoadpath(root, existingEntries, project, new SubProgressMonitor(monitor, 1));
											folder.delete(true, true, new SubProgressMonitor(monitor, 1));
										}
									}
								} else {
									res= removeFromLoadpath(root, existingEntries, project, new SubProgressMonitor(monitor, 1));
								}
							} else {
								res= removeFromLoadpath(root, existingEntries, project, new SubProgressMonitor(monitor, 1));
							}
						
					} else {
						archiveRemoved= true;
						LoadPathContainer container= (LoadPathContainer) element;
						existingEntries.remove(CPListElement.createFromExisting(container.getLoadpathEntry(), project));
					}
				}
				if (res != null) {
					resultElements.add(res);
				}
				
			}

			updateLoadpath(existingEntries, project, new SubProgressMonitor(monitor, 1));
			fireEvent(existingEntries);
			if (archiveRemoved && resultElements.size() == 0)
				resultElements.add(project);
			return resultElements;
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Remove the Ruby project from the build path
	 * 
	 * @param project the project to be removed
	 * @param existingEntries a list of existing <code>CPListElement</code>. This list 
	 * will be traversed and the entry for the project will be removed.
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return returns the Ruby project
	 * @throws CoreException
	 */
	public static IRubyProject removeFromLoadpath(IRubyProject project, List existingEntries, IProgressMonitor monitor) throws CoreException {
		CPListElement elem= getListElement(project.getPath(), existingEntries);
		if (elem != null) {
			existingEntries.remove(elem);
		}
		return project;
	}
	
	/**
	 * Remove a given <code>ISourceFolderRoot</code> from the build path.
	 * 
	 * @param root the <code>ISourceFolderRoot</code> to be removed from the build path
	 * @param existingEntries a list of <code>CPListElements</code> representing the build path 
	 * entries of the project. The entry for the root will be looked up and removed from the list.
	 * @param project the Ruby project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return returns the <code>IResource</code> that has been removed from the build path; 
	 * is of type <code>IFile</code> if the root was an archive, otherwise <code>IFolder</code> or <code>null<code> for external archives.
	 */
	public static IResource removeFromLoadpath(ISourceFolderRoot root, List existingEntries, IRubyProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_RemoveFromBuildpath, 1); 
			ILoadpathEntry entry= root.getRawLoadpathEntry();
			CPListElement elem= CPListElement.createFromExisting(entry, project);
			existingEntries.remove(elem);
			removeFilters(elem.getPath(), project, existingEntries);
			return elem.getResource();
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Exclude a list of <code>IJavaElement</code>s. This means that the exclusion filter for the
	 * corresponding <code>IPackageFragmentRoot</code>s needs to be modified.
	 * 
	 * Note: the <code>IJavaElement</code>'s fragment (if there is one)
	 * is not allowed to be excluded! However, inclusion (or simply no
	 * filter) on the parent fragment is allowed.
	 * 
	 * @param javaElements list of Java elements to be excluded
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return list of objects representing the excluded elements
	 * @throws JavaModelException
	 */
	protected List exclude(List javaElements, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_Excluding, javaElements.size() + 4); 

			List existingEntries= getExistingEntries(project);
			List resources= new ArrayList();
			for (int i= 0; i < javaElements.size(); i++) {
				IRubyElement javaElement= (IRubyElement) javaElements.get(i);
				ISourceFolderRoot root= (ISourceFolderRoot) javaElement.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
				CPListElement entry= getLoadpathEntry(existingEntries, root);

				IResource resource= exclude(javaElement, entry, project, new SubProgressMonitor(monitor, 1));
				if (resource != null) {
					resources.add(resource);
				}
			}

			updateLoadpath(existingEntries, project, new SubProgressMonitor(monitor, 4));
			return resources;
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Exclude a <code>IRubyElement</code>. This means that the exclusion filter for the
	 * corresponding <code>ISourceFolderRoot</code>s need to be modified.
	 * 
	 * Note: the <code>IRubyElement</code>'s fragment (if there is one)
	 * is not allowed to be excluded! However, inclusion (or simply no
	 * filter) on the parent fragment is allowed.
	 * 
	 * @param javaElement the Ruby element to be excluded
	 * @param entry the <code>CPListElement</code> representing the 
	 * <code>ILoadpathEntry</code> of the Ruby element's root.
	 * @param project the Ruby project
	 * @param monitor progress monitor, can be <code>null</code>
	 * 
	 * @return the resulting <code>IResource<code>
	 * @throws RubyModelException
	 */
	public static IResource exclude(IRubyElement javaElement, CPListElement entry, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			String name= getName(javaElement.getPath(), entry.getPath());
			return exclude(name, javaElement.getPath(), entry, project, new SubProgressMonitor(monitor, 1));
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Inverse operation to <code>exclude</code>.
	 * The list of elements of type <code>IResource</code> will be 
	 * removed from the exclusion filters of their parent roots.
	 * 
	 * Note: the <code>IRubyElement</code>'s fragment (if there is one)
	 * is not allowed to be excluded! However, inclusion (or simply no
	 * filter) on the parent fragment is allowed.
	 * 
	 * @param elements list of <code>IResource</code>s to be unexcluded
	 * @param project the Ruby project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return an object representing the unexcluded element 
	 * @throws RubyModelException
	 * 
	 * @see #exclude(List, IRubyProject, IProgressMonitor)
	 * @see #unExclude(List, IRubyProject, IProgressMonitor)
	 */
	protected List unExclude(List elements, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_Including, 2 * elements.size()); 

			List entries= getExistingEntries(project);
			for (int i= 0; i < elements.size(); i++) {
				IResource resource= (IResource) elements.get(i);
				ISourceFolderRoot root= getFolderRoot(resource, project, new SubProgressMonitor(monitor, 1));
				if (root != null) {
					CPListElement entry= getLoadpathEntry(entries, root);
					unExclude(resource, entry, project, new SubProgressMonitor(monitor, 1));
				}
			}

			updateLoadpath(entries, project, new SubProgressMonitor(monitor, 4));
			List resultElements= getCorrespondingElements(elements, project);
			return resultElements;
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Inverse operation to <code>exclude</code>.
	 * The resource removed from it's fragment roots exlusion filter.
	 * 
	 * Note: the <code>IRubyElement</code>'s fragment (if there is one)
	 * is not allowed to be excluded! However, inclusion (or simply no
	 * filter) on the parent fragment is allowed.
	 * 
	 * @param resource the resource to be unexcluded
	 * @param entry the <code>CPListElement</code> representing the 
	 * <code>ILoadpathEntry</code> of the resource's root.
	 * @param project the Ruby project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @throws RubyModelException
	 * 
	 * @see #exclude(List, IRubyProject, IProgressMonitor)
	 */
	public static void unExclude(IResource resource, CPListElement entry, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_RemoveExclusion, 10); 
			String name= getName(resource.getFullPath(), entry.getPath());
			IPath[] excludedPath= (IPath[]) entry.getAttribute(CPListElement.EXCLUSION);
			IPath[] newExcludedPath= remove(new Path(completeName(name)), excludedPath, new SubProgressMonitor(monitor, 3));
			entry.setAttribute(CPListElement.EXCLUSION, newExcludedPath);
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * For a given list of entries, find out what representation they 
	 * will have in the project and return a list with corresponding 
	 * elements.
	 * 
	 * @param entries a list of entries to find an appropriate representation 
	 * for. The list can contain elements of two types: 
	 * <li><code>IResource</code></li>
	 * <li><code>IRubyElement</code></li>
	 * @param project the Ruby project
	 * @return a list of elements corresponding to the passed entries.
	 */
	public static List getCorrespondingElements(List entries, IRubyProject project) {
		List result= new ArrayList();
		for (int i= 0; i < entries.size(); i++) {
			Object element= entries.get(i);
			IPath path;
			if (element instanceof IResource)
				path= ((IResource) element).getFullPath();
			else
				path= ((IRubyElement) element).getPath();
			IResource resource= getResource(path, project);
			if (resource != null) {
				IRubyElement elem= RubyCore.create(resource);
				if (elem != null && project.isOnLoadpath(elem))
					result.add(elem);
				else
					result.add(resource);
			}

		}
		return result;
	}

	public static void commitLoadPath(List newEntries, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			ILoadpathEntry[] entries= convert(newEntries);

			IRubyModelStatus status= RubyConventions.validateLoadpath(project, entries, null);
			if (!status.isOK())
				throw new RubyModelException(status);

			project.setRawLoadpath(entries, null, new SubProgressMonitor(monitor, 2));
		} finally {
			monitor.done();
		}
	}

	/**
	 * For a given <code>IResource</code>, try to
	 * convert it into a <code>ISourceFolder</code>
	 * if possible or return <code>null</code> if no
	 * fragment root could be created.
	 * 
	 * @param resource the resource to be converted
	 * @return the <code>resource<code> as
	 * <code>ISourceFolder</code>,or <code>null</code>
	 * if failed to convert
	 */
	public static ISourceFolder getFolder(IResource resource) {
		IRubyElement elem= RubyCore.create(resource);
		if (elem instanceof ISourceFolder)
			return (ISourceFolder) elem;
		return null;
	}
	
	/**
	 * Find out whether one of the <code>IResource</code>'s parents
	 * is excluded.
	 * 
	 * @param resource check the resources parents whether they are
	 * excluded or not
	 * @param project the Ruby project
	 * @return <code>true</code> if there is an excluded parent, 
	 * <code>false</code> otherwise
	 * @throws RubyModelException
	 */
	public static boolean parentExcluded(IResource resource, IRubyProject project) throws RubyModelException {
		if (resource.getFullPath().equals(project.getPath()))
			return false;
		ISourceFolderRoot root= getFolderRoot(resource, project, null);
		if (root == null) {
			return true;
		}
		IPath path= resource.getFullPath().removeFirstSegments(root.getPath().segmentCount());
		ILoadpathEntry entry= root.getRawLoadpathEntry();
		if (entry == null)
			return true; // there is no build path entry, this is equal to the fact that the parent is excluded
		while (path.segmentCount() > 0) {
			if (contains(path, entry.getExclusionPatterns(), null))
				return true;
			path= path.removeLastSegments(1);
		}
		return false;
	}
	
	/**
	 * Determines whether the current selection (of type
	 * <code>ICompilationUnit</code> or <code>IPackageFragment</code>)
	 * is on the inclusion filter of it's parent source folder.
	 * 
	 * @param selection the current Java element
	 * @param project the Java project
	 * @param monitor progress monitor, can be <code>null</code>
	 * @return <code>true</code> if the current selection is included,
	 * <code>false</code> otherwise.
	 * @throws RubyModelException 
	 */
	public static boolean isIncluded(IRubyElement selection, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.ClasspathModifier_Monitor_ContainsPath, 4); 
			ISourceFolderRoot root= (ISourceFolderRoot) selection.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
			ILoadpathEntry entry= root.getRawLoadpathEntry();
			if (entry == null)
				return false;
			return contains(selection.getPath().removeFirstSegments(root.getPath().segmentCount()), entry.getInclusionPatterns(), new SubProgressMonitor(monitor, 2));
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Check whether the <code>ISourceFolder</code>
	 * corresponds to the project's default fragment.
	 * 
	 * @param fragment the package fragment to be checked
	 * @return <code>true</code> if is the default package fragment,
	 * <code>false</code> otherwise.
	 */
	public static boolean isDefaultFolder(ISourceFolder fragment) {
		return fragment.getElementName().length() == 0;
	}

}
