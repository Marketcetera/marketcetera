package org.marketcetera.core.marketdata;

import quickfix.Message;
@Deprecated
public interface IMarketDataListener {

	public void onMessage(Message aMessage);
	public void onMessages(Message [] messages);
	
}
