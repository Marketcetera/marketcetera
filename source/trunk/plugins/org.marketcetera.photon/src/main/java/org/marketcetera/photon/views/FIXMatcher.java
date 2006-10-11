package org.marketcetera.photon.views;

import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import ca.odell.glazedlists.matchers.Matcher;

public class FIXMatcher<T> implements Matcher<MessageHolder> {

	private final int matcherFIXField;
	private final T matcherValue;

	
	public FIXMatcher(int fixField, T value) {
		matcherFIXField = fixField;
		matcherValue = value;
	}

	public boolean matches(MessageHolder item) {
		try
		{
			Message aMessage = item.getMessage();
			if (matcherValue == null){
				try {
					String value = getFieldValueString(aMessage, matcherFIXField);
					return (value == null);
				} catch (FieldNotFound ex){
					return true;
				}
			} else {
				String value = getFieldValueString(aMessage, matcherFIXField);
				return value.equals(matcherValue.toString());
			}
		} catch (Exception ex)
		{
			//do nothing
		}
		return false;
	}

	private String getFieldValueString(Message msg, int fieldNum) throws FieldNotFound{
		DataDictionary dictionary = FIXDataDictionaryManager.getDictionary();
		if (dictionary.isHeaderField(fieldNum)){
			return msg.getHeader().getString(fieldNum);
		} else if (dictionary.isTrailerField(fieldNum)) {
			return msg.getTrailer().getString(fieldNum);
		} else {
			return msg.getString(fieldNum);
		}
	}
}
