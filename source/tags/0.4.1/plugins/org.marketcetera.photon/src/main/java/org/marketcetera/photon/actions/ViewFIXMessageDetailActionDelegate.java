package org.marketcetera.photon.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.views.fixmessagedetail.FIXMessageDetailView;
import org.marketcetera.photon.views.fixmessagedetail.IFIXMessageDetail;

import quickfix.Message;

public class ViewFIXMessageDetailActionDelegate extends ActionDelegate {
	public final static String ID = "org.marketcetera.photon.actions.ViewFIXMessageDetailActionDelegate";

	private ISelection currentSelection;

	public ViewFIXMessageDetailActionDelegate() {
	}

	private Message getMessageFromSelection(ISelection selection) {
		Message fixMessage = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object firstElement = structuredSelection.getFirstElement();
				if (firstElement instanceof Message) {
					fixMessage = (Message) firstElement;
				} else if (firstElement instanceof MessageHolder) {
					fixMessage = ((MessageHolder) firstElement).getMessage();
				}

			}
		}
		return fixMessage;
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		boolean shouldEnable = false;
		currentSelection = selection;
		Message fixMessage = getMessageFromSelection(selection);
		if (fixMessage != null) {
			shouldEnable = true;
		}
		action.setEnabled(shouldEnable);
	}

	@Override
	public void runWithEvent(IAction action, Event event) {
		Message fixMessage = getMessageFromSelection(currentSelection);
		if (fixMessage != null) {
			try {
				IWorkbenchWindow activeWindow = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				IWorkbenchPage targetPage = activeWindow.getActivePage();
				if (targetPage == null) {
					throw new MarketceteraException(
							"The target page for the new view is null."); // $NON-NLS-1$
				}
				IViewPart viewPart = targetPage
						.showView(FIXMessageDetailView.ID);
				if (viewPart instanceof IFIXMessageDetail) {
					IFIXMessageDetail messageDetailView = (IFIXMessageDetail) viewPart;
					messageDetailView.showMessage(fixMessage);
				} else {
					throw new MarketceteraException(
							"The view does not implement " // $NON-NLS-1$
									+ IFIXMessageDetail.class);
				}
			} catch (Exception anyException) {
				PhotonPlugin.getMainConsoleLogger().error(
						"Failed to open new view with ID: " // $NON-NLS-1$
								+ FIXMessageDetailView.ID, anyException); // $NON-NLS-1$
			}
		}
	}
}
