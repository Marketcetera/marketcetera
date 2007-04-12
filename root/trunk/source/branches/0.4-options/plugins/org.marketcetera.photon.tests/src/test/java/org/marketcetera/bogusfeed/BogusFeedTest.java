package org.marketcetera.bogusfeed;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMessageSelector;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;

import quickfix.Message;
import quickfix.field.Symbol;

public class BogusFeedTest extends TestCase {
	
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public void testSubscribe() throws Exception {
		BogusFeedFactory factory = new BogusFeedFactory();
		IMarketDataFeed feed = factory.getInstance("", "", "");

		Exchanger<Message> exchanger = new Exchanger<Message>();
		MyMarketDataListener myMarketDataListener = new MyMarketDataListener(exchanger);
		feed.setMarketDataListener(myMarketDataListener);
		feed.start();

		SymbolMessageSelector aSelector = new SymbolMessageSelector(feed.symbolFromString("IBM"));
		feed.subscribe(aSelector);

		Message message = exchanger.exchange(null);

		assertEquals("IBM", message.getString(Symbol.FIELD));
		feed.stop();
		
		try {
			while (message != null){
				message = exchanger.exchange(null, 1, TimeUnit.MILLISECONDS);
			}
		} catch (TimeoutException ex){
			// do nothing
		}
	}
	
	public void testConjunctionSelector() throws Exception {
		MSymbol theSymbol = new MSymbol("IBZ");
		ConjunctionMessageSelector aSelector = new ConjunctionMessageSelector(
				new SymbolMessageSelector(theSymbol), new MessageTypeSelector(
						true, false, false));
		boolean foundOne = false;
		for (IMessageSelector selector : aSelector) {
			foundOne = true;
			if (selector instanceof SymbolMessageSelector) {
				SymbolMessageSelector sms = (SymbolMessageSelector) selector;
				assertEquals(sms.getExactSymbol().toString(), theSymbol
						.toString());
			} else if (selector instanceof MessageTypeSelector) {
				MessageTypeSelector mts = (MessageTypeSelector) selector;
				assertTrue(mts.isQuotes());
				assertTrue(!mts.isLevel2());
				assertTrue(!mts.isTrades());
			}
		}
		assertTrue("No sub-selctors found", foundOne);
	}

	public void testConjunctionSubscribe() throws Exception {
		BogusFeedFactory factory = new BogusFeedFactory();
		IMarketDataFeed feed = factory.getInstance("", "", "");

		Exchanger<Message> exchanger = new Exchanger<Message>();
		MyMarketDataListener myMarketDataListener = new MyMarketDataListener(exchanger);
		feed.setMarketDataListener(myMarketDataListener);
		feed.start();

		 ConjunctionMessageSelector aSelector = new ConjunctionMessageSelector(
				 new SymbolMessageSelector(feed.symbolFromString("IBM")),
				 new MessageTypeSelector(true, false, false));
		feed.subscribe(aSelector);

		Message message = exchanger.exchange(null);

		assertEquals("IBM", message.getString(Symbol.FIELD));
		feed.stop();
		
		try {
			while (message != null){
				message = exchanger.exchange(null, 1, TimeUnit.MILLISECONDS);
			}
		} catch (TimeoutException ex){
			// do nothing
		}
	}

	class MyMarketDataListener extends MarketDataListener{

		private final Exchanger<Message> exchanger;

		public MyMarketDataListener (Exchanger<Message> exchanger){
			this.exchanger = exchanger;
		}
		
		public void onLevel2Quote(Message aQuote) {
			try {
				exchanger.exchange(aQuote);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		public void onQuote(Message aQuote) {
			try {
				exchanger.exchange(aQuote);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		public void onTrade(Message aTrade) {
			try {
				exchanger.exchange(aTrade);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
}
