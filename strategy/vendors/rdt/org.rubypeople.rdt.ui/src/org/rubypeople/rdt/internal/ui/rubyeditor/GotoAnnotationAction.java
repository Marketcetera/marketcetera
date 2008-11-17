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
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;


public class GotoAnnotationAction extends TextEditorAction {
		
	private boolean fForward;
	
	public GotoAnnotationAction(String prefix, boolean forward) {
		super(RubyEditorMessages.getResourceBundle(), prefix, null);
		fForward= forward;
		if (forward) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.GOTO_NEXT_ERROR_ACTION);
	    } else {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.GOTO_PREVIOUS_ERROR_ACTION);
		}
	}
	
	public void run() {
		RubyEditor e= (RubyEditor) getTextEditor();
		e.gotoAnnotation(fForward);
	}
	
	public void setEditor(ITextEditor editor) {
		if (editor instanceof RubyEditor) 
			super.setEditor(editor);
		update();
	}
	
	public void update() {
		setEnabled(getTextEditor() instanceof RubyEditor);
	}
}
