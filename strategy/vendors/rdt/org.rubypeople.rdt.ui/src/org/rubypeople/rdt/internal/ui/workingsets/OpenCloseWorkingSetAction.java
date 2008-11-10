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
package org.rubypeople.rdt.internal.ui.workingsets;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDEActionFactory;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.ui.actions.SelectionDispatchAction;

public abstract class OpenCloseWorkingSetAction extends SelectionDispatchAction implements IResourceChangeListener {

	private static final class CloseWorkingSetAction extends OpenCloseWorkingSetAction {
		private IAction fProjectAction;
		private CloseWorkingSetAction(IWorkbenchSite site, String label) {
			super(site, label);
			IActionBars actionBars= getActionBars();
			if (actionBars != null) {
				fProjectAction= actionBars.getGlobalActionHandler(IDEActionFactory.CLOSE_PROJECT.getId());
			}
		}
		protected boolean validate(IProject project) {
			return project.isOpen();
		}
		protected void performOperation(IProject project, IProgressMonitor monitor) throws CoreException {
			project.close(monitor);
		}
		protected void connectToActionBar(IActionBars actionBars) {
			actionBars.setGlobalActionHandler(IDEActionFactory.CLOSE_PROJECT.getId(), this);
			actionBars.updateActionBars();
		}
		protected void disconnectFromActionBar(IActionBars actionBars) {
			actionBars.setGlobalActionHandler(IDEActionFactory.CLOSE_PROJECT.getId(), fProjectAction);
			actionBars.updateActionBars();
		}
		protected String getErrorTitle() {
			return WorkingSetMessages.OpenCloseWorkingSetAction_close_error_title; 
		}
		protected String getErrorMessage() {
			return WorkingSetMessages.OpenCloseWorkingSetAction_close_error_message; 
		}
	}

	private static final class OpenWorkingSetAction extends OpenCloseWorkingSetAction {
		private IAction fProjectAction;
		private OpenWorkingSetAction(IWorkbenchSite site, String label) {
			super(site, label);
			IActionBars actionBars= getActionBars();
			if (actionBars != null) {
				fProjectAction= actionBars.getGlobalActionHandler(IDEActionFactory.OPEN_PROJECT.getId());
			}
		}
		protected boolean validate(IProject project) {
			return !project.isOpen();
		}
		protected void performOperation(IProject project, IProgressMonitor monitor) throws CoreException {
			project.open(monitor);
		}
		protected void connectToActionBar(IActionBars actionBars) {
			actionBars.setGlobalActionHandler(IDEActionFactory.OPEN_PROJECT.getId(), this);
			actionBars.updateActionBars();
		}
		protected void disconnectFromActionBar(IActionBars actionBars) {
			actionBars.setGlobalActionHandler(IDEActionFactory.OPEN_PROJECT.getId(), fProjectAction);
			actionBars.updateActionBars();
		}
		protected String getErrorTitle() {
			return WorkingSetMessages.OpenCloseWorkingSetAction_open_error_title; 
		}
		protected String getErrorMessage() {
			return WorkingSetMessages.OpenCloseWorkingSetAction_open_error_message; 
		}
	}
	
	private OpenCloseWorkingSetAction(IWorkbenchSite site, String label) {
		super(site);
		setText(label);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}
	
	public static OpenCloseWorkingSetAction createCloseAction(IWorkbenchSite site) {
		return new CloseWorkingSetAction(site, WorkingSetMessages.OpenCloseWorkingSetAction_close_label);
	}

	public static OpenCloseWorkingSetAction createOpenAction(IWorkbenchSite site) {
		return new OpenWorkingSetAction(site, WorkingSetMessages.OpenCloseWorkingSetAction_open_label);
	}
	
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public void selectionChanged(IStructuredSelection selection) {
		List projects= getProjects(selection);
		IActionBars actionBars= getActionBars();
		if (projects != null && projects.size() > 0) {
			setEnabled(true);
			if (actionBars != null) {
				connectToActionBar(actionBars);
			}
		} else {
			setEnabled(false);
			if (actionBars != null) {
				disconnectFromActionBar(actionBars);
			}
		}
	}
	
	public void run(IStructuredSelection selection) {
		final List projects= getProjects(selection);
		if (projects != null && projects.size() > 0) {
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(
					new WorkbenchRunnableAdapter(new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							monitor.beginTask("", projects.size()); //$NON-NLS-1$
							for (Iterator iter= projects.iterator(); iter.hasNext();) {
								IProject project= (IProject)iter.next();
								performOperation(project, new SubProgressMonitor(monitor, 1));
							}
							monitor.done();
						}
					}));
			} catch (InvocationTargetException e) {
				ExceptionHandler.handle(e, getShell(), getErrorTitle(), getErrorMessage());
			} catch (InterruptedException e) {
				// do nothing. Got cancelled.
			}
		}
	}
	
	protected abstract boolean validate(IProject project);
	
	protected abstract void performOperation(IProject project, IProgressMonitor monitor) throws CoreException;
	
	protected abstract void connectToActionBar(IActionBars actionBars);
	
	protected abstract void disconnectFromActionBar(IActionBars actionBars);
	
	protected abstract String getErrorTitle();

	protected abstract String getErrorMessage();
	
	private List getProjects(IStructuredSelection selection) {
		List result= new ArrayList();
		List elements= selection.toList();
		for (Iterator iter= elements.iterator(); iter.hasNext();) {
			Object element= iter.next();
			if (!(element instanceof IWorkingSet))
				return null;
			List projects= getProjects((IWorkingSet)element);
			if (projects == null)
				return null;
			result.addAll(projects);
		}
		return result;
	}

	private List getProjects(IWorkingSet set) {
		List result= new ArrayList();
		IAdaptable[] elements= set.getElements();
		for (int i= 0; i < elements.length; i++) {
			Object element= elements[i];
			IProject project= null;
			if (element instanceof IProject) {
				project= (IProject)element;
			} else if (element instanceof IRubyProject) {
				project= ((IRubyProject)element).getProject();
			}
			if (project != null && validate(project))
				result.add(project);
		}
		return result;
	}
	
	protected IActionBars getActionBars() {
		if (getSite() instanceof IViewSite) {
			return ((IViewSite)getSite()).getActionBars();
		} else {
			return null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null) {
			IResourceDelta[] projDeltas = delta.getAffectedChildren(IResourceDelta.CHANGED);
			for (int i = 0; i < projDeltas.length; ++i) {
				IResourceDelta projDelta = projDeltas[i];
				if ((projDelta.getFlags() & IResourceDelta.OPEN) != 0) {
					Shell shell= getShell();
					if (!shell.isDisposed()) {
						shell.getDisplay().asyncExec(new Runnable() {
							public void run() {
								update(getSelection());
							}
						});
					}
					return;
				}
			}
		}
	}
}