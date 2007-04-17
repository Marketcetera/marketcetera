package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Control;
import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

public interface IOrderTicket {

	void updateMessage(Message aMessage) throws MarketceteraException;

	void showMessage(Message order);

	void showErrorForControl(Control aControl, int severity, String message);

	void clearErrors();

	void showErrorMessage(String errorMessage, int severity);

	void clear();
}
