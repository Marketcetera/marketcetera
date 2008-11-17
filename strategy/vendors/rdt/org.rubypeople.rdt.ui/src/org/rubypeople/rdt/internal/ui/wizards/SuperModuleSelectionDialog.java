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
package org.rubypeople.rdt.internal.ui.wizards;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.corext.util.TypeInfo;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.TypeSelectionDialog2;
import org.rubypeople.rdt.ui.wizards.NewTypeWizardPage;

public class SuperModuleSelectionDialog extends TypeSelectionDialog2 {
	
	private static final int ADD_ID= IDialogConstants.CLIENT_ID + 1;
	
	private NewTypeWizardPage fTypeWizardPage;
	private List fOldContent;
	
	public SuperModuleSelectionDialog(Shell parent, IRunnableContext context, NewTypeWizardPage page, IRubyProject p) {
		super(parent, true, context, createSearchScope(p), IRubySearchConstants.MODULE);
		fTypeWizardPage= page;
		// to restore the content of the dialog field if the dialog is canceled
		fOldContent= fTypeWizardPage.getSuperModules();
		setStatusLineAboveButtons(true);
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ADD_ID, NewWizardMessages.SuperModuleSelectionDialog_addButton_label, true); 
		super.createButtonsForButtonBar(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		return RubyPlugin.getDefault().getDialogSettingsSection("DialogBounds_SuperModuleSelectionDialog"); //$NON-NLS-1$
	}
	
	protected void updateButtonsEnableState(IStatus status) {
	    super.updateButtonsEnableState(status);
	    Button addButton = getButton(ADD_ID);
	    if (addButton != null && !addButton.isDisposed())
	        addButton.setEnabled(!status.matches(IStatus.ERROR));
	}
	
	protected void handleShellCloseEvent() {
		super.handleShellCloseEvent();
		// Handle the closing of the shell by selecting the close icon
		fTypeWizardPage.setSuperModules(fOldContent, true);
	}	

	protected void cancelPressed() {
		fTypeWizardPage.setSuperModules(fOldContent, true);
		super.cancelPressed();
	}
	
	protected void buttonPressed(int buttonId) {
		if (buttonId == ADD_ID){
			addSelectedInterface();
		}
		super.buttonPressed(buttonId);	
	}
	
	protected void okPressed() {
		addSelectedInterface();
		super.okPressed();
	}
		
	private void addSelectedInterface() {
		TypeInfo[] selection= getSelectedTypes();
		if (selection == null)
			return;
		for (int i= 0; i < selection.length; i++) {
			TypeInfo type= selection[i];
			String qualifiedName= type.getFullyQualifiedName();
			String message;
			if (fTypeWizardPage.addSuperModule(qualifiedName)) {
				message= Messages.format(NewWizardMessages.SuperModuleSelectionDialog_interfaceadded_info, qualifiedName); 
			} else {
				message= Messages.format(NewWizardMessages.SuperModuleSelectionDialog_interfacealreadyadded_info, qualifiedName); 
			}
			updateStatus(new StatusInfo(IStatus.INFO, message));
		}
	}

	private static IRubySearchScope createSearchScope(IRubyProject p) {
		return SearchEngine.createRubySearchScope(new IRubyProject[] { p });
	}
	
	protected void handleDefaultSelected(TypeInfo[] selection) {
		if (selection.length > 0)
			buttonPressed(ADD_ID);
	}
	
	protected void handleWidgetSelected(TypeInfo[] selection) {
		super.handleWidgetSelected(selection);
		getButton(ADD_ID).setEnabled(selection.length > 0);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IRubyHelpContextIds.SUPER_INTERFACE_SELECTION_DIALOG);
	}
}
