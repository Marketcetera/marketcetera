package org.rubypeople.rdt.internal.ui;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PerspectiveTracker;
import org.eclipse.ui.progress.UIJob;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.packageview.PackageExplorerPart;

/**
 * RubyExplorerTracker
 * 
 * @author Kevin Sawicki (added documentation)
 */
public class RubyExplorerTracker implements ISelectionChangedListener
{

	private IProject currentlySelectedProject;
	private Set<IRubyProjectListener> projectListeners;

	/**
	 * RubyExplorerTracker constructor
	 */
	public RubyExplorerTracker()
	{
		currentlySelectedProject = null;
		projectListeners = new HashSet<IRubyProjectListener>();
		listenToRubyExplorerSelectionChanges();
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (event == null)
			return;
		ISelection sel = event.getSelection();
		if (!(sel instanceof IStructuredSelection))
			return;
		IProject inProject = getProjectFromSelection(sel);
		if (inProject == null)
			return;
		if (!inProject.isOpen())
		{
			setSelectedProject(null);
		}
		else
		{
			setSelectedProject(inProject);
		}
	}

	/**
	 * Add project listener
	 * 
	 * @param listener
	 */
	public void addProjectListener(IRubyProjectListener listener)
	{
		projectListeners.add(listener);
		listener.projectSelected(currentlySelectedProject);
	}

	/**
	 * Remove project listener
	 * 
	 * @param listener
	 */
	public void removeProjectListener(IRubyProjectListener listener)
	{
		projectListeners.remove(listener);
	}

	private void notifyObservers()
	{
		for (IRubyProjectListener listener : projectListeners)
		{
			listener.projectSelected(currentlySelectedProject);
		}
	}

	private void setSelectedProject(IProject currentSelectedProject)
	{
		if (currentSelectedProject != this.currentlySelectedProject)
		{
			this.currentlySelectedProject = currentSelectedProject;
			notifyObservers();
		}
	}

	/**
	 * Gets the selected project
	 * 
	 * @return - selected project
	 */
	public IProject getSelectedProject()
	{
		return currentlySelectedProject;
	}

	/**
	 * Gets the selected project with the nature specified
	 * 
	 * @param natureId
	 * @return - project selected matching nature
	 */
	public IProject getSelectedByNatureID(String natureId)
	{
		try
		{
			if (currentlySelectedProject == null)
			{
				IViewPart part = PackageExplorerPart.getFromActivePerspective();
				if (part != null)
				{
					ISelection selection = part.getSite().getSelectionProvider().getSelection();
					currentlySelectedProject = getProjectFromSelection(selection);
				}
			}
			if ((currentlySelectedProject != null) && currentlySelectedProject.hasNature(natureId))
			{
				// Selected project nature matches
				return currentlySelectedProject;
			}
		}
		catch (CoreException e)
		{
			RubyPlugin.log(Status.ERROR, "CoreException getting project nature for project: "
					+ currentlySelectedProject.getName(), e);
		}
		// Project does not have desired nature.
		return null;
	}

	private static IProject getProjectFromSelection(ISelection sel)
	{
		if (!(sel instanceof IStructuredSelection))
			return null;

		IStructuredSelection selection = (IStructuredSelection) sel;

		if (selection == null || selection.getFirstElement() == null)
		{
			return null;
		}

		Object element = selection.getFirstElement();
		if (element instanceof IRubyElement)
		{
			return ((IRubyElement) selection.getFirstElement()).getRubyProject().getProject();
		}
		else if (element instanceof IResource)
		{
			return ((IResource) element).getProject();
		}
		return null;
	}

	/**
	 * Gets the selected ruby project
	 * 
	 * @return - ruby project
	 */
	public IProject getSelectedRubyProject()
	{
		return getSelectedByNatureID(RubyCore.NATURE_ID);
	}

	private void listenToRubyExplorerSelectionChanges()
	{
		// Hook onto Ruby Explorer to listen for project selection changes
		// HACK FIXME This will likely go haywire if user closes the Ruby Explorer and re-opens, etc.
		final ISelectionChangedListener listener = this;
		UIJob job = new UIJob("Add Rails project selection listener")
		{

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				final IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (dw != null)
				{
					IWorkbenchPage page = dw.getActivePage();
					if (page != null)
					{ // if we're in rails perspective, show the new view and hide the old
						IPerspectiveDescriptor desc = page.getPerspective();
						if (desc != null)
						{
							PackageExplorerPart explorer = PackageExplorerPart.getFromActivePerspective();
							if (explorer != null)
							{
								explorer.getSite().getSelectionProvider().addSelectionChangedListener(listener);
								return Status.OK_STATUS;
							}
						}
					}
					// if we're not in rails perspective, add listener to wait until we are
					new PerspectiveTracker(dw)
					{
						private boolean addedListener = false;

						@Override
						protected void update(IPerspectiveDescriptor persp)
						{
							if (persp == null)
								return;
							if (addedListener)
								return;
							String id = persp.getId();
							// if (!id.equals(IRailsUIConstants.ID_RAILS_PERSPECTIVE) &&
							// !id.equals(RubyUI.ID_PERSPECTIVE)) return;
							IWorkbenchPage page = dw.getActivePage();
							PackageExplorerPart explorer = PackageExplorerPart.getFromActivePerspective();
							if (explorer != null)
							{
								explorer.getSite().getSelectionProvider().addSelectionChangedListener(listener);
								addedListener = true;
							}
						}

					};
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/**
	 * IRubyProjectListener interface for project selected events
	 */
	public interface IRubyProjectListener
	{

		/**
		 * Indicates a project has been selected
		 * 
		 * @param project
		 */
		void projectSelected(IProject project);

	}

}
