package org.marketcetera.photon.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.marketcetera.photon.model.FIXMessageHistory.MessageHolder;

import quickfix.FieldNotFound;
import quickfix.Message;

public class FIXFilter extends ViewerFilter {

	private int fixField;
	private String fixValue;
	
	public FIXFilter(int fixField, String fixValue) {
		super();
		this.fixField = fixField;
		this.fixValue = fixValue;		
	}

	@Override
	public boolean select(Viewer arg0, Object parent, Object element) {
		Message message = null;
		if (element instanceof Message) {
			message = (Message)element;
		} else if (element instanceof MessageHolder){
			message = ((MessageHolder)element).getMessage();
		}
		if (message != null){
			try {
				String value = message.getString(fixField);
				if (value == null){
					return fixValue == null;
				} else {
					boolean equals = value.equals(fixValue);
					return equals;
				}
			} catch (FieldNotFound e) {
				return false;
			}
		}
		return true;
	}
}
