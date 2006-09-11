package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.editors.OrderHistoryEditor;
import org.marketcetera.photon.editors.OrderHistoryInput;

/**
 * OrderHistoryAction is the action responsible for opening the OrderHistoryEditor.
 * Because the OrderHistoryEditor is implemented as an editor--though it doesn't
 * really edit anything--this action is required to create a new OrderHistoryInput,
 * and pass that to {@link IWorkbenchPage#openEditor(org.eclipse.ui.IEditorInput, String)}.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class OrderHistoryAction extends Action implements IWorkbenchAction {

	public static final String ID = "org.marketcetera.photon.actions.OrderHistoryAction";
	private IWorkbenchWindow window;
	
	/**
	 * Create a new OrderHistoryAction with a pointer to the 
	 * application window, and the default Id, Text, ToolTipText,
	 * and ImageDescriptor.
	 * 
	 * @param window the application window.
	 */
	public OrderHistoryAction(IWorkbenchWindow window){
		this.window = window;
		setId(ID);
		setText("&Order History");
		setToolTipText("Open the order history");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.ORDER_HISTORY));
	}
	
	/**
	 * Do nothing
	 * @see org.eclipse.ui.actions.ActionFactory$IWorkbenchAction#dispose()
	 */
	public void dispose() {
	}
	/**
	 * Creates the new OrderHistoryEditor by creating a new 
	 * OrderHistoryInput and then calling {@link IWorkbenchPage#openEditor(org.eclipse.ui.IEditorInput, String)}
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		IWorkbenchPage page = window.getActivePage();
		OrderHistoryInput input = new OrderHistoryInput(Application.getFIXMessageHistory());
		try {
			page.openEditor(input, OrderHistoryEditor.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
