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
package org.rubypeople.rdt.ui.text.ruby.hover;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.ui.IEditorPart;

/**
 * Provides a hover popup which appears on top of an editor with relevant
 * display information. If the text hover does not provide information no hover
 * popup is shown.
 * <p>
 * Clients may implement this interface.
 * </p>
 * 
 * @see org.eclipse.ui.IEditorPart
 * @see org.eclipse.jface.text.ITextHover
 * 
 * @since 1.0
 */
public interface IRubyEditorTextHover extends ITextHover {

	/**
	 * Sets the editor on which the hover is shown.
	 * 
	 * @param editor
	 *            the editor on which the hover popup should be shown
	 */
	void setEditor(IEditorPart editor);

}
