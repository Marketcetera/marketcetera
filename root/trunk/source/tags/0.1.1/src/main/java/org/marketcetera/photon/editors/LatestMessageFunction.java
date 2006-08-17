package org.marketcetera.photon.editors;

import java.util.Date;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.model.MessageHolder;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.TransactTime;
import ca.odell.glazedlists.FunctionList.Function;

/**
 * LatestMessageFunction is a {@link Function} that
 * will process a list of FIX messages and find only the
 * most recent message.  Note that this function does not
 * do any filtering to ensure that only messages of the
 * same type are compared.  You must ensure that the
 * messages passed to {@link #evaluate(List)} are 
 * all comparable based on the {@link TransactTime} field.
 * 
 * Note that subclasses may restrict the messages that are
 * considered by this function by implementing the
 * {@link #filter(MessageHolder)} method.
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class LatestMessageFunction implements
  Function<List<MessageHolder>, MessageHolder>{

	/** Given a list of messages, compare them all
	 * based on the value of the {@link TransactTime} field
	 * returning only the most recent message.
	 * 
	 * @param messages the list of messages to process
	 * @return the message object representing the most recent message from the input
	 * @see ca.odell.glazedlists.FunctionList$Function#evaluate(java.lang.Object)
	 */
	public MessageHolder evaluate(List<MessageHolder> messages) {
		MessageHolder latestMessage = null;
		for (MessageHolder holder : messages) {
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
	/**
	 * Determines whether a given {@link MessageHolder} should
	 * be considered for inclusion in the "latest message" calculation.
	 * This method is called once per message in the input to
	 * {@link #evaluate(List)}.
	 * 
	 * @param holder the message to consider
	 * @return true if the message should be considered for inclusion in the "latest message" calculation, false otherwise
	 */
	protected boolean filter(MessageHolder holder)
	{
		return true;
	}
}
