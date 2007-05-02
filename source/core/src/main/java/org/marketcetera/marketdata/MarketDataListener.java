package org.marketcetera.marketdata;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;

public abstract class MarketDataListener implements IMarketDataListener {

	public void onMessage(Message aMessage) {
		try {
			String msgTypeString = aMessage.getHeader().getString(MsgType.FIELD);
			if (MsgType.EXECUTION_REPORT.equals(msgTypeString)) {
				onTrade(aMessage);
			}
			if (MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.equals(msgTypeString)) {
				onQuote(aMessage);
			}
		} catch (Exception e) {
			System.out.println(""+e);
		}
	}

	public void onMessages(Message[] messages) {
        for (Message message : messages) {
            onMessage(message);
        }
    }

	public void onQuotes(Message[] messages) {
        for (Message message : messages) {
            onQuote(message);
        }
    }

	public void onTrades(Message[] trades) {
        for (Message trade : trades) {
            onTrade(trade);
        }
    }


    public abstract void onQuote(Message aQuote);

    public abstract void onTrade(Message aTrade);
    

}
