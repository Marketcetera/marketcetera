package org.marketcetera.marketdata;

import quickfix.Message;
@Deprecated
public interface ISubscription {
	public boolean isResponse(Message possibleResponse);
}
