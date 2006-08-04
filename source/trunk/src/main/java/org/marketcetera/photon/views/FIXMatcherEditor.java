package org.marketcetera.photon.views;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.model.MessageHolder;
import org.marketcetera.quickfix.FIXDataDictionaryManager;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

@ClassVersion("$Id$")
public class FIXMatcherEditor<FIX_TYPE extends Comparable<FIX_TYPE>> extends
		AbstractMatcherEditor<MessageHolder> implements Comparable<FIXMatcherEditor<FIX_TYPE>>{

	class FIXMatcher<FIX_TYPE_INNER> implements Matcher<MessageHolder> {

		private final int matcherFIXField;
		private final FIX_TYPE_INNER matcherValue;

		
		public FIXMatcher(int fixField, FIX_TYPE_INNER value) {
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
	
	

	FIX_TYPE mItem;

	private final int fixField;

	private Matcher<MessageHolder> matcher;

	String mName;

	public FIXMatcherEditor(int fixField, FIX_TYPE item, String name) {
		mItem = item;
		mName = name;
		this.fixField = fixField;
		matcher = new FIXMatcher<FIX_TYPE>(fixField, item);
	}

	@Override
	public Matcher<MessageHolder> getMatcher() {
		return matcher;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof FIXMatcherEditor) {
			FIXMatcherEditor filt = (FIXMatcherEditor) obj;
			return filt.fixField == this.fixField
					&& this.getItem().equals(filt.getItem());
		}
		return false;
	}

	public FIX_TYPE getItem() {
		return mItem;
	}

	public void setItem(FIX_TYPE item) {
		mItem = item;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}
	
	public String toString(){
		return mName;
	}

	public int compareTo(FIXMatcherEditor<FIX_TYPE> arg0) {
		if (arg0.fixField == fixField){
			if (this.mItem == null){
				return arg0.mItem == null ? 0 : 1;
			} else if (arg0.mItem == null)
			{
				return -1;
			}
			return this.mItem.compareTo(arg0.mItem);
		} else {
			return this.fixField - arg0.fixField;
		}
	}

}