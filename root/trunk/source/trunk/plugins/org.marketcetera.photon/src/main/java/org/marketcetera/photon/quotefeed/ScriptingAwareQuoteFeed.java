package org.marketcetera.photon.quotefeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.preferences.MapEditorUtil;
import org.marketcetera.photon.preferences.ScriptRegistryPage;
import org.marketcetera.photon.scripting.Script;
import org.marketcetera.photon.scripting.ScriptScheduler;
import org.marketcetera.quotefeed.IMessageListener;
import org.marketcetera.quotefeed.IQuoteFeed;

import quickfix.Message;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.matchers.Matcher;

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
		List<String> scriptPaths = getOnQuoteScripts();

		for (String path : scriptPaths) {
			Script aScript;
			try {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IResource resource = workspace.getRoot().findMember(path);
				URI resourceURI = resource.getRawLocationURI();

				aScript = new Script(readFileAsString(resourceURI.getPath()));
				scriptScheduler.submitScript(aScript);
			} catch (Exception e) {
				Application.getMainConsoleLogger().error("Error executing script "+path, e);
			}
		}
	}

	private List<String> getOnQuoteScripts() {
		return getEventScripts("quote"); // agl todo:change use a constant
											// from the model (once we have it)
	}

	private List<String> getOnTradeScripts() {
		return getEventScripts("trade"); // agl todo:change use a constant
											// from the model (once we have it)
	}

	private List<String> getEventScripts(final String eventType) {
		String encodedScriptList = Application.getPreferenceStore().getString(
				ScriptRegistryPage.SCRIPT_REGISTRY_PREFERENCE);
		// agl (any event type, script path) tuples
		EventList<Entry<String, String>> completeEventScriptList = MapEditorUtil
				.parseString(encodedScriptList);
		// agl (specified event type, script path) tuples
		FilterList<Entry<String, String>> eventScriptList = new FilterList<Entry<String, String>>(
				completeEventScriptList, new Matcher<Entry<String, String>>() {
					public boolean matches(Entry<String, String> entry) {
						return entry.getKey().equalsIgnoreCase(eventType);
					}
				});
		// agl only (script path)'s
		List<String> scriptList = new FunctionList<Entry<String, String>, String>(
				eventScriptList,
				new FunctionList.Function<Entry<String, String>, String>() {
					public String evaluate(Entry<String, String> entry) {
						return entry.getValue();
					}
				});
		return scriptList;
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

	/**
	 * @param filePath
	 *            the name of the file to open. Not sure if it can accept URLs
	 *            or just filenames. Path handling could be better, and buffer
	 *            sizes are hardcoded
	 */
	private static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
		} finally {
			if (reader != null ) reader.close();
		}
		return fileData.toString();
	}
}
