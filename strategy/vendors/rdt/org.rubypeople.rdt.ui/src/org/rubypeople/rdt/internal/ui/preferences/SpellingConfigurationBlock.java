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

package org.rubypeople.rdt.internal.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.text.spelling.SpellCheckEngine;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.util.SWTUtil;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * Options configuration block for spell-check related settings.
 * 
 * @since 3.0
 */
public class SpellingConfigurationBlock extends OptionsConfigurationBlock {

	/** Preference keys for the preferences in this block */
	private static final Key PREF_SPELLING_IGNORE_DIGITS= getRDTUIKey(PreferenceConstants.SPELLING_IGNORE_DIGITS);
	private static final Key PREF_SPELLING_IGNORE_MIXED= getRDTUIKey(PreferenceConstants.SPELLING_IGNORE_MIXED);
	private static final Key PREF_SPELLING_IGNORE_SENTENCE= getRDTUIKey(PreferenceConstants.SPELLING_IGNORE_SENTENCE);
	private static final Key PREF_SPELLING_IGNORE_UPPER= getRDTUIKey(PreferenceConstants.SPELLING_IGNORE_UPPER);
	private static final Key PREF_SPELLING_IGNORE_URLS= getRDTUIKey(PreferenceConstants.SPELLING_IGNORE_URLS);
	private static final Key PREF_SPELLING_LOCALE= getRDTUIKey(PreferenceConstants.SPELLING_LOCALE);
	private static final Key PREF_SPELLING_PROPOSAL_THRESHOLD= getRDTUIKey(PreferenceConstants.SPELLING_PROPOSAL_THRESHOLD);
	private static final Key PREF_SPELLING_USER_DICTIONARY= getRDTUIKey(PreferenceConstants.SPELLING_USER_DICTIONARY);
	private static final Key PREF_SPELLING_ENABLE_CONTENTASSIST= getRDTUIKey(PreferenceConstants.SPELLING_ENABLE_CONTENTASSIST);

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

	/**
	 * Returns the locale codes for the locale list.
	 * 
	 * @param locales
	 *                   The list of locales
	 * @return Array of locale codes for the list
	 */
	protected static String[] getDictionaryCodes(final Set locales) {

		int index= 0;
		Locale locale= null;

		final String[] codes= new String[locales.size()];
		for (final Iterator iterator= locales.iterator(); iterator.hasNext();) {

			locale= (Locale)iterator.next();
			codes[index++]= locale.toString();
		}
		return codes;
	}

	/**
	 * Returns the display labels for the locale list.
	 * 
	 * @param locales
	 *                   The list of locales
	 * @return Array of display labels for the list
	 */
	protected static String[] getDictionaryLabels(final Set locales) {

		int index= 0;
		Locale locale= null;

		final String[] labels= new String[locales.size()];
		for (final Iterator iterator= locales.iterator(); iterator.hasNext();) {

			locale= (Locale)iterator.next();
			labels[index++]= locale.getDisplayName();
		}
		return labels;
	}

	/**
	 * Validates that the file with the specified absolute path exists and can
	 * be opened.
	 * 
	 * @param path
	 *                   The path of the file to validate
	 * @return <code>true</code> iff the file exists and can be opened,
	 *               <code>false</code> otherwise
	 */
	protected static IStatus validateAbsoluteFilePath(final String path) {

		final StatusInfo status= new StatusInfo();
		if (path.length() > 0) {

			final File file= new File(path);
			if (!file.isFile() || !file.isAbsolute() || !file.exists() || !file.canRead() || !file.canWrite())
				status.setError(PreferencesMessages.SpellingPreferencePage_dictionary_error); 

		}
		return status;
	}

	/**
	 * Validates that the specified locale is available.
	 * 
	 * @param locale
	 *                   The locale to validate
	 * @return The status of the validation
	 */
	protected static IStatus validateLocale(final String locale) {

		final StatusInfo status= new StatusInfo(IStatus.ERROR, PreferencesMessages.SpellingPreferencePage_locale_error); 
		final Set locales= SpellCheckEngine.getAvailableLocales();

		Locale current= null;
		for (final Iterator iterator= locales.iterator(); iterator.hasNext();) {

			current= (Locale)iterator.next();
			if (current.toString().equals(locale))
				return new StatusInfo();
		}
		return status;
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

	/** The dictionary path field */
	private Text fDictionaryPath= null;

	/** The status for the workspace dictionary file */
	private IStatus fFileStatus= new StatusInfo();

	/** The status for the proposal threshold */
	private IStatus fThresholdStatus= new StatusInfo();

	/**
	 * All controls
	 * @since 3.1
	 */
	private Control[] fAllControls;
	
	/**
	 * All previously enabled controls
	 * @since 3.1
	 */
	private Control[] fEnabledControls;
	
	/**
	 * Creates a new spelling configuration block.
	 * 
	 * @param context
	 *                   The status change listener
	 * @param project
	 *                   The Java project
	 */
	public SpellingConfigurationBlock(final IStatusChangeListener context, final IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getAllKeys(), container);

		IStatus status= validateAbsoluteFilePath(getValue(PREF_SPELLING_USER_DICTIONARY));
		if (status.getSeverity() != IStatus.OK)
			setValue(PREF_SPELLING_USER_DICTIONARY, ""); //$NON-NLS-1$

		status= validateLocale(getValue(PREF_SPELLING_LOCALE));
		if (status.getSeverity() != IStatus.OK)
			setValue(PREF_SPELLING_LOCALE, SpellCheckEngine.getDefaultLocale().toString());
	}

	protected Combo addComboBox(Composite parent, String label, Key key, String[] values, String[] valueLabels, int indent) {
		ControlData data= new ControlData(key, values);
		
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= indent;
				
		Label labelControl= new Label(parent, SWT.LEFT | SWT.WRAP);
		labelControl.setText(label);
		labelControl.setLayoutData(gd);
		
		Combo comboBox= new Combo(parent, SWT.READ_ONLY);
		comboBox.setItems(valueLabels);
		comboBox.setData(data);
		gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		comboBox.setLayoutData(gd);
		comboBox.addSelectionListener(getSelectionListener());
		
		fLabels.put(comboBox, labelControl);
		
		String currValue= getValue(key);	
		comboBox.select(data.getSelection(currValue));
		
		fComboBoxes.add(comboBox);
		return comboBox;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(final Composite parent) {

		Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		List allControls= new ArrayList();
		final PixelConverter converter= new PixelConverter(parent);

		final String[] trueFalse= new String[] { IPreferenceStore.TRUE, IPreferenceStore.FALSE };

		Group user= new Group(composite, SWT.NONE);
		user.setText(PreferencesMessages.SpellingPreferencePage_preferences_user); 
		user.setLayout(new GridLayout());		
		user.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allControls.add(user);

		String label= PreferencesMessages.SpellingPreferencePage_ignore_digits_label; 
		Control slave= addCheckBox(user, label, PREF_SPELLING_IGNORE_DIGITS, trueFalse, 0);
		allControls.add(slave);

		label= PreferencesMessages.SpellingPreferencePage_ignore_mixed_label; 
		slave= addCheckBox(user, label, PREF_SPELLING_IGNORE_MIXED, trueFalse, 0);
		allControls.add(slave);

		label= PreferencesMessages.SpellingPreferencePage_ignore_sentence_label; 
		slave= addCheckBox(user, label, PREF_SPELLING_IGNORE_SENTENCE, trueFalse, 0);
		allControls.add(slave);

		label= PreferencesMessages.SpellingPreferencePage_ignore_upper_label; 
		slave= addCheckBox(user, label, PREF_SPELLING_IGNORE_UPPER, trueFalse, 0);
		allControls.add(slave);

		label= PreferencesMessages.SpellingPreferencePage_ignore_url_label; 
		slave= addCheckBox(user, label, PREF_SPELLING_IGNORE_URLS, trueFalse, 0);
		allControls.add(slave);

		final Group engine= new Group(composite, SWT.NONE);
		engine.setText(PreferencesMessages.SpellingPreferencePage_preferences_engine); 
		engine.setLayout(new GridLayout(4, false));
		engine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allControls.add(engine);

		label= PreferencesMessages.SpellingPreferencePage_dictionary_label; 
		final Set locales= SpellCheckEngine.getAvailableLocales();

		Combo combo= addComboBox(engine, label, PREF_SPELLING_LOCALE, getDictionaryCodes(locales), getDictionaryLabels(locales), 0);
		combo.setEnabled(locales.size() > 1);
		allControls.add(combo);
		allControls.add(fLabels.get(combo));
		
		new Label(engine, SWT.NONE); // placeholder

		label= PreferencesMessages.SpellingPreferencePage_workspace_dictionary_label; 
		fDictionaryPath= addTextField(engine, label, PREF_SPELLING_USER_DICTIONARY, 0, 0);
		GridData gd= (GridData) fDictionaryPath.getLayoutData();
		gd.grabExcessHorizontalSpace= true;
		gd.widthHint= converter.convertWidthInCharsToPixels(40);
		allControls.add(fDictionaryPath);
		allControls.add(fLabels.get(fDictionaryPath));

		
		Button button= new Button(engine, SWT.PUSH);
		button.setText(PreferencesMessages.SpellingPreferencePage_browse_label); 
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(final SelectionEvent event) {
				handleBrowseButtonSelected();
			}
		});
		button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		SWTUtil.setButtonDimensionHint(button);
		allControls.add(button);
		
		Group advanced= new Group(composite, SWT.NONE);
		advanced.setText(PreferencesMessages.SpellingPreferencePage_preferences_advanced); 
		advanced.setLayout(new GridLayout(3, false));
		advanced.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		allControls.add(advanced);
		
		label= PreferencesMessages.SpellingPreferencePage_proposals_threshold; 
		Text text= addTextField(advanced, label, PREF_SPELLING_PROPOSAL_THRESHOLD, 0, 0);
		text.setTextLimit(3);
		gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint= converter.convertWidthInCharsToPixels(4);
		text.setLayoutData(gd);
		allControls.add(text);
		allControls.add(fLabels.get(text));
		
		label= PreferencesMessages.SpellingPreferencePage_enable_contentassist_label; 
		button= addCheckBox(advanced, label, PREF_SPELLING_ENABLE_CONTENTASSIST, trueFalse, 0);
		allControls.add(button);

		fAllControls= (Control[]) allControls.toArray(new Control[allControls.size()]);
		
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IJavaHelpContextIds.JAVA_EDITOR_PREFERENCE_PAGE);
		return composite;
	}

	private static Key[] getAllKeys() {
		return new Key[] { PREF_SPELLING_USER_DICTIONARY, PREF_SPELLING_IGNORE_DIGITS, PREF_SPELLING_IGNORE_MIXED, PREF_SPELLING_IGNORE_SENTENCE, PREF_SPELLING_IGNORE_UPPER, PREF_SPELLING_IGNORE_URLS, PREF_SPELLING_LOCALE, PREF_SPELLING_PROPOSAL_THRESHOLD, PREF_SPELLING_ENABLE_CONTENTASSIST };
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#getFullBuildDialogStrings(boolean)
	 */
	protected final String[] getFullBuildDialogStrings(final boolean workspace) {
		return null;
	}

	/**
	 * Handles selections of the browse button.
	 */
	protected void handleBrowseButtonSelected() {

		final FileDialog dialog= new FileDialog(fDictionaryPath.getShell(), SWT.OPEN);
		dialog.setText(PreferencesMessages.SpellingPreferencePage_filedialog_title); 
		dialog.setFilterExtensions(new String[] { PreferencesMessages.SpellingPreferencePage_filter_dictionary_extension, PreferencesMessages.SpellingPreferencePage_filter_all_extension }); 
		dialog.setFilterNames(new String[] { PreferencesMessages.SpellingPreferencePage_filter_dictionary_label, PreferencesMessages.SpellingPreferencePage_filter_all_label }); 

		final String path= dialog.open();
		if (path != null)
			fDictionaryPath.setText(path);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#validateSettings(java.lang.String,java.lang.String)
	 */
	protected void validateSettings(final Key key, final String oldValue, final String newValue) {

		if (key == null || PREF_SPELLING_PROPOSAL_THRESHOLD.equals(key))
			fThresholdStatus= validatePositiveNumber(getValue(PREF_SPELLING_PROPOSAL_THRESHOLD));

		if (key == null || PREF_SPELLING_USER_DICTIONARY.equals(key))
			fFileStatus= validateAbsoluteFilePath(getValue(PREF_SPELLING_USER_DICTIONARY));

		fContext.statusChanged(StatusUtil.getMostSevere(new IStatus[] { fThresholdStatus, fFileStatus }));
	}
	
	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#updateCheckBox(org.eclipse.swt.widgets.Button)
	 * @since 3.1
	 */
	protected void updateCheckBox(Button curr) {
		super.updateCheckBox(curr);
		Event event= new Event();
		event.type= SWT.Selection;
		event.display= curr.getDisplay();
		event.widget= curr;
		curr.notifyListeners(SWT.Selection, event);
	}
	
	/**
	 * @since 3.1
	 */
	protected void setEnabled(boolean enabled) {
		if (enabled && fEnabledControls != null) {
			for (int i= fEnabledControls.length - 1; i >= 0; i--)
				fEnabledControls[i].setEnabled(true);
			fEnabledControls= null;
		}
		if (!enabled && fEnabledControls == null) {
			List enabledControls= new ArrayList();
			for (int i= fAllControls.length - 1; i >= 0; i--) {
				Control control= fAllControls[i];
				if (control.isEnabled()) {
					enabledControls.add(control);
					control.setEnabled(false);
				}
			}
			fEnabledControls= (Control[]) enabledControls.toArray(new Control[enabledControls.size()]);
		}
	}
}
