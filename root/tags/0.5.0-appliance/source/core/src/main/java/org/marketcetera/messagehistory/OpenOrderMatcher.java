package org.marketcetera.messagehistory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrdStatus;
import ca.odell.glazedlists.matchers.Matcher;

@ClassVersion("$Id$")
public class OpenOrderMatcher implements Matcher<MessageHolder> {

	public boolean matches(MessageHolder item) {
		if (item instanceof IncomingMessageHolder) {
			IncomingMessageHolder inHolder = (IncomingMessageHolder) item;
			Message message = inHolder.getMessage();
			if (FIXMessageUtil.isExecutionReport(message)){
				try {
					char ordStatus = message.getChar(OrdStatus.FIELD);
					return FIXMessageUtil.isCancellable(ordStatus);
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
		}
		return false;
	}
	
}
