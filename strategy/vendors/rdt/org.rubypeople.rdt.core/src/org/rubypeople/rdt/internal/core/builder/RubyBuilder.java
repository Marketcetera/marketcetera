/*
?* Author: Chris, David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.core.builder;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.LoadpathEntry;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;


public class RubyBuilder extends IncrementalProjectBuilder {

    public static boolean DEBUG;

    private IProject currentProject;
	private RubyProject rubyProject;
	private IWorkspaceRoot workspaceRoot;

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	    this.currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible()) 
            return null;
		
		AbstractRdtCompiler compiler = createCompiler(kind);
		compiler.aboutToBuild();
		
        if (DEBUG)
            RubyCore.trace("Started " + buildType(kind) + " build of " + buildDescription()); //$NON-NLS-1$
        
        compiler.compile(monitor);

        if (DEBUG)
            RubyCore.trace("Finished build of " + buildDescription()); //$NON-NLS-1$
        
        IProject[] requiredProjects = getRequiredProjects(true);
		return requiredProjects;
	}
	
	@Override
	protected void clean(IProgressMonitor monitor) throws CoreException {
		this.currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible()) return;
		
		initializeBuilder();
//		compiler.cleanStarting(); TODO Send notification to participants!
		RubyModelManager.getRubyModelManager().indexManager.indexAll(currentProject);
		super.clean(monitor);
	}
    
    private void initializeBuilder() {
    	this.rubyProject = (RubyProject) RubyCore.create(currentProject);
    	this.workspaceRoot = currentProject.getWorkspace().getRoot();		
	}

	/*
	 * Return the list of projects for which it requires a resource delta. This
	 * builder's project is implicitly included and need not be specified.
	 * Builders must re-specify the list of interesting projects every time they
	 * are run as this is not carried forward beyond the next build. Missing
	 * projects should be specified but will be ignored until they are added to
	 * the workspace.
	 */
	private IProject[] getRequiredProjects(boolean includeBinaryPrerequisites) {
		if (rubyProject == null || workspaceRoot == null)
			return new IProject[0];

		ArrayList projects = new ArrayList();
		try {
			ILoadpathEntry[] entries = rubyProject.getExpandedLoadpath(true);
			for (int i = 0, l = entries.length; i < l; i++) {
				ILoadpathEntry entry = entries[i];
				IPath path = entry.getPath();
				IProject p = null;
				switch (entry.getEntryKind()) {
				case ILoadpathEntry.CPE_PROJECT:
					p = workspaceRoot.getProject(path.lastSegment()); // missing
																		// projects
																		// are
																		// considered
																		// too
					if (((LoadpathEntry) entry).isOptional()
							&& !RubyProject.hasRubyNature(p)) // except if
																// entry is
																// optional
						p = null;
					break;
				case ILoadpathEntry.CPE_LIBRARY:
					if (includeBinaryPrerequisites && path.segmentCount() > 1) {
						// some binary resources on the class path can come from
						// projects that are not included in the project
						// references
						IResource resource = workspaceRoot.findMember(path
								.segment(0));
						if (resource instanceof IProject)
							p = (IProject) resource;
					}
				}
				if (p != null && !projects.contains(p))
					projects.add(p);
			}
		} catch (RubyModelException e) {
			return new IProject[0];
		}
		IProject[] result = new IProject[projects.size()];
		projects.toArray(result);
		return result;
	}

	private AbstractRdtCompiler createCompiler(int kind) {
        if (isPartialBuild(kind))
            return new IncrementalRdtCompiler(currentProject, getDelta(currentProject));
        return new CleanRdtCompiler(currentProject);
        
    }
    private String buildType(int kind) {
        return isPartialBuild(kind) ? "Incremental" : "Full";
    }
    
    private String buildDescription() {
        return currentProject.getName() + " @ " + new Date(System.currentTimeMillis());
    }

    private boolean isPartialBuild(int kind) {
        return kind == INCREMENTAL_BUILD || kind == AUTO_BUILD;
    }
    
    public static void setVerbose(boolean verbose) {
        RubyBuilder.DEBUG = verbose;
    }

	public static void writeState(Object savedState, DataOutputStream out) {
		// TODO Actually write out build state to the stream!		
	}

	public static void removeProblemsAndTasksFor(IResource resource) {
		try {
			if (resource != null && resource.exists()) {
				resource.deleteMarkers(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);
				resource.deleteMarkers(IRubyModelMarker.TASK_MARKER, false, IResource.DEPTH_INFINITE);
				
				// delete managed markers
				Set markerTypes = RubyModelManager.getRubyModelManager().compilationParticipants.managedMarkerTypes();
				if (markerTypes.size() == 0) return;
				Iterator iterator = markerTypes.iterator();
				while (iterator.hasNext())
					resource.deleteMarkers((String) iterator.next(), false, IResource.DEPTH_INFINITE);
			}
		} catch (CoreException e) {
			// assume there were no problems
		}
	}

	public static void buildStarting() {
		// TODO Auto-generated method stub
		
	}

	public static void buildFinished() {
		// TODO Auto-generated method stub
		
	}
}
