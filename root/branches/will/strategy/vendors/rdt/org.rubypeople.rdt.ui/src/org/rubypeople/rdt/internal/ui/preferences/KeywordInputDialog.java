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
package org.rubypeople.rdt.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.internal.ui.dialogs.StatusDialog;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.preferences.KeywordConfigurationBlock.Keyword;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;

/**
 * Dialog to enter a new keyword
 */
public class KeywordInputDialog extends StatusDialog {
	
	private class KeywordInputAdapter implements IDialogFieldListener {
		public void dialogFieldChanged(DialogField field) {
			doValidation();
		}			
	}
	
	private StringDialogField fNameDialogField;
	
	private List fExistingNames;
		
	public KeywordInputDialog(Shell parent, Keyword task, List existingEntries) {
		super(parent);
		
		fExistingNames= new ArrayList(existingEntries.size());
		for (int i= 0; i < existingEntries.size(); i++) {
			Keyword curr= (Keyword) existingEntries.get(i);
			if (!curr.equals(task)) {
				fExistingNames.add(curr.name);
			}
		}
		
		if (task == null) {
			setTitle(PreferencesMessages.KeywordInputDialog_new_title); 
		} else {
			setTitle(PreferencesMessages.KeywordInputDialog_edit_title); 
		}

		KeywordInputAdapter adapter= new KeywordInputAdapter();

		fNameDialogField= new StringDialogField();
		fNameDialogField.setLabelText(PreferencesMessages.KeywordInputDialog_name_label); 
		fNameDialogField.setDialogFieldListener(adapter);
		
		fNameDialogField.setText((task != null) ? task.name : ""); //$NON-NLS-1$
	}
	
	public Keyword getResult() {
		Keyword task= new Keyword();
		task.name= fNameDialogField.getText().trim();
		return task;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite composite= (Composite) super.createDialogArea(parent);
		
		Composite inner= new Composite(composite, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns= 2;
		inner.setLayout(layout);
		
		fNameDialogField.doFillIntoGrid(inner, 2);
		
		LayoutUtil.setHorizontalGrabbing(fNameDialogField.getTextControl(null));
		LayoutUtil.setWidthHint(fNameDialogField.getTextControl(null), convertWidthInCharsToPixels(45));
		
		fNameDialogField.postSetFocusOnDialogField(parent.getDisplay());
		
		applyDialogFont(composite);		
		return composite;
	}
		
	private void doValidation() {
		StatusInfo status= new StatusInfo();
		String newText= fNameDialogField.getText();
		if (newText.length() == 0) {
			status.setError(PreferencesMessages.KeywordInputDialog_error_enterName);
		} else {
			if (newText.indexOf(',') != -1) {
				status.setError(PreferencesMessages.KeywordInputDialog_error_comma); 
			} else if (fExistingNames.contains(newText)) {
				status.setError(PreferencesMessages.KeywordInputDialog_error_entryExists);
			} else if (Character.isWhitespace(newText.charAt(0)) ||  Character.isWhitespace(newText.charAt(newText.length() - 1))) {
				status.setError(PreferencesMessages.KeywordInputDialog_error_noSpace); 
			}
		}
		updateStatus(status);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// FIXME Uncomment for help context!
		//WorkbenchHelp.setHelp(newShell, IJavaHelpContextIds.TODO_TASK_INPUT_DIALOG);
	}
}
