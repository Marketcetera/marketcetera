package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rdocexport.RDocUtility;

public class GenerateRdocAction implements IWorkbenchWindowActionDelegate {

	private ISelection fSelection;

	private Shell fCurrentShell;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		fCurrentShell = window.getShell();
	}

	private void showNoSelectionMessage() {
        MessageDialog.openInformation(
                fCurrentShell.getShell(),
                "No ruby project selected",
                "Please select a ruby project or resource for the generation of Rdoc.");
            return;
	}
	
	private IResource findSelectedResource() {
		// the first shot is the selection
		if (fSelection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) fSelection;
			Object first = selection.getFirstElement();
			if (first instanceof IResource) { return ((IResource) first); }
		}

		// the second is to check the active editor
		// this behaviour is similar to the RubyApplicationShortcut, which also
		// takes a selection in the navigator or the active editor to determine which
		// ruby file to launch
		IWorkbenchPage page = RubyPlugin.getActivePage();
		if (page == null) { return null; }
		IEditorPart editor = page.getActiveEditor();
		if (editor == null) { return null; }
		IEditorInput input = editor.getEditorInput();
		if (input == null) { return null; }
		IRubyElement rubyElement = (IRubyElement) input.getAdapter(IRubyElement.class);
		if (rubyElement == null) { return null; }
		return rubyElement.getResource();
	}
	
	public void run(IAction action) {
		IResource resource = this.findSelectedResource() ;
		if (resource == null) {
			showNoSelectionMessage();
		}
		else {
			RDocUtility.generateDocumentation(resource);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}
}
