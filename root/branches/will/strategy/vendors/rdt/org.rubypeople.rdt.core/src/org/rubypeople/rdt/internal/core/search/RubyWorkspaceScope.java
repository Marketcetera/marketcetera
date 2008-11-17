package org.rubypeople.rdt.internal.core.search;

import java.util.HashSet;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;

public class RubyWorkspaceScope extends RubySearchScope {

	protected boolean needsInitialize;
	
	public IPath[] enclosingProjectsAndJars() {
		if (this.needsInitialize) {
			this.initialize(5);
		}
		return super.enclosingProjectsAndJars();
	}
	
	public void initialize(int size) {
		super.initialize(size);
		try {
			IRubyProject[] projects = RubyModelManager.getRubyModelManager().getRubyModel().getRubyProjects();
			for (int i = 0, length = projects.length; i < length; i++) {
				int includeMask = SOURCES | APPLICATION_LIBRARIES | SYSTEM_LIBRARIES;
				add((RubyProject) projects[i], null, includeMask, new HashSet(length*2, 1), null);
			}
		} catch (RubyModelException ignored) {
			// ignore
		}
		this.needsInitialize = false;
	}
}
