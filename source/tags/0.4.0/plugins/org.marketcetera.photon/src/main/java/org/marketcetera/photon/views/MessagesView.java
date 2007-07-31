package org.marketcetera.photon.views;

import org.marketcetera.photon.core.MessageHolder;

public abstract class MessagesView extends MessagesViewBase<MessageHolder> {

	public MessagesView() {
		this(true);
	}

    public MessagesView(boolean sortableColumns) {
    	super(sortableColumns);    	
    }

}
