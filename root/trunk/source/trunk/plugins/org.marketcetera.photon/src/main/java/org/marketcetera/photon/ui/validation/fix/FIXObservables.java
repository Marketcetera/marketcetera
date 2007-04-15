package org.marketcetera.photon.ui.validation.fix;


import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;

import quickfix.DataDictionary;
import quickfix.Message;

public class FIXObservables {

	public static IObservableValue observeValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict) {
		return new FIXObservableValue(realm, message, fieldNumber, dict);
	}
	public static IObservableValue observePriceValue(Realm realm, Message message,
			int fieldNumber, DataDictionary dict) {
		return new PriceObservableValue(realm, message, fieldNumber, dict);
	}
}
