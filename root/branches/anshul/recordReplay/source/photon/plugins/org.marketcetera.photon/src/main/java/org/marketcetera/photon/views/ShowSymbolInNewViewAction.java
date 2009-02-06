package org.marketcetera.photon.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.I18NBoundMessage4P;

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ShowSymbolInNewViewAction
    extends Action
    implements Messages
{
	private static final String ID = "org.marketcetera.photon.actions.ShowSymbolInNewViewAction"; //$NON-NLS-1$

	private IWorkbenchWindow targetWindow;

	private TextContributionItem text;

	private String targetViewPrimayID;

	public ShowSymbolInNewViewAction(IWorkbenchWindow targetWindow,
			String targetViewPrimayID, TextContributionItem text) {
		super(SHOW_SYMBOL_NEW_VIEW_LABEL.getText(),
		      AS_PUSH_BUTTON);
		setId(ID);
		setToolTipText(SHOW_SYMBOL_NEW_VIEW_TOOLTIPS.getText());
		setImageDescriptor(PhotonPlugin
				.getImageDescriptor(IImageKeys.SHOW_SYMBOL_NEW_WINDOW));

		if (targetWindow == null) {
			PhotonPlugin.getMainConsoleLogger().error(NULL_VIEW.getText(getClass().getName()));
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
					throw new CoreException(NULL_TARGET);
				}
				IViewPart viewPart = targetPage.showView(targetViewPrimayID,
						secondaryId, IWorkbenchPage.VIEW_CREATE);
				if (viewPart instanceof IMSymbolListener) {
					IMSymbolListener listener = (IMSymbolListener) viewPart;
					listener.onAssertSymbol(new MSymbol(text.getText()));
				} else {
					throw new CoreException(new I18NBoundMessage4P(VIEW_DOES_NOT_ACCEPT_SYMBOLS, targetViewPrimayID,
					                                                                     IMSymbolListener.class.getName(),
					                                                                     (viewPart == null ? 0 : 1),
					                                                                     (viewPart == null ? null : viewPart.getClass().getName())));
				}
			} catch (Exception anyException) {
				PhotonPlugin.getMainConsoleLogger().error(FAILED_TO_OPEN_VIEW.getText(targetViewPrimayID),
				                                          anyException);
			}
		}
		text.setText(""); //$NON-NLS-1$
	}
}
