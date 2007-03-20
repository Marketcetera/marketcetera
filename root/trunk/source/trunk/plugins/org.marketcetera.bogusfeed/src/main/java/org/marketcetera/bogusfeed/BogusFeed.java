package org.marketcetera.bogusfeed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.ConjunctionMessageSelector;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.IMessageSelector;
import org.marketcetera.marketdata.MarketDataFeedBase;
import org.marketcetera.marketdata.MessageTypeSelector;
import org.marketcetera.marketdata.SymbolMessageSelector;

import quickfix.Message;
import quickfix.StringField;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class BogusFeed extends MarketDataFeedBase {

	protected List<SymbolMessageSelector> subscriptions = new LinkedList<SymbolMessageSelector>();

	private QuoteGeneratorThread quoteGeneratorThread;
	AtomicBoolean isRunning = new AtomicBoolean(false);

	class QuoteGeneratorThread extends Thread {
		boolean shouldShutdown = false;
		Random rand = new Random();

		public QuoteGeneratorThread() {
			setName("BogusFeed quote generator");
		}
		
		@Override
		public void run() 
		{
			setFeedStatus(FeedStatus.AVAILABLE);
			while (!shouldShutdown){
				try {
					LinkedList<SymbolMessageSelector> theSubscriptions;
					synchronized (subscriptions){
						theSubscriptions = new LinkedList<SymbolMessageSelector>(subscriptions);
					}
					Set<MSymbol> symbolSet = new HashSet<MSymbol>();
					for (SymbolMessageSelector selector : theSubscriptions) {
						MSymbol symbol = selector.getExactSymbol();
						symbolSet.add(symbol);
					}
					
					for (MSymbol symbol : symbolSet){
						Message quote = generateQuote(symbol);
						IMarketDataListener listener = BogusFeed.this.getMarketDataListener();
						if (listener != null ) {
							listener.onMessage(quote);
						}
					}
					sleep(randAmount());
				} catch (InterruptedException ex){
					setFeedStatus(FeedStatus.OFFLINE);
				}
			}
		}
		private long randAmount() {
			return (long)((rand.nextDouble()*1500)+1000);
		}
		public void shutdown()
		{
			shouldShutdown = true;
			interrupt();
		}
		Message generateQuote(MSymbol symbol){
			MarketDataSnapshotFullRefresh quoteMessage = new MarketDataSnapshotFullRefresh();
			quoteMessage.setField(new Symbol(symbol.getBaseSymbol()));
			BigDecimal randBid;
			BigDecimal randAsk;
			if (rand.nextBoolean()){
				randBid = getRandPrice(symbol);
				randAsk = randBid.add(getRandMarketWidth(randBid));
			} else {
				randAsk = getRandPrice(symbol);
				randBid = randAsk.subtract(getRandMarketWidth(randAsk));
			}
			addGroup(quoteMessage, MDEntryType.BID, randBid.setScale(2, RoundingMode.HALF_UP), new BigDecimal(200), new Date(), "BGUS");
			addGroup(quoteMessage, MDEntryType.OFFER, randAsk.setScale(2, RoundingMode.HALF_UP), new BigDecimal(300), new Date(), "BGUS");
			quoteMessage.setString(LastPx.FIELD,randBid.add(randAsk).divide(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP).toPlainString());
			return quoteMessage;	
		}
		private void addGroup(Message message, char side, BigDecimal price, BigDecimal quantity, Date time, String mkt) {
			MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			group.set(new MDEntryType(side));
			group.set(new MDEntryTime(time));
			group.set(new MDMkt(mkt));
			group.setField(new StringField(MDEntryPx.FIELD, price.toPlainString()));
			group.setField(new StringField(MDEntrySize.FIELD, quantity.toPlainString()));
			message.addGroup(group);
		}

		Map<MSymbol, BigDecimal> priceMap = new HashMap<MSymbol, BigDecimal>();
		private BigDecimal getRandPrice(MSymbol symbol){
			BigDecimal oldPrice = priceMap.get(symbol);
			BigDecimal newPrice;
			if (oldPrice == null){
				oldPrice = new BigDecimal(100* rand.nextDouble());
			}
			BigDecimal offset = new BigDecimal(.05 * rand.nextDouble());
			newPrice = oldPrice.add(offset).max(BigDecimal.ONE.divide(new BigDecimal(10)));
			priceMap.put(symbol, newPrice);
			return newPrice;
		}
		private BigDecimal getRandMarketWidth(BigDecimal price){
			return new BigDecimal(.005 * rand.nextDouble()).multiply(price)
			  .max(BigDecimal.ONE.divide(new BigDecimal(100)));
		}
		
	}
	
	public void start() {
		boolean oldValue = isRunning.getAndSet(true);
		if (oldValue)
			throw new IllegalStateException();
				
		quoteGeneratorThread = new QuoteGeneratorThread();
		quoteGeneratorThread.start();
		setFeedStatus(FeedStatus.AVAILABLE);
	}

	public void stop() {
		boolean oldValue = isRunning.getAndSet(false);
		if (oldValue){
			setFeedStatus(FeedStatus.OFFLINE);
			quoteGeneratorThread.shutdown();
		}
	}

	
	public FeedType getFeedType() {
		return FeedType.SIMULATED;
	}

	public String getID() {
		return "Bogus Book";
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public void subscribe(IMessageSelector selector) {
		SymbolMessageSelector toAdd = null;
		boolean error = false;
		if (selector instanceof SymbolMessageSelector) {
			toAdd = (SymbolMessageSelector) selector;
		} else if (selector instanceof ConjunctionMessageSelector){
			for (IMessageSelector aSelector : ((ConjunctionMessageSelector)selector)) {
				if (aSelector instanceof SymbolMessageSelector) {
					toAdd = (SymbolMessageSelector) aSelector;
				} else if (aSelector instanceof MessageTypeSelector) {
					if (!((MessageTypeSelector)aSelector).isQuotes() && 
							!((MessageTypeSelector)aSelector).isLevel2()	) {
						error = true;
					}
				} else {
					error = true;
				}
			}
		}
		if (toAdd != null && !error){
			synchronized (subscriptions) {
				subscriptions.add((SymbolMessageSelector) toAdd);
			}
		} else {
			throw new IllegalArgumentException("Unsupported IMessageSelector: "+selector);
		}
	}

	public MSymbol symbolFromString(String symbolString) {
		return new MSymbol(symbolString);
	}

	public boolean unsubscribe(IMessageSelector selector) {
		synchronized (subscriptions) {
			return subscriptions.remove(selector);
		}
	}

}
