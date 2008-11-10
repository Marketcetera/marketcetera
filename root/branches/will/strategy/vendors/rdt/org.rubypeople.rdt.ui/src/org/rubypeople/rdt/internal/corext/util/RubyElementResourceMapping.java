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
package org.rubypeople.rdt.internal.corext.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.mapping.RemoteResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.model.RubyModelProvider;

/**
 * An abstract super class to describe mappings from a Ruby element to a
 * set of resources. The class also provides factory methods to create
 * resource mappings.
 * 
 * @since 3.1
 */
public abstract class RubyElementResourceMapping extends ResourceMapping {
	
	protected RubyElementResourceMapping() {
	}
	
	public IRubyElement getRubyElement() {
		Object o= getModelObject();
		if (o instanceof IRubyElement)
			return (IRubyElement)o;
		return null;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof RubyElementResourceMapping))
			return false;
		return getRubyElement().equals(((RubyElementResourceMapping)obj).getRubyElement());
	}
	
	public int hashCode() {
		IRubyElement javaElement= getRubyElement();
		if (javaElement == null)
			return super.hashCode();
		
		return javaElement.hashCode();
	}
	
	public String getModelProviderId() {
		return RubyModelProvider.RUBY_MODEL_PROVIDER_ID;
	}
	
	public boolean contains(ResourceMapping mapping) {
		if (mapping instanceof RubyElementResourceMapping) {
			RubyElementResourceMapping javaMapping = (RubyElementResourceMapping) mapping;
			IRubyElement element = getRubyElement();
			IRubyElement other = javaMapping.getRubyElement();
			if (other != null && element != null)
				return element.getPath().isPrefixOf(other.getPath());
		}
		return false;
	}
	
	//---- the factory code ---------------------------------------------------------------
	
	private static final class RubyModelResourceMapping extends RubyElementResourceMapping {
		private final IRubyModel fRubyModel;
		private RubyModelResourceMapping(IRubyModel model) {
			Assert.isNotNull(model);
			fRubyModel= model;
		}
		public Object getModelObject() {
			return fRubyModel;
		}
		public IProject[] getProjects() {
			IRubyProject[] projects= null;
			try {
				projects= fRubyModel.getRubyProjects();
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
				return new IProject[0];
			}
			IProject[] result= new IProject[projects.length];
			for (int i= 0; i < projects.length; i++) {
				result[i]= projects[i].getProject();
			}
			return result;
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			IRubyProject[] projects= fRubyModel.getRubyProjects();
			ResourceTraversal[] result= new ResourceTraversal[projects.length];
			for (int i= 0; i < projects.length; i++) {
				result[i]= new ResourceTraversal(new IResource[] {projects[i].getProject()}, IResource.DEPTH_INFINITE, 0);
			}
			return result;
		}
	}
	
	private static final class RubyProjectResourceMapping extends RubyElementResourceMapping {
		private final IRubyProject fProject;
		private RubyProjectResourceMapping(IRubyProject project) {
			Assert.isNotNull(project);
			fProject= project;
		}
		public Object getModelObject() {
			return fProject;
		}
		public IProject[] getProjects() {
			return new IProject[] {fProject.getProject() };
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			return new ResourceTraversal[] {
				new ResourceTraversal(new IResource[] {fProject.getProject()}, IResource.DEPTH_INFINITE, 0)
			};
		}
	}
	
	private static final class PackageFragementRootResourceMapping extends RubyElementResourceMapping {
		private final ISourceFolderRoot fRoot;
		private PackageFragementRootResourceMapping(ISourceFolderRoot root) {
			Assert.isNotNull(root);
			fRoot= root;
		}
		public Object getModelObject() {
			return fRoot;
		}
		public IProject[] getProjects() {
			return new IProject[] {fRoot.getRubyProject().getProject() };
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			return new ResourceTraversal[] {
				new ResourceTraversal(new IResource[] {fRoot.getResource()}, IResource.DEPTH_INFINITE, 0)
			};
		}
	}
	
	private static final class LocalPackageFragementTraversal extends ResourceTraversal {
		private final ISourceFolder fPack;
		public LocalPackageFragementTraversal(ISourceFolder pack) throws CoreException {
			super(new IResource[] {pack.getResource()}, IResource.DEPTH_ONE, 0);
			fPack= pack;
		}
		public void accept(IResourceVisitor visitor) throws CoreException {
			IFile[] files= getPackageContent(fPack);
			final IResource resource= fPack.getResource();
			if (resource != null)
				visitor.visit(resource);
			for (int i= 0; i < files.length; i++) {
				visitor.visit(files[i]);
			}
		}
	}
	
	private static final class SourceFolderResourceMapping extends RubyElementResourceMapping {
		private final ISourceFolder fPack;
		private SourceFolderResourceMapping(ISourceFolder pack) {
			Assert.isNotNull(pack);
			fPack= pack;
		}
		public Object getModelObject() {
			return fPack;
		}
		public IProject[] getProjects() {
			return new IProject[] { fPack.getRubyProject().getProject() };
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			if (context instanceof RemoteResourceMappingContext) {
				return new ResourceTraversal[] {
					new ResourceTraversal(new IResource[] {fPack.getResource()}, IResource.DEPTH_ONE, 0)
				};
			} else {
				return new ResourceTraversal[] { new LocalPackageFragementTraversal(fPack) };
			}
		}
		public void accept(ResourceMappingContext context, IResourceVisitor visitor, IProgressMonitor monitor) throws CoreException {
			if (context instanceof RemoteResourceMappingContext) {
				super.accept(context, visitor, monitor);
			} else {
				// We assume a local context.
				IFile[] files= getPackageContent(fPack);
				if (monitor == null)
					monitor= new NullProgressMonitor();
				monitor.beginTask("", files.length + 1); //$NON-NLS-1$
				final IResource resource= fPack.getResource();
				if (resource != null)
					visitor.visit(resource);
				monitor.worked(1);
				for (int i= 0; i < files.length; i++) {
					visitor.visit(files[i]);
					monitor.worked(1);
				}
			}
		}
	}
	
	private static IFile[] getPackageContent(ISourceFolder pack) throws CoreException {
		List result= new ArrayList();
		IContainer container= (IContainer)pack.getResource();
		if (container != null) {
			IResource[] members= container.members();
			for (int m= 0; m < members.length; m++) {
				IResource member= members[m];
				if (member instanceof IFile) {
					IFile file= (IFile)member;
					if ("class".equals(file.getFileExtension()) && file.isDerived()) //$NON-NLS-1$
						continue;
					result.add(member);
				}
			}
		}
		return (IFile[])result.toArray(new IFile[result.size()]);
	}
	
	
	private static final class RubyScriptResourceMapping extends RubyElementResourceMapping {
		private final IRubyScript fUnit;
		private RubyScriptResourceMapping(IRubyScript unit) {
			Assert.isNotNull(unit);
			fUnit= unit;
		}
		public Object getModelObject() {
			return fUnit;
		}
		public IProject[] getProjects() {
			return new IProject[] {fUnit.getRubyProject().getProject() };
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			return new ResourceTraversal[] {
				new ResourceTraversal(new IResource[] {fUnit.getResource()}, IResource.DEPTH_ONE, 0)
			};
		}
	}
	
	public static ResourceMapping create(IRubyElement element) {
		switch (element.getElementType()) {
			case IRubyElement.TYPE:
				return create((IType)element);
			case IRubyElement.SCRIPT:
				return create((IRubyScript)element);
			case IRubyElement.SOURCE_FOLDER:
				return create((ISourceFolder)element);
			case IRubyElement.SOURCE_FOLDER_ROOT:
				return create((ISourceFolderRoot)element);
			case IRubyElement.RUBY_PROJECT:
				return create((IRubyProject)element);
			case IRubyElement.RUBY_MODEL:
				return create((IRubyModel)element);
			default:
				return null;
		}		
		
	}

	public static ResourceMapping create(final IRubyModel model) {
		return new RubyModelResourceMapping(model);
	}
	
	public static ResourceMapping create(final IRubyProject project) {
		return new RubyProjectResourceMapping(project);
	}
	
	public static ResourceMapping create(final ISourceFolderRoot root) {
		if (root.isExternal())
			return null;
		return new PackageFragementRootResourceMapping(root);
	}
	
	public static ResourceMapping create(final ISourceFolder pack) {
		// test if in an archive
		ISourceFolderRoot root= (ISourceFolderRoot)pack.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
		if (!root.isExternal()) {
			return new SourceFolderResourceMapping(pack);
		}
		return null;
	}
	
	public static ResourceMapping create(IRubyScript unit) {
		if (unit == null)
			return null;
		return new RubyScriptResourceMapping(unit.getPrimary());
	}
	
	public static ResourceMapping create(IType type) {
		// top level types behave like the CU
		IRubyElement parent= type.getParent();
		if (parent instanceof IRubyScript) {
			return create((IRubyScript)parent);
		}
		return null;
	}
}
