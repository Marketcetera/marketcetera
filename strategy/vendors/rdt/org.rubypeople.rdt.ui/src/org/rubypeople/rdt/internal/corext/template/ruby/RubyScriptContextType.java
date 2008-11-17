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
package org.rubypeople.rdt.internal.corext.template.ruby;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.rubypeople.rdt.core.IRubyScript;

/**
 * A very simple context type.
 */
public abstract class RubyScriptContextType extends TemplateContextType {
	/**
	 * Creates a new Ruby context type.
	 */
	public RubyScriptContextType(String name) {
		super(name);

	}

	public abstract RubyScriptContext createContext(IDocument document,
			int completionPosition, int length, IRubyScript script);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.corext.template.ContextType#validateVariables(org.eclipse.jdt.internal.corext.template.TemplateVariable[])
	 */
	protected void validateVariables(TemplateVariable[] variables)
			throws TemplateException {
		// check for multiple cursor variables
		for (int i = 0; i < variables.length; i++) {
			TemplateVariable var = variables[i];
			if (var.getType().equals(GlobalTemplateVariables.Cursor.NAME)) {
				if (var.getOffsets().length > 1) {
					throw new TemplateException(
							RubyTemplateMessages.ContextType_error_multiple_cursor_variables);
				}
			}
		}
	}

}
