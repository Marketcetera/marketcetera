package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.OrderManager;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.views.StockOrderTicket;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.fix42.OrderCancelReplaceRequest;

/**
 * CancelOrderActionDelegate is a subclass of {@link ActionDelegate}
 * that is responsible for cancelling an order based on a selected
 * message with the appropriate {@link ClOrdID}.  This action is usually
 * invoked by the user by right clicking and choosing "Cancel" from
 * the context menu.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class CancelReplaceOrderActionDelegate extends ActionDelegate {
	public final static String ID = "org.marketcetera.photon.actions.CancelReplaceOrderActionDelegate";

	private IStructuredSelection selection;

	private OrderManager manager;

	/**
	 * Create a new {@link CancelReplaceOrderActionDelegate}
	 */
	public CancelReplaceOrderActionDelegate(){
	}
	
	/**
	 * Initializes this ActionDelegate with the specified action
	 * object.  Also gets the Application's OrderManager and stores
	 * it in an instance variable.
	 * @see org.eclipse.ui.actions.ActionDelegate#init(org.eclipse.jface.action.IAction)
	 * @see Application#getOrderManager()
	 */
	public void init(IAction arg0) {
		this.manager = Application.getOrderManager();
	}

	/**
	 * Does nothing
	 * @see org.eclipse.ui.actions.ActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/**
	 * Determines whether this action delegate should be
	 * enabled, depending on the value of the new selection.
	 * Checks the selectino to see if it is non-trivial, 
	 * and if it contains a FIX message of the appropriate
	 * type as its first element.
	 * 
	 * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
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
				if (theMessage != null && theMessage.isSetField(ClOrdID.FIELD) &&
						(FIXMessageUtil.isOrderSingle(theMessage) || FIXMessageUtil.isExecutionReport(theMessage))){
					shouldEnable = true;
				}
			}
		}
		action.setEnabled(shouldEnable);
	}

	/**
	 * Cancels the order specified by the FIX message in the selection,
	 * by extracting the {@link ClOrdID} and then calling {@link OrderManager#cancelOneOrderByClOrdID(String)}
	 * 
	 * @see org.eclipse.ui.actions.ActionDelegate#runWithEvent(org.eclipse.jface.action.IAction, org.eclipse.swt.widgets.Event)
	 */
	public void runWithEvent(IAction arg0, Event arg1) {
		Object item = selection.getFirstElement();
		Message oldMessage = null;
		if (item instanceof Message) {
			oldMessage = (Message) item;
			
		} else if (item instanceof MessageHolder) {
			MessageHolder holder = (MessageHolder) item;
			oldMessage = holder.getMessage();
		}
		if (oldMessage != null){
			try {
				Message cancelReplaceMessage = FIXMessageUtil.newCancelReplaceFromMessage(oldMessage);
				cancelReplaceMessage.setField(new ClOrdID(Application.getIDFactory().getNext()));
				StockOrderTicket.getDefault().showOrder(cancelReplaceMessage);
			} catch (NoMoreIDsException e) {
				Application.getMainConsoleLogger().error("Ran out of order ID's");
			} catch (FieldNotFound e) {
				Application.getMainConsoleLogger().error("Could not send order: "+e.getMessage());
			}
		}
	}




}
