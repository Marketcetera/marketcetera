package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Composite;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.PhotonPlugin;

import ca.odell.glazedlists.EventList;

public abstract class HistoryMessagesView extends MessagesView {

	protected abstract EventList<MessageHolder> extractList(FIXMessageHistory input);

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		FIXMessageHistory messageHistory = PhotonPlugin.getDefault().getFIXMessageHistory();
		if (messageHistory!= null){
			setInput(messageHistory);
		}
	}

	public void setInput(FIXMessageHistory input) {
		EventList<MessageHolder> extractedList = extractList(input);
		super.setInput(extractedList);
	}

}
