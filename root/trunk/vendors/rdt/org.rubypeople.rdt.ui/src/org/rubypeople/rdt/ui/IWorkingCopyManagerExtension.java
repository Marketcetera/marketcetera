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

package org.rubypeople.rdt.ui;

import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.core.IRubyScript;

/**
 * Extension interface for {@link IWorkingCopyManager}.
 * @since 2.1
 */
public interface IWorkingCopyManagerExtension {
	
	/**
	 * Sets the given working copy for the given editor input. If the given editor input
	 * is not connected to this working copy manager, this call has no effect. <p>
	 * This working copy manager does not assume the ownership of this working copy, i.e.,
	 * the given working copy is not automatically be freed when this manager is shut down.
	 * 
	 * @param input the editor input
	 * @param workingCopy the working copy
	 */
	void setWorkingCopy(IEditorInput input, IRubyScript workingCopy);
	
	/**
	 * Removes the working copy set for the given editor input. If there is no
	 * working copy set for this input or this input is not connected to this
	 * working copy manager, this call has no effect.
	 * 
	 * @param input the editor input
	 */
	void removeWorkingCopy(IEditorInput input);
}
