package org.marketcetera.photon.scripting;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.bogusfeed.AbstractQuoteFeedBase;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quotefeed.IQuoteFeed;

import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


/**
 * todo:doc
 * 
 * @author andrei@lissovski.org
 */
public class MockQuoteFeed extends AbstractQuoteFeedBase implements IQuoteFeed {

	AtomicBoolean isRunning = new AtomicBoolean(false);
	
	/**
	 * Synchronously simulates a quote with a given symbol.
	 */
	public void simulateQuote(MSymbol symbol) {
		MarketDataSnapshotFullRefresh quoteMessage = new MarketDataSnapshotFullRefresh();
		quoteMessage.setField(new Symbol(symbol.getBaseSymbol()));  //agl not setting any other fields as only care about the symbol for mocking

		getQuoteJmsOperations().convertAndSend(quoteMessage);
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.quotefeed.IQuoteFeed#connect()
	 */
	public void connect() throws IOException {
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.quotefeed.IQuoteFeed#disconnect()
	 */
	public void disconnect() {
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#getFeedType()
	 */
	public FeedType getFeedType() {
		throw new NotImplementedException();
	}

	/* (non-Javadoc)
	 * @see org.marketcetera.core.FeedComponent#getID()
	 */
	public String getID() {
		throw new NotImplementedException();
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public void start() {
		boolean oldValue = isRunning.getAndSet(true);
		if (oldValue)
			throw new IllegalStateException();
	}

	public void stop() {
		isRunning.getAndSet(false);
	}

}
