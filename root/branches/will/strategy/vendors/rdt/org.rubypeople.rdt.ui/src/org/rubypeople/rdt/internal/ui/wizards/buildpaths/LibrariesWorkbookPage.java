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
 *******************************************************************************
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ITreeListAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.TreeListDialogField;
import org.rubypeople.rdt.ui.wizards.BuildPathDialogAccess;

public class LibrariesWorkbookPage extends BuildPathBasePage {
	
	private ListDialogField fClassPathList;
	private IRubyProject fCurrJProject;
	
	private TreeListDialogField fLibrariesList;
	
	private Control fSWTControl;

	private final int IDX_ADDEXT= 0;
	private final int IDX_ADDVAR= 1;
	private final int IDX_EDIT= 3;
	private final int IDX_REMOVE= 4;
		
	public LibrariesWorkbookPage(CheckedListDialogField classPathList, IWorkbenchPreferenceContainer pageContainer) {
		fClassPathList= classPathList;
		fSWTControl= null;
		
		String[] buttonLabels= new String[] { 
			NewWizardMessages.LibrariesWorkbookPage_libraries_addextjar_button, 
			NewWizardMessages.LibrariesWorkbookPage_libraries_addvariable_button,
			/* */ null,  
			NewWizardMessages.LibrariesWorkbookPage_libraries_edit_button, 
			NewWizardMessages.LibrariesWorkbookPage_libraries_remove_button,
		};		
				
		LibrariesAdapter adapter= new LibrariesAdapter();
				
		fLibrariesList= new TreeListDialogField(adapter, buttonLabels, new CPListLabelProvider());
		fLibrariesList.setDialogFieldListener(adapter);
		fLibrariesList.setLabelText(NewWizardMessages.LibrariesWorkbookPage_libraries_label); 

		fLibrariesList.enableButton(IDX_REMOVE, false);
		fLibrariesList.enableButton(IDX_EDIT, false);

		fLibrariesList.setViewerSorter(new CPListElementSorter());

	}
		
	public void init(IRubyProject jproject) {
		fCurrJProject= jproject;
		updateLibrariesList();
	}
	
	
	private void updateLibrariesList() {
		List cpelements= fClassPathList.getElements();
		List libelements= new ArrayList(cpelements.size());
		
		int nElements= cpelements.size();
		for (int i= 0; i < nElements; i++) {
			CPListElement cpe= (CPListElement)cpelements.get(i);
			if (isEntryKind(cpe.getEntryKind())) {
				libelements.add(cpe);
			}
		}
		fLibrariesList.setElements(libelements);
	}		
		
	// -------- UI creation
	
	public Control getControl(Composite parent) {
		PixelConverter converter= new PixelConverter(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
			
		LayoutUtil.doDefaultLayout(composite, new DialogField[] { fLibrariesList }, true, SWT.DEFAULT, SWT.DEFAULT);
		LayoutUtil.setHorizontalGrabbing(fLibrariesList.getTreeControl(null));
		
		int buttonBarWidth= converter.convertWidthInCharsToPixels(24);
		fLibrariesList.setButtonsMinWidth(buttonBarWidth);
		
		fLibrariesList.getTreeViewer().setSorter(new CPListElementSorter());
		
		fSWTControl= composite;
				
		return composite;
	}
	
	private Shell getShell() {
		if (fSWTControl != null) {
			return fSWTControl.getShell();
		}
		return RubyPlugin.getActiveWorkbenchShell();
	}
	
	
	private class LibrariesAdapter implements IDialogFieldListener, ITreeListAdapter {
		
		private final Object[] EMPTY_ARR= new Object[0];
		
		// -------- IListAdapter --------
		public void customButtonPressed(TreeListDialogField field, int index) {
			libaryPageCustomButtonPressed(field, index);
		}
		
		public void selectionChanged(TreeListDialogField field) {
			libaryPageSelectionChanged(field);
		}
		
		public void doubleClicked(TreeListDialogField field) {
			libaryPageDoubleClicked(field);
		}
		
		public void keyPressed(TreeListDialogField field, KeyEvent event) {
			libaryPageKeyPressed(field, event);
		}

		public Object[] getChildren(TreeListDialogField field, Object element) {
			if (element instanceof CPListElement) {
				return ((CPListElement) element).getChildren(false);
			}
			return EMPTY_ARR;
		}

		public Object getParent(TreeListDialogField field, Object element) {
			if (element instanceof CPListElementAttribute) {
				return ((CPListElementAttribute) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(TreeListDialogField field, Object element) {
			return getChildren(field, element).length > 0;
		}		
			
		// ---------- IDialogFieldListener --------
	
		public void dialogFieldChanged(DialogField field) {
			libaryPageDialogFieldChanged(field);
		}
	}
	
	private void libaryPageCustomButtonPressed(DialogField field, int index) {
		CPListElement[] libentries= null;
		switch (index) {	
		case IDX_ADDEXT: /* add external folder */
			libentries= openExtFolderDialog(null);
			break;
		case IDX_ADDVAR: /* add variable */
			libentries= openVariableSelectionDialog(null);
			break;
		case IDX_EDIT: /* edit */
			editEntry();
			return;
		case IDX_REMOVE: /* remove */
			removeEntry();
			return;
		}
		if (libentries != null) {
			int nElementsChosen= libentries.length;					
			// remove duplicates
			List cplist= fLibrariesList.getElements();
			List elementsToAdd= new ArrayList(nElementsChosen);
			
			for (int i= 0; i < nElementsChosen; i++) {
				CPListElement curr= libentries[i];
				if (!cplist.contains(curr) && !elementsToAdd.contains(curr)) {
					elementsToAdd.add(curr);
				}
			}
			
			fLibrariesList.addElements(elementsToAdd);
			fLibrariesList.postSetSelection(new StructuredSelection(libentries));
		}
	}
	
	private CPListElement[] openExtFolderDialog(CPListElement existing) {
		if (existing == null) {
			IPath[] selected= BuildPathDialogAccess.chooseExternalFolderEntries(getShell());
			if (selected != null) {
				ArrayList res= new ArrayList();
				for (int i= 0; i < selected.length; i++) {
					res.add(new CPListElement(fCurrJProject, ILoadpathEntry.CPE_LIBRARY, selected[i], null));
				}
				return (CPListElement[]) res.toArray(new CPListElement[res.size()]);
			}
		} else {
			IPath configured= BuildPathDialogAccess.configureExternalFolderEntry(getShell(), existing.getPath());
			if (configured != null) {
				return new CPListElement[] { new CPListElement(fCurrJProject, ILoadpathEntry.CPE_LIBRARY, configured, null) };
			}
		}		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage#addElement(org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement)
	 */
	public void addElement(CPListElement element) {
		fLibrariesList.addElement(element);
		fLibrariesList.postSetSelection(new StructuredSelection(element));
	}
	
	protected void libaryPageDoubleClicked(TreeListDialogField field) {
		List selection= fLibrariesList.getSelectedElements();
		if (canEdit(selection)) {
			editEntry();
		}
	}

	protected void libaryPageKeyPressed(TreeListDialogField field, KeyEvent event) {
		if (field == fLibrariesList) {
			if (event.character == SWT.DEL && event.stateMask == 0) {
				List selection= field.getSelectedElements();
				if (canRemove(selection)) {
					removeEntry();
				}
			}
		}	
	}	

	private void removeEntry() {
		List selElements= fLibrariesList.getSelectedElements();
		HashMap containerEntriesToUpdate= new HashMap();
		for (int i= selElements.size() - 1; i >= 0 ; i--) {
			Object elem= selElements.get(i);
			if (elem instanceof CPListElementAttribute) {
				CPListElementAttribute attrib= (CPListElementAttribute) elem;
				String key= attrib.getKey();
				Object value= null;
				attrib.getParent().setAttribute(key, value);
				selElements.remove(i);
				
				if (attrib.getParent().getParentContainer() instanceof CPListElement) { // inside a container: apply changes right away
					CPListElement containerEntry= attrib.getParent();
					HashSet changedAttributes= (HashSet) containerEntriesToUpdate.get(containerEntry);
					if (changedAttributes == null) {
						changedAttributes= new HashSet();
						containerEntriesToUpdate.put(containerEntry, changedAttributes);
					}
					changedAttributes.add(key); // collect the changed attributes
				}
			}
		}
		if (selElements.isEmpty()) {
			fLibrariesList.refresh();
			fClassPathList.dialogFieldChanged(); // validate
		} else {
			fLibrariesList.removeElements(selElements);
		}
		for (Iterator iter= containerEntriesToUpdate.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entry= (Entry) iter.next();
			CPListElement curr= (CPListElement) entry.getKey();
			HashSet attribs= (HashSet) entry.getValue();
			String[] changedAttributes= (String[]) attribs.toArray(new String[attribs.size()]);
			ILoadpathEntry changedEntry= curr.getLoadpathEntry();
			updateContainerEntry(changedEntry, changedAttributes, fCurrJProject, ((CPListElement) curr.getParentContainer()).getPath());
		}
	}
	
	private boolean canRemove(List selElements) {
		if (selElements.size() == 0) {
			return false;
		}
		for (int i= 0; i < selElements.size(); i++) {
			Object elem= selElements.get(i);
			if (elem instanceof CPListElementAttribute) {
				CPListElementAttribute attrib= (CPListElementAttribute) elem;
				if (attrib.isInNonModifiableContainer()) {
					return false;
				}
				if (attrib.getValue() == null) {
					return false;
				}
			} else if (elem instanceof CPListElement) {
				CPListElement curr= (CPListElement) elem;
				if (curr.getParentContainer() != null) {
					return false;
				}
			} else { // unknown element
				return false;
			}
		}		
		return true;
	}	

	/**
	 * Method editEntry.
	 */
	private void editEntry() {
		List selElements= fLibrariesList.getSelectedElements();
		if (selElements.size() != 1) {
			return;
		}
		Object elem= selElements.get(0);
		if (fLibrariesList.getIndexOfElement(elem) != -1) {
			editElementEntry((CPListElement) elem);
		}
	}

	private void updateContainerEntry(final ILoadpathEntry newEntry, final String[] changedAttributes, final IRubyProject jproject, final IPath containerPath) {
		try {
			IWorkspaceRunnable runnable= new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {				
					BuildPathSupport.modifyLoadpathEntry(null, newEntry, changedAttributes, jproject, containerPath, monitor);
				}
			};
			PlatformUI.getWorkbench().getProgressService().run(true, true, new WorkbenchRunnableAdapter(runnable));

		} catch (InvocationTargetException e) {
			String title= NewWizardMessages.LibrariesWorkbookPage_configurecontainer_error_title; 
			String message= NewWizardMessages.LibrariesWorkbookPage_configurecontainer_error_message; 
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch (InterruptedException e) {
			// 
		}
	}
		
	private void editElementEntry(CPListElement elem) {
		CPListElement[] res= null;
		
		switch (elem.getEntryKind()) {
		case ILoadpathEntry.CPE_LIBRARY:
			IResource resource= elem.getResource();
			if (resource == null) {
				res= openExtFolderDialog(elem);
			} else if (resource.getType() == IResource.FOLDER) {
				if (resource.exists()) {
					res= openScriptFolderDialog(elem);
				} else {
					res= openNewScriptFolderDialog(elem);
				} 
			}
			break;
		case ILoadpathEntry.CPE_VARIABLE:
			res= openVariableSelectionDialog(elem);
			break;
		}
		if (res != null && res.length > 0) {
			CPListElement curr= res[0];
			curr.setExported(elem.isExported());
			fLibrariesList.replaceElement(elem, curr);
			if (elem.getEntryKind() == ILoadpathEntry.CPE_VARIABLE) {
				fLibrariesList.refresh();
			}
		}		
			
	}

	private void libaryPageSelectionChanged(DialogField field) {
		updateEnabledState();
	}

	private void updateEnabledState() {
		List selElements= fLibrariesList.getSelectedElements();
		fLibrariesList.enableButton(IDX_EDIT, canEdit(selElements));
		fLibrariesList.enableButton(IDX_REMOVE, canRemove(selElements));
	}
	
	private boolean canEdit(List selElements) {
		if (selElements.size() != 1) {
			return false;
		}
		Object elem= selElements.get(0);
		if (elem instanceof CPListElement) {
			CPListElement curr= (CPListElement) elem;
			return !(curr.getResource() instanceof IFolder) && curr.getParentContainer() == null;
		}
		if (elem instanceof CPListElementAttribute) {
			CPListElementAttribute attrib= (CPListElementAttribute) elem;
			if (attrib.isInNonModifiableContainer()) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private void libaryPageDialogFieldChanged(DialogField field) {
		if (fCurrJProject != null) {
			// already initialized
			updateLoadpathList();
		}
	}	
		
	private void updateLoadpathList() {
		List projelements= fLibrariesList.getElements();
		
		List cpelements= fClassPathList.getElements();
		int nEntries= cpelements.size();
		// backwards, as entries will be deleted
		int lastRemovePos= nEntries;
		for (int i= nEntries - 1; i >= 0; i--) {
			CPListElement cpe= (CPListElement)cpelements.get(i);
			int kind= cpe.getEntryKind();
			if (isEntryKind(kind)) {
				if (!projelements.remove(cpe)) {
					cpelements.remove(i);
					lastRemovePos= i;
				}	
			}
		}
		
		cpelements.addAll(lastRemovePos, projelements);

		if (lastRemovePos != nEntries || !projelements.isEmpty()) {
			fClassPathList.setElements(cpelements);
		}
	}
	
		
	private CPListElement[] openNewScriptFolderDialog(CPListElement existing) {
		String title= (existing == null) ? NewWizardMessages.LibrariesWorkbookPage_NewClassFolderDialog_new_title : NewWizardMessages.LibrariesWorkbookPage_NewClassFolderDialog_edit_title; 
		IProject currProject= fCurrJProject.getProject();
		
		NewContainerDialog dialog= new NewContainerDialog(getShell(), title, currProject, getUsedContainers(existing), existing);
		IPath projpath= currProject.getFullPath();
		dialog.setMessage(Messages.format(NewWizardMessages.LibrariesWorkbookPage_NewClassFolderDialog_description, projpath.toString())); 
		if (dialog.open() == Window.OK) {
			IFolder folder= dialog.getFolder();
			return new CPListElement[] { newCPLibraryElement(folder) };
		}
		return null;
	}
			
			
	private CPListElement[] openScriptFolderDialog(CPListElement existing) {
		if (existing == null) {
			IPath[] selected= BuildPathDialogAccess.chooseSourceFolderEntries(getShell(), fCurrJProject.getPath(), getUsedContainers(existing));
			if (selected != null) {
				IWorkspaceRoot root= fCurrJProject.getProject().getWorkspace().getRoot();
				ArrayList res= new ArrayList();
				for (int i= 0; i < selected.length; i++) {
					IPath curr= selected[i];
					IResource resource= root.findMember(curr);
					if (resource instanceof IContainer) {
						res.add(newCPLibraryElement(resource));
					}
				}
				return (CPListElement[]) res.toArray(new CPListElement[res.size()]);
			}
		} else {
			// disabled
		}		
		return null;
	}
	
	private IPath[] getUsedContainers(CPListElement existing) {
		ArrayList res= new ArrayList();		
		List cplist= fLibrariesList.getElements();
		for (int i= 0; i < cplist.size(); i++) {
			CPListElement elem= (CPListElement)cplist.get(i);
			if (elem.getEntryKind() == ILoadpathEntry.CPE_LIBRARY && (elem != existing)) {
				IResource resource= elem.getResource();
				if (resource instanceof IContainer && !resource.equals(existing)) {
					res.add(resource.getFullPath());
				}
			}
		}
		return (IPath[]) res.toArray(new IPath[res.size()]);
	}
	
	private CPListElement newCPLibraryElement(IResource res) {
		return new CPListElement(fCurrJProject, ILoadpathEntry.CPE_LIBRARY, res.getFullPath(), res);
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage#isEntryKind(int)
	 */
	public boolean isEntryKind(int kind) {
		return kind == ILoadpathEntry.CPE_LIBRARY || kind == ILoadpathEntry.CPE_VARIABLE || kind == ILoadpathEntry.CPE_CONTAINER;
	}
	
	/*
	 * @see BuildPathBasePage#getSelection
	 */
	public List getSelection() {
		return fLibrariesList.getSelectedElements();
	}

	/*
	 * @see BuildPathBasePage#setSelection
	 */	
	public void setSelection(List selElements, boolean expand) {
		fLibrariesList.selectElements(new StructuredSelection(selElements));
		if (expand) {
			for (int i= 0; i < selElements.size(); i++) {
				fLibrariesList.expandElement(selElements.get(i), 1);
			}
		}
	}	
	
	private CPListElement[] openVariableSelectionDialog(CPListElement existing) {
		List existingElements= fLibrariesList.getElements();
		ArrayList existingPaths= new ArrayList(existingElements.size());
		for (int i= 0; i < existingElements.size(); i++) {
			CPListElement elem= (CPListElement) existingElements.get(i);
			if (elem.getEntryKind() == ILoadpathEntry.CPE_VARIABLE) {
				existingPaths.add(elem.getPath());
			}
		}
		IPath[] existingPathsArray= (IPath[]) existingPaths.toArray(new IPath[existingPaths.size()]);
		
		if (existing == null) {
			IPath[] paths= BuildPathDialogAccess.chooseVariableEntries(getShell(), existingPathsArray);
			if (paths != null) {
				ArrayList result= new ArrayList();
				for (int i = 0; i < paths.length; i++) {
					CPListElement elem= new CPListElement(fCurrJProject, ILoadpathEntry.CPE_VARIABLE, paths[i], null);
					IPath resolvedPath= RubyCore.getResolvedVariablePath(paths[i]);
					elem.setIsMissing((resolvedPath == null) || !resolvedPath.toFile().exists());
					if (!existingElements.contains(elem)) {
						result.add(elem);
					}
				}
				return (CPListElement[]) result.toArray(new CPListElement[result.size()]);
			}
		} else {
			IPath path= BuildPathDialogAccess.configureVariableEntry(getShell(), existing.getPath(), existingPathsArray);
			if (path != null) {
				CPListElement elem= new CPListElement(fCurrJProject, ILoadpathEntry.CPE_VARIABLE, path, null);
				return new CPListElement[] { elem };
			}
		}
		return null;
	}
}
