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

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;

/**
 * A <tt>ExternalFileTypeInfo</tt> represents a type in a Jar file.
 */
public class ExternalFileTypeInfo extends TypeInfo {

	private final String fPath;
	
	public ExternalFileTypeInfo(String pkg, String name, char[][] enclosingTypes, boolean isModule, String path) {
		super(pkg, name, enclosingTypes, isModule);
		fPath = path;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!ExternalFileTypeInfo.class.equals(obj.getClass()))
			return false;
		ExternalFileTypeInfo other= (ExternalFileTypeInfo)obj;
		return doEquals(other) && fPath.equals(other.fPath);
	}
	
	public int getElementType() {
		return TypeInfo.JAR_FILE_ENTRY_TYPE_INFO;
	}
	
	protected IRubyElement getContainer(IRubySearchScope scope) throws RubyModelException {
		IRubyModel jmodel= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot());
		IPath[] enclosedPaths= scope.enclosingProjectsAndJars();

		IPath filePath = new Path(fPath);
		for (int i= 0; i < enclosedPaths.length; i++) {
			IPath curr= enclosedPaths[i];
			if (curr.segmentCount() == 1) { // check the project
				IRubyProject jproject= jmodel.getRubyProject(curr.segment(0));
				ISourceFolderRoot[] roots = jproject.getSourceFolderRoots();
				for (int j = 0; j < roots.length; j++) {
					ISourceFolderRoot root = roots[j];
					if (root.isExternal() && root.getPath().isPrefixOf(filePath)) {
						IPath relative = filePath.removeFirstSegments(root.getPath().segmentCount());
						return findElementInRoot(root, relative);
					}
				}
			}
		}
		List paths= Arrays.asList(enclosedPaths);
		IRubyProject[] projects= jmodel.getRubyProjects();
		for (int i= 0; i < projects.length; i++) {
			IRubyProject jproject= projects[i];
			if (!paths.contains(jproject.getPath())) {
				ISourceFolderRoot[] roots = jproject.getSourceFolderRoots();
				for (int j = 0; j < roots.length; j++) {
					ISourceFolderRoot root = roots[j];
					if (root.isExternal() && root.getPath().isPrefixOf(filePath)) {
						IPath relative = filePath.removeFirstSegments(root.getPath().segmentCount());
						return findElementInRoot(root, relative);
					}
				}
			}
		}
		return null;
	}
	
	private IRubyElement findElementInRoot(ISourceFolderRoot root, IPath relative) {
		IRubyElement res;
		ISourceFolder frag= root.getSourceFolder(relative.removeLastSegments(1).segments());
		String extension= getExtension();
		String fullName= getFileName() + '.' + extension;
		
		if (RubyCore.isRubyLikeFileName(fullName)) {
			res=  frag.getRubyScript(fullName);
		} else {
			return null;
		}
		if (res.exists()) {
			return res;
		}
		return null;
	}
	
	private String getFileName() {
		String name = new File(fPath).getName();
		return name.substring(0, name.lastIndexOf('.'));
	}

	private String getExtension() {
		String name = new File(fPath).getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	public IPath getPackageFragmentRootPath() {
		return new Path(fPath);
	}
	
	public String getPackageFragmentRootName() {
		// we can't remove the '/' since the jar can be external.
		return fPath;
	}
		
	public String getPath() {
		return fPath;
	}
	
	public long getContainerTimestamp() {
		// First try internal Jar
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		IPath path= new Path(fPath);
		IResource resource= root.findMember(path);
		IFileInfo info= null;
		IRubyElement element= null;
		if (resource != null && resource.exists()) {
			URI location= resource.getLocationURI();
			if (location != null) {
				try {
					info= EFS.getStore(location).fetchInfo();
					if (info.exists()) {
						element= RubyCore.create(resource);
						// The exist test for external jars is expensive due to
						// JDT/Core. So do the test here since we know that the
						// Ruby element points to an internal Jar. 
						if (element != null && !element.exists())
							element= null;
					}
				} catch (CoreException e) {
					// Fall through
				}
			}
		} else {
			info= EFS.getLocalFileSystem().getStore(Path.fromOSString(fPath)).fetchInfo();
			if (info.exists()) {
				element= getPackageFragementRootForExternalJar();
			}
		}
		if (info != null && info.exists() && element != null) {
			return info.getLastModified();
		}
		return IResource.NULL_STAMP;
	}
	
	public boolean isContainerDirty() {
		return false;
	}
		
	private void getElementPath(StringBuffer result) {
		String pack= getPackageName();
		if (pack != null && pack.length() > 0) {
			result.append(pack.replace(TypeInfo.PACKAGE_PART_SEPARATOR, TypeInfo.SEPARATOR));
			result.append(TypeInfo.SEPARATOR);
		}
		result.append(getFileName());
		result.append('.');
		result.append(getExtension());
	}
	
	private ISourceFolderRoot getPackageFragementRootForExternalJar() {
		try {
			IRubyModel jmodel= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot());
			IRubyProject[] projects= jmodel.getRubyProjects();
			for (int i= 0; i < projects.length; i++) {
				IRubyProject project= projects[i];
				ISourceFolderRoot root= project.getSourceFolderRoot(fPath);
				// Cheaper check than calling root.exists().
				if (project.isOnLoadpath(root))
					return root;
			}
		} catch (RubyModelException e) {
			// Fall through
		}
		return null;
	}
}
