package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.OrderManager;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;

public class CancelOrderActionDelegate extends ActionDelegate {
	public final static String ID = "org.marketcetera.photon.actions.CancelOrderActionDelegate";

	private IStructuredSelection selection;

	private OrderManager manager;

	public CancelOrderActionDelegate(){
	}
	
	public CancelOrderActionDelegate(OrderManager manager) {
	}
	public void init(IAction arg0) {
		init(Application.getOrderManager());
	}

	private void init(OrderManager manager) {
		this.manager = manager;
	}

	public void dispose() {
	}

	public void selectionChanged(IAction action, ISelection incoming) {
		boolean shouldEnable = false;
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			if (selection.size() == 1){
				Object firstElement = selection.getFirstElement();
				Message theMessage = null;
				if (firstElement instanceof Message) {
					theMessage = (Message) firstElement;
				} else if (firstElement instanceof MessageHolder){
					theMessage = ((MessageHolder) firstElement).getMessage();
				}
				if (theMessage != null && (FIXMessageUtil.isOrderSingle(theMessage)
						|| FIXMessageUtil.isExecutionReport(theMessage))){
					shouldEnable = true;
				}
			}
		}
		action.setEnabled(shouldEnable);
	}

	public void runWithEvent(IAction arg0, Event arg1) {
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
