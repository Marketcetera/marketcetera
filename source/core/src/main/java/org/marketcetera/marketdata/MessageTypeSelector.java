package org.marketcetera.marketdata;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;

public class MessageTypeSelector implements IMessageSelector {

	protected boolean quotes;
	protected boolean trades;
	protected boolean level2;
	
	public MessageTypeSelector(boolean quotes, boolean trades, boolean level2) {
		this.quotes = quotes;
		this.trades = trades;
		this.level2 = level2;
	}
	public boolean isLevel2() {
		return level2;
	}

	public boolean isQuotes() {
		return quotes;
	}

	public boolean isTrades() {
		return trades;
	}
	public boolean select(Message aMessage) {
		try {
			String msgTypeString = aMessage.getString(MsgType.FIELD);
			if (MsgType.EXECUTION_REPORT.equals(msgTypeString)) {
				return trades;
			}
			if (MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.equals(msgTypeString)) {
				int noEntries = aMessage.getInt(NoMDEntries.FIELD);
				if (noEntries > 2){
					return level2;
				} else {
					return quotes;
				}
			}
		} catch (FieldNotFound e) {
		}
		return false;
	}

}
