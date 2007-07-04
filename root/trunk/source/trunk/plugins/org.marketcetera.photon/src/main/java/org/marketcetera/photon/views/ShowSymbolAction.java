package org.marketcetera.photon.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.TextContributionItem;

public class ShowSymbolAction extends Action {
	private static final String ID = "org.marketcetera.photon.actions.ShowSymbolAction";
	IMSymbolListener listener;
	TextContributionItem text;
	/**
	 * @param text
	 */
	public ShowSymbolAction(TextContributionItem text, IMSymbolListener listener) {
		super("&Show Symbol",AS_PUSH_BUTTON);
		setId(ID);
		setToolTipText("Show Symbol");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.SHOW_SYMBOL));

		this.text = text;
		this.listener = listener;

		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}
		});

	}
	
	protected void handleKeyReleased(KeyEvent e) {
		if ('\r' == e.keyCode && e.stateMask == 0) {
			run();
		}
	}

	private boolean isValidInput(String inputString) {
		return inputString!= null && inputString.trim().length()>0;
	}
	
	@Override
	public void run() {
		String theInputString = text.getText();
		if (isValidInput(theInputString))
			listener.onAssertSymbol(new MSymbol(text.getText()));
		text.setText("");
	}
	
}
