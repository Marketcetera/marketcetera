package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.photon.ui.BookComposite;

public interface IOptionOrderTicket extends IOrderTicket {

	 Text getAccountText();

	 BookComposite getBookComposite();
	 
	 Button getCancelButton();

	 Label getErrorMessageLabel();
	 
	 CCombo getExpireMonthCCombo();

	 CCombo getOpenCloseCCombo();
	 
	 CCombo getOrderCapacityCCombo();
			 
	 Text getPriceText();

	 CCombo getPutOrCallCCombo();

	 Text getQuantityText();

	 Button getSendButton();
	
	 CCombo getSideCCombo();

	 Text getStrikeText();

	 Text getSymbolText();

	 CheckboxTableViewer getTableViewer();

	 CCombo getTifCCombo();

	 CCombo getExpireYearCCombo();

}
