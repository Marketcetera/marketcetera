package org.marketcetera.photon.actions;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.views.MarketDataView;

import quickfix.Message;
import quickfix.field.Symbol;

@ClassVersion("$Id$")
public class DeleteItemActionDelegate implements IViewActionDelegate {
	public final static String ID = "org.marketcetera.photon.actions.DeleteItemActionDelegate";

	private IStructuredSelection selection;

	private MarketDataView view;

	/**
	 * Create a new {@link CancelOrderActionDelegate}
	 */
	public DeleteItemActionDelegate(){
	}
	
	public void init(IViewPart view) {
		this.view = (MarketDataView) view;
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
			if (selection.size() >= 1){
				Object firstElement = selection.getFirstElement();
				Message theMessage = null;
				if (firstElement instanceof Message) {
					theMessage = (Message) firstElement;
				} else if (firstElement instanceof MessageHolder){
					theMessage = ((MessageHolder) firstElement).getMessage();
				}
				if (theMessage != null && theMessage.isSetField(Symbol.FIELD)){
					shouldEnable = true;
				}
			}
		}
		action.setEnabled(shouldEnable);
	}

	public void run(IAction action) {
		Iterator iter = selection.iterator();
		while (iter.hasNext()) {
			view.removeItem((MessageHolder) iter.next());
		}
	}




}
