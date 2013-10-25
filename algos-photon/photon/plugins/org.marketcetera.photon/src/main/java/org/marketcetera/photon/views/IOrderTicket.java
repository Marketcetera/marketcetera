package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Base XSWT order ticket.
 *
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public interface IOrderTicket {

	Button getClearButton();

	Button getSendButton();

	Text getPriceText();

	Text getQuantityText();
	
	Text getDisplayQuantityText();

	Combo getSideCombo();

	Combo getOrderTypeCombo();

    Text getSymbolText();

	Combo getBrokerCombo();
	/**
	 * Gets the control corresponding to the selected algo.
	 *
	 * @return a <code>Combo</code> value
	 */
	Combo getAlgoCombo();
	/**
	 * Gets the control corresponding to the selected algo tags table.
	 *
	 * @return a <code>Table</code> value
	 */
	Table getAlgoTagsTable();

	Combo getTifCombo();

	Text getAccountText();

	Label getErrorMessageLabel();
	
	Label getErrorIconLabel();
	
	ExpandableComposite getCustomExpandableComposite();

	ExpandableComposite getOtherExpandableComposite();
	
	ScrolledForm getForm();

	Table getCustomFieldsTable();
	
	Text getMessageDebugText();
	
}
