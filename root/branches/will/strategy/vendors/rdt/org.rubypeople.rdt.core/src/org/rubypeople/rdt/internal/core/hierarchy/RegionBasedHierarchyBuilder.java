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
package org.rubypeople.rdt.internal.core.hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.RubyProject;

public class RegionBasedHierarchyBuilder extends HierarchyBuilder {
	
	public RegionBasedHierarchyBuilder(TypeHierarchy hierarchy)
		throws RubyModelException {
			
		super(hierarchy);
	}
	
public void build(boolean computeSubtypes) {
		
//	RubyModelManager manager = RubyModelManager.getRubyModelManager();
	try {
		// optimize access to zip files while building hierarchy
//		manager.cacheZipFiles();
				
		if (this.hierarchy.focusType == null || computeSubtypes) {
			IProgressMonitor typeInRegionMonitor = 
				this.hierarchy.progressMonitor == null ? 
					null : 
					new SubProgressMonitor(this.hierarchy.progressMonitor, 30);
			HashMap allOpenablesInRegion = determineOpenablesInRegion(typeInRegionMonitor);
			this.hierarchy.initialize(allOpenablesInRegion.size());
			IProgressMonitor buildMonitor = 
				this.hierarchy.progressMonitor == null ? 
					null : 
					new SubProgressMonitor(this.hierarchy.progressMonitor, 70);
			createTypeHierarchyBasedOnRegion(allOpenablesInRegion, buildMonitor);
			((RegionBasedTypeHierarchy)this.hierarchy).pruneDeadBranches();
		} else {
			this.hierarchy.initialize(1);
			this.buildSupertypes();
		}
	} finally {
//		manager.flushZipFiles();
	}
}
/**
 * Configure this type hierarchy that is based on a region.
 */
private void createTypeHierarchyBasedOnRegion(HashMap allOpenablesInRegion, IProgressMonitor monitor) {
	
	int size = allOpenablesInRegion.size();
	if (size == 0) {
		if (monitor != null) monitor.done();
		return;
	}
		
	this.infoToHandle = new HashMap(size);
	Iterator javaProjects = allOpenablesInRegion.keySet().iterator();
	while (javaProjects.hasNext()) {
		RubyProject project = (RubyProject) javaProjects.next();
		ArrayList allOpenables = (ArrayList) allOpenablesInRegion.get(project);
		Openable[] openables = new Openable[allOpenables.size()];
		allOpenables.toArray(openables);
	
		try {
			// resolve
			if (monitor != null) monitor.beginTask("", size * 2/* 1 for build binding, 1 for connect hierarchy*/); //$NON-NLS-1$
//			SearchableEnvironment searchableEnvironment = project.newSearchableNameEnvironment(this.hierarchy.workingCopies);
//			this.nameLookup = searchableEnvironment.nameLookup;
			this.hierarchyResolver.resolve(openables, null, monitor);
//		} catch (RubyModelException e) {
			// project doesn't exit: ignore
		} finally {
			if (monitor != null) monitor.done();
		}
	}
}
	
	/**
	 * Returns all of the openables defined in the region of this type hierarchy.
	 * Returns a map from IRubyProject to ArrayList of Openable
	 */
	private HashMap determineOpenablesInRegion(IProgressMonitor monitor) {

		try {
			HashMap allOpenables = new HashMap();
			IRubyElement[] roots =
				((RegionBasedTypeHierarchy) this.hierarchy).region.getElements();
			int length = roots.length;
			if (monitor != null) monitor.beginTask("", length); //$NON-NLS-1$
			for (int i = 0; i <length; i++) {
				IRubyElement root = roots[i];
				IRubyProject javaProject = root.getRubyProject();
				ArrayList openables = (ArrayList) allOpenables.get(javaProject);
				if (openables == null) {
					openables = new ArrayList();
					allOpenables.put(javaProject, openables);
				}
				switch (root.getElementType()) {
					case IRubyElement.RUBY_PROJECT :
						injectAllOpenablesForRubyProject((IRubyProject) root, openables);
						break;
					case IRubyElement.SOURCE_FOLDER_ROOT :
						injectAllOpenablesForSourceFolderRoot((ISourceFolderRoot) root, openables);
						break;
					case IRubyElement.SOURCE_FOLDER :
						injectAllOpenablesForSourceFolder((ISourceFolder) root, openables);
						break;
					case IRubyElement.SCRIPT :
						openables.add(root);
						break;
					case IRubyElement.TYPE :
						IType type = (IType)root;						
						openables.add(type.getRubyScript());						
						break;
					default :
						break;
				}
				worked(monitor, 1);
			}
			return allOpenables;
		} finally {
			if (monitor != null) monitor.done();
		}
	}
	
	/**
	 * Adds all of the openables defined within this java project to the
	 * list.
	 */
	private void injectAllOpenablesForRubyProject(
		IRubyProject project,
		ArrayList openables) {
		try {
			ISourceFolderRoot[] devPathRoots =
				((RubyProject) project).getSourceFolderRoots();
			if (devPathRoots == null) {
				return;
			}
			for (int j = 0; j < devPathRoots.length; j++) {
				ISourceFolderRoot root = devPathRoots[j];
				injectAllOpenablesForSourceFolderRoot(root, openables);
			}
		} catch (RubyModelException e) {
			// ignore
		}
	}
	
	/**
	 * Adds all of the openables defined within this package fragment to the
	 * list.
	 */
	private void injectAllOpenablesForSourceFolder(
		ISourceFolder packFrag,
		ArrayList openables) {
			
		try {
			IRubyScript[] cus = packFrag.getRubyScripts();
			for (int i = 0, length = cus.length; i < length; i++) {
				openables.add(cus[i]);
			}			
		} catch (RubyModelException e) {
			// ignore
		}
	}
	
	/**
	 * Adds all of the openables defined within this package fragment root to the
	 * list.
	 */
	private void injectAllOpenablesForSourceFolderRoot(
		ISourceFolderRoot root,
		ArrayList openables) {
		try {
			IRubyElement[] packFrags = root.getChildren();
			for (int k = 0; k < packFrags.length; k++) {
				ISourceFolder packFrag = (ISourceFolder) packFrags[k];
				injectAllOpenablesForSourceFolder(packFrag, openables);
			}
		} catch (RubyModelException e) {
			return;
		}
	}
	
}
