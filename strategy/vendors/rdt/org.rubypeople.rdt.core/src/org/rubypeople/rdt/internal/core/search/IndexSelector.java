/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.search;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.internal.compiler.util.SimpleSet;
import org.rubypeople.rdt.internal.core.ExternalSourceFolderRoot;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;
import org.rubypeople.rdt.internal.core.search.matching.MatchLocator;
import org.rubypeople.rdt.internal.core.search.matching.MethodPattern;

/**
 * Selects the indexes that correspond to projects in a given search scope
 * and that are dependent on a given focus element.
 */
public class IndexSelector {
	IRubySearchScope searchScope;
	SearchPattern pattern;
	IPath[] indexLocations; // cache of the keys for looking index up
	
public IndexSelector(
		IRubySearchScope searchScope,
		SearchPattern pattern) {
	
	this.searchScope = searchScope;
	this.pattern = pattern;
}
/**
 * Returns whether elements of the given project or jar can see the given focus (an IRubyProject or
 * a JarSourceFolderRot) either because the focus is part of the project or the jar, or because it is 
 * accessible throught the project's classpath
 */
public static boolean canSeeFocus(IRubyElement focus, boolean isPolymorphicSearch, IPath projectOrJarPath) {
	try {
		ILoadpathEntry[] focusEntries = null;
		if (isPolymorphicSearch) {
			RubyProject focusProject = (RubyProject) focus;
			focusEntries = focusProject.getExpandedLoadpath(true);
		}
		IRubyModel model = focus.getRubyModel();
		IRubyProject project = getRubyProject(projectOrJarPath, model);
		if (project != null)
			return canSeeFocus(focus, (RubyProject) project, focusEntries);

		// projectOrJarPath is a jar
		// it can see the focus only if it is on the classpath of a project that can see the focus
		IRubyProject[] allProjects = model.getRubyProjects();
		for (int i = 0, length = allProjects.length; i < length; i++) {
			RubyProject otherProject = (RubyProject) allProjects[i];
			ILoadpathEntry[] entries = otherProject.getResolvedLoadpath(true);
			for (int j = 0, length2 = entries.length; j < length2; j++) {
				ILoadpathEntry entry = entries[j];
				if (entry.getEntryKind() == ILoadpathEntry.CPE_LIBRARY && entry.getPath().equals(projectOrJarPath))
					if (canSeeFocus(focus, otherProject, focusEntries))
						return true;
			}
		}
		return false;
	} catch (RubyModelException e) {
		return false;
	}
}
public static boolean canSeeFocus(IRubyElement focus, RubyProject javaProject, ILoadpathEntry[] focusEntriesForPolymorphicSearch) {
	try {
		if (focus.equals(javaProject))
			return true;

		if (focusEntriesForPolymorphicSearch != null) {
			// look for refering project
			IPath projectPath = javaProject.getProject().getFullPath();
			for (int i = 0, length = focusEntriesForPolymorphicSearch.length; i < length; i++) {
				ILoadpathEntry entry = focusEntriesForPolymorphicSearch[i];
				if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT && entry.getPath().equals(projectPath))
					return true;
			}
		}
		if (focus instanceof ExternalSourceFolderRoot) {
			// focus is part of a jar
			IPath focusPath = focus.getPath();
			ILoadpathEntry[] entries = javaProject.getExpandedLoadpath(true);
			for (int i = 0, length = entries.length; i < length; i++) {
				ILoadpathEntry entry = entries[i];
				if (entry.getEntryKind() == ILoadpathEntry.CPE_LIBRARY && entry.getPath().equals(focusPath))
					return true;
			}
			return false;
		}
		// look for dependent projects
		IPath focusPath = ((RubyProject) focus).getProject().getFullPath();
		ILoadpathEntry[] entries = javaProject.getExpandedLoadpath(true);
		for (int i = 0, length = entries.length; i < length; i++) {
			ILoadpathEntry entry = entries[i];
			if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT && entry.getPath().equals(focusPath))
				return true;
		}
		return false;
	} catch (RubyModelException e) {
		return false;
	}
}
/*
 *  Compute the list of paths which are keying index files.
 */
private void initializeIndexLocations() {
	IPath[] projectsAndJars = this.searchScope.enclosingProjectsAndJars();
	IndexManager manager = RubyModelManager.getRubyModelManager().getIndexManager();
	SimpleSet locations = new SimpleSet();
	IRubyElement focus = MatchLocator.projectOrJarFocus(this.pattern);
	if (focus == null) {
		for (int i = 0; i < projectsAndJars.length; i++)
			locations.add(manager.computeIndexLocation(projectsAndJars[i]));
	} else {
		try {
			// find the projects from projectsAndJars that see the focus then walk those projects looking for the jars from projectsAndJars
			int length = projectsAndJars.length;
			RubyProject[] projectsCanSeeFocus = new RubyProject[length];
			SimpleSet visitedProjects = new SimpleSet(length);
			int projectIndex = 0;
			SimpleSet jarsToCheck = new SimpleSet(length);
			ILoadpathEntry[] focusEntries = null;
			if (this.pattern instanceof MethodPattern) { // should consider polymorphic search for method patterns
				RubyProject focusProject = (RubyProject) focus;
				focusEntries = focusProject.getExpandedLoadpath(true);
			}
			IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			for (int i = 0; i < length; i++) {
				IPath path = projectsAndJars[i];
				RubyProject project = (RubyProject) getRubyProject(path, model);
				if (project != null) {
					visitedProjects.add(project);
					if (canSeeFocus(focus, project, focusEntries)) {
						locations.add(manager.computeIndexLocation(path));
						projectsCanSeeFocus[projectIndex++] = project;
					}
				} else {
					jarsToCheck.add(path);
				}
			}
			for (int i = 0; i < projectIndex && jarsToCheck.elementSize > 0; i++) {
				ILoadpathEntry[] entries = projectsCanSeeFocus[i].getResolvedLoadpath(true);
				for (int j = entries.length; --j >= 0;) {
					ILoadpathEntry entry = entries[j];
					if (entry.getEntryKind() == ILoadpathEntry.CPE_LIBRARY) {
						IPath path = entry.getPath();
						if (jarsToCheck.includes(path)) {
							locations.add(manager.computeIndexLocation(entry.getPath()));
							jarsToCheck.remove(path);
						}
					}
				}
			}
			// jar files can be included in the search scope without including one of the projects that references them, so scan all projects that have not been visited
			if (jarsToCheck.elementSize > 0) {
				IRubyProject[] allProjects = model.getRubyProjects();
				for (int i = 0, l = allProjects.length; i < l && jarsToCheck.elementSize > 0; i++) {
					RubyProject project = (RubyProject) allProjects[i];
					if (!visitedProjects.includes(project)) {
						ILoadpathEntry[] entries = project.getResolvedLoadpath(true);
						for (int j = entries.length; --j >= 0;) {
							ILoadpathEntry entry = entries[j];
							if (entry.getEntryKind() == ILoadpathEntry.CPE_LIBRARY) {
								IPath path = entry.getPath();
								if (jarsToCheck.includes(path)) {
									locations.add(manager.computeIndexLocation(entry.getPath()));
									jarsToCheck.remove(path);
								}
							}
						}
					}
				}
			}
		} catch (RubyModelException e) {
			// ignored
		}
	}

	this.indexLocations = new IPath[locations.elementSize];
	Object[] values = locations.values;
	int count = 0;
	for (int i = values.length; --i >= 0;)
		if (values[i] != null)
			this.indexLocations[count++] = (IPath) values[i];
}
public IPath[] getIndexLocations() {
	if (this.indexLocations == null) {
		this.initializeIndexLocations(); 
	}
	return this.indexLocations;
}

/**
 * Returns the java project that corresponds to the given path.
 * Returns null if the path doesn't correspond to a project.
 */
private static IRubyProject getRubyProject(IPath path, IRubyModel model) {
	IRubyProject project = model.getRubyProject(path.lastSegment());
	if (project.exists()) {
		return project;
	}
	return null;
}
}
