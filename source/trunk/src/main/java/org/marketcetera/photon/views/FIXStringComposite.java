package org.marketcetera.photon.views;

import java.util.prefs.BackingStoreException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.core.ConfigData;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import org.marketcetera.quickfix.OrderModifier;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;

public class FIXStringComposite extends FIXComposite implements OrderModifier {

	
	private Text textField;
	public FIXStringComposite(Composite parent, int style, FormToolkit toolkit, int fixFieldNumber) {
		super(parent, style, toolkit, fixFieldNumber);
		this.setLayout(new RowLayout(SWT.HORIZONTAL));
		toolkit.createLabel(this, FIXDataDictionaryManager.getHumanFieldName(fixFieldNumber)+": ");
		textField = toolkit.createText(this, "");
	}

	public void init(ConfigData arg0) throws BackingStoreException {
		
	}

	@Override
	public boolean modifyOrder(Message arg0) throws MarketceteraException {
		String text = textField.getText();
		if (text == null || "".equals(text)) {
			return false;
		} else {
			arg0.setString(fixFieldNumber, text);
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	@Override
	public boolean setFocus() {
		return textField.setFocus();
	}

	@Override
	public boolean populateFromMessage(Message aMessage) {
		try {
			String value = aMessage.getString(fixFieldNumber);
			textField.setText(value);
			return true;
		} catch (FieldNotFound e) {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.views.FIXComposite#clear()
	 */
	@Override
	public void clear() {
		textField.setText("");
	}
	
	
	

}
