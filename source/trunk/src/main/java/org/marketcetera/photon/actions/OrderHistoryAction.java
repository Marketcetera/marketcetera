package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.editors.OrderHistoryInput;
import org.marketcetera.photon.editors.OrderHistoryEditor;

public class OrderHistoryAction extends Action implements IWorkbenchAction {

	public static final String ID = "org.marketcetera.photon.actions.OrderHistoryAction";
	private IWorkbenchWindow window;
	
	public OrderHistoryAction(IWorkbenchWindow window){
		this.window = window;
		setId(ID);
		setText("&Order History");
		setToolTipText("Open the order history editor");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, IImageKeys.ORDER_HISTORY));
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
