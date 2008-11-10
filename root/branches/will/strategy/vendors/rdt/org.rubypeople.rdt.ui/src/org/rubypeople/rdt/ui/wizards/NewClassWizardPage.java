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
package org.rubypeople.rdt.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.SelectionButtonDialogFieldGroup;


/**
 * Wizard page to  create a new class. 
 * <p>
 * Note: This class is not intended to be subclassed, but clients can instantiate.
 * To implement a different kind of a new class wizard page, extend <code>NewTypeWizardPage</code>.
 * </p>
 * 
 * @since 0.9.0
 */
public class NewClassWizardPage extends NewTypeWizardPage {
	
	private final static String PAGE_NAME= "NewClassWizardPage"; //$NON-NLS-1$
	
	private final static String SETTINGS_CREATECONSTR= "create_constructor"; //$NON-NLS-1$
	
	private SelectionButtonDialogFieldGroup fMethodStubsButtons;
	
	/**
	 * Creates a new <code>NewClassWizardPage</code>
	 */
	public NewClassWizardPage() {
		super(true, PAGE_NAME);
		
		setTitle(NewWizardMessages.NewClassWizardPage_title); 
		setDescription(NewWizardMessages.NewClassWizardPage_description); 
		
		String[] buttonNames3= new String[] {
			NewWizardMessages.NewClassWizardPage_methods_constructors
		};		
		fMethodStubsButtons= new SelectionButtonDialogFieldGroup(SWT.CHECK, buttonNames3, 1);
		fMethodStubsButtons.setLabelText(NewWizardMessages.NewClassWizardPage_methods_label);		 
	}
	
	// -------- Initialization ---------
	
	/**
	 * The wizard owning this page is responsible for calling this method with the
	 * current selection. The selection is used to initialize the fields of the wizard 
	 * page.
	 * 
	 * @param selection used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		IRubyElement jelem= getInitialRubyElement(selection);
		initContainerPage(jelem);
		initTypePage(jelem);
		doStatusUpdate();
		
		boolean createConstructors= false;
		boolean createUnimplemented= true;
		IDialogSettings dialogSettings= getDialogSettings();
		if (dialogSettings != null) {
			IDialogSettings section= dialogSettings.getSection(PAGE_NAME);
			if (section != null) {
				createConstructors= section.getBoolean(SETTINGS_CREATECONSTR);
			}
		}
		
		setMethodStubSelection(createConstructors, true);
	}
	
	
	// ------ UI --------
	
	/*
	 * @see WizardPage#createControl
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		int nColumns= 4;
		
		GridLayout layout= new GridLayout();
		layout.numColumns= nColumns;		
		composite.setLayout(layout);
		
		// pick & choose the wanted UI components
		
		createContainerControls(composite, nColumns);	
//		createPackageControls(composite, nColumns);	
//		createEnclosingTypeControls(composite, nColumns);
				
		createSeparator(composite, nColumns);
		
		createTypeNameControls(composite, nColumns);
//		createModifierControls(composite, nColumns);
			
		createSuperClassControls(composite, nColumns);
//		createSuperInterfacesControls(composite, nColumns);
				
		createMethodStubSelectionControls(composite, nColumns);
		
//		createCommentControls(composite, nColumns);
//		enableCommentControl(true);
		
		setControl(composite);
			
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IRubyHelpContextIds.NEW_CLASS_WIZARD_PAGE);	
	}
	
	/*
	 * @see WizardPage#becomesVisible
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setFocus();
		} else {
			IDialogSettings dialogSettings= getDialogSettings();
			if (dialogSettings != null) {
				IDialogSettings section= dialogSettings.getSection(PAGE_NAME);
				if (section == null) {
					section= dialogSettings.addNewSection(PAGE_NAME);
				}
				section.put(SETTINGS_CREATECONSTR, isCreateConstructors());
			}
		}
	}	
	
	private void createMethodStubSelectionControls(Composite composite, int nColumns) {
		Control labelControl= fMethodStubsButtons.getLabelControl(composite);
		LayoutUtil.setHorizontalSpan(labelControl, nColumns);
		
		DialogField.createEmptySpace(composite);
		
		Control buttonGroup= fMethodStubsButtons.getSelectionButtonsGroup(composite);
		LayoutUtil.setHorizontalSpan(buttonGroup, nColumns - 1);	
	}
	
	/**
	 * Returns the current selection state of the 'Create Constructors' checkbox.
	 * 
	 * @return the selection state of the 'Create Constructors' checkbox
	 */
	public boolean isCreateConstructors() {
		return fMethodStubsButtons.isSelected(0);
	}
	
	/**
	 * Sets the selection state of the method stub checkboxes.
	 * 
	 * @param createMain initial selection state of the 'Create Main' checkbox.
	 * @param createConstructors initial selection state of the 'Create Constructors' checkbox.
	 * @param canBeModified if <code>true</code> the method stub checkboxes can be changed by 
	 * the user. If <code>false</code> the buttons are "read-only"
	 */
	public void setMethodStubSelection(boolean createConstructors, boolean canBeModified) {
		fMethodStubsButtons.setSelection(0, createConstructors);
		
		fMethodStubsButtons.setEnabled(canBeModified);
	}	
	
	// ---- creation ----------------
	
	/*
	 * @see NewTypeWizardPage#createTypeMembers
	 */
	protected void createTypeMembers(IType type, IProgressMonitor monitor) throws CoreException {
		boolean doConstr= isCreateConstructors();

		if (doConstr) {
			StringBuffer buf= new StringBuffer();
			final String lineDelim= "\n"; // OK, since content is formatted afterwards //$NON-NLS-1$
			buf.append("def initialize"); //$NON-NLS-1$
			buf.append(lineDelim);
			buf.append("super");
			buf.append(lineDelim);
			buf.append("end"); //$NON-NLS-1$
			buf.append(lineDelim);
			type.createMethod(buf.toString(), null, false, null);
		}
		
		if (monitor != null) {
			monitor.done();
		}	
	}
	
}
