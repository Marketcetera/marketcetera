package org.marketcetera.photon.core;

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
					switch (ordStatus){
					case OrdStatus.ACCEPTED_FOR_BIDDING:
					case OrdStatus.CALCULATED:
					case OrdStatus.NEW:
					case OrdStatus.PARTIALLY_FILLED:
					case OrdStatus.PENDING_CANCEL:
					case OrdStatus.PENDING_NEW:
					case OrdStatus.PENDING_REPLACE:
					case OrdStatus.SUSPENDED:
						return true;
					}
				} catch (FieldNotFound e) {
					// do nothing
				}
			}
		}
		return false;
	}
	
}
