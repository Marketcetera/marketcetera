package org.marketcetera.photon.ui;

import quickfix.Group;
import quickfix.Message;
import ca.odell.glazedlists.EventList;

public interface IBookComposite {

	public abstract void dispose();

	public abstract void setInput(Message marketRefresh);

	public abstract Message getInput();

	public abstract EventList<Group> getBookEntryList(Message marketRefresh,
			char mdEntryType);

	public abstract void onQuote(final Message aMarketRefresh);
	
}