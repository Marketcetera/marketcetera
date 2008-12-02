package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public interface IOrderTicket {

	Button getClearButton();

	Button getSendButton();

	Text getPriceText();

	Text getQuantityText();

	Combo getSideCombo();

	Text getSymbolText();

	Combo getBrokerCombo();

	Combo getTifCombo();

	Text getAccountText();

	Label getErrorMessageLabel();
	
	Label getErrorIconLabel();
	
	ExpandableComposite getCustomExpandableComposite();

	ExpandableComposite getOtherExpandableComposite();
	
	ScrolledForm getForm();

	CheckboxTableViewer getCustomFieldsTableViewer();
	
	Text getMessageDebugText();
	
}
