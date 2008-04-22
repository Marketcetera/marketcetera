package org.marketcetera.photon.marketdata.mock;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.AbstractMessageTranslator;

import quickfix.Message;

public class MockMarketDataFeedMessageTranslator extends
		AbstractMessageTranslator<Object> 
{
	public Object translate(Message message) throws MarketceteraException {
		return message;
	}
	
	public Message translate(Object obj) throws MarketceteraException {
		return (Message)obj;
	}
}
