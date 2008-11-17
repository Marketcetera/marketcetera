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
package org.rubypeople.rdt.internal.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.corext.util.BusyIndicatorRunnableContext;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathsBlock;

/**
 * Property page for configuring the Ruby build path
 */
public class BuildPathsPropertyPage extends PropertyPage implements IStatusChangeListener {
	
	public static final String PROP_ID= "org.rubypeople.rdt.ui.propertyPages.BuildPathsPropertyPage"; //$NON-NLS-1$
		
	private static final String PAGE_SETTINGS= "BuildPathsPropertyPage"; //$NON-NLS-1$
	private static final String INDEX= "pageIndex"; //$NON-NLS-1$

	public static final Object DATA_ADD_ENTRY= "add_classpath_entry"; //$NON-NLS-1$
	
	public static final Object DATA_REVEAL_ENTRY= "select_classpath_entry"; //$NON-NLS-1$
	public static final Object DATA_REVEAL_ATTRIBUTE_KEY= "select_classpath_attribute_key"; //$NON-NLS-1$
	
	public static final Object DATA_BLOCK= "block_until_buildpath_applied"; //$NON-NLS-1$
		
	private BuildPathsBlock fBuildPathsBlock;
	private boolean fBlockOnApply= false;
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	protected Control createContents(Composite parent) {
		// ensure the page has no special buttons
		noDefaultAndApplyButton();		
		
		IProject project= getProject();
		Control result;
		if (project == null || !isRubyProject(project)) {
			result= createWithoutRuby(parent);
		} else if (!project.isOpen()) {
			result= createForClosedProject(parent);
		} else {
			result= createWithRuby(parent, project);
		}
		Dialog.applyDialogFont(result);
		return result;
	}
	
	/*
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IRubyHelpContextIds.BUILD_PATH_PROPERTY_PAGE);
	}	
	
	private IDialogSettings getSettings() {
		IDialogSettings javaSettings= RubyPlugin.getDefault().getDialogSettings();
		IDialogSettings pageSettings= javaSettings.getSection(PAGE_SETTINGS);
		if (pageSettings == null) {
			pageSettings= javaSettings.addNewSection(PAGE_SETTINGS);
			pageSettings.put(INDEX, 3);
		}
		return pageSettings;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (fBuildPathsBlock != null) {
			if (!visible) {
				if (fBuildPathsBlock.hasChangesInDialog()) {
					String title= PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_title; 
					String message= PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_message; 
					String[] buttonLabels= new String[] {
							PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_button_save, 
							PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_button_discard, 
							PreferencesMessages.BuildPathsPropertyPage_unsavedchanges_button_ignore
					};
					MessageDialog dialog= new MessageDialog(getShell(), title, null, message, MessageDialog.QUESTION, buttonLabels, 0);
					int res= dialog.open();
					if (res == 0) {
						performOk();
					} else if (res == 1) {
						fBuildPathsBlock.init(RubyCore.create(getProject()), null, null);
					} else {
						// keep unsaved
					}
				}
			} else {
				if (!fBuildPathsBlock.hasChangesInDialog() && fBuildPathsBlock.hasChangesInLoadpathFile()) {
					fBuildPathsBlock.init(RubyCore.create(getProject()), null, null);
				}
			}
		}
		super.setVisible(visible);
	}
	
	
	/*
	 * Content for valid projects.
	 */
	private Control createWithRuby(Composite parent, IProject project) {
		IWorkbenchPreferenceContainer pageContainer= null;	
		IPreferencePageContainer container= getContainer();
		if (container instanceof IWorkbenchPreferenceContainer) {
			pageContainer= (IWorkbenchPreferenceContainer) container;
		}
		
		fBuildPathsBlock= new BuildPathsBlock(new BusyIndicatorRunnableContext(), this, getSettings().getInt(INDEX), false, pageContainer);
		fBuildPathsBlock.init(RubyCore.create(project), null, null);
		return fBuildPathsBlock.createControl(parent);
	}

	/*
	 * Content for non-Ruby projects.
	 */	
	private Control createWithoutRuby(Composite parent) {
		Label label= new Label(parent, SWT.LEFT);
		label.setText(PreferencesMessages.BuildPathsPropertyPage_no_java_project_message); 
		
		fBuildPathsBlock= null;
		setValid(true);
		return label;
	}

	/*
	 * Content for closed projects.
	 */		
	private Control createForClosedProject(Composite parent) {
		Label label= new Label(parent, SWT.LEFT);
		label.setText(PreferencesMessages.BuildPathsPropertyPage_closed_project_message); 
		
		fBuildPathsBlock= null;
		setValid(true);
		return label;
	}
	
	private IProject getProject() {
		IAdaptable adaptable= getElement();
		if (adaptable != null) {
			IRubyElement elem= (IRubyElement) adaptable.getAdapter(IRubyElement.class);
			if (elem instanceof IRubyProject) {
				return ((IRubyProject) elem).getProject();
			}
		}
		return null;
	}
	
	private boolean isRubyProject(IProject proj) {
		try {
			return proj.hasNature(RubyCore.NATURE_ID);
		} catch (CoreException e) {
			RubyPlugin.log(e);
		}
		return false;
	}
	
	/*
	 * @see IPreferencePage#performOk
	 */
	public boolean performOk() {
		if (fBuildPathsBlock != null) {
			getSettings().put(INDEX, fBuildPathsBlock.getPageIndex());
			if (fBuildPathsBlock.hasChangesInDialog()) {
				IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
					public void run(IProgressMonitor monitor)	throws CoreException, OperationCanceledException {
						fBuildPathsBlock.configureRubyProject(monitor);
					}
				};
				WorkbenchRunnableAdapter op= new WorkbenchRunnableAdapter(runnable);
				if (fBlockOnApply) {
					try {
						new ProgressMonitorDialog(getShell()).run(true, true, op);
					} catch (InvocationTargetException e) {
						ExceptionHandler.handle(e, getShell(), PreferencesMessages.BuildPathsPropertyPage_error_title, PreferencesMessages.BuildPathsPropertyPage_error_message);
						return false;
					} catch (InterruptedException e) {
						return false;
					}
				} else {
					op.runAsUserJob(PreferencesMessages.BuildPathsPropertyPage_job_title, null);
				}
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see IStatusChangeListener#statusChanged
	 */
	public void statusChanged(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#applyData(java.lang.Object)
	 */
	public void applyData(Object data) {
		if (data instanceof Map) {
			Map map= (Map) data;
			Object selectedLibrary= map.get(DATA_REVEAL_ENTRY);
			if (selectedLibrary instanceof ILoadpathEntry) {
				ILoadpathEntry entry= (ILoadpathEntry) selectedLibrary;
				Object attr= map.get(DATA_REVEAL_ATTRIBUTE_KEY);
				String attributeKey= attr instanceof String ? (String) attr : null;
				if (fBuildPathsBlock != null) {
					fBuildPathsBlock.setElementToReveal(entry, attributeKey);
				}
			}
			Object entryToAdd= map.get(DATA_ADD_ENTRY);
			if (entryToAdd instanceof ILoadpathEntry) {
				if (fBuildPathsBlock != null) {
					fBuildPathsBlock.addElement((ILoadpathEntry) entryToAdd);
				}
			}
			fBlockOnApply= Boolean.TRUE.equals(map.get(DATA_BLOCK));
		}
	}

}
