package org.marketcetera.photon.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.TextContributionItem;
import org.marketcetera.trade.MSymbol;

public class AddSymbolAction
    extends Action
    implements Messages
{
	private static final String ID = "org.marketcetera.photon.actions.AddSymbolAction"; //$NON-NLS-1$
	IMSymbolListener listener;
	TextContributionItem text;
	/**
	 * @param text
	 */
	public AddSymbolAction(TextContributionItem text, IMSymbolListener listener)
	{
		super(SET_SYMBOL_LABEL.getText(),
		      AS_PUSH_BUTTON);
		setId(ID);
		setToolTipText(SET_SYMBOL_TOOTLTIPS.getText());
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.ADD_SYMBOL));

		this.text = text;
		this.listener = listener;

		text.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}
		});

	}
	
	protected void handleKeyReleased(KeyEvent e) {
		if ('\r' == e.character && e.stateMask == 0) {
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
		text.setText(""); //$NON-NLS-1$
	}
	
}
