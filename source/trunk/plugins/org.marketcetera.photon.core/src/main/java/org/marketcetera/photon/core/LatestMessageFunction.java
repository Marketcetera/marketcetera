package org.marketcetera.photon.core;

import java.util.Date;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrderID;
import quickfix.field.SendingTime;
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
		MessageHolder latestMessageHolder = null;
		for (MessageHolder loopMessageHolder : messages) {
			if (filter(loopMessageHolder)) {
				try {
					if (latestMessageHolder == null){
						latestMessageHolder = loopMessageHolder;
					} else {
						if (isLater(loopMessageHolder, latestMessageHolder)){
							latestMessageHolder = loopMessageHolder;
						}
					}
				} catch (FieldNotFound fnf){
					// do nothing
				}
			}
		}
		return latestMessageHolder;
	}
	
	/**
	 * Determines whether msg1 is a "later" message than msg2
	 * @param messageHolder1 the first message to consider
	 * @param messageHolder2 the second message to consider
	 * @return true if messageHolder1 occurred after messageHolder2, false otherwise
	 */
	protected boolean isLater(MessageHolder messageHolder1, MessageHolder messageHolder2) throws FieldNotFound {
        long ref1 = messageHolder1.getMessageReference();
        long ref2 = messageHolder2.getMessageReference();
        if(ref1 != ref2) {
            return (ref1 > ref2);
        }

        Date date1;
        Date date2;
        date1 = messageHolder1.getMessage().getHeader().getUtcTimeStamp(SendingTime.FIELD);
        date2 = messageHolder2.getMessage().getHeader().getUtcTimeStamp(SendingTime.FIELD);
        return (date1.compareTo(date2) > 0);
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
