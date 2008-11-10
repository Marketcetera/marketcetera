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
package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.OpenTypeSelectionDialog2;
import org.rubypeople.rdt.internal.ui.util.OpenTypeHierarchyUtil;

public class OpenTypeInHierarchyAction extends Action implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow fWindow;
	
	public OpenTypeInHierarchyAction() {
		super();
		setText(ActionMessages.OpenTypeInHierarchyAction_label); 
		setDescription(ActionMessages.OpenTypeInHierarchyAction_description); 
		setToolTipText(ActionMessages.OpenTypeInHierarchyAction_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.OPEN_TYPE_IN_HIERARCHY_ACTION);
	}

	public void run() {
		Shell parent= RubyPlugin.getActiveWorkbenchShell();
		OpenTypeSelectionDialog2 dialog= new OpenTypeSelectionDialog2(parent, false, 
			PlatformUI.getWorkbench().getProgressService(), 
			SearchEngine.createWorkspaceScope(), IRubySearchConstants.TYPE);
		
		dialog.setTitle(ActionMessages.OpenTypeInHierarchyAction_dialogTitle); 
		dialog.setMessage(ActionMessages.OpenTypeInHierarchyAction_dialogMessage); 
		int result= dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;
		
		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			IType type= (IType)types[0];
			OpenTypeHierarchyUtil.open(new IType[] { type }, fWindow);
		}
	}

	//---- IWorkbenchWindowActionDelegate ------------------------------------------------

	public void run(IAction action) {
		run();
	}
	
	public void dispose() {
		fWindow= null;
	}
	
	public void init(IWorkbenchWindow window) {
		fWindow= window;
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
}
