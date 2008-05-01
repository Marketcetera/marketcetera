package org.marketcetera.photon.marketdata.mock;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.IEventTranslator;
import org.marketcetera.event.TradeEvent;

import quickfix.Message;

public class MockMarketDataFeedEventTranslator implements IEventTranslator {

	public List<EventBase> translate(Object obj) throws MarketceteraException {
		LinkedList<EventBase> eventList = new LinkedList<EventBase>();
		long currentTimeMillis = System.currentTimeMillis();
		TradeEvent tradeEvent = new TradeEvent(currentTimeMillis, currentTimeMillis, "ASYMBOL", BigDecimal.ONE, BigDecimal.ONE, (Message)obj);
		eventList.add(tradeEvent);
		return eventList;
	}

	public Object translate(EventBase event) throws MarketceteraException {
		return event.getFIXMessage();
	}

}
