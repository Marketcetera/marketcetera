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
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.IUIConstants;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;

public class VariableCreationDialog extends StatusDialog {
		
	private IDialogSettings fDialogSettings;
	
	private StringDialogField fNameField;
	private StatusInfo fNameStatus;	
	
	private StringButtonDialogField fPathField;
	private StatusInfo fPathStatus;
	private SelectionButtonDialogField fDirButton;
		
	private CPVariableElement fElement;
	
	private List fExistingNames;
		
	public VariableCreationDialog(Shell parent, CPVariableElement element, List existingNames) {
		super(parent);
		if (element == null) {
			setTitle(NewWizardMessages.VariableCreationDialog_titlenew); 
		} else {
			setTitle(NewWizardMessages.VariableCreationDialog_titleedit); 
		}
		
		fDialogSettings= RubyPlugin.getDefault().getDialogSettings();
		
		fElement= element;
		
		fNameStatus= new StatusInfo();
		fPathStatus= new StatusInfo();
		
		NewVariableAdapter adapter= new NewVariableAdapter();
		fNameField= new StringDialogField();
		fNameField.setDialogFieldListener(adapter);
		fNameField.setLabelText(NewWizardMessages.VariableCreationDialog_name_label); 

		fPathField= new StringButtonDialogField(adapter);
		fPathField.setDialogFieldListener(adapter);
		fPathField.setLabelText(NewWizardMessages.VariableCreationDialog_path_label); 
		fPathField.setButtonLabel(NewWizardMessages.VariableCreationDialog_path_file_button); 
		
		fDirButton= new SelectionButtonDialogField(SWT.PUSH);
		fDirButton.setDialogFieldListener(adapter);
		fDirButton.setLabelText(NewWizardMessages.VariableCreationDialog_path_dir_button); 
		
		fExistingNames= existingNames;
		
		if (element != null) {
			fNameField.setText(element.getName());
			fPathField.setText(element.getPath().toString());
			fExistingNames.remove(element.getName());
		} else {
			fNameField.setText(""); //$NON-NLS-1$
			fPathField.setText(""); //$NON-NLS-1$
		}
	}
	
	/*
	 * @see Windows#configureShell
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IRubyHelpContextIds.VARIABLE_CREATION_DIALOG);
	}
	

	public CPVariableElement getClasspathElement() {
		return new CPVariableElement(fNameField.getText(), new IPath[] {new Path(fPathField.getText())}, false);
	}

	/*
	 * @see Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */	
	protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite) super.createDialogArea(parent);
				
		Composite inner= new Composite(composite, SWT.NONE);
		inner.setFont(composite.getFont());
		
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		layout.numColumns= 3;
		inner.setLayout(layout);
		
		int fieldWidthHint= convertWidthInCharsToPixels(50);
		
		fNameField.doFillIntoGrid(inner, 2);
		LayoutUtil.setWidthHint(fNameField.getTextControl(null), fieldWidthHint);
		LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		
		DialogField.createEmptySpace(inner, 1);
		
		fPathField.doFillIntoGrid(inner, 3);
		LayoutUtil.setWidthHint(fPathField.getTextControl(null), fieldWidthHint);
		
		DialogField.createEmptySpace(inner, 2);
		fDirButton.doFillIntoGrid(inner, 1);
		
		DialogField focusField= (fElement == null) ? fNameField : fPathField;
		focusField.postSetFocusOnDialogField(parent.getDisplay());
		applyDialogFont(composite);		
		return composite;
	}

		
	// -------- NewVariableAdapter --------

	private class NewVariableAdapter implements IDialogFieldListener, IStringButtonAdapter {
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			doFieldUpdated(field);
		}
		
		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			doChangeControlPressed(field);
		}
	}
	
	private void doChangeControlPressed(DialogField field) {
		if (field == fPathField) {
			IPath path= chooseExtJarFile();
			if (path != null) {
				fPathField.setText(path.toString());
			}
		}
	}
	
	private void doFieldUpdated(DialogField field) {	
		if (field == fNameField) {
			fNameStatus= nameUpdated();
		} else if (field == fPathField) {
			fPathStatus= pathUpdated();
		} else if (field == fDirButton) {
			IPath path= chooseExtDirectory();
			if (path != null) {
				fPathField.setText(path.toString());
			}
		}		
		updateStatus(StatusUtil.getMoreSevere(fPathStatus, fNameStatus));
	}		
	
	private StatusInfo nameUpdated() {
		StatusInfo status= new StatusInfo();
		String name= fNameField.getText();
		if (name.length() == 0) {
			status.setError(NewWizardMessages.VariableCreationDialog_error_entername); 
			return status;
		}
		if (name.trim().length() != name.length()) {
			status.setError(NewWizardMessages.VariableCreationDialog_error_whitespace); 
		} else if (!Path.ROOT.isValidSegment(name)) {
			status.setError(NewWizardMessages.VariableCreationDialog_error_invalidname); 
		} else if (nameConflict(name)) {
			status.setError(NewWizardMessages.VariableCreationDialog_error_nameexists); 
		}
		return status;
	}
	
	private boolean nameConflict(String name) {
		if (fElement != null && fElement.getName().equals(name)) {
			return false;
		}
		for (int i= 0; i < fExistingNames.size(); i++) {
			CPVariableElement elem= (CPVariableElement)fExistingNames.get(i);
			if (name.equals(elem.getName())){
				return true;
			}
		}
		return false;
	}
	
	
	private StatusInfo pathUpdated() {
		StatusInfo status= new StatusInfo();
		
		String path= fPathField.getText();
		if (path.length() > 0) { // empty path is ok
			if (!Path.ROOT.isValidPath(path)) {
				status.setError(NewWizardMessages.VariableCreationDialog_error_invalidpath); 
			} else if (!new File(path).exists()) {
				status.setWarning(NewWizardMessages.VariableCreationDialog_warning_pathnotexists); 
			}
		}
		return status;
	}
	
	
	private String getInitPath() {
		String initPath= fPathField.getText();
		if (initPath.length() == 0) {		
			initPath= fDialogSettings.get(IUIConstants.DIALOGSTORE_LASTEXTJAR);
			if (initPath == null) {
				initPath= ""; //$NON-NLS-1$
			}
//		} else {
//			IPath entryPath= new Path(initPath);
//			if (ArchiveFileFilter.isArchivePath(entryPath)) {
//				entryPath.removeLastSegments(1);
//			}
//			initPath= entryPath.toOSString();
		}
		return initPath;
	}		
	
	
	/*
	 * Open a dialog to choose a jar from the file system
	 */
	private IPath chooseExtJarFile() {
		String initPath= getInitPath();
		
		FileDialog dialog= new FileDialog(getShell());
		dialog.setText(NewWizardMessages.VariableCreationDialog_extjardialog_text); 
		dialog.setFilterExtensions(new String[] {"*.jar;*.zip"}); //$NON-NLS-1$
		dialog.setFilterPath(initPath);
		String res= dialog.open();
		if (res != null) {
			fDialogSettings.put(IUIConstants.DIALOGSTORE_LASTEXTJAR, dialog.getFilterPath());
			return Path.fromOSString(res).makeAbsolute();
		}
		return null;
	}
	
	private IPath chooseExtDirectory() {
		String initPath= getInitPath();
		
		DirectoryDialog dialog= new DirectoryDialog(getShell());
		dialog.setText(NewWizardMessages.VariableCreationDialog_extdirdialog_text); 
		dialog.setMessage(NewWizardMessages.VariableCreationDialog_extdirdialog_message); 
		dialog.setFilterPath(initPath);
		String res= dialog.open();
		if (res != null) {
			fDialogSettings.put(IUIConstants.DIALOGSTORE_LASTEXTJAR, dialog.getFilterPath());
			return Path.fromOSString(res);
		}
		return null;		
	}
	
		
	
}
