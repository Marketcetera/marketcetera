package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.ui.BookComposite;

public interface IStockOrderTicket extends IOrderTicket {

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

}