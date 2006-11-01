package org.marketcetera.photon.scripting;

import java.io.IOException;
import java.util.List;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.Application;
import org.marketcetera.quotefeed.IMessageListener;
import org.marketcetera.quotefeed.IQuoteFeed;

import quickfix.Message;


/**
 * A quote feed wrapper that executes registered scripts on events (i.e., trade,
 * quote) whose symbols have listeners.
 * 
 * @author andrei@lissovski.org
 */
public class ScriptingAwareQuoteFeed implements IQuoteFeed, IMessageListener {

	private IQuoteFeed targetFeed;

	// agl todo:revisit script scheduler should probably be a singleton
	private ScriptScheduler scriptScheduler = new ScriptScheduler();

	public ScriptingAwareQuoteFeed(IQuoteFeed feed) {
		this.targetFeed = feed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#connect()
	 */
	public void connect() throws IOException {
		targetFeed.connect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#disconnect()
	 */
	public void disconnect() {
		targetFeed.disconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#listenLevel2(org.marketcetera.core.MSymbol,
	 *      org.marketcetera.quotefeed.IMessageListener)
	 */
	public void listenLevel2(MSymbol symbol, IMessageListener listener) {
		targetFeed.listenLevel2(symbol, this);
		targetFeed.listenLevel2(symbol, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#listenQuotes(org.marketcetera.core.MSymbol,
	 *      org.marketcetera.quotefeed.IMessageListener)
	 */
	public void listenQuotes(MSymbol symbol, IMessageListener listener) {
		targetFeed.listenQuotes(symbol, this);
		targetFeed.listenQuotes(symbol, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#listenTrades(org.marketcetera.core.MSymbol,
	 *      org.marketcetera.quotefeed.IMessageListener)
	 */
	public void listenTrades(MSymbol symbol, IMessageListener listener) {
		targetFeed.listenTrades(symbol, this);
		targetFeed.listenTrades(symbol, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#unlistenLevel2(org.marketcetera.core.MSymbol,
	 *      org.marketcetera.quotefeed.IMessageListener)
	 */
	public void unlistenLevel2(MSymbol symbol, IMessageListener listener) {
		targetFeed.unlistenLevel2(symbol, this);
		targetFeed.unlistenLevel2(symbol, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#unlistenQuotes(org.marketcetera.core.MSymbol,
	 *      org.marketcetera.quotefeed.IMessageListener)
	 */
	public void unlistenQuotes(MSymbol symbol, IMessageListener listener) {
		targetFeed.unlistenQuotes(symbol, this);
		targetFeed.unlistenQuotes(symbol, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IQuoteFeed#unlistenTrades(org.marketcetera.core.MSymbol,
	 *      org.marketcetera.quotefeed.IMessageListener)
	 */
	public void unlistenTrades(MSymbol symbol, IMessageListener listener) {
		targetFeed.unlistenTrades(symbol, this);
		targetFeed.unlistenTrades(symbol, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.core.FeedComponent#addFeedComponentListener(org.marketcetera.core.IFeedComponentListener)
	 */
	public void addFeedComponentListener(IFeedComponentListener listener) {
		targetFeed.addFeedComponentListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.core.FeedComponent#getFeedStatus()
	 */
	public FeedStatus getFeedStatus() {
		return targetFeed.getFeedStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.core.FeedComponent#getFeedType()
	 */
	public FeedType getFeedType() {
		return targetFeed.getFeedType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.core.FeedComponent#getID()
	 */
	public String getID() {
		return targetFeed.getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.core.FeedComponent#removeFeedComponentListener(org.marketcetera.core.IFeedComponentListener)
	 */
	public void removeFeedComponentListener(IFeedComponentListener listener) {
		targetFeed.removeFeedComponentListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IMessageListener#onQuote(quickfix.Message)
	 */
	public void onQuote(Message message) {
		List<IScript> scripts = getOnQuoteScripts();
		for (IScript script : scripts) {
			try {
				scriptScheduler.submitScript(script);
			} catch (Exception e) {
				Application.getMainConsoleLogger().error("Error executing script "+script, e);
			}
		}
	}

	private List<IScript> getOnQuoteScripts() {
		return Application.getScriptRegistry().listScriptsByEventType(ScriptingEventType.QUOTE);
	}

	private List<IScript> getOnTradeScripts() {
		return Application.getScriptRegistry().listScriptsByEventType(ScriptingEventType.TRADE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IMessageListener#onQuotes(quickfix.Message[])
	 */
	public void onQuotes(Message[] messages) {
		for (Message message : messages) {
			onQuote(message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IMessageListener#onTrade(quickfix.Message)
	 */
	public void onTrade(Message message) {
		// agl todo:implement
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.marketcetera.quotefeed.IMessageListener#onTrades(quickfix.Message[])
	 */
	public void onTrades(Message[] messages) {
		// agl todo:implement
	}
}
