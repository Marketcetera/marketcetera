package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

public interface IOptionOrderTicket extends IOrderTicket {

	 Combo getExpireMonthCombo();

	 Combo getOpenCloseCombo();
	 
	 Combo getOrderCapacityCombo();
			 
	 Combo getPutOrCallCombo();

	 Text getStrikeText();

	 Combo getExpireYearCombo();

}
