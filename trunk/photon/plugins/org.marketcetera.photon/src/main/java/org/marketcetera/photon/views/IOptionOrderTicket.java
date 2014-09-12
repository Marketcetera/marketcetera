package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The XSWT option order ticket.
 *
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public interface IOptionOrderTicket extends IOrderTicket {

	Text getOptionExpiryText();
	
	Button getSelectExpiryButton();

    Combo getOpenCloseCombo();

	Combo getOrderCapacityCombo();

	Combo getPutOrCallCombo();
	
	Text getStrikePriceText();

}
