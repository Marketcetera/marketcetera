package org.marketcetera.photon.editors;

import java.util.Date;
import java.util.List;

import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.model.MessageHolder;

import ca.odell.glazedlists.FunctionList.Function;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.TransactTime;

public class LatestMessageFunction implements
Function<List<MessageHolder>, MessageHolder>{

	public MessageHolder evaluate(List<MessageHolder> arg0) {
		MessageHolder latestMessage = null;
		for (MessageHolder holder : arg0) {
			Message message = holder.getMessage();
			if (filter(holder)) {
				try {
					if (latestMessage == null){
						latestMessage = holder;
					} else {
						Date newTime = message.getUtcTimeStamp(TransactTime.FIELD);
						Date existingTime = latestMessage.getMessage().getUtcTimeStamp(TransactTime.FIELD);
						int compareVal = newTime.compareTo(existingTime);
						if (compareVal > 0 ||
								(compareVal == 0 && 
										holder.getMessageReference() > latestMessage.getMessageReference()))
						{
							latestMessage = holder;
						}
					}
				} catch (FieldNotFound fnf){
					// do nothing
				}
			}
		}
		return latestMessage;
	}
	protected boolean filter(MessageHolder holder)
	{
		return true;
	}
}
