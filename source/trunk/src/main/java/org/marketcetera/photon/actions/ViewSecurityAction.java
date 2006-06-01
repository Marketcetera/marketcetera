package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.photon.model.PositionEntry;
import org.marketcetera.photon.views.WebBrowserView;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Symbol;

public class ViewSecurityAction implements IActionDelegate {
	public final static String ID = "org.marketcetera.photon.ViewSecurity";

	private IStructuredSelection selection;

	public ViewSecurityAction(){
	}


	public void selectionChanged(IAction proxyAction, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			Object firstElement = selection.getFirstElement();
			proxyAction.setEnabled(selection.size() == 1
					&& (firstElement instanceof PositionEntry||
					firstElement instanceof Message ||
					firstElement instanceof MessageHolder));
		} else {
			// Other selections, for example containing text or of other kinds.
			proxyAction.setEnabled(false);
		}
	}

	public void run(IAction arg0) {
		if (selection == null){
			return;
		}
		try {
			Object firstElement = selection.getFirstElement();
			MSymbol symbol = null;
			
			if (firstElement instanceof PositionEntry) {
				PositionEntry pos = (PositionEntry) firstElement;
				symbol = pos.getSymbol();
			} else if (firstElement instanceof Message) {
				Message message = (Message) firstElement;
				try {
					symbol = new MSymbol(message.getString(Symbol.FIELD));
				} catch (FieldNotFound e) {
					// do nothing
				}
			} else if (firstElement instanceof MessageHolder) {
				MessageHolder holder = (MessageHolder) firstElement;
				try {
					symbol = new MSymbol(holder.getMessage().getString(Symbol.FIELD));
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
			if (symbol != null){
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow(); 
				IWorkbenchPage page = window.getActivePage();
				page.showView(WebBrowserView.ID);
				IViewReference[] viewReferences = page.getViewReferences();
				for (IViewReference reference : viewReferences) {
					if (WebBrowserView.ID.equals(reference.getId())) {
						IViewPart view = reference.getView(true);
						((WebBrowserView) view).browseToGoogleFinanceForSymbol(symbol);
					}
				}
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
