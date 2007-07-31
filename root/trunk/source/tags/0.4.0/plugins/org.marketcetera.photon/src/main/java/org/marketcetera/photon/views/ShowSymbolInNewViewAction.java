package org.marketcetera.photon.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.TextContributionItem;

public class ShowSymbolInNewViewAction extends Action {
	private static final String ID = "org.marketcetera.photon.actions.ShowSymbolInNewViewAction";

	private IWorkbenchWindow targetWindow;

	private TextContributionItem text;

	private String targetViewPrimayID;

	public ShowSymbolInNewViewAction(IWorkbenchWindow targetWindow,
			String targetViewPrimayID, TextContributionItem text) {
		super("Show Symbol in &New View", AS_PUSH_BUTTON);
		setId(ID);
		setToolTipText("Show Symbol in New View");
		setImageDescriptor(PhotonPlugin
				.getImageDescriptor(IImageKeys.SHOW_SYMBOL_NEW_WINDOW));

		if (targetWindow == null) {
			PhotonPlugin
					.getMainConsoleLogger()
					.error(
							getClass().getName()
									+ " created with a null target window. Opening new views will fail."); // $NON-NLS-1$
		}
		this.targetWindow = targetWindow;
		this.targetViewPrimayID = targetViewPrimayID;
		this.text = text;

		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}
		});

	}

	protected void handleKeyReleased(KeyEvent e) {
		if ('\r' == e.keyCode && (e.stateMask & SWT.CTRL) != 0) {
			run();
		}
	}

	// todo: This duplicates AddySymbolAction.isValidInput
	private boolean isValidInput(String inputString) {
		return inputString != null && inputString.trim().length() > 0;
	}

	@Override
	public void run() {
		String theInputString = text.getText();
		if (isValidInput(theInputString)) {
			String secondaryId = PhotonPlugin.getDefault().getNextSecondaryID();
			try {
				IWorkbenchPage targetPage = targetWindow.getActivePage();
				if (targetPage == null) {
					throw new MarketceteraException(
							"The target page for the new view is null."); // $NON-NLS-1$
				}
				IViewPart viewPart = targetPage.showView(targetViewPrimayID,
						secondaryId, IWorkbenchPage.VIEW_CREATE);
				if (viewPart instanceof IMSymbolListener) {
					IMSymbolListener listener = (IMSymbolListener) viewPart;
					listener.onAssertSymbol(new MSymbol(text.getText()));
				} else {
					throw new MarketceteraException(
							"The view with ID " // $NON-NLS-1$
									+ targetViewPrimayID
									+ " does not accept symbols. (It does not implement: " // $NON-NLS-1$
									+ IMSymbolListener.class.getName()
									+ ". It was: " // $NON-NLS-1$
									+ (viewPart != null ? viewPart.getClass()
											.getName() : null));
				}
			} catch (Exception anyException) {
				PhotonPlugin.getMainConsoleLogger().error(
						"Failed to open new view with ID: " // $NON-NLS-1$
								+ targetViewPrimayID, anyException); // $NON-NLS-1$
			}
		}
		text.setText("");
	}

}
