package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.photon.ui.BookComposite;

import quickfix.Message;

public interface IStockOrderTicket {

	public abstract Text getAccountText();

	public abstract BookComposite getBookComposite();

	public abstract Button getCancelButton();

	public abstract Table getCustomFieldsTable();

	public abstract Label getErrorMessageLabel();

	public abstract Text getPriceText();

	public abstract Text getQuantityText();

	public abstract Button getSendButton();

	public abstract CCombo getSideCCombo();

	public abstract Text getSymbolText();

	public abstract CheckboxTableViewer getTableViewer();

	public abstract CCombo getTifCCombo();

	public void updateMessage(Message aMessage) throws MarketceteraException;
	public void showMessage(Message order);
	public void showErrorForControl(Control aControl, int severity, String message);
	public void clearErrors();
	public void showErrorMessage(String errorMessage, int severity);
	
	public void clear();

	

}