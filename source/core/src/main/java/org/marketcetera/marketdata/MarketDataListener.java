package org.marketcetera.marketdata;

import quickfix.Message;
@Deprecated
public abstract class MarketDataListener implements IMarketDataListener {

	public abstract void onMessage(Message aMessage);

	public void onMessages(Message[] messages) {
        for (Message message : messages) {
            onMessage(message);
        }
    }
}
