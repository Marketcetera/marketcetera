package org.marketcetera.photon.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;

import quickfix.DataDictionary;
import quickfix.Message;

public interface IFIXControllerBinding {

	void bind(Realm realm, DataBindingContext dataBindingContext,
			DataDictionary dictionary, Message message);

}
