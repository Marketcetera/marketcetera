/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SafeRunner;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.core.compiler.ReconcileContext;
import org.rubypeople.rdt.internal.core.parser.MarkerUtility;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * Reconcile a working copy and signal the changes through a delta.
 */
public class ReconcileWorkingCopyOperation extends RubyModelOperation {

	public static boolean PERF = false;
	boolean createAST;
	boolean forceProblemDetection;
	WorkingCopyOwner workingCopyOwner;
	public RootNode ast;
	public RubyElementDeltaBuilder deltaBuilder;
	public HashMap problems;

	public ReconcileWorkingCopyOperation(IRubyElement workingCopy, boolean forceProblemDetection, WorkingCopyOwner workingCopyOwner) {
		super(new IRubyElement[] { workingCopy });
		this.forceProblemDetection = forceProblemDetection;
		this.workingCopyOwner = workingCopyOwner;
	}

	/**
	 * @exception RubyModelException
	 *                if setting the source of the original compilation unit
	 *                fails
	 */
	protected void executeOperation() throws RubyModelException {
		if (this.progressMonitor != null) {
			if (this.progressMonitor.isCanceled())
				throw new OperationCanceledException();
			this.progressMonitor.beginTask(Messages.element_reconciling, 2);
		}

		RubyScript workingCopy = getWorkingCopy();
		boolean wasConsistent = workingCopy.isConsistent();
		IProblemRequestor problemRequestor = workingCopy.getPerWorkingCopyInfo();

		// create the delta builder (this remembers the current content of the
		// cu)
		this.deltaBuilder = new RubyElementDeltaBuilder(workingCopy);

		// make working copy consistent if needed and compute AST if needed
		makeConsistent(workingCopy, problemRequestor);

		// notify reconcile participants
		notifyParticipants(workingCopy);

		// recreate ast if needed
//		if (this.ast == null)
//			makeConsistent(workingCopy, problemRequestor);

		// report problems
		if (this.problems != null && (this.forceProblemDetection || !wasConsistent)) {
			try {
				problemRequestor.beginReporting();
				for (Iterator iteraror = this.problems.values().iterator(); iteraror.hasNext();) {
					CategorizedProblem[] categorizedProblems = (CategorizedProblem[]) iteraror.next();
					if (categorizedProblems == null)
						continue;
					for (int i = 0, length = categorizedProblems.length; i < length; i++) {
						CategorizedProblem problem = categorizedProblems[i];
						if (RubyModelManager.VERBOSE) {
							System.out.println("PROBLEM FOUND while reconciling : " + problem.getMessage());//$NON-NLS-1$
						}
						if (this.progressMonitor != null && this.progressMonitor.isCanceled())
							break;
						if (!MarkerUtility.ignoring(workingCopy.getResource(), problem.getID(), problem.getSourceStart(), problem.getSourceEnd()))
							problemRequestor.acceptProblem(problem);
					}
				}
			} finally {
				problemRequestor.endReporting();
			}
		}

		// report delta
		try {
			RubyElementDelta delta = this.deltaBuilder.delta;
			if (delta != null) {
				addReconcileDelta(workingCopy, delta);
			}
		} finally {
			if (this.progressMonitor != null)
				this.progressMonitor.done();
		}
	}

	/*
	 * Makes the given working copy consistent, computes the delta and computes
	 * an AST if needed. Returns the AST.
	 */
	public RootNode makeConsistent(RubyScript workingCopy, IProblemRequestor problemRequestor) throws RubyModelException {
		if (!workingCopy.isConsistent()) {
			// make working copy consistent
			if (this.problems == null)
				this.problems = new HashMap();
			this.ast = workingCopy.makeConsistent(true, this.problems, this.progressMonitor);
			this.deltaBuilder.buildDeltas();
			if (this.ast != null && this.deltaBuilder.delta != null)
			 this.deltaBuilder.delta.changedAST(this.ast);
			return this.ast;
		}
		if (this.ast != null) return this.ast; // no need to recompute AST if known already
		if (this.forceProblemDetection) {
			if (RubyProject.hasRubyNature(workingCopy.getRubyProject().getProject())) {
				HashMap problemMap;
				if (this.problems == null) {
					problemMap = new HashMap();
					if (this.forceProblemDetection)
						this.problems = problemMap;
				} else
					problemMap = this.problems;
				// find problems
				char[] contents = workingCopy.getContents();
				this.ast = RubyScriptProblemFinder.process(workingCopy, contents, problemMap, this.progressMonitor);
				if (this.ast != null) {
					this.deltaBuilder.delta = new RubyElementDelta(workingCopy);
					this.deltaBuilder.delta.changedAST(this.ast);
				}				
				if (this.progressMonitor != null) this.progressMonitor.worked(1);

			} // else working copy not in a Ruby project
			return this.ast;
		}
		return null;
	}

	/**
	 * Returns the working copy this operation is working on.
	 */
	protected RubyScript getWorkingCopy() {
		return (RubyScript) getElementToProcess();
	}

	/**
	 * @see RubyModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}

	protected IRubyModelStatus verify() {
		IRubyModelStatus status = super.verify();
		if (!status.isOK()) {
			return status;
		}
		RubyScript workingCopy = getWorkingCopy();
		if (!workingCopy.isWorkingCopy()) {
			return new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST, workingCopy); // was
			// destroyed
		}
		return status;
	}

	private void notifyParticipants(final RubyScript workingCopy) {
		IRubyProject rubyProject = getWorkingCopy().getRubyProject();
		CompilationParticipant[] participants = RubyModelManager.getRubyModelManager().compilationParticipants.getCompilationParticipants(rubyProject);
		if (participants == null)
			return;

		final ReconcileContext context = new ReconcileContext(this, workingCopy);
		for (int i = 0, length = participants.length; i < length; i++) {
			final CompilationParticipant participant = participants[i];
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
					if (exception instanceof Error) {
						throw (Error) exception; // errors are not supposed
													// to be caught
					} else if (exception instanceof OperationCanceledException)
						throw (OperationCanceledException) exception;
					else if (exception instanceof UnsupportedOperationException) {
						// might want to disable participant as it tried to
						// modify the buffer of the working copy being
						// reconciled
						Util.log(exception, "Reconcile participant attempted to modify the buffer of the working copy being reconciled"); //$NON-NLS-1$
					} else
						Util.log(exception, "Exception occurred in reconcile participant"); //$NON-NLS-1$
				}

				public void run() throws Exception {
					participant.reconcile(context);
				}
			});
		}
	}

}