package org.rubypeople.rdt.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.ui.actions.AbstractOpenWizardAction;

public class NewClassWizardAction extends AbstractOpenWizardAction implements IWorkbenchWindowActionDelegate  {

    public NewClassWizardAction() {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
                IRubyHelpContextIds.OPEN_CLASS_WIZARD_ACTION);
    }
    
    protected INewWizard createWizard() throws CoreException {
        return new NewClassCreationWizard();
    }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		setShell(window.getShell());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		super.run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			setSelection((IStructuredSelection) selection);
		} else {
			setSelection(StructuredSelection.EMPTY);
		}
	}

}
