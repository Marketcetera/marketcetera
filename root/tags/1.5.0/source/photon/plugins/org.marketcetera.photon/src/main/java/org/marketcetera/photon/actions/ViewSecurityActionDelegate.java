package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.views.WebBrowserView;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Symbol;

/**
 * ViewSecurityAction is responsible for navigating
 * the internal browser component to an informational
 * view for the specified security.  Currently it is
 * set up to use Google Finance to display information
 * relevant to the symbol.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ViewSecurityActionDelegate implements IObjectActionDelegate {
	public final static String ID = "org.marketcetera.photon.actions.ViewSecurityActionDelegate"; //$NON-NLS-1$

	private IStructuredSelection selection;

	/**
	 * Creates a new default ViewSecurityAction
	 */
	public ViewSecurityActionDelegate(){
	}


	/**
	 * Determines whether this action should be enabled,
	 * by testing to see whether the selection is non-trivial
	 * and if its first element is a {@link quickfix.Message}.
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction proxyAction, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			Object firstElement = selection.getFirstElement();
			proxyAction.setEnabled(selection.size() == 1
					&& (firstElement instanceof Message ||
					firstElement instanceof ReportHolder));
		} else {
			// Other selections, for example containing text or of other kinds.
			proxyAction.setEnabled(false);
		}
	}

	/**
	 * Executes this action, by finding the {@link quickfix.Message}
	 * referenced by the current selection, extracting the {@link quickfix.field.Symbol}
	 * field, and if it is present and not null, calling 
	 * {@link WebBrowserView#browseToGoogleFinanceForSymbol(MSymbol)}
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction arg0) {
		if (selection == null){
			return;
		}
		try {
			Object firstElement = selection.getFirstElement();
			MSymbol symbol = null;
			
			Message message;
			if (firstElement instanceof Message) {
				message = (Message) firstElement;
			} else if (firstElement instanceof ReportHolder) {
				ReportHolder holder = (ReportHolder) firstElement;
				message = holder.getMessage();
			} else {
				return;
			}
			try {
				symbol = new MSymbol(message.getString(Symbol.FIELD));
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
			} catch (FieldNotFound e) {
				// do nothing
			}
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}



}
