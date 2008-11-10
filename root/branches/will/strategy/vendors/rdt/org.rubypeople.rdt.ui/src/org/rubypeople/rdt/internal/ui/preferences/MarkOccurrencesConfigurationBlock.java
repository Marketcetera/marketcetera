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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * Configures Ruby Editor mark occurrences preferences.
 * 
 * @since 0.9.0
 */
class MarkOccurrencesConfigurationBlock implements IPreferenceConfigurationBlock {

	private OverlayPreferenceStore fStore;
	
	
	private Map fCheckBoxes= new HashMap();
	private SelectionListener fCheckBoxListener= new SelectionListener() {
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		public void widgetSelected(SelectionEvent e) {
			Button button= (Button) e.widget;
			fStore.setValue((String) fCheckBoxes.get(button), button.getSelection());
		}
	};
	
	/**
	 * List of master/slave listeners when there's a dependency.
	 * 
	 * @see #createDependency(Button, String, Control)
	 * @since 3.0
	 */
	private ArrayList fMasterSlaveListeners= new ArrayList();
	
	private StatusInfo fStatus;

	public MarkOccurrencesConfigurationBlock(OverlayPreferenceStore store) {
		Assert.isNotNull(store);
		fStore= store;
		
		fStore.addKeys(createOverlayStoreKeys());
	}
	
	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		
		ArrayList overlayKeys= new ArrayList();

		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_OCCURRENCES));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_TYPE_OCCURRENCES));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_METHOD_OCCURRENCES));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_CONSTANT_OCCURRENCES));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_FIELD_OCCURRENCES));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_LOCAL_VARIABLE_OCCURRENCES));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_MARK_METHOD_EXIT_POINTS));
		overlayKeys.add(new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_STICKY_OCCURRENCES));
		
		OverlayPreferenceStore.OverlayKey[] keys= new OverlayPreferenceStore.OverlayKey[overlayKeys.size()];
		overlayKeys.toArray(keys);
		return keys;
	}	

	/**
	 * Creates page for mark occurrences preferences.
	 * 
	 * @param parent the parent composite
	 * @return the control for the preference page
	 */
	public Control createControl(Composite parent) {
	
		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		composite.setLayout(layout);
		
		String label;
		
		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markOccurrences; 
		Button master= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_OCCURRENCES, 0); 
		
		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markTypeOccurrences; 
		Button slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_TYPE_OCCURRENCES, 0); 
		createDependency(master, PreferenceConstants.EDITOR_STICKY_OCCURRENCES, slave);
		
//		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markMethodOccurrences; 
//		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_METHOD_OCCURRENCES, 0); 
//		createDependency(master, PreferenceConstants.EDITOR_MARK_METHOD_OCCURRENCES, slave);
		
		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markConstantOccurrences; 
		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_CONSTANT_OCCURRENCES, 0); 
		createDependency(master, PreferenceConstants.EDITOR_MARK_CONSTANT_OCCURRENCES, slave);

//		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markFieldOccurrences; 
//		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_FIELD_OCCURRENCES, 0); 
//		createDependency(master, PreferenceConstants.EDITOR_MARK_FIELD_OCCURRENCES, slave);

		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markLocalVariableOccurrences; 
		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_LOCAL_VARIABLE_OCCURRENCES, 0); 
		createDependency(master, PreferenceConstants.EDITOR_MARK_LOCAL_VARIABLE_OCCURRENCES, slave);

		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_markMethodExitPoints; 
		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_MARK_METHOD_EXIT_POINTS, 0); 
		createDependency(master, PreferenceConstants.EDITOR_MARK_METHOD_EXIT_POINTS, slave);

		addFiller(composite);
		
		label= PreferencesMessages.MarkOccurrencesConfigurationBlock_stickyOccurrences; 
		slave= addCheckBox(composite, label, PreferenceConstants.EDITOR_STICKY_OCCURRENCES, 0); 
		createDependency(master, PreferenceConstants.EDITOR_STICKY_OCCURRENCES, slave);

		return composite;
	}
	
	private void addFiller(Composite composite) {
		PixelConverter pixelConverter= new PixelConverter(composite);
		
		Label filler= new Label(composite, SWT.LEFT );
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.heightHint= pixelConverter.convertHeightInCharsToPixels(1) / 2;
		filler.setLayoutData(gd);
	}

	private Button addCheckBox(Composite parent, String label, String key, int indentation) {		
		Button checkBox= new Button(parent, SWT.CHECK);
		checkBox.setText(label);
		
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalIndent= indentation;
		gd.horizontalSpan= 2;
		checkBox.setLayoutData(gd);
		checkBox.addSelectionListener(fCheckBoxListener);
		
		fCheckBoxes.put(checkBox, key);
		
		return checkBox;
	}

	private void createDependency(final Button master, String masterKey, final Control slave) {
		indent(slave);
		boolean masterState= fStore.getBoolean(masterKey);
		slave.setEnabled(masterState);
		SelectionListener listener= new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				slave.setEnabled(master.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {}
		};
		master.addSelectionListener(listener);
		fMasterSlaveListeners.add(listener);
	}

	private static void indent(Control control) {
		GridData gridData= new GridData();
		gridData.horizontalIndent= 20;
		control.setLayoutData(gridData);		
	}

	public void initialize() {
		initializeFields();
	}

	void initializeFields() {
		
		Iterator iter= fCheckBoxes.keySet().iterator();
		while (iter.hasNext()) {
			Button b= (Button) iter.next();
			String key= (String) fCheckBoxes.get(b);
			b.setSelection(fStore.getBoolean(key));
		}
		
        // Update slaves
        iter= fMasterSlaveListeners.iterator();
        while (iter.hasNext()) {
            SelectionListener listener= (SelectionListener)iter.next();
            listener.widgetSelected(null);
        }
        
	}

	public void performOk() {
	}

	public void performDefaults() {
		restoreFromPreferences();
		initializeFields();
	}

	private void restoreFromPreferences() {

	}

	IStatus getStatus() {
		if (fStatus == null)
			fStatus= new StatusInfo();
		return fStatus;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.IPreferenceConfigurationBlock#dispose()
	 * @since 3.0
	 */
	public void dispose() {
	}
}
