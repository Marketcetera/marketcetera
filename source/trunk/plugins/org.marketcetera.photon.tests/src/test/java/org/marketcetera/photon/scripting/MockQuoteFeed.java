package org.marketcetera.photon.scripting;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.marketcetera.bogusfeed.AbstractQuoteFeedBase;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quotefeed.IMessageListener;
import org.marketcetera.quotefeed.IQuoteFeed;

import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.matchers.Matcher;


/**
 * todo:doc
 * 
 * @author andrei@lissovski.org
 */
public class MockQuoteFeed extends AbstractQuoteFeedBase implements IQuoteFeed {

	/**
	 * Synchronously simulates a quote with a given symbol.
	 */
	public void simulateQuote(MSymbol symbol) {
		MarketDataSnapshotFullRefresh quoteMessage = new MarketDataSnapshotFullRefresh();
		quoteMessage.setField(new Symbol(symbol.getBaseSymbol()));  //agl not setting any other fields as only care about the symbol for mocking

		List<IMessageListener> messageListeners = getSymbolListeners(symbol);
		for (IMessageListener messageListener : messageListeners) {
			messageListener.onQuote(quoteMessage);
		}
	}

	//agl todo:refactor this method can probably be pulled up with proper synchronization added
	private List<IMessageListener> getSymbolListeners(final MSymbol symbol) {
		//agl (specified symbol, msg listener) tuples 
		FilterList<Map.Entry<MSymbol, IMessageListener>> symbolListenerList = new FilterList<Map.Entry<MSymbol, IMessageListener>>(
				listenedSymbols, new Matcher<Map.Entry<MSymbol, IMessageListener>>() {
					public boolean matches(Map.Entry<MSymbol, IMessageListener> entry) {
						return entry.getKey().equals(symbol);
					}
				});
		//agl (msg listener)'s 
		List<IMessageListener> listenerList = new FunctionList<Map.Entry<MSymbol, IMessageListener>, IMessageListener>(
				symbolListenerList,
				new FunctionList.Function<Map.Entry<MSymbol, IMessageListener>, IMessageListener>() {
					public IMessageListener evaluate(Map.Entry<MSymbol, IMessageListener> entry) {
						return entry.getValue();
					}
				});

		return listenerList;
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

}
