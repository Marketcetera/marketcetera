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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;

/**
  */
public class ProblemSeveritiesConfigurationBlock extends OptionsConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME= null; //"ProblemSeveritiesConfigurationBlock"; 	
	
	// values
	private static final String ERROR= RubyCore.ERROR;
	private static final String WARNING= RubyCore.WARNING;
	private static final String IGNORE= RubyCore.IGNORE;

	private static final String ENABLED= RubyCore.ENABLED;
	private static final String DISABLED= RubyCore.DISABLED;

	private static Key[] fgKeys;
	private static Map<String, String> fgCategories;
	private static HashMap<String, List<Error>> fgErrors;

	private PixelConverter fPixelConverter;
	private IStatus fStatus;
	
	public ProblemSeveritiesConfigurationBlock(IStatusChangeListener context, IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
		fStatus= new StatusInfo();
	}
	
	private static List<IConfigurationElement> getErrorProviderElements() {
		List<IConfigurationElement> elements = new ArrayList<IConfigurationElement>();
		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(RubyCore.PLUGIN_ID, "errorProvider");
		if (extension == null)
			return elements;		
		IExtension[] extensions = extension.getExtensions();
		for(int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) elements.add(configElements[j]);
		}
		return elements;
	}
	
	private static Map<String, String> getErrorCategories() {
		if (fgCategories != null) return fgCategories;
		Map<String, String> categories = new HashMap<String, String>();
		List<IConfigurationElement> configElements = getErrorProviderElements();
		for (IConfigurationElement configElement : configElements) {
			String name = configElement.getName();
			String contributorName = configElement.getContributor().getName();
			if (name.equals("category")) { // Grab the categories
				categories.put(configElement.getAttribute("id"), configElement.getAttribute("name"));
			}
		}
		fgCategories = categories;
		return fgCategories;
	}
	
	private static class Error {
		private String id;
		private String label;
		private String contributor;
		private String argument;
		private String type;
		
		Error(IConfigurationElement element) {
			this.id = element.getAttribute("prefKey");
			this.label = element.getAttribute("label");
			this.contributor = element.getContributor().getName();
			IConfigurationElement[] elements = element.getChildren("argument");
			if (elements != null && elements.length > 0) {
				this.argument = elements[0].getAttribute("prefKey");
				this.type = elements[0].getAttribute("type");
				if (type == null) {
					type = "int";
				}
			}
		}
		public boolean hasArgument() {
			return argument != null;
		}
		public String getContributor() {
			return contributor;
		}
		public String getId() {
			return id;
		}
		public String getLabel() {
			return label;
		}
		public String getArgument() {
			return argument;
		}
		public boolean argumentIsInt() {
			return hasArgument() && type.equals("int");
		}
	}
	
	private static Key[] getKeys() {
		if (fgKeys != null) return fgKeys;
		List<Key> keys = new ArrayList<Key>();
		Map<String, String> categories = getErrorCategories();
		for (String categoryId : categories.keySet()) {
			List<Error> errors = getErrors(categoryId);
			for (Error error : errors) {
				keys.add(getKey(error.getContributor(), error.getId()));
			}
		}
		fgKeys = keys.toArray(new Key[keys.size()]);
		return fgKeys;
	}
	
	/*
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		fPixelConverter= new PixelConverter(parent);
		setShell(parent.getShell());
		
		Composite mainComp= new Composite(parent, SWT.NONE);
		mainComp.setFont(parent.getFont());
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		mainComp.setLayout(layout);
		
		Composite commonComposite= createStyleTabContent(mainComp);
		GridData gridData= new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.heightHint= fPixelConverter.convertHeightInCharsToPixels(20);
		commonComposite.setLayoutData(gridData);
		
		validateSettings(null, null, null);
	
		return mainComp;
	}
	
	private Composite createStyleTabContent(Composite folder) {
		String[] errorWarningIgnore= new String[] { ERROR, WARNING, IGNORE };
		
		String[] errorWarningIgnoreLabels= new String[] {
			PreferencesMessages.ProblemSeveritiesConfigurationBlock_error,  
			PreferencesMessages.ProblemSeveritiesConfigurationBlock_warning, 
			PreferencesMessages.ProblemSeveritiesConfigurationBlock_ignore
		};
		
		String[] enabledDisabled= new String[] { ENABLED, DISABLED };
		
		int nColumns= 3;
		
		final ScrolledPageContent sc1 = new ScrolledPageContent(folder);
		
		Composite composite= sc1.getBody();
		GridLayout layout= new GridLayout(nColumns, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		
		Label description= new Label(composite, SWT.LEFT | SWT.WRAP);
		description.setFont(description.getFont());
		description.setText(PreferencesMessages.ProblemSeveritiesConfigurationBlock_common_description); 
		description.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, nColumns - 1, 1));
				
		int indentStep=  fPixelConverter.convertWidthInCharsToPixels(1);
		
		int defaultIndent= indentStep * 0;
		int extraIndent= indentStep * 2;
		String label;
		ExpandableComposite excomposite;
		Composite inner;
		
		Map<String, String> categories = getErrorCategories();
		for (String categoryId : categories.keySet()) {			
			List<Error> errors = getErrors(categoryId);
			if (errors == null || errors.isEmpty()) continue;
			excomposite= createStyleSection(composite, categories.get(categoryId), nColumns);
			inner= new Composite(excomposite, SWT.NONE);
			inner.setFont(composite.getFont());
			inner.setLayout(new GridLayout(nColumns, false));
			excomposite.setClient(inner);
			
			for (Error error : errors) {
				addComboBox(inner, error.label + ':', getKey(error.getContributor(), error.getId()), errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
				if (error.hasArgument()) {
					Text text= addTextField(inner, "", getKey(error.getContributor(), error.getArgument()), 0, 0);
					GridData gd= (GridData) text.getLayoutData();
					gd.widthHint= fPixelConverter.convertWidthInCharsToPixels(8);
					gd.horizontalAlignment= GridData.END;
					text.setTextLimit(6);
				}
			}	
		}		

		IDialogSettings section= RubyPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		restoreSectionExpansionStates(section);
		
		return sc1;
	}
	
	private static List<Error> getErrors(String categoryId) {
		if (fgErrors == null) {
			fgErrors = new HashMap<String, List<Error>>();
		}
		if (fgErrors.get(categoryId) != null) return fgErrors.get(categoryId);
		List<Error> categories = new ArrayList<Error>();
		List<IConfigurationElement> configElements = getErrorProviderElements();
		for (IConfigurationElement configElement : configElements) {
			String name = configElement.getName();
			if (name.equals("error") && configElement.getAttribute("categoryId").equals(categoryId)) { // Grab the errors for this category
				categories.add(new Error(configElement));
			}
		}
		fgErrors.put(categoryId, categories);
		return categories;
	}

	/* (non-javadoc)
	 * Update fields and validate.
	 * @param changedKey Key that changed, or null, if all changed.
	 */	
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (!areSettingsEnabled()) {
			return;
		}		
		if (changedKey != null) {
			List<Error> errors = getErrors();
			for (Error error : errors) {
				if (error.hasArgument() && error.argumentIsInt() && changedKey.getName().equals(error.getArgument())) {
					fStatus = validateMaxNumber(changedKey, error.getLabel());
					fContext.statusChanged(fStatus);
					return;
				}
			}
		} else {
			updateEnableStates();
		}		
		IStatus status= StatusUtil.getMostSevere(new IStatus[] { fStatus });
		fContext.statusChanged(status);
	}
	
	private static List<Error> getErrors() {
		List<Error> errors = new ArrayList<Error>();
		Map<String, String> categories = getErrorCategories();
		for (String categoryId : categories.keySet()) {
			errors.addAll(getErrors(categoryId));
		}
		return errors;
	}

	private IStatus validateMaxNumber(Key key, String label) {
		String number= getValue(key);
		StatusInfo status= new StatusInfo();
		if (number.length() == 0) {
			status.setError(PreferencesMessages.RubyBuildConfigurationBlock_empty_input); 
		} else {
			try {
				int value= Integer.parseInt(number);
				if (value <= 0) {
					status.setError(Messages.format(PreferencesMessages.RubyBuildConfigurationBlock_invalid_input, new Object[] {number, label})); 
				}
			} catch (NumberFormatException e) {
				status.setError(Messages.format(PreferencesMessages.RubyBuildConfigurationBlock_invalid_input, new Object[] {number, label})); 
			}
		}
		return status;
	}
	
	private void updateEnableStates() {
		// TODO Handle enabling/disabling checkboxes as prefs change
		// FIXME Iterate through errors which have "arguments", if error is set to ignore, disable value textbox!
	}

	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title= PreferencesMessages.ProblemSeveritiesConfigurationBlock_needsbuild_title; 
		String message;
		if (workspaceSettings) {
			message= PreferencesMessages.ProblemSeveritiesConfigurationBlock_needsfullbuild_message; 
		} else {
			message= PreferencesMessages.ProblemSeveritiesConfigurationBlock_needsprojectbuild_message; 
		}
		return new String[] { title, message };
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#dispose()
	 */
	public void dispose() {
		IDialogSettings section= RubyPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION_NAME);
		storeSectionExpansionStates(section);
		super.dispose();
	}
	
}
