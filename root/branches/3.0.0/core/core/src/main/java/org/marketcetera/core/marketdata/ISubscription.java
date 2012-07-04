package org.marketcetera.core.marketdata;

import quickfix.Message;
@Deprecated
public interface ISubscription {
	public boolean isResponse(Message possibleResponse);
}
