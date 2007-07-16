package org.marketcetera.bogusfeed;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.core.BigDecimalUtils;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.marketdata.ISubscription;
import org.marketcetera.marketdata.MarketDataFeedBase;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.MsgType;
import quickfix.field.NoRelatedSym;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

public class BogusFeed extends MarketDataFeedBase {


	private static final BigDecimal PENNY = new BigDecimal("0.01");
	private static final String BGUS_MARKET = "BGUS";
	
	AtomicBoolean isRunning = new AtomicBoolean(false);
	Map<BogusSubscription, String> subscriptionMap;
	Map<String, BigDecimal> valueMap = new WeakHashMap<String, BigDecimal>();
	IDFactory idFactory = new InMemoryIDFactory(5000);
	
	private ScheduledThreadPoolExecutor executor;
	private Random random = new Random();

	public void start() {
		boolean oldValue = isRunning.getAndSet(true);
		if (oldValue)
			throw new IllegalStateException();
				
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					sendQuotes();
				} catch (Throwable t){
					t.printStackTrace();
				}
			}

        }, 0, 1, TimeUnit.SECONDS);
		setFeedStatus(FeedStatus.AVAILABLE);
	}

	protected void sendQuotes() {
		for (String symbol : valueMap.keySet()) {
			BigDecimal currentValue = valueMap.get(symbol);
			valueMap.put(symbol, currentValue.add(PENNY));
			sendQuote(symbol, currentValue);
		}
	}

	private void sendQuote(String symbol, BigDecimal currentValue) {
		Message refresh = new MarketDataSnapshotFullRefresh();
		refresh.setField(new Symbol(symbol));

		{
			Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			group.setField(new MDEntryType(MDEntryType.BID));
			group.setField(new StringField(MDEntryPx.FIELD, currentValue.subtract(PENNY).toPlainString()));
			group.setField(new MDMkt(BGUS_MARKET));
			group.setField(new MDEntrySize(Math.round((currentValue.subtract(PENNY)).doubleValue()* 100)));
			refresh.addGroup(group);
		}
		{
			Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			group.setField(new MDEntryType(MDEntryType.OFFER));
			group.setField(new StringField(MDEntryPx.FIELD, currentValue.add(PENNY).toPlainString()));
			group.setField(new MDMkt(BGUS_MARKET));
			group.setField(new MDEntrySize(Math.round((currentValue.add(PENNY)).doubleValue()* 100)));
			refresh.addGroup(group);
		}
		{
			Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			group.setField(new MDEntryType(MDEntryType.TRADE));
			group.setField(new StringField(MDEntryPx.FIELD, currentValue.toPlainString()));
			group.setField(new MDMkt(BGUS_MARKET));
			group.setField(new MDEntrySize(Math.round(currentValue.doubleValue())));
			refresh.addGroup(group);
		}
		
		fireMarketDataMessage(refresh);
	}

	public void stop() {
		boolean oldValue = isRunning.getAndSet(false);
		if (oldValue){
			setFeedStatus(FeedStatus.OFFLINE);
			executor.shutdownNow();
		}
	}

	
	public FeedType getFeedType() {
		return FeedType.SIMULATED;
	}

	public String getID() {
		return "Bogus";
	}

	public boolean isRunning() {
		return isRunning.get();
	}


	public MSymbol symbolFromString(String symbolString) {
		return new MSymbol(symbolString);
	}

	public BogusSubscription asyncQuery(Message query) throws MarketceteraException {
		try {
			String reqID = idFactory.getNext();
			if (FIXMessageUtil.isMarketDataRequest(query)) {
				char subscriptionRequestType = query.getChar(SubscriptionRequestType.FIELD);
				if (SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES == subscriptionRequestType)
				{
					int noRelatedSyms = query.getInt(NoRelatedSym.FIELD);
					if (noRelatedSyms == 1){
						Group symGroup = new MarketDataRequest.NoRelatedSym();
						query.getGroup(1, symGroup);
						String symbol = symGroup.getString(Symbol.FIELD);
						if (valueMap.containsKey(symbol)){
							// This may seem a little bit silly,
							// but because valueMap is a weak map,
							// we need to use the actual string that 
							// is the key.
							for (String aKey : valueMap.keySet()) {
								if (symbol.equals(aKey)){
									symbol = aKey;
									break;
								}
							}
						} else {
							valueMap.put(symbol, getRandPrice());
						}
						return subscribe(reqID, symbol);
					}
				} else {
					throw new MarketceteraException("Market data request must have exactly one related symbol.");
				}
			}
			return null;
		} catch (FieldNotFound e) {
			throw new MarketceteraException(e);
		}
	}

	private BigDecimal getRandPrice() {
		return BigDecimalUtils.multiply(new BigDecimal(100), random.nextDouble()).setScale(2, RoundingMode.HALF_UP);
	}

	private BogusSubscription subscribe(final String reqID, final String symbol) {
		final BogusSubscription bogusSubscription = new BogusSubscription(reqID, MsgType.MARKET_DATA_REQUEST);
		bogusSubscription.setSymbol(symbol);
		executor.submit(new Runnable() {
			public void run() {
				subscriptionMap.put(bogusSubscription, symbol);
			}
		});
		return bogusSubscription;
	}

	public void asyncUnsubscribe(final ISubscription subscription) throws MarketceteraException {
		executor.submit(new Runnable() {
			public void run() {
				subscriptionMap.remove(subscription);
			}
		});
	}

	public List<Message> syncQuery(Message query, long timeout, TimeUnit units) throws MarketceteraException, TimeoutException {
		throw new UnsupportedOperationException();
	}


}
