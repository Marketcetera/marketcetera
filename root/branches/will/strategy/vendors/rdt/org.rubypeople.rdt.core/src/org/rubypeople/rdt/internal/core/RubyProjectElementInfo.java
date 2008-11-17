/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.HashtableOfArrayToObject;

/**
 * Info for IRubyProject.
 * <p>
 * Note: <code>getChildren()</code> returns all of the
 * <code>IPackageFragmentRoots</code> specified on the classpath for the
 * project. This can include roots external to the project. See
 * <code>RubyProject#getAllPackageFragmentRoots()</code> and
 * <code>RubyProject#getPackageFragmentRoots()</code>. To get only the
 * <code>IPackageFragmentRoots</code> that are internal to the project, use
 * <code>RubyProject#getChildren()</code>.
 */

/* package */
class RubyProjectElementInfo extends OpenableElementInfo {

	/**
	 * A array with all the non-ruby resources contained by this PackageFragment
	 */
	private Object[] nonRubyResources;

	public ProjectCache projectCache;

	static class ProjectCache {
		ProjectCache(ISourceFolderRoot[] allPkgFragmentRootsCache, HashtableOfArrayToObject allPkgFragmentsCache, HashtableOfArrayToObject isPackageCache, Map rootToResolvedEntries) {
			this.allPkgFragmentRootsCache = allPkgFragmentRootsCache;
			this.allPkgFragmentsCache = allPkgFragmentsCache;
			this.isPackageCache = isPackageCache;
			this.rootToResolvedEntries = rootToResolvedEntries;
		}
		
		/*
		 * A cache of all package fragment roots of this project.
		 */
		public ISourceFolderRoot[] allPkgFragmentRootsCache;
		
		/*
		 * A cache of all package fragments in this project.
		 * (a map from String[] (the package name) to IPackageFragmentRoot[] (the package fragment roots that contain a package fragment with this name)
		 */
		public HashtableOfArrayToObject allPkgFragmentsCache;
		
		/*
		 * A set of package names (String[]) that are known to be packages.
		 */
		public HashtableOfArrayToObject isPackageCache;
	
		public Map rootToResolvedEntries;		
	}
	
	/**
	 * Create and initialize a new instance of the receiver
	 */
	public RubyProjectElementInfo() {
		this.nonRubyResources = null;
	}

	/**
	 * Compute the non-java resources contained in this java project.
	 */
	private Object[] computeNonRubyResources(RubyProject project) {
		// determine if src == project and/or if bin == project
		IPath projectPath = project.getProject().getFullPath();
		boolean srcIsProject = false;
		char[][] inclusionPatterns = null;
		char[][] exclusionPatterns = null;
		ILoadpathEntry[] classpath = null;
		try {
			classpath = project.getResolvedLoadpath(true/* ignoreUnresolvedEntry */, false/* don't generateMarkerOnError */, false/* don't returnResolutionInProgress */);
			for (int i = 0; i < classpath.length; i++) {
				ILoadpathEntry entry = classpath[i];
				if (projectPath.equals(entry.getPath())) {
					srcIsProject = true;
					inclusionPatterns = ((LoadpathEntry) entry).fullInclusionPatternChars();
					exclusionPatterns = ((LoadpathEntry) entry).fullExclusionPatternChars();
					break;
				}
			}
		} catch (RubyModelException e) {
			// ignore
		}

		Object[] resources = new IResource[5];
		int resourcesCounter = 0;
		try {
			IResource[] members = ((IContainer) project.getResource()).members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource res = members[i];
				switch (res.getType()) {
				case IResource.FILE:
					IPath resFullPath = res.getFullPath();
					String resName = res.getName();

					// ignore .java file if src == project
					if (srcIsProject && !org.rubypeople.rdt.internal.core.util.Util.isExcluded(res, inclusionPatterns, exclusionPatterns)) {
						break;
					}
					// else add non java resource
					if (resources.length == resourcesCounter) {
						// resize
						System.arraycopy(resources, 0, (resources = new IResource[resourcesCounter * 2]), 0, resourcesCounter);
					}
					resources[resourcesCounter++] = res;
					break;
				case IResource.FOLDER:
					resFullPath = res.getFullPath();

					// ignore non-excluded folders on the classpath or that
					// correspond to an output location
					if ((srcIsProject && !org.rubypeople.rdt.internal.core.util.Util.isExcluded(res, inclusionPatterns, exclusionPatterns)) || this.isLoadpathEntryOrOutputLocation(resFullPath, classpath)) {
						break;
					}
					// else add non java resource
					if (resources.length == resourcesCounter) {
						// resize
						System.arraycopy(resources, 0, (resources = new IResource[resourcesCounter * 2]), 0, resourcesCounter);
					}
					resources[resourcesCounter++] = res;
				}
			}
			if (resources.length != resourcesCounter) {
				System.arraycopy(resources, 0, (resources = new IResource[resourcesCounter]), 0, resourcesCounter);
			}
		} catch (CoreException e) {
			resources = NO_NON_RUBY_RESOURCES;
			resourcesCounter = 0;
		}
		return resources;
	}

	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	Object[] getNonRubyResources(RubyProject project) {

		if (this.nonRubyResources == null) {
			this.nonRubyResources = computeNonRubyResources(project);
		}
		return this.nonRubyResources;
	}

	/*
	 * Returns whether the given path is a classpath entry
	 */
	private boolean isLoadpathEntryOrOutputLocation(IPath path, ILoadpathEntry[] resolvedLoadpath) {
		for (int i = 0, length = resolvedLoadpath.length; i < length; i++) {
			ILoadpathEntry entry = resolvedLoadpath[i];
			if (entry.getPath().equals(path)) { return true; }
		}
		return false;
	}

	/*
	 * Reset the package fragment roots and package fragment caches
	 */
	void resetCaches() {
		this.projectCache = null;
//		JavaModelManager.getJavaModelManager().resetJarTypeCache();
	}

	/**
	 * Set the fNonRubyResources to res value
	 */
	void setNonRubyResources(Object[] resources) {

		this.nonRubyResources = resources;
	}
	
	ProjectCache getProjectCache(RubyProject project) {
		ProjectCache cache = this.projectCache;
		if (cache == null) {
			ISourceFolderRoot[] roots;
			Map reverseMap = new HashMap(3);
			try {
				roots = project.getAllSourceFolderRoots(reverseMap);
			} catch (RubyModelException e) {
				// project does not exist: cannot happen since this is the info of the project
				roots = new ISourceFolderRoot[0];
				reverseMap.clear();
			}
			HashtableOfArrayToObject fragmentsCache = new HashtableOfArrayToObject();
			HashtableOfArrayToObject isPackageCache = new HashtableOfArrayToObject();
			for (int i = 0, length = roots.length; i < length; i++) {
				ISourceFolderRoot root = roots[i];
				IRubyElement[] frags = null;
				try {
//					if (root.isArchive() && !root.isOpen()) {
//						JarPackageFragmentRootInfo info = new JarPackageFragmentRootInfo();
//						((JarPackageFragmentRoot) root).computeChildren(info, new HashMap());
//						frags = info.children;
//					} else 
						frags = root.getChildren();
				} catch (RubyModelException e) {
					// root doesn't exist: ignore
					continue;
				}
				for (int j = 0, length2 = frags.length; j < length2; j++) {
					SourceFolder fragment= (SourceFolder) frags[j];
					String[] pkgName = fragment.names;
					Object existing = fragmentsCache.get(pkgName);
					if (existing == null) {
						fragmentsCache.put(pkgName, root);
						// cache whether each package and its including packages (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=119161)
						// are actual packages
						addNames(pkgName, isPackageCache);
					} else {
						if (existing instanceof SourceFolderRoot) {
							fragmentsCache.put(pkgName, new ISourceFolderRoot[] {(SourceFolderRoot) existing, root});
						} else {
							ISourceFolderRoot[] entry= (ISourceFolderRoot[]) existing;
							ISourceFolderRoot[] copy= new ISourceFolderRoot[entry.length + 1];
							System.arraycopy(entry, 0, copy, 0, entry.length);
							copy[entry.length]= root;
							fragmentsCache.put(pkgName, copy);
						}
					}
				}
			}
			cache = new ProjectCache(roots, fragmentsCache, isPackageCache, reverseMap);
			this.projectCache = cache;
		}
		return cache;
	}

	/*
	 * Adds the given name and its super names to the given set
	 * (e.g. for {"a", "b", "c"}, adds {"a", "b", "c"}, {"a", "b"}, and {"a"})
	 */
	public static void addNames(String[] name, HashtableOfArrayToObject set) {
		set.put(name, name);
		int length = name.length;
		for (int i = length-1; i > 0; i--) {
			String[] superName = new String[i];
			System.arraycopy(name, 0, superName, 0, i);
			set.put(superName, superName);
		}
	}
}
