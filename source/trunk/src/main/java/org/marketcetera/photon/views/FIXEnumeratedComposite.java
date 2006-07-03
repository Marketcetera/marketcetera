package org.marketcetera.photon.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

public class FIXEnumeratedComposite extends FIXComposite {
	private Button[] buttons;

	public FIXEnumeratedComposite(Composite parent, int style,
			FormToolkit toolkit, int fixFieldNumber, 
			String[] valuesToDisplay) {
		super(parent, style, toolkit, fixFieldNumber);

		this.setLayout(new RowLayout(SWT.HORIZONTAL));
		toolkit.createLabel(this, FIXDataDictionaryManager.getHumanFieldName(fixFieldNumber)
				+ ": ");
		buttons = new Button[valuesToDisplay.length];
		Composite buttonComposite = toolkit.createComposite(this);
		buttonComposite.setLayout(new RowLayout(SWT.VERTICAL));
		for (int i = 0; i < valuesToDisplay.length; i++) {
			buttons[i] = toolkit.createButton(buttonComposite, FIXDataDictionaryManager
					.getHumanFieldValue(fixFieldNumber, valuesToDisplay[i]),
					SWT.RADIO);
			buttons[i].setData(new StringField(fixFieldNumber,
					valuesToDisplay[i]));
		}
	}

	@Override
	public boolean modifyOrder(Message arg0) throws MarketceteraException {
		for (Button aButton : buttons) {
			if (aButton.getSelection()) {
				if (((StringField)aButton.getData()).getField() == 54){
					LoggerAdapter.debug("modifyingOrder "+aButton + aButton.getSelection()+" "+aButton.hashCode(),this);
				}
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
	public boolean setSelection(String value, boolean isEnabled) {
		boolean found = false;
		for (Button aButton : buttons) {
			StringField stringField = (StringField) aButton.getData();
			if (value.equals(stringField.getValue())){
				aButton.setSelection(isEnabled);
				if (((StringField)aButton.getData()).getField() == 54){
					LoggerAdapter.debug("setting "+aButton+" "+aButton.hashCode(),this);
				}
				assert(aButton.getSelection() == isEnabled);
				found = true;
			} else {
				aButton.setSelection(false);
				LoggerAdapter.debug("unsetting "+aButton + aButton.getSelection()+" "+aButton.hashCode(),this);
				assert(!aButton.getSelection());
			}
		}
		return found;
	}

	@Override
	public boolean populateFromMessage(Message aMessage) {
		try {
			String valueFromMessage;
			valueFromMessage = aMessage.getString(fixFieldNumber);
			return setSelection(valueFromMessage, true);
		} catch (FieldNotFound e) {
			return false;
		}
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
