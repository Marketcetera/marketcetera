package org.marketcetera.photon.views;

import org.marketcetera.photon.marketdata.OptionMessageHolder;

public abstract class OptionMessagesView extends MessagesViewBase<OptionMessageHolder> {

	public OptionMessagesView() {
		this(true);
	}

    public OptionMessagesView(boolean sortableColumns) {
    	super(sortableColumns);    	
    }

}
