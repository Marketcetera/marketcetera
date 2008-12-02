package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public interface IOptionOrderTicket extends IOrderTicket {

	/**
	 * @return symbol for the specific option contract (e.g. "MSQ+GE")
	 */
	Text getOptionSymbolText();

	Combo getExpireMonthCombo();

	Combo getOpenCloseCombo();

	Combo getOrderCapacityCombo();

	Combo getPutOrCallCombo();
	
	Combo getStrikePriceCombo();

	Combo getExpireYearCombo();

}
