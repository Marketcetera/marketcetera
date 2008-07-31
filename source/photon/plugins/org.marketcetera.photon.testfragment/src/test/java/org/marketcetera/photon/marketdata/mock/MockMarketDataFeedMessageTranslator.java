package org.marketcetera.photon.marketdata.mock;

import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.AbstractMessageTranslator;

import quickfix.Message;

public class MockMarketDataFeedMessageTranslator extends
		AbstractMessageTranslator<Object> 
{
	public Object translate(Message message) throws CoreException {
		return message;
	}
	
	public Message asMessage(Object obj) throws CoreException {
		return (Message)obj;
	}
}
