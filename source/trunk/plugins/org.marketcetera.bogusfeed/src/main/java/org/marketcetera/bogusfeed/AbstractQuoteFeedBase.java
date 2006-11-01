package org.marketcetera.bogusfeed;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MMapEntry;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quotefeed.IMessageListener;
import org.marketcetera.quotefeed.IQuoteFeed;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ca.odell.glazedlists.BasicEventList;


/**
 * An abstract base class for all quote feeds. Contains logic common to all quote feed impls with 
 * the mechanics of adding/removing symbol/feed component listeners and keeping track of the feed 
 * status.
 *
 * @author andrei@lissovski.org
 * @author gmiller
 */
//agl todo:refactor move into the core plugin -- there's nothing bogus feed related in this class
public abstract class AbstractQuoteFeedBase implements IQuoteFeed {

	protected FeedStatus feedStatus;
	private List<IFeedComponentListener> feedComponentListeners = new LinkedList<IFeedComponentListener>();
	protected BasicEventList<Map.Entry<MSymbol, IMessageListener>> listenedSymbols = new BasicEventList<Map.Entry<MSymbol, IMessageListener>>();

	
	public void listenLevel2(MSymbol symbol, IMessageListener listener) {
		synchronized (listenedSymbols) {
			MMapEntry<MSymbol, IMessageListener> entry = new MMapEntry<MSymbol, IMessageListener>(symbol, listener);
			listenedSymbols.add(entry);
		}
	}

	public void unlistenLevel2(MSymbol symbol, IMessageListener listener) {
		synchronized (listenedSymbols) {
			for (Map.Entry<MSymbol, IMessageListener> entry : listenedSymbols) {
				if (entry.getKey().equals(symbol) && entry.getValue().equals(listener)){
					listenedSymbols.remove(entry);
					break;
				}
			}
		}
	}

	public void addFeedComponentListener(IFeedComponentListener arg0) {
		feedComponentListeners.add(arg0);
	}

	public FeedStatus getFeedStatus() {
		return feedStatus;
	}

	public void removeFeedComponentListener(IFeedComponentListener arg0) {
		feedComponentListeners.remove(arg0);
	}

	public void fireFeedStatusChanged() {
		for (IFeedComponentListener listener: feedComponentListeners) {
			listener.feedComponentChanged(this);
		}
	}

	public void listenQuotes(MSymbol symbol, IMessageListener listener) {
		synchronized (listenedSymbols) {
			MMapEntry<MSymbol, IMessageListener> entry = new MMapEntry<MSymbol, IMessageListener>(symbol, listener);
			listenedSymbols.add(entry);
		}
	}

	public void listenTrades(MSymbol symbol, IMessageListener listener) {
		//agl todo:implement listenTrades()
		throw new NotImplementedException();
	}

	public void unlistenQuotes(MSymbol symbol, IMessageListener listener) {
		synchronized (listenedSymbols) {
			listenedSymbols.remove(new MMapEntry<MSymbol, IMessageListener>(symbol, listener));
		}
	}

	public void unlistenTrades(MSymbol symbol, IMessageListener listener) {
		//agl todo:implement unlistenTrades()
		throw new NotImplementedException();
	}

	protected void setFeedStatus(FeedStatus status) {
		feedStatus = status;
		fireFeedStatusChanged();
	}

}
