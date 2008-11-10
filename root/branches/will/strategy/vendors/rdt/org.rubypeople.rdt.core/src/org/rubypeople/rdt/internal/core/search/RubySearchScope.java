package org.rubypeople.rdt.internal.core.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.core.LoadpathEntry;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.SourceFolder;
import org.rubypeople.rdt.internal.core.util.Util;

public class RubySearchScope implements IRubySearchScope {
	
	private ArrayList elements;
	
	/* The paths of the resources in this search scope 
	    (or the classpath entries' paths if the resources are projects) 
	*/
	private ArrayList projectPaths = new ArrayList(); // container paths projects 
	private int[] projectIndexes; // Indexes of projects in list
	private String[] containerPaths; // path to the container (e.g. /P/src, /P/lib.jar, c:\temp\mylib.jar)
	private String[] relativePaths; // path relative to the container (e.g. x/y/Z.class, x/y, (empty))
	private boolean[] isPkgPath; // in the case of packages, matches must be direct children of the folder
	private int pathsCount;
	private int threshold;
	
	private IPath[] enclosingProjectsAndJars;
	
	public RubySearchScope() {
		this(5);
	}

	private RubySearchScope(int size) {
		initialize(size);
	}

	protected void initialize(int size) {
		this.pathsCount = 0;
		this.threshold = size; // size represents the expected number of elements
		int extraRoom = (int) (size * 1.75f);
		if (this.threshold == extraRoom)
			extraRoom++;
		this.relativePaths = new String[extraRoom];
		this.containerPaths = new String[extraRoom];
		this.projectPaths = new ArrayList();
		this.projectIndexes = new int[extraRoom];
		this.isPkgPath = new boolean[extraRoom];

		this.enclosingProjectsAndJars = new IPath[0];
	}
	
	/* 
	 * E.g.
	 * 
	 * 1. /P/src/pkg/X.java
	 * 2. /P/src/pkg
	 * 3. /P/lib.jar|org/eclipse/jdt/core/IJavaElement.class
	 * 4. /home/mylib.jar|x/y/z/X.class
	 * 5. c:\temp\mylib.jar|x/y/Y.class
	 * 
	 * @see IJavaSearchScope#encloses(String)
	 */
	public boolean encloses(String resourcePathString) {
		// resource in workspace (case 1 or 2)
		int index1 = indexOf(resourcePathString);
		if (index1 >= 0) return true;
		
		// resource in external file
		for (int i = 0; i < this.containerPaths.length; i++) {
			String containerPath = this.containerPaths[i];
			if (containerPath == null) continue;
			if (resourcePathString.startsWith(containerPath)) return true;
		}
		return false;		
	}
	
	/**
	 * Returns paths list index of given path or -1 if not found.
	 * NOTE: Use indexOf(String, String) for path inside jars
	 * 
	 * @param fullPath the full path of the resource, e.g.
	 *   1. /P/src/pkg/X.java
	 *   2. /P/src/pkg
	 */
	private int indexOf(String fullPath) {
		// cannot guess the index of the container path
		// fallback to sequentially looking at all known paths
		for (int i = 0, length = this.relativePaths.length; i < length; i++) {
			String currentRelativePath = this.relativePaths[i];
			if (currentRelativePath == null) continue;
			String currentContainerPath = this.containerPaths[i];
			String currentFullPath = currentRelativePath.length() == 0 ? currentContainerPath : (currentContainerPath + '/' + currentRelativePath);
			if (encloses(currentFullPath, fullPath, i))
				return i;
		}
		return -1;
	}
	
	/*
	 * Returns whether the enclosing path encloses the given path (or is equal to it)
	 */
	private boolean encloses(String enclosingPath, String path, int index) {
		// normalize given path as it can come from outside
		path = normalize(path);
		
		int pathLength = path.length();
		int enclosingLength = enclosingPath.length();
		if (pathLength < enclosingLength) {
			return false;
		}
		if (enclosingLength == 0) {
			return true;
		}
		if (pathLength == enclosingLength) {
			return path.equals(enclosingPath);
		}
		if (!this.isPkgPath[index]) {
			return path.startsWith(enclosingPath)
				&& path.charAt(enclosingLength) == '/';
		} else {
			// if looking at a package, this scope encloses the given path 
			// if the given path is a direct child of the folder
			// or if the given path path is the folder path (see bug 13919 Declaration for package not found if scope is not project)
			if (path.startsWith(enclosingPath) 
				&& ((enclosingPath.length() == path.lastIndexOf('/'))
					|| (enclosingPath.length() == path.length()))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds the given path to this search scope. Remember if subfolders need to be included
	 * and associated access restriction as well.
	 */
	private void add(String projectPath, String relativePath, String containerPath, boolean isPackage) {
		// normalize containerPath and relativePath
		containerPath = normalize(containerPath);
		relativePath = normalize(relativePath);
		int length = this.containerPaths.length,
			index = (containerPath.hashCode()& 0x7FFFFFFF) % length;
		String currentRelativePath, currentContainerPath;
		while ((currentRelativePath = this.relativePaths[index]) != null && (currentContainerPath = this.containerPaths[index]) != null) {
			if (currentRelativePath.equals(relativePath) && currentContainerPath.equals(containerPath))
				return;
			if (++index == length) {
				index = 0;
			}
		}
		int idx = this.projectPaths.indexOf(projectPath);
		if (idx == -1) {
			// store project in separated list to minimize memory footprint
			this.projectPaths.add(projectPath);
			idx = this.projectPaths.indexOf(projectPath);
		}
		this.projectIndexes[index] = idx;
		this.relativePaths[index] = relativePath;
		this.containerPaths[index] = containerPath;
		this.isPkgPath[index] = isPackage;
		// assumes the threshold is never equal to the size of the table
		if (++this.pathsCount > this.threshold)
			rehash();
	}
	
	/**
	 * Add a path to current java search scope or all project fragment roots if null.
	 * Use project resolved classpath to retrieve and store access restriction on each classpath entry.
	 * Recurse if dependent projects are found.
	 * @param rubyProject Project used to get resolved classpath entries
	 * @param pathToAdd Path to add in case of single element or null if user want to add all project package fragment roots
	 * @param includeMask Mask to apply on classpath entries
	 * @param visitedProjects Set to avoid infinite recursion
	 * @param referringEntry Project raw entry in referring project classpath
	 * @throws RubyModelException May happen while getting java model info 
	 */
	void add(RubyProject rubyProject, IPath pathToAdd, int includeMask, HashSet visitedProjects, ILoadpathEntry referringEntry) throws RubyModelException {
		IProject project = rubyProject.getProject();
		if (!project.isAccessible() || !visitedProjects.add(project)) return;

		IPath projectPath = project.getFullPath();
		String projectPathString = projectPath.toString();
		this.addEnclosingProjectOrJar(projectPath);

		ILoadpathEntry[] entries = rubyProject.getResolvedLoadpath(true);
		IRubyModel model = rubyProject.getRubyModel();
		RubyModelManager.PerProjectInfo perProjectInfo = rubyProject.getPerProjectInfo();
		for (int i = 0, length = entries.length; i < length; i++) {
			ILoadpathEntry entry = entries[i];
			LoadpathEntry cpEntry = (LoadpathEntry) entry;
			if (referringEntry != null) {
				// Add only exported entries.
				// Source folder are implicitly exported.
				if (!entry.isExported() && entry.getEntryKind() != ILoadpathEntry.CPE_SOURCE) continue;
				cpEntry = cpEntry.combineWith((LoadpathEntry)referringEntry);
//					cpEntry = ((LoadpathEntry)referringEntry).combineWith(cpEntry);
			}
			switch (entry.getEntryKind()) {
				case ILoadpathEntry.CPE_LIBRARY:
					ILoadpathEntry rawEntry = null;
					Map resolvedPathToRawEntries = perProjectInfo.resolvedPathToRawEntries;
					if (resolvedPathToRawEntries != null) {
						rawEntry = (ILoadpathEntry) resolvedPathToRawEntries.get(entry.getPath());
					}
					if (rawEntry == null) break;
					switch (rawEntry.getEntryKind()) {
						case ILoadpathEntry.CPE_LIBRARY:
						case ILoadpathEntry.CPE_VARIABLE:
							if ((includeMask & APPLICATION_LIBRARIES) != 0) {
								IPath path = entry.getPath();
								if (pathToAdd == null || pathToAdd.equals(path)) {
									String pathToString = path.getDevice() == null ? path.toString() : path.toOSString();
									add(projectPath.toString(), "", pathToString, false/*not a package*/); //$NON-NLS-1$
									addEnclosingProjectOrJar(path);
								}
							}
							break;
						case ILoadpathEntry.CPE_CONTAINER:
							ILoadpathContainer container = RubyCore.getLoadpathContainer(rawEntry.getPath(), rubyProject);
							if (container == null) break;
							if ((container.getKind() == ILoadpathContainer.K_APPLICATION && (includeMask & APPLICATION_LIBRARIES) != 0)
									|| (includeMask & SYSTEM_LIBRARIES) != 0) {
								IPath path = entry.getPath();
								if (pathToAdd == null || pathToAdd.equals(path)) {
									String pathToString = path.getDevice() == null ? path.toString() : path.toOSString();
									add(projectPath.toString(), "", pathToString, false/*not a package*/); //$NON-NLS-1$
									addEnclosingProjectOrJar(path);
								}
							}
							break;
					}
					break;
				case ILoadpathEntry.CPE_PROJECT:
					if ((includeMask & REFERENCED_PROJECTS) != 0) {
						IPath path = entry.getPath();
						if (pathToAdd == null || pathToAdd.equals(path)) {
							add((RubyProject) model.getRubyProject(entry.getPath().lastSegment()), null, includeMask, visitedProjects, cpEntry);
						}
					}
					break;
				case ILoadpathEntry.CPE_SOURCE:
					if ((includeMask & SOURCES) != 0) {
						IPath path = entry.getPath();
						if (pathToAdd == null || pathToAdd.equals(path)) {
							add(projectPath.toString(), Util.relativePath(path,1/*remove project segment*/), projectPathString, false/*not a package*/);
						}
					}
					break;
			}
		}
	}
	
	private void addEnclosingProjectOrJar(IPath path) {
		int length = this.enclosingProjectsAndJars.length;
		for (int i = 0; i < length; i++) {
			if (this.enclosingProjectsAndJars[i].equals(path)) return;
		}
		System.arraycopy(
			this.enclosingProjectsAndJars,
			0,
			this.enclosingProjectsAndJars = new IPath[length+1],
			0,
			length);
		this.enclosingProjectsAndJars[length] = path;
	}

	/*
	 * Removes trailing slashes from the given path
	 */
	private String normalize(String path) {
		int pathLength = path.length();
		int index = pathLength-1;
		while (index >= 0 && path.charAt(index) == '/')
			index--;
		if (index != pathLength-1)
			return path.substring(0, index + 1);
		return path;
	}
	
	private void rehash() {
		RubySearchScope newScope = new RubySearchScope(this.pathsCount * 2);		// double the number of expected elements
		newScope.projectPaths.ensureCapacity(this.projectPaths.size());
		String currentPath;
		for (int i = this.relativePaths.length; --i >= 0;)
			if ((currentPath = this.relativePaths[i]) != null) {
				int idx = this.projectIndexes[i];
				String projectPath = idx == -1 ? null : (String)this.projectPaths.get(idx);
				newScope.add(projectPath, currentPath, this.containerPaths[i], this.isPkgPath[i]);
			}

		this.relativePaths = newScope.relativePaths;
		this.containerPaths = newScope.containerPaths;
		this.projectPaths = newScope.projectPaths;
		this.projectIndexes = newScope.projectIndexes;
		this.isPkgPath = newScope.isPkgPath;
		this.threshold = newScope.threshold;
	}
	
	/* (non-Javadoc)
	 * @see IJavaSearchScope#enclosingProjectsAndJars()
	 */
	public IPath[] enclosingProjectsAndJars() {
		return this.enclosingProjectsAndJars;
	}
	
	/**
	 * Add ruby project all fragment roots to current ruby search scope.
	 * @see #add(RubyProject, IPath, int, HashSet, ILoadpathEntry)
	 */
	public void add(RubyProject project, int includeMask, HashSet visitedProject) throws RubyModelException {
		add(project, null, includeMask, visitedProject, null);
	}
	
	/**
	 * Add an element to the ruby search scope.
	 * @param element The element we want to add to current ruby search scope
	 * @throws RubyModelException May happen if some Ruby Model info are not available
	 */
	public void add(IRubyElement element) throws RubyModelException {
		IPath containerPath = null;
		String containerPathToString = null;
		int includeMask = SOURCES | APPLICATION_LIBRARIES | SYSTEM_LIBRARIES;
		switch (element.getElementType()) {
			case IRubyElement.RUBY_MODEL:
				// a workspace sope should be used
				break; 
			case IRubyElement.RUBY_PROJECT:
				add((RubyProject)element, null, includeMask, new HashSet(2), null);
				break;
			case IRubyElement.SOURCE_FOLDER_ROOT:
				ISourceFolderRoot root = (ISourceFolderRoot)element;
				containerPath = root.getPath();
				containerPathToString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
				IResource rootResource = root.getResource();
				if (rootResource != null && rootResource.isAccessible()) {
					String relativePath = Util.relativePath(rootResource.getFullPath(), containerPath.segmentCount());
					add(relativePath, containerPathToString, false/*not a package*/);
				} else {
					add("", containerPathToString, false/*not a package*/); //$NON-NLS-1$
				}
				break;
			case IRubyElement.SOURCE_FOLDER:
				root = (ISourceFolderRoot)element.getParent();
				if (root.isExternal()) {
					String relativePath = Util.concatWith(((SourceFolder) element).names, '/');
					containerPath = root.getPath();
					containerPathToString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
					add(relativePath, containerPathToString, true/*package*/);
				} else {
					IResource resource = element.getResource();
					if (resource != null) {
						if (resource.isAccessible()) {
							containerPath = root.getParent().getPath();
						} else {
							// for working copies, get resource container full path
							containerPath = resource.getParent().getFullPath();
						}
						containerPathToString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
						String relativePath = Util.relativePath(resource.getFullPath(), containerPath.segmentCount());
						add(relativePath, containerPathToString, true/*package*/);
					}
				}
				break;
			default:
				// remember sub-cu (or sub-class file) ruby elements
				if (element instanceof IMember) {
					if (this.elements == null) {
						this.elements = new ArrayList();
					}
					this.elements.add(element);
				}
				root = (ISourceFolderRoot) element.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
				String relativePath;
				
					containerPath = root.getPath();
					relativePath = Util.relativePath(getPath(element, true/*full path*/), 0/*remove project segment*/);
				
				containerPathToString = containerPath.getDevice() == null ? containerPath.toString() : containerPath.toOSString();
				add(relativePath, containerPathToString, false/*not a package*/);
		}
		
		if (containerPath != null)
			addEnclosingProjectOrJar(containerPath);
	}

	/**
	 * Adds the given path to this search scope. Remember if subfolders need to be included
	 * and associated access restriction as well.
	 */
	private void add(String relativePath, String containerPath, boolean isPackage) {
		// normalize containerPath and relativePath
		containerPath = normalize(containerPath);
		relativePath = normalize(relativePath);
		int length = this.containerPaths.length,
			index = (containerPath.hashCode()& 0x7FFFFFFF) % length;
		String currentRelativePath, currentContainerPath;
		while ((currentRelativePath = this.relativePaths[index]) != null && (currentContainerPath = this.containerPaths[index]) != null) {
			if (currentRelativePath.equals(relativePath) && currentContainerPath.equals(containerPath))
				return;
			if (++index == length) {
				index = 0;
			}
		}
		this.relativePaths[index] = relativePath;
		this.containerPaths[index] = containerPath;
		this.isPkgPath[index] = isPackage;

		// assumes the threshold is never equal to the size of the table
		if (++this.pathsCount > this.threshold)
			rehash();
	}
	
	private IPath getPath(IRubyElement element, boolean relativeToRoot) {
		switch (element.getElementType()) {
			case IRubyElement.RUBY_MODEL:
				return Path.EMPTY;
			case IRubyElement.RUBY_PROJECT:
				return element.getPath();
			case IRubyElement.SOURCE_FOLDER_ROOT:
				if (relativeToRoot)
					return Path.EMPTY;
				return element.getPath();
			case IRubyElement.SOURCE_FOLDER:
				String relativePath = Util.concatWith(((SourceFolder) element).names, '/');
				return getPath(element.getParent(), relativeToRoot).append(new Path(relativePath));
			case IRubyElement.SCRIPT:
				return getPath(element.getParent(), relativeToRoot).append(new Path(element.getElementName()));
			default:
				return getPath(element.getParent(), relativeToRoot);
		}
	}
	
	/* (non-Javadoc)
	 * @see IRubySearchScope#encloses(IRubyElement)
	 */
	public boolean encloses(IRubyElement element) {
		if (this.elements != null) {
			for (int i = 0, length = this.elements.size(); i < length; i++) {
				IRubyElement scopeElement = (IRubyElement)this.elements.get(i);
				IRubyElement searchedElement = element;
				while (searchedElement != null) {
					if (searchedElement.equals(scopeElement))
						return true;
					searchedElement = searchedElement.getParent();
				}
			}
			return false;
		}
		ISourceFolderRoot root = (ISourceFolderRoot) element.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
		if (root != null && root.isExternal()) {
			// external
			IPath rootPath = root.getPath();
			String rootPathToString = rootPath.getDevice() == null ? rootPath.toString() : rootPath.toOSString();
			IPath relativePath = getPath(element, true/*relative path*/);
			return indexOf(rootPathToString, relativePath.toString()) >= 0;
		}
		// resource in workspace
		String fullResourcePathString = getPath(element, false/*full path*/).toString();
		return indexOf(fullResourcePathString) >= 0;
	}
	
	/**
	 * Returns paths list index of given path or -1 if not found.
	 * @param containerPath the path of the container, e.g.
	 *   1. /P/src
	 *   2. /P
	 *   3. /P/lib.jar
	 *   4. /home/mylib.jar
	 *   5. c:\temp\mylib.jar
	 * @param relativePath the forward slash path relatively to the container, e.g.
	 *   1. x/y/Z.class
	 *   2. x/y
	 *   3. X.java
	 *   4. (empty)
	 */
	private int indexOf(String containerPath, String relativePath) {
		// use the hash to get faster comparison
		int length = this.containerPaths.length,
			index = (containerPath.hashCode()& 0x7FFFFFFF) % length;
		String currentContainerPath;
		while ((currentContainerPath = this.containerPaths[index]) != null) {
			if (currentContainerPath.equals(containerPath)) {
				String currentRelativePath = this.relativePaths[index];
				if (encloses(currentRelativePath, relativePath, index))
					return index;
			}
			if (++index == length) {
				index = 0;
			}
		}
		return -1;
	}

}
