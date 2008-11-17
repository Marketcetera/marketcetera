/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    mkaufman@bea.com - initial API as ICompilationParticipant
 *    IBM - changed from interface ICompilationParticipant to abstract class CompilationParticipant
 *    IBM - rewrote spec
 *    
 *******************************************************************************/

package org.rubypeople.rdt.core.compiler;

import java.util.List;

import org.rubypeople.rdt.core.IRubyProject;

/**
 * A compilation participant is notified of events occuring during the compilation process.
 * The compilation process not only involves generating .class files (i.e. building), it also involve
 * cleaning the output directory, reconciling a working copy, etc.
 * So the notified events are the result of a build action, a clean action, a reconcile operation 
 * (for a working copy), etc.
 * <p>
 * Clients wishing to participate in the compilation process must suclass this class, and implement
 * {@link #isActive(IRubyProject)}, {@link #aboutToBuild(IRubyProject)}, 
 * {@link #reconcile(ReconcileContext)}, etc.
* </p><p>
 * This class is intended to be subclassed by clients.
 * </p>
 * @since 1.0
 */
public abstract class CompilationParticipant {

public static int READY_FOR_BUILD = 1;
public static int NEEDS_FULL_BUILD = 2;

/**
 * Notifies this participant that a build is about to start and provides it the opportunity to
 * create missing source folders for generated source files.
 * Only sent to participants interested in the project.
 * <p>
 * Default is to return <code>READY_FOR_BUILD</code>.
 * </p>
 * @param project the project about to build
 * @return READY_FOR_BUILD or NEEDS_FULL_BUILD
 */
public int aboutToBuild(IRubyProject project) {
	return READY_FOR_BUILD;
}

/**
 * Notifies this participant that a compile operation is about to start and provides it the opportunity to
 * generate source files based on the source files about to be compiled.
 * When isBatchBuild is true, then files contains all source files in the project.
 * Only sent to participants interested in the current build project.
 *
 * @param files is an array of BuildContext
 * @param isBatch identifies when the build is a batch build
  */
public void buildStarting(BuildContext[] files, boolean isBatch) {
	// do nothing by default
}

/**
 * Notifies this participant that a clean is about to start and provides it the opportunity to
 * delete generated source files.
 * Only sent to participants interested in the project.
 * @param project the project about to be cleaned
 */
public void cleanStarting(IRubyProject project) {
	// do nothing by default
}

/**
 * Returns whether this participant is active for a given project.
 * <p>
 * Default is to return <code>false</code>.
 * </p><p>
 * For efficiency, participants that are not interested in the 
 * given project should return <code>false</code> for that project.
 * </p>
 * @param project the project to participate in
 * @return whether this participant is active for a given project
 */
public boolean isActive(IRubyProject project) {
	return false;
}

/**
 * Returns whether this participant is interested in only Annotations.
 * <p>
 * Default is to return <code>false</code>.
 * </p>
 * @return whether this participant is interested in only Annotations.
 */
public boolean isAnnotationProcessor() {
	return false;
}

/**
 * Notifies this participant that a compile operation has found source files using Annotations.
 * Only sent to participants interested in the current build project that answer true to isAnnotationProcessor().
 * Each BuildContext was informed whether its source file currently hasAnnotations().
 *
 * @param files is an array of BuildContext
  */
public void processAnnotations(BuildContext[] files) {
	// do nothing by default
}

/**
 * Notifies this participant that a reconcile operation is happening. The participant can act on this reconcile
 * operation by using the given context. Other participant can then see the result of this participation
 * on this context.
 * <p>
 * Note that a participant should not modify the buffer of the working copy that is being reconciled.
 * </p><p>
 * Default is to do nothing.
 * </p>
 * @param context the reconcile context to act on
  */
public void reconcile(ReconcileContext context) {
	// do nothing by default
}

protected void addProblems(ReconcileContext context, String type, List<CategorizedProblem> problems) {
	if (problems == null || problems.size() == 0) return;
	CategorizedProblem[] oldProblems = context.getProblems(type);
	if (oldProblems == null || oldProblems.length == 0) {
		context.putProblems(type, problems.toArray(new CategorizedProblem[problems.size()]));
		return;
	}
	CategorizedProblem[] combined = new CategorizedProblem[problems.size() + oldProblems.length];
	for (int i = 0; i < oldProblems.length; i++) {
		combined[i] = oldProblems[i];
	}
	int j = oldProblems.length;
	for (CategorizedProblem problem : problems) {
		combined[j++] = problem;
	}
	context.putProblems(type, combined);
}

}
