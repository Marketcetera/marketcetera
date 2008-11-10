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
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.compiler.util.ObjectVector;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * This operation sets an <code>IRubyProject</code>'s classpath.
 *
 * @see IRubyProject
 */
public class SetLoadpathOperation extends RubyModelOperation {

	ILoadpathEntry[] oldResolvedPath, newResolvedPath;
	ILoadpathEntry[] newRawPath;
	boolean canChangeResources;
	boolean loadpathWasSaved;
	boolean needCycleCheck;
	boolean needValidation;
	boolean needSave;
	IPath newOutputLocation;
	RubyProject project;
	boolean identicalRoots;
	
	/*
	 * Used to indicate that the classpath entries remain the same.
	 */
	public static final ILoadpathEntry[] DO_NOT_SET_ENTRIES = new ILoadpathEntry[0];
	
	public static final ILoadpathEntry[] DO_NOT_UPDATE_PROJECT_REFS = new ILoadpathEntry[0];
	
	/*
	 * Used to indicate that the output location remains the same.
	 */
	public static final IPath DO_NOT_SET_OUTPUT = new Path("Reuse Existing Output Location");  //$NON-NLS-1$
	
	/**
	 * When executed, this operation sets the classpath of the given project.
	 */
	public SetLoadpathOperation(
		RubyProject project,
		ILoadpathEntry[] oldResolvedPath,
		ILoadpathEntry[] newRawPath,
		IPath newOutputLocation,
		boolean canChangeResource,
		boolean needValidation,
		boolean needSave) {

		super(new IRubyElement[] { project });
		this.oldResolvedPath = oldResolvedPath;
		this.newRawPath = newRawPath;
		this.newOutputLocation = newOutputLocation;
		this.canChangeResources = canChangeResource;
		this.needValidation = needValidation;
		this.needSave = needSave;
		this.project = project;
	}

	/**
	 * Adds deltas for the given roots, with the specified change flag,
	 * and closes the root. Helper method for #setLoadpath
	 */
	protected void addLoadpathDeltas(
		ISourceFolderRoot[] roots,
		int flag,
		RubyElementDelta delta) {

		for (int i = 0; i < roots.length; i++) {
			ISourceFolderRoot root = roots[i];
			delta.changed(root, flag);
			if ((flag & IRubyElementDelta.F_REMOVED_FROM_CLASSPATH) != 0 
					|| (flag & IRubyElementDelta.F_SOURCEATTACHED) != 0
					|| (flag & IRubyElementDelta.F_SOURCEDETACHED) != 0){
				try {
					root.close();
				} catch (RubyModelException e) {
					// ignore
				}
			}
		}
	}

	protected boolean canModifyRoots() {
		// setting classpath can modify roots
		return true;
	}

	/**
	 * Returns the index of the item in the list if the given list contains the specified entry. If the list does
	 * not contain the entry, -1 is returned.
	 * A helper method for #setLoadpath
	 */
	protected int classpathContains(
		ILoadpathEntry[] list,
		ILoadpathEntry entry) {

		IPath[] exclusionPatterns = entry.getExclusionPatterns();
		IPath[] inclusionPatterns = entry.getInclusionPatterns();
		nextEntry: for (int i = 0; i < list.length; i++) {
			ILoadpathEntry other = list[i];
			if (other.getEntryKind() == entry.getEntryKind()
				&& other.isExported() == entry.isExported()
				&& other.getPath().equals(entry.getPath())) {				
					// check inclusion patterns
					IPath[] otherIncludes = other.getInclusionPatterns();
					if (inclusionPatterns != otherIncludes) {
					    if (inclusionPatterns == null) continue;
						int includeLength = inclusionPatterns.length;
						if (otherIncludes == null || otherIncludes.length != includeLength)
							continue;
						for (int j = 0; j < includeLength; j++) {
							// compare toStrings instead of IPaths 
							// since IPath.equals is specified to ignore trailing separators
							if (!inclusionPatterns[j].toString().equals(otherIncludes[j].toString()))
								continue nextEntry;
						}
					}
					// check exclusion patterns
					IPath[] otherExcludes = other.getExclusionPatterns();
					if (exclusionPatterns != otherExcludes) {
					    if (exclusionPatterns == null) continue;
						int excludeLength = exclusionPatterns.length;
						if (otherExcludes == null || otherExcludes.length != excludeLength)
							continue;
						for (int j = 0; j < excludeLength; j++) {
							// compare toStrings instead of IPaths 
							// since IPath.equals is specified to ignore trailing separators
							if (!exclusionPatterns[j].toString().equals(otherExcludes[j].toString()))
								continue nextEntry;
						}
					}
					return i;
			}
		}
		return -1;
	}

	/**
	 * Recursively adds all subfolders of <code>folder</code> to the given collection.
	 */
	protected void collectAllSubfolders(IFolder folder, ArrayList collection) throws RubyModelException {
		try {
			IResource[] members= folder.members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource r= members[i];
				if (r.getType() == IResource.FOLDER) {
					collection.add(r);
					collectAllSubfolders((IFolder)r, collection);
				}
			}	
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}

	/**
	 * Returns a collection of package fragments that have been added/removed
	 * as the result of changing the output location to/from the given
	 * location. The collection is empty if no package fragments are
	 * affected.
	 */
	protected ArrayList determineAffectedPackageFragments(IPath location) throws RubyModelException {
		ArrayList fragments = new ArrayList();
	
		// see if this will cause any package fragments to be affected
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResource resource = null;
		if (location != null) {
			resource = workspace.getRoot().findMember(location);
		}
		if (resource != null && resource.getType() == IResource.FOLDER) {
			IFolder folder = (IFolder) resource;
			// only changes if it actually existed
			ILoadpathEntry[] classpath = project.getExpandedLoadpath(true);
			for (int i = 0; i < classpath.length; i++) {
				ILoadpathEntry entry = classpath[i];
				IPath path = classpath[i].getPath();
				if (entry.getEntryKind() != ILoadpathEntry.CPE_PROJECT && path.isPrefixOf(location) && !path.equals(location)) {
					ISourceFolderRoot[] roots = project.computeSourceFolderRoots(classpath[i]);
					SourceFolderRoot root = (SourceFolderRoot) roots[0];
					// now the output location becomes a package fragment - along with any subfolders
					ArrayList folders = new ArrayList();
					folders.add(folder);
					collectAllSubfolders(folder, folders);
					Iterator elements = folders.iterator();
					int segments = path.segmentCount();
					while (elements.hasNext()) {
						IFolder f = (IFolder) elements.next();
						IPath relativePath = f.getFullPath().removeFirstSegments(segments);
						String[] pkgName = relativePath.segments();
						ISourceFolder pkg = root.getSourceFolder(pkgName);
						fragments.add(pkg);
					}
				}
			}
		}
		return fragments;
	}

	/**
	 * Sets the classpath of the pre-specified project.
	 */
	protected void executeOperation() throws RubyModelException {
		// project reference updated - may throw an exception if unable to write .project file
		updateProjectReferencesIfNecessary();

		// loadpath file updated - may throw an exception if unable to write .loadpath file
		saveLoadpathIfNecessary();
		
		// perform classpath and output location updates, if exception occurs in loadpath update,
		// make sure the output location is updated before surfacing the exception (in case the output
		// location update also throws an exception, give priority to the loadpath update one).
		RubyModelException originalException = null;

		try {
			if (this.newRawPath == DO_NOT_UPDATE_PROJECT_REFS) this.newRawPath = project.getRawLoadpath();
			if (this.newRawPath != DO_NOT_SET_ENTRIES){
				updateLoadpath();
				project.updateSourceFolderRoots();
				RubyModelManager.getRubyModelManager().getDeltaProcessor().addForRefresh(project);
			}

		} catch(RubyModelException e){
			originalException = e;
			throw e;

		} finally { // if traversed by an exception we still need to update the output location when necessary

				// ensures the project is getting rebuilt if only variable is modified
				if (!this.identicalRoots && this.canChangeResources) {
					try {
						this.project.getProject().touch(this.progressMonitor);
					} catch (CoreException e) {
						if (RubyModelManager.CP_RESOLVE_VERBOSE){
							Util.verbose("CPContainer INIT - FAILED to touch project: "+ this.project.getElementName(), System.err); //$NON-NLS-1$
							e.printStackTrace();
						}
					}
			
			}
		}
		done();
	}

	/**
	 * Generates the delta of removed/added/reordered roots.
	 * Use three deltas in case the same root is removed/added/reordered (for
	 * instance, if it is changed from K_SOURCE to K_BINARY or vice versa)
	 */
	protected void generateLoadpathChangeDeltas() {

		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		if (manager.deltaState.findRubyProject(this.project.getElementName()) == null)
			// project doesn't exist yet (we're in an IWorkspaceRunnable)
			// no need to create a delta here and no need to index (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=133334)
			// the delta processor will create an ADDED project delta, and index the project
			return;
		boolean needToUpdateDependents = false;
		RubyElementDelta delta = new RubyElementDelta(getRubyModel());
		boolean hasDelta = false;
		if (this.loadpathWasSaved) {
			delta.changed(this.project, IRubyElementDelta.F_CLASSPATH_CHANGED);
			hasDelta = true;
		}
		int oldLength = oldResolvedPath.length;
		int newLength = newResolvedPath.length;
			
		final IndexManager indexManager = manager.getIndexManager();
		Map oldRoots = null;
		ISourceFolderRoot[] roots = null;
		if (project.isOpen()) {
			try {
				roots = project.getSourceFolderRoots();
			} catch (RubyModelException e) {
				// ignore
			}
		} else {
			Map allRemovedRoots ;
			if ((allRemovedRoots = manager.getDeltaProcessor().removedRoots) != null) {
		 		roots = (ISourceFolderRoot[]) allRemovedRoots.get(project);
			}
		}
		if (roots != null) {
			oldRoots = new HashMap();
			for (int i = 0; i < roots.length; i++) {
				ISourceFolderRoot root = roots[i];
				oldRoots.put(root.getPath(), root);
			}
		}
		for (int i = 0; i < oldLength; i++) {
			
			int index = classpathContains(newResolvedPath, oldResolvedPath[i]);
			if (index == -1) {
				// do not notify remote project changes
				if (oldResolvedPath[i].getEntryKind() == ILoadpathEntry.CPE_PROJECT){
					needToUpdateDependents = true;
					this.needCycleCheck = true;
					continue; 
				}

				ISourceFolderRoot[] pkgFragmentRoots = null;
				if (oldRoots != null) {
					ISourceFolderRoot oldRoot = (ISourceFolderRoot)  oldRoots.get(oldResolvedPath[i].getPath());
					if (oldRoot != null) { // use old root if any (could be none if entry wasn't bound)
						pkgFragmentRoots = new ISourceFolderRoot[] { oldRoot };
					}
				}
				if (pkgFragmentRoots == null) {
					try {
						ObjectVector accumulatedRoots = new ObjectVector();
						HashSet rootIDs = new HashSet(5);
						rootIDs.add(project.rootID());
						project.computeSourceFolderRoots(
							oldResolvedPath[i], 
							accumulatedRoots, 
							rootIDs,
							null, // inside original project
							false, // don't check existency
							false, // don't retrieve exported roots
							null); /*no reverse map*/
						pkgFragmentRoots = new ISourceFolderRoot[accumulatedRoots.size()];
						accumulatedRoots.copyInto(pkgFragmentRoots);
					} catch (RubyModelException e) {
						pkgFragmentRoots =  new ISourceFolderRoot[] {};
					}
				}
				addLoadpathDeltas(pkgFragmentRoots, IRubyElementDelta.F_REMOVED_FROM_CLASSPATH, delta);
				
				int changeKind = oldResolvedPath[i].getEntryKind();
				needToUpdateDependents |= (changeKind == ILoadpathEntry.CPE_SOURCE) || oldResolvedPath[i].isExported();

				// Remove the .java files from the index for a source folder
				// For a lib folder or a .jar file, remove the corresponding index if not shared.
				if (indexManager != null) {
					ILoadpathEntry oldEntry = oldResolvedPath[i];
					final IPath path = oldEntry.getPath();
					switch (changeKind) {
						case ILoadpathEntry.CPE_SOURCE:
							final char[][] inclusionPatterns = ((LoadpathEntry)oldEntry).fullInclusionPatternChars();
							final char[][] exclusionPatterns = ((LoadpathEntry)oldEntry).fullExclusionPatternChars();
							postAction(new IPostAction() {
								public String getID() {
									return path.toString();
								}
								public void run() /* throws RubyModelException */ {
									indexManager.removeSourceFolderFromIndex(project, path, inclusionPatterns, exclusionPatterns);
								}
							}, 
							REMOVEALL_APPEND);
							break;
						case ILoadpathEntry.CPE_LIBRARY:
							final DeltaProcessingState deltaState = manager.deltaState;
							postAction(new IPostAction() {
								public String getID() {
									return path.toString();
								}
								public void run() /* throws RubyModelException */ {
									if (deltaState.otherRoots.get(path) == null) { // if root was not shared
										indexManager.discardJobs(path.toString());
										indexManager.removeIndex(path);
										// TODO (kent) we could just remove the in-memory index and have the indexing check for timestamps
									}
								}
							}, 
							REMOVEALL_APPEND);
							break;
					}		
				}
				hasDelta = true;

			} else {
				// do not notify remote project changes
				if (oldResolvedPath[i].getEntryKind() == ILoadpathEntry.CPE_PROJECT){
					// Need to updated dependents in case old and/or new entries are exported and have an access restriction
					LoadpathEntry oldEntry = (LoadpathEntry) oldResolvedPath[i];
					LoadpathEntry newEntry = (LoadpathEntry) newResolvedPath[index];
					this.needCycleCheck |= (oldEntry.isExported() != newEntry.isExported());
					continue; 
				}				
				needToUpdateDependents |= (oldResolvedPath[i].isExported() != newResolvedPath[index].isExported());
				if (index != i) { //reordering of the classpath
						addLoadpathDeltas(
							project.computeSourceFolderRoots(oldResolvedPath[i]),
							IRubyElementDelta.F_REORDER,
							delta);
						int changeKind = oldResolvedPath[i].getEntryKind();
						needToUpdateDependents |= (changeKind == ILoadpathEntry.CPE_SOURCE);
		
						hasDelta = true;
				}
			}
		}

		for (int i = 0; i < newLength; i++) {

			int index = classpathContains(oldResolvedPath, newResolvedPath[i]);
			if (index == -1) {
				// do not notify remote project changes
				if (newResolvedPath[i].getEntryKind() == ILoadpathEntry.CPE_PROJECT){
					needToUpdateDependents = true;
					this.needCycleCheck = true;
					continue; 
				}
				addLoadpathDeltas(
					project.computeSourceFolderRoots(newResolvedPath[i]),
					IRubyElementDelta.F_ADDED_TO_CLASSPATH,
					delta);
				int changeKind = newResolvedPath[i].getEntryKind();
				
				// Request indexing
				if (indexManager != null) {
					switch (changeKind) {
						case ILoadpathEntry.CPE_LIBRARY:
							boolean pathHasChanged = true;
							final IPath newPath = newResolvedPath[i].getPath();
							for (int j = 0; j < oldLength; j++) {
								ILoadpathEntry oldEntry = oldResolvedPath[j];
								if (oldEntry.getPath().equals(newPath)) {
									pathHasChanged = false;
									break;
								}
							}
							if (pathHasChanged) {
								postAction(new IPostAction() {
									public String getID() {
										return newPath.toString();
									}
									public void run() /* throws RubyModelException */ {
										indexManager.indexLibrary(newPath, project.getProject());
									}
								}, 
								REMOVEALL_APPEND);
							}
							break;
						case ILoadpathEntry.CPE_SOURCE:
							ILoadpathEntry entry = newResolvedPath[i];
							final IPath path = entry.getPath();
							final char[][] inclusionPatterns = ((LoadpathEntry)entry).fullInclusionPatternChars();
							final char[][] exclusionPatterns = ((LoadpathEntry)entry).fullExclusionPatternChars();
							postAction(new IPostAction() {
								public String getID() {
									return path.toString();
								}
								public void run() /* throws RubyModelException */ {
									indexManager.indexSourceFolder(project, path, inclusionPatterns, exclusionPatterns);
								}
							}, 
							APPEND); // append so that a removeSourceFolder action is not removed
							break;
					}
				}
				
				needToUpdateDependents |= (changeKind == ILoadpathEntry.CPE_SOURCE) || newResolvedPath[i].isExported();
				hasDelta = true;

			} // classpath reordering has already been generated in previous loop
		}

		if (hasDelta) {
			this.addDelta(delta);
		} else {
			this.identicalRoots = true;
		}
		if (needToUpdateDependents){
			updateAffectedProjects(project.getProject().getFullPath());
		}
	}
	protected ISchedulingRule getSchedulingRule() {
		return null; // no lock taken while setting the classpath
	}

	/**
	 * Returns <code>true</code> if this operation performs no resource modifications,
	 * otherwise <code>false</code>. Subclasses must override.
	 */
	public boolean isReadOnly() {
		return !this.canChangeResources;
	}

	protected void saveLoadpathIfNecessary() throws RubyModelException {
		
		if (!this.canChangeResources || !this.needSave) return;
				
		ILoadpathEntry[] loadpathForSave;
		if (this.newRawPath == DO_NOT_SET_ENTRIES || this.newRawPath == DO_NOT_UPDATE_PROJECT_REFS){
			loadpathForSave = project.getRawLoadpath();
		} else {
			loadpathForSave = this.newRawPath;
		}
		// if read-only .loadpath, then the loadpath setting will never been performed completely
		if (project.saveLoadpath(loadpathForSave, null)) {
			this.loadpathWasSaved = true;
			this.setAttribute(HAS_MODIFIED_RESOURCE_ATTR, TRUE); 
		}
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer(20);
		buffer.append("SetLoadpathOperation\n"); //$NON-NLS-1$
		buffer.append(" - classpath : "); //$NON-NLS-1$
		if (this.newRawPath == DO_NOT_SET_ENTRIES){
			buffer.append("<Reuse Existing Loadpath Entries>"); //$NON-NLS-1$
		} else {
			buffer.append("{"); //$NON-NLS-1$
			for (int i = 0; i < this.newRawPath.length; i++) {
				if (i > 0) buffer.append(","); //$NON-NLS-1$
				ILoadpathEntry element = this.newRawPath[i];
				buffer.append(" ").append(element.toString()); //$NON-NLS-1$
			}
		}
		buffer.append("\n - output location : ");  //$NON-NLS-1$
		if (this.newOutputLocation == DO_NOT_SET_OUTPUT){
			buffer.append("<Reuse Existing Output Location>"); //$NON-NLS-1$
		} else {
			buffer.append(this.newOutputLocation.toString());
		}
		return buffer.toString();
	}

	private void updateLoadpath() throws RubyModelException {

		beginTask(Messages.bind(Messages.classpath_settingProgress, project.getElementName()), 2); 

		// SIDE-EFFECT: from thereon, the loadpath got modified
		project.getPerProjectInfo().updateLoadpathInformation(this.newRawPath);

		// resolve new path (asking for marker creation if problems)
		if (this.newResolvedPath == null) {
			this.newResolvedPath = project.getResolvedLoadpath(true, this.canChangeResources, false/*don't returnResolutionInProgress*/);
		}
		
		if (this.oldResolvedPath != null) {
			generateLoadpathChangeDeltas();
		} else {
			this.needCycleCheck = true;
			updateAffectedProjects(project.getProject().getFullPath());
		}
		
		updateCycleMarkersIfNecessary();
	}

	/**
	 * Update projects which are affected by this classpath change:
	 * those which refers to the current project as source (indirectly)
	 */
	protected void updateAffectedProjects(IPath prerequisiteProjectPath) {

		// remove all update classpath post actions for this project
		final String updateLoadpath = "UpdateClassPath:"; //$NON-NLS-1$
		removeAllPostAction(updateLoadpath + prerequisiteProjectPath.toString());
		
		try {
			IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			IRubyProject initialProject = this.project;
			IRubyProject[] projects = model.getRubyProjects();
			for (int i = 0, projectCount = projects.length; i < projectCount; i++) {
				try {
					final RubyProject affectedProject = (RubyProject) projects[i];
					if (affectedProject.equals(initialProject)) continue; // skip itself
					if (!affectedProject.isOpen()) continue; // skip project as its namelookup caches do not exist
					
					// consider ALL dependents (even indirect ones), since they may need to
					// flush their respective namelookup caches (all pkg fragment roots).

					ILoadpathEntry[] classpath = affectedProject.getExpandedLoadpath(true);
					for (int j = 0, entryCount = classpath.length; j < entryCount; j++) {
						ILoadpathEntry entry = classpath[j];
						if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT
							&& entry.getPath().equals(prerequisiteProjectPath)) {
								
							postAction(new IPostAction() {
									public String getID() {
										return updateLoadpath + affectedProject.getPath().toString();
									}
									public void run() throws RubyModelException {
										affectedProject.setRawLoadpath(
											DO_NOT_UPDATE_PROJECT_REFS, 
											SetLoadpathOperation.DO_NOT_SET_OUTPUT, 
											SetLoadpathOperation.this.progressMonitor, 
											SetLoadpathOperation.this.canChangeResources,  
											affectedProject.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/), 
											false, // updating only - no validation
											false); // updating only - no need to save
									}
								},
								REMOVEALL_APPEND);
							break;
						}
					}
				} catch (RubyModelException e) {
					// ignore
				}
			}
		} catch (RubyModelException e) {
			// ignore
		}
		
	}

	/**
	 * Update cycle markers
	 */
	protected void updateCycleMarkersIfNecessary() {

		if (!this.needCycleCheck) return;
		if (!this.canChangeResources) return;
		 
		if (!project.hasCycleMarker() && !project.hasLoadpathCycle(newResolvedPath)){
			return;
		}
	
		postAction(
			new IPostAction() {
				public String getID() {
					return "updateCycleMarkers";  //$NON-NLS-1$
				}
				public void run() throws RubyModelException {
					RubyProject.updateAllCycleMarkers(null);
				}
			},
			REMOVEALL_APPEND);
	}

	/**
	 * Update projects references so that the build order is consistent with the classpath
	 */
	protected void updateProjectReferencesIfNecessary() throws RubyModelException {
		
		if (this.newRawPath == DO_NOT_SET_ENTRIES || this.newRawPath == DO_NOT_UPDATE_PROJECT_REFS) return;
		// will run now, or be deferred until next pre-auto-build notification if resource tree is locked
		RubyModelManager.getRubyModelManager().deltaState.updateProjectReferences(
		        project, 
		        oldResolvedPath, 
		        newResolvedPath, 
		        newRawPath, 
		        canChangeResources);
	}

	public IRubyModelStatus verify() {

		IRubyModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}

		if (needValidation) {
			// retrieve classpath 
			ILoadpathEntry[] entries = this.newRawPath;
			if (entries == DO_NOT_SET_ENTRIES){
				try {
					entries = project.getRawLoadpath();			
				} catch (RubyModelException e) {
					return e.getRubyModelStatus();
				}
			}							
			// perform validation
			return LoadpathEntry.validateLoadpath(
				project,
				entries, null);
		}
		
		return RubyModelStatus.VERIFIED_OK;
	}
}
