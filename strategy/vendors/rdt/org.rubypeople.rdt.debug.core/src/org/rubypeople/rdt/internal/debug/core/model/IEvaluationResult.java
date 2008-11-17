/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.debug.core.model;


import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;

/**
 * The result of an evaluation. An evaluation result may
 * contain problems and/or a result value.
 * <p>
 * Clients are not intended to implement this interface.
 * </p>
 * @see IRubyValue
 * @since 2.0
 */

public interface IEvaluationResult {
	
	/**
	 * Returns the value representing the result of the
	 * evaluation, or <code>null</code> if the
	 * associated evaluation failed. If
	 * the associated evaluation failed, there will
	 * be problems, or an exception in this result.
	 *
	 * @return the resulting value, possibly
	 * <code>null</code>
	 */
	public IValue getValue();
	
	/**
	 * Returns whether the evaluation had any problems
	 * or if an exception occurred while performing the
	 * evaluation.
	 *
	 * @return whether there were any problems.
	 * @see #getErrors()
	 * @see #getException()
	 */
	public boolean hasErrors();
			
	/**
	 * Returns an array of problem messages. Each message describes a problem that
	 * occurred while compiling the snippet.
	 *
	 * @return compilation error messages, or an empty array if no errors occurred
	 * @since 2.1
	 */
	public String[] getErrorMessages();
		
	/**
	 * Returns the snippet that was evaluated.
	 *
	 * @return The string code snippet.
	 */
	public String getSnippet();
	
	/**
	 * Returns any exception that occurred while performing the evaluation
	 * or <code>null</code> if an exception did not occur.
	 * The exception will be a debug exception or a debug exception
	 * that wrappers a JDI exception that indicates a problem communicating
	 * with the target or with actually performing some action in the target.
	 *
	 * @return The exception that occurred during the evaluation
	 * @see com.sun.jdi.InvocationException
	 * @see org.eclipse.debug.core.DebugException
	 */
	public DebugException getException();
	
	/**
	 * Returns the thread in which the evaluation was performed.
	 * 
	 * @return the thread in which the evaluation was performed
	 */
	public IThread getThread();
}
