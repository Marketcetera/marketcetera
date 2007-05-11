package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public interface IOptionOrderTicket extends IOrderTicket {

	Text getOptionSymbolControl();

	Combo getExpireMonthCombo();

	Combo getOpenCloseCombo();

	Combo getOrderCapacityCombo();

	Combo getPutOrCallCombo();

	Combo getStrikePriceControl();

	Combo getExpireYearCombo();

}
