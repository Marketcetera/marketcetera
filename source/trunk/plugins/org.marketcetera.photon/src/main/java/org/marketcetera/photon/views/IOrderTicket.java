package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.ui.BookComposite;

import quickfix.Message;

public interface IOrderTicket {

	Button getCancelButton();

	Button getSendButton();

	BookComposite getBookComposite();

	Text getPriceText();

	Text getQuantityText();

	Combo getSideCombo();

	Text getSymbolText();

	Combo getTifCombo();

	Text getAccountText();

	Label getErrorMessageLabel();
	
	CheckboxTableViewer getTableViewer();

	void updateMessage(Message aMessage) throws MarketceteraException;

	void showMessage(Message order);

	void showErrorForControl(Control aControl, int severity, String message);

	void clearErrors();

	void showErrorMessage(String errorMessage, int severity);

	void clear();
}
