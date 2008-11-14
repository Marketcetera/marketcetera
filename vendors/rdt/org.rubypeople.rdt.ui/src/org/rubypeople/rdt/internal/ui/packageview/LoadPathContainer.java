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
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.packageview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

/**
 * Representation of class path containers in Ruby UI.
 */
public class LoadPathContainer implements IAdaptable, IWorkbenchAdapter {
	private IRubyProject fProject;
	private ILoadpathEntry fClassPathEntry;
	private ILoadpathContainer fContainer;

	public static class RequiredProjectWrapper implements IAdaptable, IWorkbenchAdapter {

		private final IRubyElement fProject;
		private static ImageDescriptor DESC_OBJ_PROJECT;	
		{
			ISharedImages images= RubyPlugin.getDefault().getWorkbench().getSharedImages(); 
			DESC_OBJ_PROJECT= images.getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
		}

		public RequiredProjectWrapper(IRubyElement project) {
			this.fProject= project;
		}
		
		public IRubyElement getProject() {
			return fProject; 
		}
		
		public Object getAdapter(Class adapter) {
			if (adapter == IWorkbenchAdapter.class) 
				return this;
			return null;
		}

		public Object[] getChildren(Object o) {
			return null;
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return DESC_OBJ_PROJECT;
		}

		public String getLabel(Object o) {
			return fProject.getElementName();
		}

		public Object getParent(Object o) {
			return null;
		}
	}

	public LoadPathContainer(IRubyProject parent, ILoadpathEntry entry) {
		fProject= parent;
		fClassPathEntry= entry;
		try {
			fContainer= RubyCore.getLoadpathContainer(entry.getPath(), parent);
		} catch (RubyModelException e) {
			fContainer= null;
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof LoadPathContainer) {
			LoadPathContainer other = (LoadPathContainer)obj;
			if (fProject.equals(other.fProject) &&
				fClassPathEntry.equals(other.fClassPathEntry)) {
				return true;	
			}
			
		}
		return false;
	}

	public int hashCode() {
		return fProject.hashCode()*17+fClassPathEntry.hashCode();
	}

	public Object[] getSourceFolderRoots() {
		return fProject.findSourceFolderRoots(fClassPathEntry);
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IWorkbenchAdapter.class) 
			return this;
		if ((adapter == IResource.class) && (fContainer instanceof IAdaptable))
			return ((IAdaptable)fContainer).getAdapter(IResource.class);
		return null;
	}

	public Object[] getChildren(Object o) {
		return concatenate(getSourceFolderRoots(), getRequiredProjects());
	}

	private Object[] getRequiredProjects() {
		List list= new ArrayList();
		if (fContainer != null) {
			ILoadpathEntry[] classpathEntries= fContainer.getLoadpathEntries();
			IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
			for (int i= 0; i < classpathEntries.length; i++) {
				ILoadpathEntry entry= classpathEntries[i];
				if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
					IResource resource= root.findMember(entry.getPath());
					if (resource instanceof IProject)
						list.add(new RequiredProjectWrapper(RubyCore.create(resource)));
				}
			}
		}
		return list.toArray();
	}

	protected static Object[] concatenate(Object[] a1, Object[] a2) {
		int a1Len= a1.length;
		int a2Len= a2.length;
		Object[] res= new Object[a1Len + a2Len];
		System.arraycopy(a1, 0, res, 0, a1Len);
		System.arraycopy(a2, 0, res, a1Len, a2Len); 
		return res;
	}

	public ImageDescriptor getImageDescriptor(Object object) {
		return RubyPluginImages.DESC_OBJS_LIBRARY;
	}

	public String getLabel(Object o) {
		if (fContainer != null)
			return fContainer.getDescription();
		
		IPath path= fClassPathEntry.getPath();
		String containerId= path.segment(0);
		LoadpathContainerInitializer initializer= RubyCore.getLoadpathContainerInitializer(containerId);
		if (initializer != null) {
			String description= initializer.getDescription(path, fProject);
			return Messages.format(PackagesMessages.ClassPathContainer_unbound_label, description); 
		}
		return Messages.format(PackagesMessages.ClassPathContainer_unknown_label, path.toString()); 
	}

	public Object getParent(Object o) {
		return getRubyProject();
	}

	public IRubyProject getRubyProject() {
		return fProject;
	}
	
	public ILoadpathEntry getLoadpathEntry() {
		return fClassPathEntry;
	}
	
	static boolean contains(IRubyProject project, ILoadpathEntry entry, ISourceFolderRoot root) {
		ISourceFolderRoot[] roots= project.findSourceFolderRoots(entry);
		for (int i= 0; i < roots.length; i++) {
			if (roots[i].equals(root))
				return true;
		}
		return false;
	}
}
