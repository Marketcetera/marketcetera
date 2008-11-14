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
package org.rubypeople.rdt.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.dialogs.OpenTypeSelectionDialog2;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;

public class OpenTypeAction extends Action implements IWorkbenchWindowActionDelegate {
	
	public OpenTypeAction() {
		super();
		setText(RubyUIMessages.OpenTypeAction_label); 
		setDescription(RubyUIMessages.OpenTypeAction_description); 
		setToolTipText(RubyUIMessages.OpenTypeAction_tooltip); 
		setImageDescriptor(RubyPluginImages.DESC_TOOL_OPENTYPE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.OPEN_TYPE_ACTION);
	}

	public void run() {
		Shell parent= RubyPlugin.getActiveWorkbenchShell();
		OpenTypeSelectionDialog2 dialog= new OpenTypeSelectionDialog2(parent, false, 
			PlatformUI.getWorkbench().getProgressService(),
			null, IRubySearchConstants.TYPE);
		dialog.setTitle(RubyUIMessages.OpenTypeAction_dialogTitle); 
		dialog.setMessage(RubyUIMessages.OpenTypeAction_dialogMessage); 
		
		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;
		
		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IType type= (IType)types[0];
			try {
				IEditorPart part= EditorUtility.openInEditor(type, true);
				EditorUtility.revealInEditor(part, type);
			} catch (CoreException x) {
				String title= RubyUIMessages.OpenTypeAction_errorTitle; 
				String message= RubyUIMessages.OpenTypeAction_errorMessage; 
				ExceptionHandler.handle(x, title, message);
			}
		}
	}

	//---- IWorkbenchWindowActionDelegate ------------------------------------------------

	public void run(IAction action) {
		run();
	}
	
	public void dispose() {
		// do nothing.
	}
	
	public void init(IWorkbenchWindow window) {
		// do nothing.
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
}