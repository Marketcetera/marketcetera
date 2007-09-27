package org.marketcetera.photon.ui.validation.fix;

import java.math.BigDecimal;

import org.eclipse.core.databinding.observable.Realm;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.OrdType;
import quickfix.field.Price;

public class PriceObservableValue extends FIXObservableValue {

	public PriceObservableValue(Realm realm, Message message, int fieldNumber, DataDictionary dataDictionary) {
		super(realm, message, fieldNumber, dataDictionary);
	}

	@Override
	protected Object doGetValue() {
		try {
			char ordType = message.getChar(OrdType.FIELD);
			switch (ordType){
			case OrdType.LIMIT:
			case OrdType.LIMIT_ON_CLOSE:
			case OrdType.LIMIT_OR_BETTER:
			case OrdType.LIMIT_WITH_OR_WITHOUT:
				return message.getString(Price.FIELD);
			default:
				return ordType;
			}
		} catch (FieldNotFound e) {
			return null;
		}
	}

	@Override
	protected void doSetValue(Object value) {
		if (value instanceof Character){
			message.setField(new OrdType((Character)value));
		} else if (value instanceof BigDecimal){
			message.setField(new OrdType(OrdType.LIMIT));
			message.setField(new StringField(Price.FIELD, value.toString()));
		}
	}

	@Override
	public Object getValueType() {
		return Object.class;
	}

	
}
