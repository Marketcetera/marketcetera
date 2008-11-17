package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.parser.TaskTag;

public class ProblemRequestorMarkerManager implements IProblemRequestor {

	private IMarkerManager markerManager;

	private boolean active = false;

	private IFile file;

	public ProblemRequestorMarkerManager(IFile file,
			IMarkerManager markerManager) {
		this.markerManager = markerManager;
		this.file = file;
	}

	public void acceptProblem(IProblem problem) {
		// TODO Use active flag by calling begin and end reporting
		// if (!isActive()) return;
		if (problem.isWarning() || problem.isError()) {
			markerManager.addProblem(file, problem);
			return;
		}
		if (problem.isTask()) {
			List tasks = new ArrayList();
			TaskTag task = (TaskTag) problem;
			tasks.add(task);
			try {
				markerManager.createTasks(file, tasks);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			return;
		}
	}

	public void beginReporting() {
		active = true;
	}

	public void endReporting() {
		active = false;
	}

	public boolean isActive() {
		return active;
	}

}
