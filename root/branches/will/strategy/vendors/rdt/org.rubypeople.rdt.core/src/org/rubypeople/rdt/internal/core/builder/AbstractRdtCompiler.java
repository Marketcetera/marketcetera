package org.rubypeople.rdt.internal.core.builder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.internal.core.RubyModelManager;

public abstract class AbstractRdtCompiler {

    protected final IProject project;
    protected final IMarkerManager markerManager;
    protected CompilationParticipant[] fParticipants;
	private IRubyProject fRubyProject;
    
    public AbstractRdtCompiler(IProject project, IMarkerManager markerManager) {
        this.project = project;
        this.markerManager = markerManager;
        this.fRubyProject = getRubyProject();
        fParticipants = RubyModelManager.getRubyModelManager().compilationParticipants.getCompilationParticipants(fRubyProject);
    }

    protected abstract void removeMarkers(IMarkerManager markerManager);

    public void compile(IProgressMonitor monitor) throws CoreException {
        for (int i = 0; i < fParticipants.length; i++) {
        	fParticipants[i].aboutToBuild(fRubyProject);
        }
        BuildContext[] files = getBuildContexts();
        int taskCount = files.length * (fParticipants.length + 1);
        
        monitor.beginTask("Building " + project.getName() + "...", taskCount);
        
        monitor.subTask("Removing Markers...");
        removeMarkers(markerManager);
        monitor.worked(files.length);
		
        monitor.subTask("Analyzing Files...");
        compileFiles(files, monitor);
		
        monitor.done();
    }   

	private void compileFiles(BuildContext[] contexts, IProgressMonitor monitor) throws CoreException {
 		if (fParticipants != null) {
 			for (int i = 0; i < fParticipants.length; i++) {
 				if (monitor.isCanceled())
 	                return;
 				fParticipants[i].buildStarting(contexts, true);
 			}
 		}
 		for (int i = 0; i < contexts.length; i++) {
 			CategorizedProblem[] problems = contexts[i].getProblems();
 			if (problems == null || problems.length == 0) continue;
 			for (int j = 0; j < problems.length; j++) { 				
 				markerManager.addProblem(contexts[i].getFile(), problems[j]);
 			}
 		}
    }

	abstract protected BuildContext[] getBuildContexts() throws CoreException;

	private IRubyProject getRubyProject() {
		return RubyCore.create(project);
	}

	public void aboutToBuild() {
		if (fParticipants != null) {
 			for (int i = 0; i < fParticipants.length; i++) {
 				fParticipants[i].aboutToBuild(fRubyProject);
 			}
 		}
	}
}
