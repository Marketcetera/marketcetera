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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Abstract preference page which is used to wrap a
 * {@link org.rubypeople.rdt.internal.ui.preferences.IPreferenceConfigurationBlock}.
 * 
 * @since 0.9.0
 */
public abstract class AbstractConfigurationBlockPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	
	private IPreferenceConfigurationBlock fConfigurationBlock;
	private OverlayPreferenceStore fOverlayStore;
	

	/**
	 * Creates a new preference page.
	 */
	public AbstractConfigurationBlockPreferencePage() {
		setDescription();
		setPreferenceStore();
		fOverlayStore= new OverlayPreferenceStore(getPreferenceStore(), new OverlayPreferenceStore.OverlayKey[] {});
		fConfigurationBlock= createConfigurationBlock(fOverlayStore);
	}
		
	protected abstract IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore);
	protected abstract String getHelpId();
	protected abstract void setDescription();
	protected abstract void setPreferenceStore();
	
	/*
	 * @see IWorkbenchPreferencePage#init()
	 */	
	public void init(IWorkbench workbench) {
	}

	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), getHelpId());
	}
	
	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		
		fOverlayStore.load();
		fOverlayStore.start();
		
		Control content= fConfigurationBlock.createControl(parent);
		
		initialize();
		
		Dialog.applyDialogFont(content);
		return content;
	}
	
	private void initialize() {
		fConfigurationBlock.initialize();
	}
	
    /*
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		
		fConfigurationBlock.performOk();

		fOverlayStore.propagate();
		
		RubyPlugin.getDefault().savePluginPreferences();
		
		return true;
	}
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	public void performDefaults() {
		
		fOverlayStore.loadDefaults();
		fConfigurationBlock.performDefaults();

		super.performDefaults();
	}
	
	/*
	 * @see DialogPage#dispose()
	 */
	public void dispose() {
		
		fConfigurationBlock.dispose();
		
		if (fOverlayStore != null) {
			fOverlayStore.stop();
			fOverlayStore= null;
		}
		
		super.dispose();
	}
}
