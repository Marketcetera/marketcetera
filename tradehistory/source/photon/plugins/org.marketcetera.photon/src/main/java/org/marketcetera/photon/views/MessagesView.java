package org.marketcetera.photon.views;

import org.marketcetera.messagehistory.ReportHolder;

public abstract class MessagesView extends MessagesViewBase<ReportHolder> {

	public MessagesView() {
		this(true);
	}

    public MessagesView(boolean sortableColumns) {
    	super(sortableColumns);    	
    }

}
