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
package org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.packageview.LoadPathContainer;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierQueries.IRemoveLinkedFolderQuery;

public class RemoveFromBuildpathAction extends Action implements ISelectionChangedListener {

	private final IWorkbenchSite fSite;
	private List fSelectedElements; // ISourceFolderRoot || IRubyProject || LoadPathContainer iff isEnabled()

	public RemoveFromBuildpathAction(IWorkbenchSite site) {
		super(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_label, RubyPluginImages.DESC_ELCL_REMOVE_FROM_BP);
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_tooltip);
		fSite= site;
		fSelectedElements= new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		try {

			final IRubyProject project;
			Object object= fSelectedElements.get(0);
			if (object instanceof IRubyProject) {
				project= (IRubyProject)object;
			} else if (object instanceof ISourceFolderRoot) {
				ISourceFolderRoot root= (ISourceFolderRoot)object;
				project= root.getRubyProject();
			} else {
				LoadPathContainer container= (LoadPathContainer)object;
				project= container.getRubyProject();
			}

			final List elementsToRemove= new ArrayList();
			final List foldersToDelete= new ArrayList();
			queryToRemoveLinkedFolders(elementsToRemove, foldersToDelete);
		
			final IRunnableWithProgress runnable= new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask(NewWizardMessages.LoadpathModifier_Monitor_RemoveFromBuildpath, elementsToRemove.size() + foldersToDelete.size());
						List result= removeFromLoadpath(elementsToRemove, project, new SubProgressMonitor(monitor, elementsToRemove.size()));
						result.removeAll(foldersToDelete);
						deleteFolders(foldersToDelete, new SubProgressMonitor(monitor, foldersToDelete.size()));
						if (result.size() == 0)
							result.add(project);
						selectAndReveal(new StructuredSelection(result));
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};
			PlatformUI.getWorkbench().getProgressService().run(true, false, runnable);
			
		} catch (CoreException e) {
			showExceptionDialog(e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				showExceptionDialog((CoreException)e.getCause());
			} else {
				RubyPlugin.log(e);
			}
		} catch (InterruptedException e) {
		}
	}

	private void deleteFolders(List folders, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(NewWizardMessages.LoadpathModifier_Monitor_RemoveFromBuildpath, folders.size()); 

			for (Iterator iter= folders.iterator(); iter.hasNext();) {
				IFolder folder= (IFolder)iter.next();
				folder.delete(true, true, new SubProgressMonitor(monitor, 1));
			}
		} finally {
			monitor.done();
		}
	}

	private List removeFromLoadpath(List elements, IRubyProject project, IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(NewWizardMessages.LoadpathModifier_Monitor_RemoveFromBuildpath, elements.size() + 1); 

			List existingEntries= LoadpathModifier.getExistingEntries(project);
			List result= new ArrayList();
			
			for (int i= 0; i < elements.size(); i++) {
				Object element= elements.get(i);

				if (element instanceof IRubyProject) {
					Object res= LoadpathModifier.removeFromLoadpath((IRubyProject)element, existingEntries, new SubProgressMonitor(monitor, 1));
					result.add(res);

				} else if (element instanceof ISourceFolderRoot) {
					Object res= LoadpathModifier.removeFromLoadpath((ISourceFolderRoot)element, existingEntries, project, new SubProgressMonitor(monitor, 1));
					if (res != null)
						result.add(res);
				} else {
					existingEntries.remove(CPListElement.createFromExisting(((LoadPathContainer)element).getLoadpathEntry(), project));
				}
			}

			LoadpathModifier.commitLoadPath(existingEntries, project, new SubProgressMonitor(monitor, 1));

			return result;
		} finally {
			monitor.done();
		}
	}
	
	private void queryToRemoveLinkedFolders(final List elementsToRemove, final List foldersToDelete) throws RubyModelException {
		final Shell shell= fSite.getShell() != null ? fSite.getShell() : RubyPlugin.getActiveWorkbenchShell();
		for (Iterator iter= fSelectedElements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (element instanceof ISourceFolderRoot) {
				IFolder folder= getLinkedSourceFolder((ISourceFolderRoot)element);
				if (folder != null) {
					RemoveLinkedFolderDialog dialog= new RemoveLinkedFolderDialog(shell, folder);

					final int result= dialog.open() == Window.OK?dialog.getRemoveStatus():IRemoveLinkedFolderQuery.REMOVE_CANCEL;
					
					if (result != IRemoveLinkedFolderQuery.REMOVE_CANCEL) {
						if (result == IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH) {
							elementsToRemove.add(element);
						} else if (result == IRemoveLinkedFolderQuery.REMOVE_BUILD_PATH_AND_FOLDER) {
							elementsToRemove.add(element);
							foldersToDelete.add(folder);
						}
					}
				} else {
					elementsToRemove.add(element);
				}
			} else {
				elementsToRemove.add(element);
			}
		}
	}

	private IFolder getLinkedSourceFolder(ISourceFolderRoot root) throws RubyModelException {
//		if (root.getKind() != ISourceFolderRoot.K_SOURCE)
//			return null;

		final IResource resource= root.getCorrespondingResource();
		if (!(resource instanceof IFolder))
			return null;

		final IFolder folder= (IFolder) resource;
		if (!folder.isLinked())
			return null;

		return folder;
	}

	public void selectionChanged(final SelectionChangedEvent event) {
		final ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			setEnabled(canHandle((IStructuredSelection) selection));
		} else {
			setEnabled(canHandle(StructuredSelection.EMPTY));
		}
	}

	private boolean canHandle(IStructuredSelection elements) {
		if (elements.size() == 0)
			return false;

		try {
			fSelectedElements.clear();
			for (Iterator iter= elements.iterator(); iter.hasNext();) {
				Object element= iter.next();
				fSelectedElements.add(element);
				if (!(element instanceof ISourceFolderRoot || element instanceof IRubyProject || element instanceof LoadPathContainer))
					return false;

				if (element instanceof IRubyProject) {
					IRubyProject project= (IRubyProject)element;
					if (!LoadpathModifier.isSourceFolder(project))
						return false;

				} else if (element instanceof ISourceFolderRoot) {
					ILoadpathEntry entry= ((ISourceFolderRoot) element).getRawLoadpathEntry();
					if (entry != null && entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER) {
						return false;
					}
				}
			}
			return true;
		} catch (RubyModelException e) {
		}
		return false;
	}

	private void showExceptionDialog(CoreException exception) {
		showError(exception, fSite.getShell(), NewWizardMessages.RemoveFromBuildpathAction_ErrorTitle, exception.getMessage());
	}

	private void showError(CoreException e, Shell shell, String title, String message) {
		IStatus status= e.getStatus();
		if (status != null) {
			ErrorDialog.openError(shell, message, title, status);
		} else {
			MessageDialog.openError(shell, title, message);
		}
	}
	
	private void selectAndReveal(final ISelection selection) {
		// validate the input
		IWorkbenchPage page= fSite.getPage();
		if (page == null)
			return;

		// get all the view and editor parts
		List parts= new ArrayList();
		IWorkbenchPartReference refs[]= page.getViewReferences();
		for (int i= 0; i < refs.length; i++) {
			IWorkbenchPart part= refs[i].getPart(false);
			if (part != null)
				parts.add(part);
		}
		refs= page.getEditorReferences();
		for (int i= 0; i < refs.length; i++) {
			if (refs[i].getPart(false) != null)
				parts.add(refs[i].getPart(false));
		}

		Iterator itr= parts.iterator();
		while (itr.hasNext()) {
			IWorkbenchPart part= (IWorkbenchPart) itr.next();

			// get the part's ISetSelectionTarget implementation
			ISetSelectionTarget target= null;
			if (part instanceof ISetSelectionTarget)
				target= (ISetSelectionTarget) part;
			else
				target= (ISetSelectionTarget) part.getAdapter(ISetSelectionTarget.class);

			if (target != null) {
				// select and reveal resource
				final ISetSelectionTarget finalTarget= target;
				page.getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						finalTarget.selectReveal(selection);
					}
				});
			}
		}
	}

}
