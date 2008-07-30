package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IMSymbolListener;

public class SelectOptionMarketDataCommandAction
    extends Action
    implements ISelectionListener, IWorkbenchAction, Messages
{
	public final static String ID = "org.marketcetera.photon.SelectOptionMarketDataCommandAction"; //$NON-NLS-1$

	private IWorkbenchWindow window;

	public SelectOptionMarketDataCommandAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(ID);
		setText(OPTION_MARKET_DATA_COMMAND_ACTION.getText());
		setToolTipText(OPTION_MARKET_DATA_COMMAND_ACTION_DESCRIPTION.getText());
		setImageDescriptor(PhotonPlugin
				.getImageDescriptor(IImageKeys.LIGHTNING));
	}

	public void dispose() {
	}

	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
	}

	public void run() {
		SelectSymbolDialog symbolDialog = new SelectSymbolDialog(window,
		                                                         OPTION_MARKET_DATA_SYMBOL_ACTION.getText(),
		                                                         OPTION_MARKET_DATA_SYMBOL_ACTION_DESCRIPTION.getText());
		if (symbolDialog.open() == Window.OK) {
			String targetSymbol = symbolDialog.getTargetSymbol();
			activateSymbolListenerViewPart(targetSymbol);
		}
	}

	private void activateSymbolListenerViewPart(String targetSymbol) {
		IWorkbenchPage activePage = window.getActivePage();
		if (activePage != null) {
			IViewReference[] viewReferences = window.getActivePage()
					.getViewReferences();
			if (viewReferences != null) {
				for (IViewReference viewReference : viewReferences) {
					IViewPart viewPart = viewReference.getView(false);
					if (viewPart != null
							&& viewPart instanceof IMSymbolListener) {

						IMSymbolListener symbolListener = (IMSymbolListener) viewPart;
						if (symbolListener.isListeningSymbol(new MSymbol(
								targetSymbol))) {
							activePage.activate(viewPart);
						}
					}
				}
			}
		}
	}

}
