/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.parser.MarkerUtility;
import org.rubypeople.rdt.internal.core.parser.TaskTag;

class MarkerManager implements IMarkerManager {

    public void removeProblemsAndTasksFor(IResource resource) {
    	RubyBuilder.removeProblemsAndTasksFor(resource);
    }

    public void createSyntaxError(IFile file, SyntaxException e) {
        MarkerUtility.createSyntaxError(file, e);
    }

    public void createTasks(IFile file, List<TaskTag> tasks) throws CoreException {
        MarkerUtility.createTasks(file, tasks);
    }
    
	public void addProblem(IFile file, IProblem problem) {
		MarkerUtility.createProblemMarker(file, problem);	
	}

}
