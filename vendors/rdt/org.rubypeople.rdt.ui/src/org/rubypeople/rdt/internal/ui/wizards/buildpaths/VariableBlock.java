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
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.util.CoreUtility;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.launching.IGemRuntime;


public class VariableBlock {
	
	private ListDialogField fVariablesList;
	private Control fControl;
	private boolean fHasChanges;
	
	private List fSelectedElements;
	private boolean fAskToBuild;
	private boolean fInPreferencePage;
	
	
	/**
	 * Constructor for VariableBlock
	 */
	public VariableBlock(boolean inPreferencePage, String initSelection) {
		
		fSelectedElements= new ArrayList(0);
		fInPreferencePage= inPreferencePage;
		fAskToBuild= true;
		
		String[] buttonLabels= new String[] { 
			NewWizardMessages.VariableBlock_vars_add_button, 
			NewWizardMessages.VariableBlock_vars_edit_button, 
			NewWizardMessages.VariableBlock_vars_remove_button
		};
				
		VariablesAdapter adapter= new VariablesAdapter();
		
		CPVariableElementLabelProvider labelProvider= new CPVariableElementLabelProvider(!inPreferencePage);
		
		fVariablesList= new ListDialogField(adapter, buttonLabels, labelProvider);
		fVariablesList.setDialogFieldListener(adapter);
		fVariablesList.setLabelText(NewWizardMessages.VariableBlock_vars_label); 
		fVariablesList.setRemoveButtonIndex(2);
		
		fVariablesList.enableButton(1, false);
		
		fVariablesList.setViewerSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof CPVariableElement && e2 instanceof CPVariableElement) {
					return ((CPVariableElement)e1).getName().compareTo(((CPVariableElement)e2).getName());
				}
				return super.compare(viewer, e1, e2);
			}
		});
		refresh(initSelection);
	}
	
	public boolean hasChanges() {
		return fHasChanges;
	}
	
	public void setChanges(boolean hasChanges) {
		fHasChanges= hasChanges;
	}
	
	
	private String[] getReservedVariableNames() {
		return new String[] {
			RubyRuntime.RUBYLIB_VARIABLE, IGemRuntime.GEMLIB_VARIABLE
		};
	}
	
	public Control createContents(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fVariablesList }, true, 0, 0);
		LayoutUtil.setHorizontalGrabbing(fVariablesList.getListControl(null));
		
		fControl= composite;
		return composite;
	}
	
	public void addDoubleClickListener(IDoubleClickListener listener) {
		fVariablesList.getTableViewer().addDoubleClickListener(listener);
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fVariablesList.getTableViewer().addSelectionChangedListener(listener);
	}	
		
	
	private Shell getShell() {
		if (fControl != null) {
			return fControl.getShell();
		}
		return RubyPlugin.getActiveWorkbenchShell();
	}
	
	private class VariablesAdapter implements IDialogFieldListener, IListAdapter {
		
		// -------- IListAdapter --------
			
		public void customButtonPressed(ListDialogField field, int index) {
			switch (index) {
			case 0: /* add */
				editEntries(null);
				break;
			case 1: /* edit */
				List selected= field.getSelectedElements();			
				editEntries((CPVariableElement)selected.get(0));
				break;
			}
		}
		
		public void selectionChanged(ListDialogField field) {
			doSelectionChanged(field);
		}
		
		public void doubleClicked(ListDialogField field) {
			if (fInPreferencePage) {
				List selected= field.getSelectedElements();
				if (canEdit(selected, containsReserved(selected))) {
					editEntries((CPVariableElement) selected.get(0));
				}
			}
		}			
			
		// ---------- IDialogFieldListener --------
	
		public void dialogFieldChanged(DialogField field) {
		}
	
	}
	
	private boolean containsReserved(List selected) {
		for (int i= selected.size()-1; i >= 0; i--) {
			if (((CPVariableElement)selected.get(i)).isReserved()) {
				return true;
			}
		}
		return false;
	}
	
	private static void addAll(Object[] objs, Collection dest) {
		for (int i= 0; i < objs.length; i++) {
			dest.add(objs[i]);
		}
	}
	
	private boolean canEdit(List selected, boolean containsReserved) {
		return selected.size() == 1 && !containsReserved;
	}
		
	private void doSelectionChanged(DialogField field) {
		List selected= fVariablesList.getSelectedElements();
		boolean containsReserved= containsReserved(selected);
		
		// edit
		fVariablesList.enableButton(1, canEdit(selected, containsReserved));
		// remove button
		fVariablesList.enableButton(2, !containsReserved);
		
		fSelectedElements= selected;
	}
	
	private void editEntries(CPVariableElement entry) {
		List existingEntries= fVariablesList.getElements();

		VariableCreationDialog dialog= new VariableCreationDialog(getShell(), entry, existingEntries);
		if (dialog.open() != Window.OK) {
			return;
		}
		CPVariableElement newEntry= dialog.getClasspathElement();
		if (entry == null) {
			fVariablesList.addElement(newEntry);
			entry= newEntry;
			fHasChanges= true;
		} else {
			boolean hasChanges= !(entry.getName().equals(newEntry.getName()) && entry.getPath().equals(newEntry.getPath()));
			if (hasChanges) {
				fHasChanges= true;
				entry.setName(newEntry.getName());
				entry.setPath(newEntry.getPath());
				fVariablesList.refresh();
			}
		}
		fVariablesList.selectElements(new StructuredSelection(entry));
	}
	
	public List getSelectedElements() {
		return fSelectedElements;
	}
	
	
	public void performDefaults() {
		fVariablesList.removeAllElements();
		String[] reservedName= getReservedVariableNames();
		for (int i= 0; i < reservedName.length; i++) {
			CPVariableElement elem= new CPVariableElement(reservedName[i], new IPath[] {Path.EMPTY}, true);
			elem.setReserved(true);
			fVariablesList.addElement(elem);
		}
		fHasChanges= true;
	}

	public boolean performOk() {
		ArrayList removedVariables= new ArrayList();
		ArrayList changedVariables= new ArrayList();
		removedVariables.addAll(Arrays.asList(RubyCore.getLoadpathVariableNames()));

		// remove all unchanged
		List changedElements= fVariablesList.getElements();
		for (int i= changedElements.size()-1; i >= 0; i--) {
			CPVariableElement curr= (CPVariableElement) changedElements.get(i);
			if (curr.isReserved()) {
				changedElements.remove(curr);
			} else {
				IPath[] path= curr.getPath();
				IPath[] prevPath= RubyCore.getLoadpathVariable(curr.getName());
				if (prevPath != null && prevPath.equals(path)) {
					changedElements.remove(curr);
				} else {
					changedVariables.add(curr.getName());
				}
			}
			removedVariables.remove(curr.getName());
		}
		int steps= changedElements.size() + removedVariables.size();
		if (steps > 0) {
			
			boolean needsBuild= false;
			if (fAskToBuild && doesChangeRequireFullBuild(removedVariables, changedVariables)) {
				String title= NewWizardMessages.VariableBlock_needsbuild_title; 
				String message= NewWizardMessages.VariableBlock_needsbuild_message; 
				
				MessageDialog buildDialog= new MessageDialog(getShell(), title, null, message, MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 2);
				int res= buildDialog.open();
				if (res != 0 && res != 1) {
					return false;
				}
				needsBuild= (res == 0);
			}
			
			final VariableBlockRunnable runnable= new VariableBlockRunnable(removedVariables, changedElements);			
			final ProgressMonitorDialog dialog= new ProgressMonitorDialog(getShell());
			try {
				dialog.run(true, true, runnable);
			} catch (InvocationTargetException e) {
				ExceptionHandler.handle(new InvocationTargetException(new NullPointerException()), getShell(), NewWizardMessages.VariableBlock_variableSettingError_titel, NewWizardMessages.VariableBlock_variableSettingError_message);
				return false;
			} catch (InterruptedException e) {
				return false;
			}
			
			if (needsBuild) {
				CoreUtility.getBuildJob(null).schedule();
			}
		}
		return true;
	}
	
	private boolean doesChangeRequireFullBuild(List removed, List changed) {
		try {
			IRubyModel model= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot());
			IRubyProject[] projects= model.getRubyProjects();
			for (int i= 0; i < projects.length; i++) {
				ILoadpathEntry[] entries= projects[i].getRawLoadpath();
				for (int k= 0; k < entries.length; k++) {
					ILoadpathEntry curr= entries[k];
					if (curr.getEntryKind() == ILoadpathEntry.CPE_VARIABLE) {
						String var= curr.getPath().segment(0);
						if (removed.contains(var) || changed.contains(var)) {
							return true;
						}
					}
				}
			}
		} catch (RubyModelException e) {
			return true;
		}
		return false;
	}
	
	private class VariableBlockRunnable implements IRunnableWithProgress {
		private List fToRemove;
		private List fToChange;
		
		public VariableBlockRunnable(List toRemove, List toChange) {
			fToRemove= toRemove;
			fToChange= toChange;
		}
		
		/*
	 	 * @see IRunnableWithProgress#run(IProgressMonitor)
		 */
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask(NewWizardMessages.VariableBlock_operation_desc, 1); 
			try {
				setVariables(monitor);
				
			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} catch (OperationCanceledException e) {
				throw new InterruptedException();
			} finally {
				monitor.done();
			}
		}

		public void setVariables(IProgressMonitor monitor) throws RubyModelException, CoreException {
			int nVariables= fToChange.size() + fToRemove.size();
			
			String[] names= new String[nVariables];
			IPath[][] paths= new IPath[nVariables][];
			int k= 0;
			
			for (int i= 0; i < fToChange.size(); i++) {
				CPVariableElement curr= (CPVariableElement) fToChange.get(i);
				names[k]= curr.getName();
				paths[k]= curr.getPath();
				k++;
			}
			for (int i= 0; i < fToRemove.size(); i++) {
				names[k]= (String) fToRemove.get(i);
				paths[k]= null;
				k++;					
			}
			RubyCore.setLoadpathVariables(names, paths, new SubProgressMonitor(monitor, 1));
		}
	}
	
	/**
	 * If set to true, a dialog will ask the user to build on variable changed
	 * @param askToBuild The askToBuild to set
	 */
	public void setAskToBuild(boolean askToBuild) {
		fAskToBuild= askToBuild;
	}

	/**
	 * 
	 */
	public void refresh(String initSelection) {
		CPVariableElement initSelectedElement= null;
		
		String[] reservedName= getReservedVariableNames();
		ArrayList reserved= new ArrayList(reservedName.length);
		addAll(reservedName, reserved);
				
		String[] entries= RubyCore.getLoadpathVariableNames();
		ArrayList elements= new ArrayList(entries.length);
		for (int i= 0; i < entries.length; i++) {
			String name= entries[i];
			CPVariableElement elem;
			IPath[] entryPath= RubyCore.getLoadpathVariable(name);
			if (entryPath != null) {
				elem= new CPVariableElement(name, entryPath, reserved.contains(name));
				elements.add(elem);
				if (name.equals(initSelection)) {
					initSelectedElement= elem;
				}
			} else {				
				RubyPlugin.logErrorMessage("VariableBlock: Loadpath variable with null value: " + name); //$NON-NLS-1$
			}
		}
		
		fVariablesList.setElements(elements);
		
		if (initSelectedElement != null) {
			ISelection sel= new StructuredSelection(initSelectedElement);
			fVariablesList.selectElements(sel);
		} else {
			fVariablesList.selectFirstElement();
		}
		
		fHasChanges= false;
	}

}
