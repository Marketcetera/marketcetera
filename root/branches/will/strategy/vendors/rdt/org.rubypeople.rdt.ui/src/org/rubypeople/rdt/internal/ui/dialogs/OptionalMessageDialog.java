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
package org.rubypeople.rdt.internal.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;

/**
 * This is a <code>MessageDialog</code> which allows the user
 * to choose that the dialog isn't shown again the next time.
 */ 
public class OptionalMessageDialog extends MessageDialog {
	
	// String constants for widgets
	private static final String CHECKBOX_TEXT= RubyUIMessages.OptionalMessageDialog_dontShowAgain;

	// Dialog store id constants
	private static final String STORE_ID= "OptionalMessageDialog.hide."; //$NON-NLS-1$

	public static final int NOT_SHOWN= IDialogConstants.CLIENT_ID + 1;
	
	private Button fHideDialogCheckBox;
	private String fId;

	/**
	 * Opens the dialog but only if the user hasn't choosen to hide it.
	 * Returns <code>NOT_SHOWN</code> if the dialog was not shown.
	 */
	public static int open(String id, Shell parent, String title, Image titleImage, String message, int dialogType, String[] buttonLabels, int defaultButtonIndex) {
		if (!isDialogEnabled(id))
			return OptionalMessageDialog.NOT_SHOWN;
		
		MessageDialog dialog= new OptionalMessageDialog(id, parent, title, titleImage, message, dialogType, buttonLabels, defaultButtonIndex);
		return dialog.open();
	}

	protected OptionalMessageDialog(String id, Shell parent, String title, Image titleImage, String message, int dialogType, String[] buttonLabels, int defaultButtonIndex) {
		super(parent, title, titleImage, message, dialogType, buttonLabels, defaultButtonIndex);
		fId= id;
	}

	protected Control createCustomArea(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		fHideDialogCheckBox= new Button(composite, SWT.CHECK | SWT.LEFT);
		fHideDialogCheckBox.setText(CHECKBOX_TEXT);
		fHideDialogCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDialogEnabled(fId, !((Button)e.widget).getSelection());
			}
		});
		applyDialogFont(fHideDialogCheckBox);
		return fHideDialogCheckBox;
	}

	//--------------- Configuration handling --------------
	
	/**
	 * Returns this dialog
	 * 
	 * @return the settings to be used
	 */
	private static IDialogSettings getDialogSettings() {
		IDialogSettings settings= RubyPlugin.getDefault().getDialogSettings();
		settings= settings.getSection(STORE_ID);
		if (settings == null)
			settings= RubyPlugin.getDefault().getDialogSettings().addNewSection(STORE_ID);
		return settings;
	}
		
	/**
	 * Answers whether the optional dialog is enabled and should be shown.
	 */
	public static boolean isDialogEnabled(String key) {
		IDialogSettings settings= getDialogSettings();
		return !settings.getBoolean(key);
	}
	
	/**
	 * Sets whether the optional dialog is enabled and should be shown.
	 */
	public static void setDialogEnabled(String key, boolean isEnabled) {
		IDialogSettings settings= getDialogSettings();
		settings.put(key, !isEnabled);
	}

		/**
	 * Clears all remembered information about hidden dialogs
	 */
	public static void clearAllRememberedStates() {
		IDialogSettings settings= RubyPlugin.getDefault().getDialogSettings();
		settings.addNewSection(STORE_ID);
	}
}
