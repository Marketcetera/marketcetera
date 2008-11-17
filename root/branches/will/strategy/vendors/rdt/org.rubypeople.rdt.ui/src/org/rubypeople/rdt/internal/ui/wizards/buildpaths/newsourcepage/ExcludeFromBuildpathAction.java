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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.CPListElement;

public class ExcludeFromBuildpathAction extends Action implements ISelectionChangedListener {

	private final IWorkbenchSite fSite;
	private final List fSelectedElements; //ISourceFolder || IRubyScript iff isEnabled()
	
	public ExcludeFromBuildpathAction(IWorkbenchSite site) {
		super(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_label, RubyPluginImages.DESC_ELCL_EXCLUDE_FROM_BUILDPATH);
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_tooltip);
		setDisabledImageDescriptor(RubyPluginImages.DESC_DLCL_EXCLUDE_FROM_BUILDPATH);
		fSite= site;
		fSelectedElements= new ArrayList();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {
        final IRubyProject project;
        Object object= fSelectedElements.get(0);
        if (object instanceof IRubyScript) {
        	project= ((IRubyScript)object).getRubyProject();
        } else {
        	project= ((ISourceFolder)object).getRubyProject();
        }
        
        try {
			final IRunnableWithProgress runnable= new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
				        List result= exclude(fSelectedElements, project, monitor);
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
	
	private List exclude(List javaElements, IRubyProject project, IProgressMonitor monitor) throws RubyModelException {
		if (monitor == null)
			monitor= new NullProgressMonitor();
		try {
			monitor.beginTask(NewWizardMessages.LoadpathModifier_Monitor_Excluding, javaElements.size() + 4); 

			List existingEntries= LoadpathModifier.getExistingEntries(project);
			List resources= new ArrayList();
			for (int i= 0; i < javaElements.size(); i++) {
				IRubyElement javaElement= (IRubyElement) javaElements.get(i);
				ISourceFolderRoot root= (ISourceFolderRoot) javaElement.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
				CPListElement entry= LoadpathModifier.getLoadpathEntry(existingEntries, root);

				IResource resource= LoadpathModifier.exclude(javaElement, entry, project, new SubProgressMonitor(monitor, 1));
				if (resource != null) {
					resources.add(resource);
				}
			}

			LoadpathModifier.commitLoadPath(existingEntries, project, new SubProgressMonitor(monitor, 4));
			return resources;
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
				if (element instanceof ISourceFolder) {
					int type= DialogPackageExplorerActionGroup.getType(element, ((ISourceFolder)element).getRubyProject());
					if (type != DialogPackageExplorerActionGroup.INCLUDED_FOLDER && type != DialogPackageExplorerActionGroup.SOURCE_FOLDER)
						return false;
				} else if (element instanceof IRubyScript) {
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
		showError(exception, fSite.getShell(), NewWizardMessages.ExcludeFromBuildathAction_ErrorTitle, exception.getMessage());
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
