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
package org.rubypeople.rdt.internal.ui.infoviews;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.core.ICodeAssist;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

/**
 * Helper class to convert text selections to Ruby elements.
 *
 * @since 3.0
 */
class TextSelectionConverter {

	/** Empty result. */
	private static final IRubyElement[] EMPTY_RESULT= new IRubyElement[0];

	/** Prevent instance creation. */
	private TextSelectionConverter() {
	}

	/**
	 * Finds and returns the Ruby elements for the given editor selection.
	 *
	 * @param editor the Ruby editor
	 * @param selection the text selection
	 * @return	the Ruby elements for the given editor selection
	 * @throws RubyModelException
	 */
	public static IRubyElement[] codeResolve(RubyEditor editor, ITextSelection selection) throws RubyModelException {
		return codeResolve(getInput(editor), selection);
	}

	/**
	 * Finds and returns the Ruby element that contains the
	 * text selection in the given editor.
	 *
	 * @param editor the Ruby editor
	 * @param selection the text selection
	 * @return	the Ruby elements for the given editor selection
	 * @throws RubyModelException
	 */
	public static IRubyElement getElementAtOffset(RubyEditor editor, ITextSelection selection) throws RubyModelException {
		return getElementAtOffset(getInput(editor), selection);
	}

	//-------------------- Helper methods --------------------

	private static IRubyElement getInput(RubyEditor editor) {
		if (editor == null)
			return null;
		IEditorInput input= editor.getEditorInput();
		IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();
		return manager.getWorkingCopy(input);
	}

	private static IRubyElement[] codeResolve(IRubyElement input, ITextSelection selection) throws RubyModelException {
			if (input instanceof ICodeAssist) {
				if (input instanceof IRubyScript) {
					IRubyScript cunit= (IRubyScript)input;
					if (cunit.isWorkingCopy())
						RubyModelUtil.reconcile(cunit);
				}
				IRubyElement[] elements= ((ICodeAssist)input).codeSelect(selection.getOffset(), selection.getLength());
				if (elements != null && elements.length > 0)
					return elements;
			}
			return EMPTY_RESULT;
	}

	private static IRubyElement getElementAtOffset(IRubyElement input, ITextSelection selection) throws RubyModelException {
		if (input instanceof IRubyScript) {
			IRubyScript cunit= (IRubyScript)input;
			if (cunit.isWorkingCopy())
				RubyModelUtil.reconcile(cunit);
			IRubyElement ref= cunit.getElementAt(selection.getOffset());
			if (ref == null)
				return input;
			else
				return ref;
		} 
		return null;
	}
}
