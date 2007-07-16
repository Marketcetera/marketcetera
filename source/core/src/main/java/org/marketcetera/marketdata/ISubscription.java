package org.marketcetera.marketdata;

import quickfix.Message;

public interface ISubscription {
	public boolean isResponse(Message possibleResponse);
}
