package org.marketcetera.photon.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.TextContributionItem;

public class AddSymbolAction extends Action {
	private static final String ID = "org.marketcetera.photon.actions.AddSymbolAction";
	IMSymbolListener listener;
	TextContributionItem text;
	/**
	 * @param text
	 */
	public AddSymbolAction(TextContributionItem text, IMSymbolListener listener) {
		super("&Add Symbol",AS_PUSH_BUTTON);
		setId(ID);
		setToolTipText("Add Symbol");
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
		Text theText = (Text) e.widget;
		if ('\r' == e.character) {
			String theInputString = theText.getText();
			theText.setText("");
			if (isValidInput(theInputString))
				listener.onAssertSymbol(new MSymbol(theInputString));
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
