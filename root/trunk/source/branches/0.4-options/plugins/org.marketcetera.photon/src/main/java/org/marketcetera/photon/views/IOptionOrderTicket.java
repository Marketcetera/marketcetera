package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public interface IOptionOrderTicket extends IOrderTicket {

	Text getOptionSymbolControl();

	Combo getExpireMonthCombo();

	Combo getOpenCloseCombo();

	Combo getOrderCapacityCombo();

	Combo getPutOrCallCombo();

	/**
	 * @return true if the user has selected a Put option, false if a Call or if
	 *         the user has not made a selection.
	 */
	boolean isPut();

	Combo getStrikePriceControl();

	Combo getExpireYearCombo();

}
