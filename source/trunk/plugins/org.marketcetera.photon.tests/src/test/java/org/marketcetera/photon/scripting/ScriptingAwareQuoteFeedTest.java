package org.marketcetera.photon.scripting;

import junit.framework.TestCase;

import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.Application;
import org.marketcetera.quotefeed.IQuoteFeed;


/**
 * Tests for the <code>ScriptingAwareQuoteFeed</code>. Mock objects are used for script registry, 
 * scripts, quote feed being wrapped and message listeners.
 * 
 * @author andrei@lissovski.org
 */
public class ScriptingAwareQuoteFeedTest extends TestCase {
	
	public void test_listenQuotes() throws Exception {
		MockScriptRegistry mockRegistry = new MockScriptRegistry();
		Application.setScriptRegistry(mockRegistry);
		
		MockQuoteFeed mockTargetFeed = new MockQuoteFeed();
		IQuoteFeed scriptingFeed = new ScriptingAwareQuoteFeed(mockTargetFeed);
		
		MockMessageListener mockMessageListener = new MockMessageListener();
		scriptingFeed.listenQuotes(new MSymbol("S1"), mockMessageListener);
		scriptingFeed.listenQuotes(new MSymbol("S2"), mockMessageListener);
		
		mockTargetFeed.simulateQuote(new MSymbol("S1"));
		assertTrue(mockMessageListener.onQuoteCalled(new MSymbol("S1")));
		assertFalse(mockMessageListener.onQuoteCalled(new MSymbol("S2")));
		
		mockMessageListener.reset();
		
		mockTargetFeed.simulateQuote(new MSymbol("S2"));
		assertTrue(mockMessageListener.onQuoteCalled(new MSymbol("S2")));
		assertFalse(mockMessageListener.onQuoteCalled(new MSymbol("S1")));
	}
	
	public void test_unlistenQuotes() throws Exception {
		MockScriptRegistry mockRegistry = new MockScriptRegistry();
		Application.setScriptRegistry(mockRegistry);
		
		MockQuoteFeed mockTargetFeed = new MockQuoteFeed();
		IQuoteFeed scriptingFeed = new ScriptingAwareQuoteFeed(mockTargetFeed);
		
		MockMessageListener mockMessageListener = new MockMessageListener();
		
		scriptingFeed.listenQuotes(new MSymbol("S1"), mockMessageListener);
		scriptingFeed.listenQuotes(new MSymbol("S2"), mockMessageListener);
		scriptingFeed.unlistenQuotes(new MSymbol("S1"), mockMessageListener);
		
		mockTargetFeed.simulateQuote(new MSymbol("S1"));
		assertFalse(mockMessageListener.onQuoteCalled(new MSymbol("S1")));
		
		mockTargetFeed.simulateQuote(new MSymbol("S2"));
		assertTrue(mockMessageListener.onQuoteCalled(new MSymbol("S2")));
	}
	
	public void testOnQuoteScripting() throws Exception {
		MockScriptRegistry mockRegistry = new MockScriptRegistry();
		MockScript mockScript1 = mockRegistry.createAndRegisterMockScript(ScriptingEventType.QUOTE);
		MockScript mockScript2 = mockRegistry.createAndRegisterMockScript(ScriptingEventType.QUOTE);
		MockScript mockScript3 = mockRegistry.createAndRegisterMockScript(ScriptingEventType.TRADE);
		
		Application.setScriptRegistry(mockRegistry);
		
		MockQuoteFeed mockTargetFeed = new MockQuoteFeed();
		IQuoteFeed scriptingFeed = new ScriptingAwareQuoteFeed(mockTargetFeed);
		
		MockMessageListener mockMessageListener = new MockMessageListener();
		
		scriptingFeed.listenQuotes(new MSymbol("S1"), mockMessageListener);
		scriptingFeed.listenQuotes(new MSymbol("S2"), mockMessageListener);
		
		mockTargetFeed.simulateQuote(new MSymbol("S1"));
		
		Thread.sleep(1000);  //agl todo:revisit make it more deterministic by somehow forcing all scripts to be executed? 
		
		assertTrue(mockScript1.isExeced());
		assertTrue(mockScript2.isExeced());
		assertFalse(mockScript3.isExeced());
	}
}
