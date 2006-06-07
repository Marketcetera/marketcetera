package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.core.MarketceteraException;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

public class FIXEnumeratedComposite extends FIXComposite {
	private Button[] buttons;

	public FIXEnumeratedComposite(Composite parent, int style,
			FormToolkit toolkit, int fixFieldNumber, DataDictionary dict,
			String[] valuesToDisplay) {
		super(parent, style, toolkit, fixFieldNumber);

		this.setLayout(new RowLayout(SWT.HORIZONTAL));
		toolkit.createLabel(this, dict.getFieldName(fixFieldNumber)
				+ ": ");
		buttons = new Button[valuesToDisplay.length];
		Composite buttonComposite = toolkit.createComposite(this);
		buttonComposite.setLayout(new RowLayout(SWT.VERTICAL));
		for (int i = 0; i < valuesToDisplay.length; i++) {
			buttons[i] = toolkit.createButton(buttonComposite, dict
					.getValueName(fixFieldNumber, valuesToDisplay[i]),
					SWT.RADIO);
			buttons[i].setData(new StringField(fixFieldNumber,
					valuesToDisplay[i]));
		}
	}

	@Override
	public boolean modifyOrder(Message arg0) throws MarketceteraException {
		for (Button aButton : buttons) {
			if (aButton.getSelection()) {
				arg0.setField(new StringField(fixFieldNumber, ((StringField)aButton.getData()).getValue()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean setFocus() {
		return buttons[0].setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setSelection(String value, boolean isEnabled) {
		for (Button aButton : buttons) {
			StringField stringField = (StringField) aButton.getData();
			if (value.equals(stringField.getValue()))
				aButton.setSelection(isEnabled);
		}
	}

	@Override
	public boolean populateFromMessage(Message aMessage) {
		boolean found = false;
		try {
			String valueFromMessage;
			valueFromMessage = aMessage.getString(fixFieldNumber);
			for (Button aButton : buttons) {
				StringField stringField = (StringField) aButton.getData();
				if (valueFromMessage.equals(stringField.getValue())) {
					aButton.setSelection(true);
					found = true;
				} else {
					aButton.setSelection(false);
				}
			}
		} catch (FieldNotFound e) {
			return false;
		}
		return found;
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.views.FIXComposite#clear()
	 */
	@Override
	public void clear() {
		for (Button aButton : buttons) {
			aButton.setSelection(false);
		}
	}

	
}
