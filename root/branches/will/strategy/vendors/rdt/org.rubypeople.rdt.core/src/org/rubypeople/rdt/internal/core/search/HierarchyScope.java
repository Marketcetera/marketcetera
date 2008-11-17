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
package org.rubypeople.rdt.internal.core.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.hierarchy.TypeHierarchy;

/**
 * Scope limited to the subtype and supertype hierarchy of a given type.
 */
public class HierarchyScope implements IRubySearchScope {

	public IType focusType;
	private String focusPath;
	private WorkingCopyOwner owner;
	
	private ITypeHierarchy hierarchy;
	private IType[] types;
	private HashSet resourcePaths;
	private IPath[] enclosingProjectsAndJars;

	protected IResource[] elements;
	protected int elementCount;
	
	public boolean needsRefresh;

	/* (non-Javadoc)
	 * Adds the given resource to this search scope.
	 */
	public void add(IResource element) {
		if (this.elementCount == this.elements.length) {
			System.arraycopy(
				this.elements,
				0,
				this.elements = new IResource[this.elementCount * 2],
				0,
				this.elementCount);
		}
		elements[elementCount++] = element;
	}
	
	/* (non-Javadoc)
	 * Creates a new hiearchy scope for the given type.
	 */
	public HierarchyScope(IType type, WorkingCopyOwner owner) throws RubyModelException {
		this.focusType = type;
		this.owner = owner;
		
		this.enclosingProjectsAndJars = this.computeProjectsAndJars(type);

		// resource path
		ISourceFolderRoot root = (ISourceFolderRoot)type.getSourceFolder().getParent();
		this.focusPath = type.getPath().toString();
		
		this.needsRefresh = true;
			
		//disabled for now as this could be expensive
		//RubyModelManager.getRubyModelManager().rememberScope(this);
	}
	private void buildResourceVector() {
		HashMap resources = new HashMap();
		HashMap paths = new HashMap();
		this.types = this.hierarchy.getAllTypes();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0; i < this.types.length; i++) {
			IType type = this.types[i];
			IResource resource = type.getResource();
			if (resource != null && resources.get(resource) == null) {
				resources.put(resource, resource);
				add(resource);
			}
			ISourceFolderRoot root =
				(ISourceFolderRoot) type.getSourceFolder().getParent();

				// type is a project
				paths.put(type.getRubyProject().getProject().getFullPath(), type);
			
		}
		this.enclosingProjectsAndJars = new IPath[paths.size()];
		int i = 0;
		for (Iterator iter = paths.keySet().iterator(); iter.hasNext();) {
			this.enclosingProjectsAndJars[i++] = (IPath) iter.next();
		}
	}
	/*
	 * Computes the paths of projects and jars that the hierarchy on the given type could contain.
	 * This is a super set of the project and jar paths once the hierarchy is computed.
	 */
	private IPath[] computeProjectsAndJars(IType type) throws RubyModelException {
		HashSet set = new HashSet();
		ISourceFolderRoot root = (ISourceFolderRoot)type.getSourceFolder().getParent();
		if (root.isArchive()) {
			// add the root
			set.add(root.getPath());
			// add all projects that reference this archive and their dependents
			IPath rootPath = root.getPath();
			IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			IRubyProject[] projects = model.getRubyProjects();
			HashSet visited = new HashSet();
			for (int i = 0; i < projects.length; i++) {
				RubyProject project = (RubyProject) projects[i];
				ILoadpathEntry[] classpath = project.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
				for (int j = 0; j < classpath.length; j++) {
					if (rootPath.equals(classpath[j].getPath())) {
						// add the project and its binary pkg fragment roots
						ISourceFolderRoot[] roots = project.getAllSourceFolderRoots();
						set.add(project.getPath());
						// add the dependent projects
						this.computeDependents(project, set, visited);
						break;
					}
				}
			}
		} else {
			// add all the project's pkg fragment roots
			IRubyProject project = (IRubyProject)root.getParent();
			ISourceFolderRoot[] roots = project.getAllSourceFolderRoots();
			for (int i = 0; i < roots.length; i++) {
				ISourceFolderRoot pkgFragmentRoot = roots[i];
				set.add(pkgFragmentRoot.getParent().getPath());
			}
			// add the dependent projects
			this.computeDependents(project, set, new HashSet());
		}
		IPath[] result = new IPath[set.size()];
		set.toArray(result);
		return result;
	}
	private void computeDependents(IRubyProject project, HashSet set, HashSet visited) {
		if (visited.contains(project)) return;
		visited.add(project);
		IProject[] dependents = project.getProject().getReferencingProjects();
		for (int i = 0; i < dependents.length; i++) {
			try {
				IRubyProject dependent = RubyCore.create(dependents[i]);
				ISourceFolderRoot[] roots = dependent.getSourceFolderRoots();
				set.add(dependent.getPath());
				for (int j = 0; j < roots.length; j++) {
					ISourceFolderRoot pkgFragmentRoot = roots[j];
					if (pkgFragmentRoot.isArchive()) {
						set.add(pkgFragmentRoot.getPath());
					}
				}
				this.computeDependents(dependent, set, visited);
			} catch (RubyModelException e) {
				// project is not a java project
			}
		}
	}
	/* (non-Javadoc)
	 * @see IRubySearchScope#encloses(String)
	 */
	public boolean encloses(String resourcePath) {
		if (this.hierarchy == null) {
			if (resourcePath.equals(this.focusPath)) {
				return true;
			} else {
				if (this.needsRefresh) {
					try {
						this.initialize();
					} catch (RubyModelException e) {
						return false;
					}
				} else {
					// the scope is used only to find enclosing projects and jars
					// clients is responsible for filtering out elements not in the hierarchy (see SearchEngine)
					return true;
				}
			}
		}
		if (this.needsRefresh) {
			try {
				this.refresh();
			} catch(RubyModelException e) {
				return false;
			}
		}

		for (int i = 0; i < this.elementCount; i++) {
			if (resourcePath.startsWith(this.elements[i].getFullPath().toString())) {
				return true;
			}
		}		
		return false;
	}
	/* (non-Javadoc)
	 * @see IRubySearchScope#encloses(IRubyElement)
	 */
	public boolean encloses(IRubyElement element) {
		if (this.hierarchy == null) {
			if (this.focusType.equals(element.getAncestor(IRubyElement.TYPE))) {
				return true;
			} else {
				if (this.needsRefresh) {
					try {
						this.initialize();
					} catch (RubyModelException e) {
						return false;
					}
				} else {
					// the scope is used only to find enclosing projects and jars
					// clients is responsible for filtering out elements not in the hierarchy (see SearchEngine)
					return true;
				}					
			}
		}
		if (this.needsRefresh) {
			try {
				this.refresh();
			} catch(RubyModelException e) {
				return false;
			}
		}
		IType type = null;
		if (element instanceof IType) {
			type = (IType) element;
		} else if (element instanceof IMember) {
			type = ((IMember) element).getDeclaringType();
		}
		if (type != null) {
			if (this.hierarchy.contains(type)) {
				return true;
			} else {
				// be flexible: look at original element (see bug 14106 Declarations in Hierarchy does not find declarations in hierarchy)
				IType original;
				if ((original = (IType)type.getPrimaryElement()) != null) {
					return this.hierarchy.contains(original);
				}
			}
		} 
		return false;
	}
	/* (non-Javadoc)
	 * @see IRubySearchScope#enclosingProjectsAndJars()
	 * @deprecated
	 */
	public IPath[] enclosingProjectsAndJars() {
		if (this.needsRefresh) {
			try {
				this.refresh();
			} catch(RubyModelException e) {
				return new IPath[0];
			}
		}
		return this.enclosingProjectsAndJars;
	}
	protected void initialize() throws RubyModelException {
		this.resourcePaths = new HashSet();
		this.elements = new IResource[5];
		this.elementCount = 0;
		this.needsRefresh = false;
		if (this.hierarchy == null) {
			this.hierarchy = this.focusType.newTypeHierarchy(this.owner, null);
		} else {
			this.hierarchy.refresh(null);
		}
		this.buildResourceVector();
	}
	/*
	 * @see AbstractSearchScope#processDelta(IRubyElementDelta)
	 */
	public void processDelta(IRubyElementDelta delta) {
		if (this.needsRefresh) return;
		this.needsRefresh = this.hierarchy == null ? false : ((TypeHierarchy)this.hierarchy).isAffected(delta);
	}
	protected void refresh() throws RubyModelException {
		if (this.hierarchy != null) {
			this.initialize();
		}
	}
	public String toString() {
		return "HierarchyScope on " + ((RubyElement)this.focusType).toStringWithAncestors(); //$NON-NLS-1$
	}

}
