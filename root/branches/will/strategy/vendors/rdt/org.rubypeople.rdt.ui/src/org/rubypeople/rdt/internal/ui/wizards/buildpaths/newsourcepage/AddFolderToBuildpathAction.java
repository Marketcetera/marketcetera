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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathBasePage;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;

public class AddFolderToBuildpathAction extends Action implements ISelectionChangedListener {

	private final IWorkbenchSite fSite;
	private final List fSelectedElements; //IRubyProject || IPackageFrament || IFolder

	public AddFolderToBuildpathAction(IWorkbenchSite site) {
		super(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddSelSFToCP_label, RubyPluginImages.DESC_OBJS_SOURCE_FOLDER_ROOT);
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddSelSFToCP_tooltip);
		fSite= site;
		fSelectedElements= new ArrayList();
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {

		final IRubyProject project;
		Object object= fSelectedElements.get(0);
		if (object instanceof IRubyProject) {
			project= (IRubyProject)object;
		} else if (object instanceof ISourceFolder) {
			project= ((ISourceFolder)object).getRubyProject();
		} else {
			IFolder folder= (IFolder)object;
			project= RubyCore.create(folder.getProject());
			if (project == null)
				return;
		}

		final Shell shell= fSite.getShell() != null ? fSite.getShell() : RubyPlugin.getActiveWorkbenchShell();

		IPath projPath= project.getProject().getFullPath();
				
		try {
			final IRunnableWithProgress runnable= new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						List result= addToLoadpath(fSelectedElements, project, null, false, false, monitor);
						selectAndReveal(new StructuredSelection(result));
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			PlatformUI.getWorkbench().getProgressService().run(true, false, runnable);
		} catch (final InvocationTargetException e) {
			if (e.getCause() instanceof CoreException) {
				showExceptionDialog((CoreException)e.getCause());
			} else {
				RubyPlugin.log(e);
			}
		} catch (final InterruptedException e) {
		}
	}

	private List addToLoadpath(List elements, IRubyProject project, IPath outputLocation, boolean removeProjectFromLoadpath, boolean removeOldClassFiles, IProgressMonitor monitor) throws OperationCanceledException, CoreException {
		if (!project.getProject().hasNature(RubyCore.NATURE_ID)) {
			StatusInfo rootStatus= new StatusInfo();
			rootStatus.setError(NewWizardMessages.LoadpathModifier_Error_NoNatures); 
			throw new CoreException(rootStatus);
		}
		
		try {
			monitor.beginTask(NewWizardMessages.LoadpathModifier_Monitor_AddToBuildpath, elements.size() + 4); 
			IWorkspaceRoot workspaceRoot= RubyPlugin.getWorkspace().getRoot();
					
			monitor.worked(1);			

			List existingEntries= LoadpathModifier.getExistingEntries(project);
			if (removeProjectFromLoadpath) {
				LoadpathModifier.removeFromLoadpath(project, existingEntries, new SubProgressMonitor(monitor, 1));
			} else {
				monitor.worked(1);
			}

			List newEntries= new ArrayList();
			for (int i= 0; i < elements.size(); i++) {
				Object element= elements.get(i);
				CPListElement entry;
				if (element instanceof IResource)
					entry= LoadpathModifier.addToLoadpath((IResource) element, existingEntries, newEntries, project, new SubProgressMonitor(monitor, 1));
				else
					entry= LoadpathModifier.addToLoadpath((IRubyElement) element, existingEntries, newEntries, project, new SubProgressMonitor(monitor, 1));
				newEntries.add(entry);
			}

			Set modifiedSourceEntries= new HashSet();
			BuildPathBasePage.fixNestingConflicts((CPListElement[])newEntries.toArray(new CPListElement[newEntries.size()]), (CPListElement[])existingEntries.toArray(new CPListElement[existingEntries.size()]), modifiedSourceEntries);

			LoadpathModifier.setNewEntry(existingEntries, newEntries, project, new SubProgressMonitor(monitor, 1));

			LoadpathModifier.commitLoadPath(existingEntries, project, new SubProgressMonitor(monitor, 1));

			List result= new ArrayList();
			for (int i= 0; i < newEntries.size(); i++) {
				ILoadpathEntry entry= ((CPListElement) newEntries.get(i)).getLoadpathEntry();
				IRubyElement root;
				if (entry.getPath().equals(project.getPath()))
					root= project;
				else
					root= project.findSourceFolderRoot(entry.getPath());
				if (root != null) {
					result.add(root);
				}
			}

			return result;
		} finally {
			monitor.done();
		}
	}

	/**
	 * {@inheritDoc}
	 */
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
				if (element instanceof IRubyProject) {
					if (LoadpathModifier.isSourceFolder((IRubyProject)element))
						return false;
				} else if (element instanceof ISourceFolder) {
					int type= DialogPackageExplorerActionGroup.getType(element, ((ISourceFolder)element).getRubyProject());
					if (type != DialogPackageExplorerActionGroup.SOURCE_FOLDER && type != DialogPackageExplorerActionGroup.INCLUDED_FOLDER)
						return false;
				} else if (element instanceof IFolder) {
					IProject project= ((IFolder)element).getProject();
					IRubyProject javaProject= RubyCore.create(project);
					if (javaProject == null || !javaProject.exists())
						return false;
				} else {
					return false;
				}
			}
			return true;
		} catch (CoreException e) {
		}
		return false;
	}

	private void showExceptionDialog(CoreException exception) {
		showError(exception, fSite.getShell(), NewWizardMessages.AddSourceFolderToBuildpathAction_ErrorTitle, exception.getMessage());
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
