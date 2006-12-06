package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.CommandStatusLineContribution;

/**
 * FocusCommandAction is the action responsible for putting the
 * focus into the command entry area at the bottom of the application.
 * This is bundled into an {@link Action} that implements {@link IWorkbenchAction}
 * in order to allow us to set up a keyboard shortcut for it in plugin.xml.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class FocusCommandAction extends Action implements ISelectionListener,
		IWorkbenchAction {
	public final static String ID = "org.marketcetera.photon.FocusCommand";

	CommandStatusLineContribution commandInput;
	
	/**
	 * Create a new FocusCommandAction with the default Id, ActionDefinitionId, Text
	 * ToolTipText, and ImageDescriptor, and the specified {@link IWorkbenchWindow}
	 * and the {@link CommandStatusLineContribution} that represents the command
	 * entry area itself.
	 * 
	 * @param window the application window
	 * @param commandInput the command input area as a CommandStatusLineContribution
	 */
	public FocusCommandAction(IWorkbenchWindow window, CommandStatusLineContribution commandInput) {
		setId(ID);
		setActionDefinitionId(ID);
		setText("Goto &command input area");
		setToolTipText("Put the cursor in the command input area");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.LIGHTNING));
		this.commandInput = commandInput;
	}

	/**
	 * Do nothing.
	 * @see org.eclipse.ui.actions.ActionFactory$IWorkbenchAction#dispose()
	 */
	public void dispose() {
	}

	/**
	 * Do nothing
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

	/**
	 * Sets the application focus to be the command input area by calling
	 * {@link CommandStatusLineContribution#setFocus()}.
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		commandInput.setFocus();
	}

}
