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
package org.rubypeople.rdt.internal.ui.packageview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.ui.RubyUI;

class GotoTypeAction extends Action {
	
	private PackageExplorerPart fPackageExplorer;
	
	GotoTypeAction(PackageExplorerPart part) {
		super();
		setText(PackagesMessages.GotoType_action_label); 
		setDescription(PackagesMessages.GotoType_action_description); 
		fPackageExplorer= part;
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.GOTO_TYPE_ACTION);
	}

	public void run() {
		Shell shell= RubyPlugin.getActiveWorkbenchShell();
		SelectionDialog dialog= null;
		try {
			dialog= RubyUI.createTypeDialog(shell, new ProgressMonitorDialog(shell),
				SearchEngine.createWorkspaceScope(), IRubySearchConstants.TYPE, false);
		} catch (RubyModelException e) {
			String title= getDialogTitle();
			String message= PackagesMessages.GotoType_error_message; 
			ExceptionHandler.handle(e, title, message);
			return;
		}
	
		dialog.setTitle(getDialogTitle());
		dialog.setMessage(PackagesMessages.GotoType_dialog_message); 
		if (dialog.open() == IDialogConstants.CANCEL_ID) {
			return;
		}
		
		Object[] types= dialog.getResult();
		if (types != null && types.length > 0) {
			gotoType((IType) types[0]);
		}
	}
	
	private void gotoType(IType type) {
		IRubyScript cu= (IRubyScript) type.getAncestor(IRubyElement.SCRIPT);
		IRubyElement element= null;
		if (cu != null) {
			element= cu.getPrimary();
		}
		if (element != null) {
			PackageExplorerPart view= PackageExplorerPart.openInActivePerspective();
			if (view != null) {
				view.selectReveal(new StructuredSelection(element));
				if (!element.equals(getSelectedElement(view))) {
					MessageDialog.openInformation(fPackageExplorer.getSite().getShell(), 
						getDialogTitle(), 
						Messages.format(PackagesMessages.PackageExplorer_element_not_present, element.getElementName())); 
				}
			}
		}
	}
	
	private Object getSelectedElement(PackageExplorerPart view) {
		return ((IStructuredSelection)view.getSite().getSelectionProvider().getSelection()).getFirstElement();
	}	
	
	private String getDialogTitle() {
		return PackagesMessages.GotoType_dialog_title; 
	}
}
