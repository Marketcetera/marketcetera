/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    
 *******************************************************************************/

package org.rubypeople.rdt.core.compiler;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * The context of a build event that is notified to interested compilation 
 * participants when {@link CompilationParticipant#buildStarting(BuildContext[], boolean) a build is starting},
 * or to annotations processors when {@link CompilationParticipant#processAnnotations(BuildContext[]) a source file has annotations}.
 * <p>
 * This class is not intended to be instanciated or subclassed by clients.
 * </p>
 * @since 0.9.0
 */
public class BuildContext extends CompilationParticipantResult {

/**
 * Creates a build context for the given source file.
 * <p>
 * This constructor is not intended to be called by clients.
 * </p>
 * 
 * @param sourceFile the source file being built
 */
public BuildContext(IFile resource) {
	super(resource);
}

/**
 * Returns the contents of the compilation unit.
 * 
 * @return the contents of the compilation unit
 */
public char[] getContents() {
	try {	
		return Util.getResourceContentsAsCharArray(this.resource);
	} catch (CoreException e) {
		throw new RuntimeException("Missing source file: " + this.resource);
	}
}

/**
 * Returns the <code>IFile</code> representing the compilation unit.
 * 
 * @return the <code>IFile</code> representing the compilation unit
 */
public IFile getFile() {
	return this.resource;
}

/**
 * Returns whether the compilation unit contained any annotations when it was compiled.
 * 
 * NOTE: This is only valid during {@link CompilationParticipant#processAnnotations(BuildContext[])}.
 * 
 * @return whether the compilation unit contained any annotations when it was compiled
 */
public boolean hasAnnotations() {
	return this.hasAnnotations; // only set during processAnnotations
}

/**
 * Record new problems to report against this compilationUnit.
 * Markers are persisted for these problems only for the declared managed marker type
 * (see the 'compilationParticipant' extension point).
 * 
 * @param newProblems the problems to report
 */
public void recordNewProblems(CategorizedProblem[] newProblems) {
	int length2 = newProblems.length;
	if (length2 == 0) return;

	int length1 = this.problems == null ? 0 : this.problems.length;
	CategorizedProblem[] merged = new CategorizedProblem[length1 + length2];
	if (length1 > 0) // always make a copy even if currently empty
		System.arraycopy(this.problems, 0, merged, 0, length1);	
	System.arraycopy(newProblems, 0, merged, length1, length2);	
	this.problems = merged;
}

}
