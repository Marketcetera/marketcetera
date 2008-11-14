package org.rubypeople.rdt.internal.ui.rdocexport;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CreateRdocActionDelegate implements IObjectActionDelegate {

	private ISelection fCurrentSelection;


	/*
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/*
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (fCurrentSelection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) fCurrentSelection;
			Object first = structuredSelection.getFirstElement();
			if (first instanceof IResource) {
				RDocUtility.generateDocumentation((IResource) first);
//				RdocWizard wizard = new RdocWizard((IFile) first);
//				RdocWizard.openRdocWizard(wizard, fCurrentShell,
//						structuredSelection);
			}
		}
	}

	/*
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		fCurrentSelection = selection;
	}

}
