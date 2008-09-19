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
		return extractValue(message);
	}

	public static Object extractValue(Message message) {
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
			char ordType = (Character)value;
			message.setField(new OrdType(ordType));
			switch (ordType){
			case OrdType.FOREX_MARKET:
			case OrdType.MARKET:
			case OrdType.MARKET_IF_TOUCHED:
			case OrdType.MARKET_ON_CLOSE:
			case OrdType.MARKET_WITH_LEFTOVER_AS_LIMIT:
			case OrdType.NEXT_FUND_VALUATION_POINT:
			case OrdType.PREVIOUS_FUND_VALUATION_POINT:
				message.removeField(Price.FIELD);
			default:
			}
		} else if (value instanceof Number){
			message.setField(new OrdType(OrdType.LIMIT));
			if (value instanceof BigDecimal){
				message.setField(new Price((BigDecimal)value));
			} else {
				message.setField(new StringField(Price.FIELD, value.toString()));
			}
		}
	}

	@Override
	public Object getValueType() {
		return Object.class;
	}

	
}
