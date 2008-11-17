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
package org.rubypeople.rdt.ui.wizards;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.rubypeople.rdt.internal.ui.IUIConstants;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.TypedViewerFilter;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.EditVariableEntryDialog;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.MultipleFolderSelectionDialog;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.NewVariableEntryDialog;

/**
 * Class that gives access to dialogs used by the Java build path page to configure classpath entries
 * and properties of classpath entries.
 * Static methods are provided to show dialogs for:
 * <ul>
 *  <li> configuration of source attachments</li>
 *  <li> configuration of Javadoc locations</li>
 *  <li> configuration and selection of classpath variable entries</li>
 *  <li> configuration and selection of classpath container entries</li>
 *  <li> configuration and selection of JAR and external JAR entries</li>
 *  <li> selection of class and source folders</li>
 * </ul>
 * <p>
 * This class is not intended to be instantiated or subclassed by clients.
 * </p>
 * @since 3.0
 */
public final class BuildPathDialogAccess {

	private BuildPathDialogAccess() {
		// do not instantiate
	}
	
	/**
	 * Shows the UI to select new source folders.
	 * The dialog returns the selected classpath entry paths or <code>null</code> if the dialog has
	 * been canceled The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialSelection The path of the element to initially select or <code>null</code>
	 * @param usedEntries An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns the configured classpath container entry path or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath[] chooseSourceFolderEntries(Shell shell, IPath initialSelection, IPath[] usedEntries) {
		if (usedEntries == null) {
			throw new IllegalArgumentException();
		}
		String title= NewWizardMessages.BuildPathDialogAccess_ExistingSourceFolderDialog_new_title; 
		String message= NewWizardMessages.BuildPathDialogAccess_ExistingSourceFolderDialog_new_description; 
		return internalChooseFolderEntry(shell, initialSelection, usedEntries, title, message);
	}
	
		
	private static IPath[] internalChooseFolderEntry(Shell shell, IPath initialSelection, IPath[] usedEntries, String title, String message) {	
		Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };

		ArrayList usedContainers= new ArrayList(usedEntries.length);
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < usedEntries.length; i++) {
			IResource resource= root.findMember(usedEntries[i]);
			if (resource instanceof IContainer) {
				usedContainers.add(resource);
			}
		}
		
		IResource focus= initialSelection != null ? root.findMember(initialSelection) : null;
		Object[] used= usedContainers.toArray();
		
		MultipleFolderSelectionDialog dialog= new MultipleFolderSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setExisting(used);
		dialog.setTitle(title); 
		dialog.setMessage(message); 
		dialog.setHelpAvailable(false);
		dialog.addFilter(new TypedViewerFilter(acceptedClasses, used));
		dialog.setInput(root);
		dialog.setInitialFocus(focus);
		
		if (dialog.open() == Window.OK) {
			Object[] elements= dialog.getResult();
			IPath[] res= new IPath[elements.length];
			for (int i= 0; i < res.length; i++) {
				IResource elem= (IResource) elements[i];
				res[i]= elem.getFullPath();
			}
			return res;
		}
		return null;		
	}

	/**
	 * Shows the UI to select new external JAR or ZIP archive entries.
	 * The dialog returns the selected entry paths or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @return Returns the new classpath container entry paths or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath[] chooseExternalFolderEntries(Shell shell) {
		String lastUsedPath= RubyPlugin.getDefault().getDialogSettings().get(IUIConstants.DIALOGSTORE_LASTEXTJAR);
		if (lastUsedPath == null) {
			lastUsedPath= ""; //$NON-NLS-1$
		}
		
		DirectoryDialog dialog= new DirectoryDialog(shell, SWT.MULTI);
		dialog.setText(NewWizardMessages.BuildPathDialogAccess_ExtJARArchiveDialog_new_title); 
		dialog.setFilterPath(lastUsedPath);
		
		String res= dialog.open();
		if (res == null) {
			return null;
		}
		String dirName = dialog.getText();
			
		IPath filterPath= Path.fromOSString(dialog.getFilterPath());
		IPath[] elems= { filterPath };

		RubyPlugin.getDefault().getDialogSettings().put(IUIConstants.DIALOGSTORE_LASTEXTJAR, dialog.getFilterPath());
		
		return elems;
	}

	/**
	 * Shows the UI to configure an external JAR or ZIP archive.
	 * The dialog returns the configured or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialEntry The path of the initial archive entry.
	 * @return Returns the configured classpath container entry path or <code>null</code> if the dialog has
	 * been canceled by the user.
	 */
	public static IPath configureExternalFolderEntry(Shell shell, IPath initialEntry) {
		if (initialEntry == null) {
			throw new IllegalArgumentException();
		}
		
		String lastUsedPath= initialEntry.removeLastSegments(1).toOSString();
		
		DirectoryDialog dialog= new DirectoryDialog(shell, SWT.SINGLE);
		dialog.setText(NewWizardMessages.BuildPathDialogAccess_ExtJARArchiveDialog_edit_title); 
//		dialog.setFilterExtensions(ArchiveFileFilter.FILTER_EXTENSIONS);
		dialog.setFilterPath(lastUsedPath);
//		dialog.setFileName(initialEntry.lastSegment());
		
		String res= dialog.open();
		if (res == null) {
			return null;
		}
		RubyPlugin.getDefault().getDialogSettings().put(IUIConstants.DIALOGSTORE_LASTEXTJAR, dialog.getFilterPath());

		return Path.fromOSString(res).makeAbsolute();	
	}

	/**
	 * Shows the UI for selecting new variable classpath entries. See {@link IClasspathEntry#CPE_VARIABLE} for
	 * details about variable classpath entries.
	 * The dialog returns an array of the selected variable entries or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param existingPaths An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns an non empty array of the selected variable entries or <code>null</code> if the dialog has
	 * been canceled.
	 */
	public static IPath[] chooseVariableEntries(Shell shell, IPath[] existingPaths) {
		if (existingPaths == null) {
			throw new IllegalArgumentException();
		}
		NewVariableEntryDialog dialog= new NewVariableEntryDialog(shell);
		if (dialog.open() == Window.OK) {
			return dialog.getResult();
		}
		return null;
	}
	
	/**
	 * Shows the UI for configuring a variable classpath entry. See {@link IClasspathEntry#CPE_VARIABLE} for
	 * details about variable classpath entries.
	 * The dialog returns the configured classpath entry path or <code>null</code> if the dialog has
	 * been canceled. The dialog does not apply any changes.
	 * 
	 * @param shell The parent shell for the dialog.
	 * @param initialEntryPath The initial variable classpath variable path or <code>null</code> to use
	 * an empty path. 
	 * @param existingPaths An array of paths that are already on the classpath and therefore should not be
	 * selected again.
	 * @return Returns the configures classpath entry path or <code>null</code> if the dialog has
	 * been canceled.
	 */
	public static IPath configureVariableEntry(Shell shell, IPath initialEntryPath, IPath[] existingPaths) {
		if (existingPaths == null) {
			throw new IllegalArgumentException();
		}
		
		EditVariableEntryDialog dialog= new EditVariableEntryDialog(shell, initialEntryPath, existingPaths);
		if (dialog.open() == Window.OK) {
			return dialog.getPath();
		}
		return null;
	}
}
