/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 ********************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathSupport;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;
import org.rubypeople.rdt.ui.RubyUI;

/**
 * The first page of the <code>SimpleProjectWizard</code>.
 */
public class RubyProjectWizardFirstPage extends WizardPage {
	
	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class NameGroup extends Observable implements IDialogFieldListener {

		protected final StringDialogField fNameField;

		public NameGroup(Composite composite, String initialName) {
			final Composite nameComposite= new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false), false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			// text field for project name
			fNameField= new StringDialogField();
			fNameField.setLabelText(NewWizardMessages.RubyProjectWizardFirstPage_NameGroup_label_text); 
			fNameField.setDialogFieldListener(this);

			setName(initialName);

			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		}
		
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}
		
		public void setName(String name) {
			fNameField.setText(name);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
		
	}

	/**
	 * Request a location. Fires an event whenever the checkbox or the location
	 * field is changed, regardless of whether the change originates from the
	 * user or has been invoked programmatically.
	 */
	private final class LocationGroup extends Observable implements Observer, IStringButtonAdapter, IDialogFieldListener {

		protected final SelectionButtonDialogField fWorkspaceRadio;
		protected final SelectionButtonDialogField fExternalRadio;
		protected final StringButtonDialogField fLocation;
		
		private String fPreviousExternalLocation;
		
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC= RubyUI.ID_PLUGIN + ".last.external.project"; //$NON-NLS-1$

		public LocationGroup(Composite composite) {

			final int numColumns= 3;

			final Group group= new Group(composite, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false), true));
			group.setText(NewWizardMessages.RubyProjectWizardFirstPage_LocationGroup_title); 

			fWorkspaceRadio= new SelectionButtonDialogField(SWT.RADIO);
			fWorkspaceRadio.setDialogFieldListener(this);
			fWorkspaceRadio.setLabelText(NewWizardMessages.RubyProjectWizardFirstPage_LocationGroup_workspace_desc); 

			fExternalRadio= new SelectionButtonDialogField(SWT.RADIO);
			fExternalRadio.setLabelText(NewWizardMessages.RubyProjectWizardFirstPage_LocationGroup_external_desc); 

			fLocation= new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation.setLabelText(NewWizardMessages.RubyProjectWizardFirstPage_LocationGroup_locationLabel_desc); 
			fLocation.setButtonLabel(NewWizardMessages.RubyProjectWizardFirstPage_LocationGroup_browseButton_desc); 

			fExternalRadio.attachDialogField(fLocation);
			
			fWorkspaceRadio.setSelection(true);
			fExternalRadio.setSelection(false);
			
			fPreviousExternalLocation= ""; //$NON-NLS-1$

			fWorkspaceRadio.doFillIntoGrid(group, numColumns);
			fExternalRadio.doFillIntoGrid(group, numColumns);
			fLocation.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
		}
				
		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		protected String getDefaultPath(String name) {
			final IPath path= Platform.getLocation().append(name);
			return path.toOSString();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 *      java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if (isInWorkspace()) {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isInWorkspace()) {
				return Platform.getLocation();
			}
			return Path.fromOSString(fLocation.getText().trim());
		}

		public boolean isInWorkspace() {
			return fWorkspaceRadio.isSelected();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter#changeControlPressed(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void changeControlPressed(DialogField field) {
			final DirectoryDialog dialog= new DirectoryDialog(getShell());
			dialog.setMessage(NewWizardMessages.RubyProjectWizardFirstPage_directory_message); 
			String directoryName = fLocation.getText().trim();
			if (directoryName.length() == 0) {
				String prevLocation= RubyPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
				if (prevLocation != null) {
					directoryName= prevLocation;
				}
			}
		
			if (directoryName.length() > 0) {
				final File path = new File(directoryName);
				if (path.exists())
					dialog.setFilterPath(directoryName);
			}
			final String selectedDirectory = dialog.open();
			if (selectedDirectory != null) {
				fLocation.setText(selectedDirectory);
				RubyPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC, selectedDirectory);
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			if (field == fWorkspaceRadio) {
				final boolean checked= fWorkspaceRadio.isSelected();
				if (checked) {
					fPreviousExternalLocation= fLocation.getText();
					fLocation.setText(getDefaultPath(fNameGroup.getName()));
				} else {
					fLocation.setText(fPreviousExternalLocation);
				}
			}
			fireEvent();
		}
	}
	
	private final class JREGroup implements Observer, SelectionListener, IDialogFieldListener {

		private final SelectionButtonDialogField fUseDefaultJRE, fUseProjectJRE;
		private final ComboDialogField fJRECombo;
		private final Group fGroup;
//		private String[] fComplianceLabels;
//		private String[] fComplianceData;
		private final Link fPreferenceLink;
		private IVMInstall[] fInstalledJVMs;
		
		public JREGroup(Composite composite) {
			fGroup= new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fGroup.setLayout(initGridLayout(new GridLayout(3, false), true));
			fGroup.setText(NewWizardMessages.RubyProjectWizardFirstPage_JREGroup_title); 
						
			fUseDefaultJRE= new SelectionButtonDialogField(SWT.RADIO);
			fUseDefaultJRE.setLabelText(getDefaultJVMLabel());
			fUseDefaultJRE.doFillIntoGrid(fGroup, 2);
			
			fPreferenceLink= new Link(fGroup, SWT.NONE);
			fPreferenceLink.setFont(fGroup.getFont());
			fPreferenceLink.setText(NewWizardMessages.RubyProjectWizardFirstPage_JREGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
			fPreferenceLink.addSelectionListener(this);
		
			fUseProjectJRE= new SelectionButtonDialogField(SWT.RADIO);
			fUseProjectJRE.setLabelText(NewWizardMessages.RubyProjectWizardFirstPage_JREGroup_specific_compliance);
			fUseProjectJRE.doFillIntoGrid(fGroup, 1);
			fUseProjectJRE.setDialogFieldListener(this);
						
			fJRECombo= new ComboDialogField(SWT.READ_ONLY);
			fillInstalledJREs(fJRECombo);
			fJRECombo.setDialogFieldListener(this);

			Combo comboControl= fJRECombo.getComboControl(fGroup);
			comboControl.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false)); // make sure column 2 is grabing (but no fill)
			comboControl.setVisibleItemCount(20);
			
			DialogField.createEmptySpace(fGroup);
			
			fUseDefaultJRE.setSelection(true);
			fJRECombo.setEnabled(fUseProjectJRE.isSelected());
		}

		private void fillInstalledJREs(ComboDialogField comboField) {
			String selectedItem= null;
			int selectionIndex= -1;
			if (fUseProjectJRE.isSelected()) {
				selectionIndex= comboField.getSelectionIndex();
				if (selectionIndex != -1) {//paranoia
					selectedItem= comboField.getItems()[selectionIndex];
				}
			}
			
			fInstalledJVMs= getWorkspaceJREs();
			Arrays.sort(fInstalledJVMs, new Comparator() {

				public int compare(Object arg0, Object arg1) {
					IVMInstall i0= (IVMInstall)arg0;
					IVMInstall i1= (IVMInstall)arg1;
//					if (i1 instanceof IVMInstall2 && i0 instanceof IVMInstall2) {
//						String cc0= RubyModelUtil.getCompilerCompliance((IVMInstall2) i0, RubyCore.VERSION_1_4);
//						String cc1= RubyModelUtil.getCompilerCompliance((IVMInstall2) i1, RubyCore.VERSION_1_4);
//						int result= cc1.compareTo(cc0);
//						if (result == 0)
//							result= i0.getName().compareTo(i1.getName());
//						return result;
//					} else {
						return i0.getName().compareTo(i1.getName());
//					}
				}
				
			});
			selectionIndex= -1;//find new index
//			fComplianceLabels= new String[fInstalledJVMs.length];
//			fComplianceData= new String[fInstalledJVMs.length];
//			for (int i= 0; i < fInstalledJVMs.length; i++) {
//				fComplianceLabels[i]= fInstalledJVMs[i].getName();
//				if (selectedItem != null && fComplianceLabels[i].equals(selectedItem)) {
//					selectionIndex= i;
//				}
//				if (fInstalledJVMs[i] instanceof IVMInstall2) {
//					fComplianceData[i]= RubyModelUtil.getCompilerCompliance((IVMInstall2) fInstalledJVMs[i], RubyCore.VERSION_1_4);
//				} else {
//					fComplianceData[i]= RubyCore.VERSION_1_4;
//				}
//			}
//			comboField.setItems(fComplianceLabels);
			String[] items = new String[fInstalledJVMs.length];
			for (int i= 0; i < fInstalledJVMs.length; i++) {
				items[i] = fInstalledJVMs[i].getName();
			}
			fJRECombo.setItems(items);
			if (selectionIndex == -1) {
				fJRECombo.selectItem(getDefaultJVMName());
			} else {
				fJRECombo.selectItem(selectedItem);
			}
		}
		
		private IVMInstall[] getWorkspaceJREs() {
			List standins = new ArrayList();
			IVMInstallType[] types = RubyRuntime.getVMInstallTypes();
			for (int i = 0; i < types.length; i++) {
				IVMInstallType type = types[i];
				IVMInstall[] installs = type.getVMInstalls();
				for (int j = 0; j < installs.length; j++) {
					IVMInstall install = installs[j];
					standins.add(new VMStandin(install));
				}
			}
			return ((IVMInstall[])standins.toArray(new IVMInstall[standins.size()]));	
		}

		private String getDefaultJVMName() {
			IVMInstall vm = RubyRuntime.getDefaultVMInstall();
			if (vm == null) return "";
			return vm.getName();
		}

		private String getDefaultJVMLabel() {
			return Messages.format(NewWizardMessages.RubyProjectWizardFirstPage_JREGroup_default_compliance, getDefaultJVMName());
		}

		public void update(Observable o, Object arg) {
			updateEnableState();
		}

		private void updateEnableState() {
			final boolean detect= fDetectGroup.mustDetect();
			fUseDefaultJRE.setEnabled(!detect);
			fUseProjectJRE.setEnabled(!detect);
			fJRECombo.setEnabled(!detect && fUseProjectJRE.isSelected());
			fPreferenceLink.setEnabled(!detect);
			fGroup.setEnabled(!detect);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			String jreID= BuildPathSupport.JRE_PREF_PAGE_ID;
//			String complianceId= CompliancePreferencePage.PREF_ID;
			Map data= new HashMap();
			data.put(PropertyAndPreferencePage.DATA_NO_LINK, Boolean.TRUE);
			PreferencesUtil.createPreferenceDialogOn(getShell(), jreID, new String[] { jreID  }, data).open();
			
			handlePossibleJVMChange();
			fDetectGroup.handlePossibleJVMChange();
		}
		
		public void handlePossibleJVMChange() {
			fUseDefaultJRE.setLabelText(getDefaultJVMLabel());
			fillInstalledJREs(fJRECombo);
		}
		

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
		 */
		public void dialogFieldChanged(DialogField field) {
			updateEnableState();
			fDetectGroup.handlePossibleJVMChange();
		}
		
		public boolean isUseSpecific() {
			return fUseProjectJRE.isSelected();
		}
		
		public IVMInstall getSelectedJVM() {
			if (fUseProjectJRE.isSelected()) {
				int index= fJRECombo.getSelectionIndex();
				if (index >= 0 && index < fInstalledJVMs.length) { // paranoia
					return fInstalledJVMs[index];
				}
			}
			return null;
		}
		
		public String getSelectedCompilerCompliance() {
//			if (fUseProjectJRE.isSelected()) {
//				int index= fJRECombo.getSelectionIndex();
//				if (index >= 0 && index < fComplianceData.length) { // paranoia
//					return fComplianceData[index];
//				}
//			}
			return null;
		}
	}

	
	/**
	 * Show a warning when the project location contains files.
	 */
	private final class DetectGroup extends Observable implements Observer, SelectionListener {

		private final Link fHintText;
		private boolean fDetect;
		
		public DetectGroup(Composite composite) {
			
			Link jre50Text= new Link(composite, SWT.WRAP);
			jre50Text.setFont(composite.getFont());
			jre50Text.addSelectionListener(this);
			GridData gridData= new GridData(GridData.FILL, SWT.FILL, true, true);
			gridData.widthHint= convertWidthInCharsToPixels(50);
			jre50Text.setLayoutData(gridData);
			fHintText= jre50Text;
			
			handlePossibleJVMChange();
		}
		
		public void handlePossibleJVMChange() {
		}
		
		public void update(Observable o, Object arg) {
			if (o instanceof LocationGroup) {
				boolean oldDetectState= fDetect;
				if (fLocationGroup.isInWorkspace()) {
					String name= getProjectName();
					if (name.length() == 0 || RubyPlugin.getWorkspace().getRoot().findMember(name) != null) {
						fDetect= false;
					} else {
						final File directory= fLocationGroup.getLocation().append(getProjectName()).toFile();
						fDetect= directory.isDirectory();
					}
				} else {
					final File directory= fLocationGroup.getLocation().toFile();
					fDetect= directory.isDirectory();
				}
				
				if (oldDetectState != fDetect) {
					setChanged();
					notifyObservers();
					
					if (fDetect) {
						fHintText.setVisible(true);
						fHintText.setText(NewWizardMessages.RubyProjectWizardFirstPage_DetectGroup_message);
					} else {
						handlePossibleJVMChange();
					}
				}
			}
		}

		public boolean mustDetect() {
			return fDetect;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			String jreID= BuildPathSupport.JRE_PREF_PAGE_ID;
//			String complianceId= CompliancePreferencePage.PREF_ID;
			Map data= new HashMap();
			data.put(PropertyAndPreferencePage.DATA_NO_LINK, Boolean.TRUE);
			PreferencesUtil.createPreferenceDialogOn(getShell(), jreID, new String[] { jreID  }, data).open();
			
			fJREGroup.handlePossibleJVMChange();
			handlePossibleJVMChange();
		}
	}

	/**
	 * Validate this page and show appropriate warnings and error NewWizardMessages.
	 */
	private final class Validator implements Observer {

		public void update(Observable o, Object arg) {

			final IWorkspace workspace= RubyPlugin.getWorkspace();

			final String name= fNameGroup.getName();

			// check whether the project name field is empty
			if (name.length() == 0) { 
				setErrorMessage(null);
				setMessage(NewWizardMessages.RubyProjectWizardFirstPage_Message_enterProjectName); 
				setPageComplete(false);
				return;
			}

			// check whether the project name is valid
			final IStatus nameStatus= workspace.validateName(name, IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}

			// check whether project already exists
			final IProject handle= getProjectHandle();
			if (handle.exists()) {
				setErrorMessage(NewWizardMessages.RubyProjectWizardFirstPage_Message_projectAlreadyExists); 
				setPageComplete(false);
				return;
			}

			final String location= fLocationGroup.getLocation().toOSString();

			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(null);
				setMessage(NewWizardMessages.RubyProjectWizardFirstPage_Message_enterLocation); 
				setPageComplete(false);
				return;
			}

			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) { 
				setErrorMessage(NewWizardMessages.RubyProjectWizardFirstPage_Message_invalidDirectory); 
				setPageComplete(false);
				return;
			}

			// check whether the location has the workspace as prefix
			IPath projectPath= Path.fromOSString(location);
			if (!fLocationGroup.isInWorkspace() && Platform.getLocation().isPrefixOf(projectPath)) {
				setErrorMessage(NewWizardMessages.RubyProjectWizardFirstPage_Message_cannotCreateInWorkspace); 
				setPageComplete(false);
				return;
			}

			// If we do not place the contents in the workspace validate the
			// location.
			if (!fLocationGroup.isInWorkspace()) {
				final IStatus locationStatus= workspace.validateProjectLocation(handle, projectPath);
				if (!locationStatus.isOK()) {
					setErrorMessage(locationStatus.getMessage());
					setPageComplete(false);
					return;
				}
			}
			
			setPageComplete(true);

			setErrorMessage(null);
			setMessage(null);
		}

	}

	private NameGroup fNameGroup;
	private LocationGroup fLocationGroup;
	private JREGroup fJREGroup;
	private DetectGroup fDetectGroup;
	private Validator fValidator;

	private String fInitialName;
	
	private static final String PAGE_NAME= NewWizardMessages.RubyProjectWizardFirstPage_page_pageName; 

	/**
	 * Create a new <code>SimpleProjectFirstPage</code>.
	 */
	public RubyProjectWizardFirstPage() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle(NewWizardMessages.RubyProjectWizardFirstPage_page_title); 
		setDescription(NewWizardMessages.RubyProjectWizardFirstPage_page_description); 
		fInitialName= ""; //$NON-NLS-1$
		initializeDefaultVM();
	}
	
	private void initializeDefaultVM() {
		RubyRuntime.getDefaultVMInstall();
	}
	
	public void setName(String name) {
		fInitialName= name;
		if (fNameGroup != null) {
			fNameGroup.setName(name);
		}
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final Composite composite= new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		// create UI elements
		fNameGroup= new NameGroup(composite, fInitialName);
		fLocationGroup= new LocationGroup(composite);
		fJREGroup= new JREGroup(composite);
		fDetectGroup= new DetectGroup(composite);
		
		// establish connections
		fNameGroup.addObserver(fLocationGroup);
		fDetectGroup.addObserver(fJREGroup);
		fLocationGroup.addObserver(fDetectGroup);

		// initialize all elements
		fNameGroup.notifyObservers();
		
		// create and connect validator
		fValidator= new Validator();
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);

		setControl(composite);
		Dialog.applyDialogFont(composite);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IRubyHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);
	}	

	/**
	 * Returns the current project location path as entered by the user, or its
	 * anticipated initial value. Note that if the default has been returned
	 * the path in a project description used to create a project should not be
	 * set.
	 * <p>
	 * TODO At some point this method has to be converted to return an URI instead
	 * of an path. However, this first requires support from Platform/UI to specify
	 * a project location different than in a local file system. 
	 * </p>
	 * @return the project location path or its anticipated initial value.
	 */
	public IPath getLocationPath() {
		return fLocationGroup.getLocation();
	}


	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 * <p>
	 * This method does not create the project resource; this is the
	 * responsibility of <code>IProject::create</code> invoked by the new
	 * project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(fNameGroup.getName());
	}
	
	public boolean isInWorkspace() {
		return fLocationGroup.isInWorkspace();
	}
	
	public String getProjectName() {
		return fNameGroup.getName();
	}

	public boolean getDetect() {
		return fDetectGroup.mustDetect();
	}
	
	public boolean isSrcBin() {
		return false;
	}
	
	/**
	 * @return the selected JVM, or <code>null</code> iff the default JVM should be used
	 */
	public IVMInstall getJVM() {
		return fJREGroup.getSelectedJVM();
	}
	
	/**
	 * @return the selected Compiler Compliance, or <code>null</code> iff the default Compiler Compliance should be used
	 */
	public String getCompilerCompliance() {
		return fJREGroup.getSelectedCompilerCompliance();
	}
	
	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fNameGroup.postSetFocus();
		}
	}
		
	/**
	 * Initialize a grid layout with the default Dialog settings.
	 */
	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth= convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth= 0;
			layout.marginHeight= 0;
		}
		return layout;
	}
	
	/**
	 * Set the layout data for a button.
	 */
	protected GridData setButtonLayoutData(Button button) {
		return super.setButtonLayoutData(button);
	}
}
