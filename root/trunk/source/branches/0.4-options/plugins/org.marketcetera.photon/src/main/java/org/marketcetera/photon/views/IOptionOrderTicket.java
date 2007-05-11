package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;

public interface IOptionOrderTicket extends IOrderTicket {

	 Combo getExpireMonthCombo();

	 Combo getOpenCloseCombo();
	 
	 Combo getOrderCapacityCombo();
			 
	 Combo getPutOrCallCombo();

	 Combo getStrikePriceControl();

	 Combo getExpireYearCombo();

}
