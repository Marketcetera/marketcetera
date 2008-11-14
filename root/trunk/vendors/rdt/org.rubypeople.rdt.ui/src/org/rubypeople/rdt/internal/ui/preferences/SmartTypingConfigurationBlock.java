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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * Configures Ruby Editor typing preferences.
 * 
 * @since 3.1
 */
class SmartTypingConfigurationBlock extends AbstractConfigurationBlock {

	public SmartTypingConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
		
		store.addKeys(createOverlayStoreKeys());
	}
	
	private OverlayPreferenceStore.OverlayKey[] createOverlayStoreKeys() {
		
		return new OverlayPreferenceStore.OverlayKey[] {				
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_STRINGS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_BRACKETS),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_CLOSE_BRACES),
				new OverlayPreferenceStore.OverlayKey(OverlayPreferenceStore.BOOLEAN, PreferenceConstants.EDITOR_END_STATEMENTS)
		};
	}	

	/**
	 * Creates page for mark occurrences preferences.
	 * 
	 * @param parent the parent composite
	 * @return the control for the preference page
	 */
	public Control createControl(Composite parent) {
		ScrolledPageContent scrolled= new ScrolledPageContent(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		
		Composite control= new Composite(scrolled, SWT.NONE);
		GridLayout layout= new GridLayout();
		control.setLayout(layout);

		Composite composite;
		
		composite= createSubsection(control, null, PreferencesMessages.SmartTypingConfigurationBlock_autoclose_title); 
		addAutoclosingSection(composite);

		scrolled.setContent(control);
		final Point size= control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolled.setMinSize(size.x, size.y);
		return scrolled;
	}

	private void addAutoclosingSection(Composite composite) {
		
		GridLayout layout= new GridLayout();
		layout.numColumns= 1;
		composite.setLayout(layout);

		String label;

		label= PreferencesMessages.RubyEditorPreferencePage_closeStrings; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_STRINGS, 0);

		label= PreferencesMessages.RubyEditorPreferencePage_closeBrackets; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_BRACKETS, 0);

		label= PreferencesMessages.RubyEditorPreferencePage_closeBraces; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_CLOSE_BRACES, 0);

		label= PreferencesMessages.RubyEditorPreferencePage_endStatements; 
		addCheckBox(composite, label, PreferenceConstants.EDITOR_END_STATEMENTS, 0);
	}

}
