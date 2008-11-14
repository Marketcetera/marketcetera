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
package org.rubypeople.rdt.internal.ui.preferences;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.OptionalMessageDialog;
import org.rubypeople.rdt.internal.ui.util.SWTUtil;
import org.rubypeople.rdt.ui.PreferenceConstants;
	
/*
 * The page for setting general ruby plugin preferences.
 * See PreferenceConstants to access or change these values through public API.
 */
public class RubyBasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String DOUBLE_CLICK= PreferenceConstants.DOUBLE_CLICK;
	private static final String DOUBLE_CLICK_GOES_INTO= PreferenceConstants.DOUBLE_CLICK_GOES_INTO;
	private static final String DOUBLE_CLICK_EXPANDS= PreferenceConstants.DOUBLE_CLICK_EXPANDS;

	private ArrayList fCheckBoxes;
	private ArrayList fRadioButtons;
	private ArrayList fTextControls;
	
	public RubyBasePreferencePage() {
		super();
		setPreferenceStore(RubyPlugin.getDefault().getPreferenceStore());
		setDescription(PreferencesMessages.RubyBasePreferencePage_description); 
	
		fRadioButtons= new ArrayList();
		fCheckBoxes= new ArrayList();
		fTextControls= new ArrayList();
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}		
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IRubyHelpContextIds.JAVA_BASE_PREFERENCE_PAGE);
	}	

	private Button addRadioButton(Composite parent, String label, String key, String value) { 
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		Button button= new Button(parent, SWT.RADIO);
		button.setText(label);
		button.setData(new String[] { key, value });
		button.setLayoutData(gd);

		button.setSelection(value.equals(getPreferenceStore().getString(key)));
		
		fRadioButtons.add(button);
		return button;
	}
	
	private Button addCheckBox(Composite parent, String label, String key) { 
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		Button button= new Button(parent, SWT.CHECK);
		button.setText(label);
		button.setData(key);
		button.setLayoutData(gd);

		button.setSelection(getPreferenceStore().getBoolean(key));
		
		fCheckBoxes.add(button);
		return button;
	}
	
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite result= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.verticalSpacing= convertVerticalDLUsToPixels(10);
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		result.setLayout(layout);

		Group doubleClickGroup= new Group(result, SWT.NONE);
		doubleClickGroup.setLayout(new GridLayout());		
		doubleClickGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		doubleClickGroup.setText(PreferencesMessages.RubyBasePreferencePage_doubleclick_action);  
		addRadioButton(doubleClickGroup, PreferencesMessages.RubyBasePreferencePage_doubleclick_gointo, DOUBLE_CLICK, DOUBLE_CLICK_GOES_INTO); 
		addRadioButton(doubleClickGroup, PreferencesMessages.RubyBasePreferencePage_doubleclick_expand, DOUBLE_CLICK, DOUBLE_CLICK_EXPANDS); 

		Group group= new Group(result, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(PreferencesMessages.RubyBasePreferencePage_search); 
		
		addCheckBox(group, PreferencesMessages.RubyBasePreferencePage_search_small_menu, PreferenceConstants.SEARCH_USE_REDUCED_MENU); 

		
		layout= new GridLayout();
		layout.numColumns= 2;
		
		Group dontAskGroup= new Group(result, SWT.NONE);
		dontAskGroup.setLayout(layout);
		dontAskGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dontAskGroup.setText(PreferencesMessages.RubyBasePreferencePage_dialogs); 
		
		Label label= new Label(dontAskGroup, SWT.WRAP);
		label.setText(PreferencesMessages.RubyBasePreferencePage_do_not_hide_description);
		GridData data= new GridData(GridData.FILL, GridData.CENTER, true, false);
		data.widthHint= convertVerticalDLUsToPixels(50);
		label.setLayoutData(data);
		
		Button clearButton= new Button(dontAskGroup, SWT.PUSH);
		clearButton.setText(PreferencesMessages.RubyBasePreferencePage_do_not_hide_button);
		clearButton.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));
		clearButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				unhideAllDialogs();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				unhideAllDialogs();
			}
		});
		
		
		SWTUtil.setButtonDimensionHint(clearButton);
		Dialog.applyDialogFont(result);
		return result;
	}
	
	protected final void unhideAllDialogs() {
		OptionalMessageDialog.clearAllRememberedStates();
		MessageDialog.openInformation(getShell(), PreferencesMessages.RubyBasePreferencePage_do_not_hide_dialog_title, PreferencesMessages.RubyBasePreferencePage_do_not_hide_dialog_message);
	}

	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			button.setSelection(store.getDefaultBoolean(key));
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			String[] info= (String[]) button.getData();
			button.setSelection(info[1].equals(store.getDefaultString(info[0])));
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			text.setText(store.getDefaultString(key));
		}
		super.performDefaults();
	}

	/*
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore store= getPreferenceStore();
		for (int i= 0; i < fCheckBoxes.size(); i++) {
			Button button= (Button) fCheckBoxes.get(i);
			String key= (String) button.getData();
			store.setValue(key, button.getSelection());
		}
		for (int i= 0; i < fRadioButtons.size(); i++) {
			Button button= (Button) fRadioButtons.get(i);
			if (button.getSelection()) {
				String[] info= (String[]) button.getData();
				store.setValue(info[0], info[1]);
			}
		}
		for (int i= 0; i < fTextControls.size(); i++) {
			Text text= (Text) fTextControls.get(i);
			String key= (String) text.getData();
			store.setValue(key, text.getText());
		}
		
		RubyPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}


}


