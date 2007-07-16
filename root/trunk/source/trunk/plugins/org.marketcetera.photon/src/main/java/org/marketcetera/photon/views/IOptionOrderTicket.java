package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

public interface IOptionOrderTicket extends IOrderTicket {

	/**
	 * @return symbol for the specific option contract (e.g. "MSQ+GE")
	 */
	Label getOptionSymbolControl();

	Combo getExpireMonthCombo();

	Combo getOpenCloseCombo();

	Combo getOrderCapacityCombo();

	Combo getPutOrCallCombo();
	
	/**
	 * @return true if the user has selected a Put option, false if a Call or if
	 *         the user has not made a selection.
	 */
	Integer getPutOrCall();
	
	void setPutOrCall(Integer putOrCall);

	Combo getStrikePriceControl();

	Combo getExpireYearCombo();

}
