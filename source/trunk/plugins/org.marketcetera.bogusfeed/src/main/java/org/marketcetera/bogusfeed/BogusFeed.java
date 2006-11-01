package org.marketcetera.bogusfeed;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.marketcetera.core.MSymbol;
import org.marketcetera.quotefeed.IMessageListener;
import org.marketcetera.quotefeed.IQuoteFeed;

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

public class BogusFeed extends AbstractQuoteFeedBase implements IQuoteFeed {

	private QuoteGeneratorThread quoteGeneratorThread;
	

	class QuoteGeneratorThread extends Thread {
		boolean shouldShutdown = false;
		Random rand = new Random();
		
		@Override
		public void run() 
		{
			while (!shouldShutdown){
				try {
					LinkedList<Entry<MSymbol, IMessageListener>> entries;
					synchronized (listenedSymbols){
						entries = new LinkedList<Map.Entry<MSymbol, IMessageListener>>(listenedSymbols);
					}
					for (Map.Entry<MSymbol, IMessageListener> entry : entries) {
						MSymbol symbol = entry.getKey();
						Message quote = generateQuote(symbol);
						entry.getValue().onQuote(quote);
					}
					sleep(randAmount());
				} catch (InterruptedException ex){}
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
			addGroup(quoteMessage, MDEntryType.BID, randBid.setScale(2, RoundingMode.HALF_UP), new BigDecimal(200), new Date(), "BGUS", quoteMessage);
			addGroup(quoteMessage, MDEntryType.OFFER, randAsk.setScale(2, RoundingMode.HALF_UP), new BigDecimal(300), new Date(), "BGUS", quoteMessage);
			quoteMessage.setString(LastPx.FIELD,randBid.add(randAsk).divide(new BigDecimal(2)).setScale(2, RoundingMode.HALF_UP).toPlainString());
			return quoteMessage;	
		}
		private void addGroup(Message message, char side, BigDecimal price, BigDecimal quantity, Date time, String mkt, Message refreshMessage) {
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
	
	public void connect() throws IOException {
		quoteGeneratorThread = new QuoteGeneratorThread();
		quoteGeneratorThread.start();
		setFeedStatus(FeedStatus.AVAILABLE);
	}

	public void disconnect() {
		setFeedStatus(FeedStatus.OFFLINE);
		quoteGeneratorThread.shutdown();
	}

	
	public FeedType getFeedType() {
		return FeedType.SIMULATED;
	}

	public String getID() {
		return "Bogus Book";
	}

}
