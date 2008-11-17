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

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * An operation created as a result of a call to RubyCore.run(IWorkspaceRunnable, IProgressMonitor)
 * that encapsulates a user defined IWorkspaceRunnable.
 */
public class BatchOperation extends RubyModelOperation {
	protected IWorkspaceRunnable runnable;
	public BatchOperation(IWorkspaceRunnable runnable) {
		this.runnable = runnable;
	}

	protected boolean canModifyRoots() {
		// anything in the workspace runnable can modify the roots
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.RubyModelOperation#executeOperation()
	 */
	protected void executeOperation() throws RubyModelException {
		try {
			this.runnable.run(this.progressMonitor);
		} catch (CoreException ce) {
			if (ce instanceof RubyModelException) {
				throw (RubyModelException)ce;
			} else {
				if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
					Throwable e= ce.getStatus().getException();
					if (e instanceof RubyModelException) {
						throw (RubyModelException) e;
					}
				}
				throw new RubyModelException(ce);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.RubyModelOperation#verify()
	 */
	protected IRubyModelStatus verify() {
		// cannot verify user defined operation
		return RubyModelStatus.VERIFIED_OK;
	}

	
}
