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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.text.ruby.ProposalSorterHandle;
import org.rubypeople.rdt.internal.ui.text.ruby.ProposalSorterRegistry;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * Configures the content assist preferences.
 * 
 * @since 3.0
 */
class CodeAssistConfigurationBlock extends OptionsConfigurationBlock {
	
	private static final Key PREF_CODEASSIST_AUTOACTIVATION= getRDTUIKey(PreferenceConstants.CODEASSIST_AUTOACTIVATION);
	private static final Key PREF_CODEASSIST_AUTOACTIVATION_DELAY= getRDTUIKey(PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY);
	private static final Key PREF_CODEASSIST_AUTOINSERT= getRDTUIKey(PreferenceConstants.CODEASSIST_AUTOINSERT);
	private static final Key PREF_CODEASSIST_SORTER= getRDTUIKey(PreferenceConstants.CODEASSIST_SORTER);
//	private static final Key PREF_CODEASSIST_CASE_SENSITIVITY= getRDTUIKey(PreferenceConstants.CODEASSIST_CASE_SENSITIVITY);
	private static final Key PREF_CODEASSIST_INSERT_COMPLETION= getRDTUIKey(PreferenceConstants.CODEASSIST_INSERT_COMPLETION);
	private static final Key PREF_CODEASSIST_FILL_ARGUMENT_NAMES= getRDTUIKey(PreferenceConstants.CODEASSIST_FILL_ARGUMENT_NAMES);
	private static final Key PREF_CODEASSIST_FILL_METHOD_BLOCK_ARGUMENTS= getRDTUIKey(PreferenceConstants.CODEASSIST_FILL_METHOD_BLOCK_ARGUMENTS);
	private static final Key PREF_CODEASSIST_PREFIX_COMPLETION= getRDTUIKey(PreferenceConstants.CODEASSIST_PREFIX_COMPLETION);
	private static final Key PREF_CODEASSIST_CAMEL_CASE_MATCH= getRDTCoreKey(RubyCore.CODEASSIST_CAMEL_CASE_MATCH);

	private static Key[] getAllKeys() {
		return new Key[] {
				PREF_CODEASSIST_AUTOACTIVATION,
				PREF_CODEASSIST_AUTOACTIVATION_DELAY,
				PREF_CODEASSIST_AUTOINSERT,
				PREF_CODEASSIST_SORTER,
//				PREF_CODEASSIST_CASE_SENSITIVITY,
				PREF_CODEASSIST_INSERT_COMPLETION,
				PREF_CODEASSIST_FILL_ARGUMENT_NAMES,
				PREF_CODEASSIST_FILL_METHOD_BLOCK_ARGUMENTS,
				PREF_CODEASSIST_PREFIX_COMPLETION,
				PREF_CODEASSIST_CAMEL_CASE_MATCH
		};	
	}
	
	private static final String[] trueFalse= new String[] { IPreferenceStore.TRUE, IPreferenceStore.FALSE };

	private Button fCompletionInsertsRadioButton;
	private Button fCompletionOverwritesRadioButton;

	public CodeAssistConfigurationBlock(IStatusChangeListener statusListener, IWorkbenchPreferenceContainer workbenchcontainer) {
		super(statusListener, null, getAllKeys(), workbenchcontainer);
	}

	protected Control createContents(Composite parent) {
		ScrolledPageContent scrolled= new ScrolledPageContent(parent, SWT.H_SCROLL | SWT.V_SCROLL);
//		scrolled.setDelayedReflow(true);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		
		Composite control= new Composite(scrolled, SWT.NONE);
		GridLayout layout= new GridLayout();
		control.setLayout(layout);

		Composite composite;

		composite= createSubsection(control, PreferencesMessages.CodeAssistConfigurationBlock_insertionSection_title); 
		addInsertionSection(composite);
		
		composite= createSubsection(control, PreferencesMessages.CodeAssistConfigurationBlock_sortingSection_title); 
		addSortingSection(composite);
		
		composite= createSubsection(control, PreferencesMessages.CodeAssistConfigurationBlock_autoactivationSection_title); 
		addAutoActivationSection(composite);
		
		initialize();
		
		scrolled.setContent(control);
		final Point size= control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolled.setMinSize(size.x, size.y);
		return scrolled;
	}

	protected Composite createSubsection(Composite parent, String label) {
		Group group= new Group(parent, SWT.SHADOW_NONE);
		group.setText(label);
		GridData data= new GridData(SWT.FILL, SWT.CENTER, true, false);
		group.setLayoutData(data);
		GridLayout layout= new GridLayout();
		layout.numColumns= 3;
		group.setLayout(layout);

		return group;
	}

	private void addInsertionSection(Composite composite) {
		addCompletionRadioButtons(composite);
		
		String label;		
		label= PreferencesMessages.RubyEditorPreferencePage_insertSingleProposalsAutomatically;
		addCheckBox(composite, label, PREF_CODEASSIST_AUTOINSERT, trueFalse, 0);
		
		label= PreferencesMessages.RubyEditorPreferencePage_completePrefixes; 
		addCheckBox(composite, label, PREF_CODEASSIST_PREFIX_COMPLETION, trueFalse, 0);
		
		label= PreferencesMessages.RubyEditorPreferencePage_fillArgumentNamesOnMethodCompletion; 
		Button master= addCheckBox(composite, label, PREF_CODEASSIST_FILL_ARGUMENT_NAMES, trueFalse, 0);
		
		label= PreferencesMessages.RubyEditorPreferencePage_fillBlockArgumentNamesOnMethodCompletion; 
		Button slave= addCheckBox(composite, label, PREF_CODEASSIST_FILL_METHOD_BLOCK_ARGUMENTS, trueFalse, 20);
		createSelectionDependency(master, slave);
	}

	/**
	 * Creates a selection dependency between a master and a slave control.
	 * 
	 * @param master
	 *                   The master button that controls the state of the slave
	 * @param slave
	 *                   The slave control that is enabled only if the master is
	 *                   selected
	 */
	protected static void createSelectionDependency(final Button master, final Control slave) {

		master.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent event) {
				// Do nothing
			}

			public void widgetSelected(SelectionEvent event) {
				slave.setEnabled(master.getSelection());
			}
		});
		slave.setEnabled(master.getSelection());
	}
	
	private void addSortingSection(Composite composite) {
		String label;
		label= PreferencesMessages.RubyEditorPreferencePage_presentProposalsInAlphabeticalOrder;
		ProposalSorterHandle[] sorters= ProposalSorterRegistry.getDefault().getSorters();
		String[] labels= new String[sorters.length];
		String[] values= new String[sorters.length];
		for (int i= 0; i < sorters.length; i++) {
			ProposalSorterHandle handle= sorters[i];
			labels[i]= handle.getName();
			values[i]= handle.getId();
		}
		
		addComboBox(composite, label, PREF_CODEASSIST_SORTER, values, labels, 0);
		
		label= PreferencesMessages.CodeAssistConfigurationBlock_restricted_link;

		String[] enabledDisabled= new String[] { RubyCore.ENABLED, RubyCore.DISABLED };
		
		label= PreferencesMessages.CodeAssistConfigurationBlock_matchCamelCase_label;
		addCheckBox(composite, label, PREF_CODEASSIST_CAMEL_CASE_MATCH, enabledDisabled, 0);
	}
	
	private void addAutoActivationSection(Composite composite) {
		String label;
		label= PreferencesMessages.RubyEditorPreferencePage_enableAutoActivation; 
		final Button autoactivation= addCheckBox(composite, label, PREF_CODEASSIST_AUTOACTIVATION, trueFalse, 0);
		autoactivation.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				updateAutoactivationControls();
			}
		});		
		
		label= PreferencesMessages.RubyEditorPreferencePage_autoActivationDelay; 
		addLabelledTextField(composite, label, PREF_CODEASSIST_AUTOACTIVATION_DELAY, 4, 0, true);
	}
	
	protected Text addLabelledTextField(Composite parent, String label, Key key, int textlimit, int indent, boolean dummy) {	
		PixelConverter pixelConverter= new PixelConverter(parent);
		
		Label labelControl= new Label(parent, SWT.WRAP);
		labelControl.setText(label);
		labelControl.setLayoutData(new GridData());
				
		Text textBox= new Text(parent, SWT.BORDER | SWT.SINGLE);
		textBox.setData(key);
		textBox.setLayoutData(new GridData());
		
		fLabels.put(textBox, labelControl);
		
		String currValue= getValue(key);	
		if (currValue != null) {
			textBox.setText(currValue);
		}
		textBox.addModifyListener(getTextModifyListener());

		GridData data= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		if (textlimit != 0) {
			textBox.setTextLimit(textlimit);
			data.widthHint= pixelConverter.convertWidthInCharsToPixels(textlimit + 1);
		}
		data.horizontalIndent= indent;
		data.horizontalSpan= 2;
		textBox.setLayoutData(data);

		fTextBoxes.add(textBox);
		return textBox;
	}

	private void addCompletionRadioButtons(Composite contentAssistComposite) {
		Composite completionComposite= new Composite(contentAssistComposite, SWT.NONE);
		GridData ccgd= new GridData();
		ccgd.horizontalSpan= 2;
		completionComposite.setLayoutData(ccgd);
		GridLayout ccgl= new GridLayout();
		ccgl.marginWidth= 0;
		ccgl.numColumns= 2;
		completionComposite.setLayout(ccgl);
		
		SelectionListener completionSelectionListener= new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				boolean insert= fCompletionInsertsRadioButton.getSelection();
				setValue(PREF_CODEASSIST_INSERT_COMPLETION, insert);
			}
		};
		
		fCompletionInsertsRadioButton= new Button(completionComposite, SWT.RADIO | SWT.LEFT);
		fCompletionInsertsRadioButton.setText(PreferencesMessages.RubyEditorPreferencePage_completionInserts); 
		fCompletionInsertsRadioButton.setLayoutData(new GridData());
		fCompletionInsertsRadioButton.addSelectionListener(completionSelectionListener);
		
		fCompletionOverwritesRadioButton= new Button(completionComposite, SWT.RADIO | SWT.LEFT);
		fCompletionOverwritesRadioButton.setText(PreferencesMessages.RubyEditorPreferencePage_completionOverwrites); 
		fCompletionOverwritesRadioButton.setLayoutData(new GridData());
		fCompletionOverwritesRadioButton.addSelectionListener(completionSelectionListener);
		
		Label label= new Label(completionComposite, SWT.NONE);
		label.setText(PreferencesMessages.RubyEditorPreferencePage_completionToggleHint);
		GridData gd= new GridData();
		gd.horizontalIndent= 20;
		gd.horizontalSpan= 2;
		label.setLayoutData(gd);
	}
	
	public void initialize() {
		initializeFields();
	}

	private void initializeFields() {
		boolean completionInserts= getBooleanValue(PREF_CODEASSIST_INSERT_COMPLETION);
		fCompletionInsertsRadioButton.setSelection(completionInserts);
		fCompletionOverwritesRadioButton.setSelection(!completionInserts);
		
		updateAutoactivationControls();
 	}
	
    private void updateAutoactivationControls() {
        boolean autoactivation= getBooleanValue(PREF_CODEASSIST_AUTOACTIVATION);
        setControlEnabled(PREF_CODEASSIST_AUTOACTIVATION_DELAY, autoactivation);
        setControlEnabled(PREF_CODEASSIST_FILL_METHOD_BLOCK_ARGUMENTS, getBooleanValue(PREF_CODEASSIST_FILL_ARGUMENT_NAMES));
    }

    
	public void performDefaults() {
		super.performDefaults();
		initializeFields();
	}
	
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}
	
	/**
	 * Validates that the specified number is positive.
	 * 
	 * @param number
	 *                   The number to validate
	 * @return The status of the validation
	 */
	protected static IStatus validatePositiveNumber(final String number) {

		final StatusInfo status= new StatusInfo();
		if (number.length() == 0) {
			status.setError(PreferencesMessages.SpellingPreferencePage_empty_threshold); 
		} else {
			try {
				final int value= Integer.parseInt(number);
				if (value < 0) {
					status.setError(Messages.format(PreferencesMessages.SpellingPreferencePage_invalid_threshold, number)); 
				}
			} catch (NumberFormatException exception) {
				status.setError(Messages.format(PreferencesMessages.SpellingPreferencePage_invalid_threshold, number)); 
			}
		}
		return status;
	}
	
	protected void validateSettings(Key key, String oldValue, String newValue) {
		if (key == null || PREF_CODEASSIST_AUTOACTIVATION_DELAY.equals(key))
			fContext.statusChanged(validatePositiveNumber(getValue(PREF_CODEASSIST_AUTOACTIVATION_DELAY)));
	}

	public Control createControl(Composite parent) {
		ScrolledPageContent scrolled= new ScrolledPageContent(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolled.setDelayedReflow(true);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		Control control= createContents(scrolled);
		scrolled.setContent(control);
		final Point size= control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolled.setMinSize(size.x, size.y);
		return scrolled;
	}

	protected void setControlEnabled(Key key, boolean enabled) {
		Control control= getControl(key);
		control.setEnabled(enabled);
		Label label= (Label) fLabels.get(control);
		if (label != null)
			label.setEnabled(enabled);
	}

	private Control getControl(Key key) {
		for (int i= fComboBoxes.size() - 1; i >= 0; i--) {
			Control curr= (Control) fComboBoxes.get(i);
			ControlData data= (ControlData) curr.getData();
			if (key.equals(data.getKey())) {
				return curr;
			}
		}
		for (int i= fCheckBoxes.size() - 1; i >= 0; i--) {
			Control curr= (Control) fCheckBoxes.get(i);
			ControlData data= (ControlData) curr.getData();
			if (key.equals(data.getKey())) {
				return curr;
			}
		}
		for (int i= fTextBoxes.size() - 1; i >= 0; i--) {
			Control curr= (Control) fTextBoxes.get(i);
			Key currKey= (Key) curr.getData();
			if (key.equals(currKey)) {
				return curr;
			}
		}
		return null;		
	}
}
