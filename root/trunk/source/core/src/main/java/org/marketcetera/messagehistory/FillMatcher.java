/**
 * 
 */
package org.marketcetera.messagehistory;


import org.marketcetera.core.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.field.LastShares;
import ca.odell.glazedlists.matchers.Matcher;

@ClassVersion("$Id$")
public final class FillMatcher implements Matcher<MessageHolder> {
	public boolean matches(MessageHolder holder) {
		if (holder instanceof IncomingMessageHolder) {
			IncomingMessageHolder incomingHolder = (IncomingMessageHolder) holder;
			try {
				if (incomingHolder.getMessage().getDouble(LastShares.FIELD)>0){
					return true;
				}
			} catch (FieldNotFound e) {
				return false;
			}
		}
		return false;
	}
}