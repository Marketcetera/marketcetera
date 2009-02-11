package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.CommandLineTrimWidget;

/**
 * FocusCommandAction is the action responsible for putting the
 * focus into the command entry area at the bottom of the application.
 * This is bundled into an {@link Action} that implements {@link IWorkbenchAction}
 * in order to allow us to set up a keyboard shortcut for it in plugin.xml.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FocusCommandAction 
    extends Action 
    implements ISelectionListener, IWorkbenchAction, Messages 
{
	public final static String ID = "org.marketcetera.photon.FocusCommand"; //$NON-NLS-1$
	private IWorkbenchWindow window;

	/**
	 * Create a new FocusCommandAction with the default Id, ActionDefinitionId, Text
	 * ToolTipText, and ImageDescriptor, and the specified {@link IWorkbenchWindow}
	 * 
	 * @param window the application window
	 */
	public FocusCommandAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(ID);
		setText(FOCUS_COMMAND_ACTION.getText());
		setToolTipText(FOCUS_COMMAND_ACTION_DESCRIPTION.getText());
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.LIGHTNING));
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
	 * Sets the application focus to be the command input area by rooting
	 * around in the composite hierarchy starting with the top-level shell...
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		Control[] children = window.getShell().getChildren();
		for (Control control : children) {
			if (control instanceof Composite) {
				Composite innerControl = (Composite) control;
				Control [] innerChildren = innerControl.getChildren();
				for (Control innerChild : innerChildren) {
					Object innerChildData = innerChild.getData();
					if (innerChild instanceof Composite && innerChildData != null && innerChildData instanceof CommandLineTrimWidget) {
						((CommandLineTrimWidget)innerChildData).setFocus();
					}
				}
			}
		}
	}

}
