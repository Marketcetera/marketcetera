package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.OrderManager;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.model.MessageHolder;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;

public class CancelOrderAction extends Action implements ISelectionListener,
		IWorkbenchAction {
	private final IWorkbenchWindow window;

	public final static String ID = "org.marketcetera.photon.CancelOrder";

	private IStructuredSelection selection;

	private final OrderManager manager;

	
	public CancelOrderAction(
			IWorkbenchWindow window, OrderManager manager) {
		this.window = window;
		this.manager = manager;
		setId(ID);
		setActionDefinitionId(ID);
		setText("Cancel an order");
		setToolTipText("Cancel an order");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.CANCEL));
	}

	public void dispose() {
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			Object firstElement = selection.getFirstElement();
			setEnabled(selection.size() == 1
					&& (firstElement instanceof Message ||
							firstElement instanceof MessageHolder));
		} else {
			// Other selections, for example containing text or of other kinds.
			setEnabled(false);
		}
	}

	public void run() {
		Object item = selection.getFirstElement();
		Message qfMessage = null;
		if (item instanceof Message) {
			qfMessage = (Message) item;
			
		} else if (item instanceof MessageHolder) {
			MessageHolder holder = (MessageHolder) item;
			qfMessage = holder.getMessage();
		}
		if (qfMessage != null){
			try {
				this.manager.cancelOneOrderByClOrdID(qfMessage.getString(ClOrdID.FIELD));
			} catch (NoMoreIDsException e) {
				Application.getMainConsoleLogger().error("Ran out of order ID's");
			} catch (FieldNotFound e) {
				Application.getMainConsoleLogger().error("Could not send order because message contains no ClOrdID");
			}
		}
	}




}
