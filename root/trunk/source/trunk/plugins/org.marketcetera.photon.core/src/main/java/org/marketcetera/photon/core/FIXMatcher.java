package org.marketcetera.photon.core;

import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import ca.odell.glazedlists.matchers.Matcher;

public class FIXMatcher<T> implements Matcher<MessageHolder> {

	private final int matcherFIXField;
	private final T matcherValue;
	private final boolean shouldInclude;

	
	public FIXMatcher(int fixField, T value) {
		this(fixField, value, true);
	}

	/**
	 * @param fixField the fix field to check against
	 * @param value the value of the fix field to match against
	 * @param shouldInclude Whether this matcher should include or exclude the matched messages
	 */
	public FIXMatcher(int fixField, T value, boolean shouldInclude) {
		matcherFIXField = fixField;
		matcherValue = value;
		this.shouldInclude = shouldInclude;
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
				boolean returnValue = (!shouldInclude) ^ value.equals(matcherValue.toString());
				return returnValue;
			}
		} catch (Exception ex)
		{
			//do nothing
		}
		return false;
	}

	private String getFieldValueString(Message msg, int fieldNum) throws FieldNotFound{
		DataDictionary dictionary = FIXDataDictionaryManager.getCurrentFixDataDictionary().getDictionary();
		if (dictionary.isHeaderField(fieldNum)){
			return msg.getHeader().getString(fieldNum);
		} else if (dictionary.isTrailerField(fieldNum)) {
			return msg.getTrailer().getString(fieldNum);
		} else {
			return msg.getString(fieldNum);
		}
	}
}
