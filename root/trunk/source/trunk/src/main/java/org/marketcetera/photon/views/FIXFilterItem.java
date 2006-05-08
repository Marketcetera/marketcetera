package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.ViewerFilter;
import org.marketcetera.photon.editors.FIXFilter;

public class FIXFilterItem<T> extends FilterItem<T> {

	private final int fixField;
	private FIXFilter filter;

	public FIXFilterItem(int fixField, T item, String name) {
		super(item, name);
		this.fixField = fixField;
		filter = new FIXFilter(fixField, item.toString());
	}

	@Override
	public ViewerFilter getFilter() {
		return filter;
	}
	
	public boolean equals(Object obj)
	{
		if (obj == null){
			return false;
		}
		
		if (obj instanceof FIXFilterItem) {
			FIXFilterItem filt = (FIXFilterItem) obj;
			return filt.fixField == this.fixField && this.getItem().equals(filt.getItem());
		}
		return false;
	}

}
