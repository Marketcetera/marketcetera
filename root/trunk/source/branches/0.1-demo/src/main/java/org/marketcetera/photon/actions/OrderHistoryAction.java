package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.editors.OrderHistoryEditor;
import org.marketcetera.photon.editors.OrderHistoryInput;

public class OrderHistoryAction extends Action implements IWorkbenchAction {

	public static final String ID = "org.marketcetera.photon.actions.OrderHistoryAction";
	private IWorkbenchWindow window;
	
	public OrderHistoryAction(IWorkbenchWindow window){
		this.window = window;
		setId(ID);
		setText("&Order History");
		setToolTipText("Open the order history editor");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.ORDER_HISTORY));
	}
	public void dispose() {
		// TODO Auto-generated method stub

	}
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
