package org.marketcetera.photon.ui.validation.fix;


import java.util.Calendar;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;

import quickfix.DataDictionary;
import quickfix.FieldType;
import quickfix.Message;

public class FIXObservables {

	public static IObservableValue observeValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict) {
		return new FIXObservableValue(realm, message, fieldNumber, dict);
	}
	public static IObservableValue observeValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict, String fieldName, FieldType fieldType) {
		return new FIXObservableValue(realm, message, fieldNumber, dict, fieldName, fieldType);
	}

	public static IObservableValue observePriceValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict) {
		return new PriceObservableValue(realm, message, fieldNumber, dict);
	}
	public static IObservableValue observeMonthDateValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict) {
		return new StringDateObservableValue(Calendar.MONTH, realm, message, fieldNumber, dict);
	}
	public static IObservableValue observeYearDateValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict) {
		return new StringDateObservableValue(Calendar.YEAR, realm, message, fieldNumber, dict);
	}
}
