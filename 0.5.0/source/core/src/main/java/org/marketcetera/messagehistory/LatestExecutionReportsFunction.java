package org.marketcetera.messagehistory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrderID;

/**
 * LatestExecutionReportsFunction is a subclass of 
 * LatestMessageFunction that filters for only incoming
 * execution report messages.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class LatestExecutionReportsFunction extends LatestMessageFunction {

	/** 
	 * Tests to see if the incoming {@link MessageHolder}
	 * represents an execution report.
	 * @param holder the message to test
	 * @return true if the incoming message represents an execution report, false otherwise
	 * @see org.marketcetera.messagehistory.LatestMessageFunction#filter(org.marketcetera.messagehistory.MessageHolder)
	 */
	protected boolean filter(MessageHolder holder){
		return (holder instanceof IncomingMessageHolder 
				&& FIXMessageUtil.isExecutionReport(holder.getMessage()));
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.editors.LatestMessageFunction#isLater(org.marketcetera.photon.model.MessageHolder, org.marketcetera.photon.model.MessageHolder)
	 */
	@Override
	protected boolean isLater(MessageHolder messageHolder1, MessageHolder messageHolder2) throws FieldNotFound {
		Message message1 = messageHolder1.getMessage();
		Message message2 = messageHolder2.getMessage();
		boolean hasOrderID1 = message1.isSetField(OrderID.FIELD);
		boolean hasOrderID2 = message2.isSetField(OrderID.FIELD);
		if ((hasOrderID1 && hasOrderID2) || (!hasOrderID1 && !hasOrderID2)){
			return super.isLater(messageHolder1, messageHolder2);
		} else {
			return hasOrderID1;
		}
	}
	
	
//	// special-case code to handle OrderID's
//	if (FIXMessageUtil.isExecutionReport(loopMessage) && FIXMessageUtil.isExecutionReport(latestMessage)
//			&& loopMessage.isSetField(OrderID.FIELD) && !latestMessage.isSetField(OrderID.FIELD))

}
