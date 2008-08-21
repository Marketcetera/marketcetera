package org.marketcetera.photon.views;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.FIXMatcher;
import org.marketcetera.messagehistory.MessageHolder;

import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMatcherEditor<FIX_TYPE extends Comparable<FIX_TYPE>> extends
		AbstractMatcherEditor<MessageHolder> implements Comparable<FIXMatcherEditor<FIX_TYPE>>{


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
			FIXMatcherEditor<?> filt = (FIXMatcherEditor<?>) obj;
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