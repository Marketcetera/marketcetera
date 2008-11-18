package org.marketcetera.marketdata.marketcetera;

import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;

/**
 * Testing the {@link MarketceteraFeed} class.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MarketceteraFeedTest extends TestCase {
//    private MockMarketceteraFeed feed;

    public MarketceteraFeedTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(MarketceteraFeedTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        HashMap<String, Object> map = new HashMap<String, Object>();
//        map.put(MarketceteraFeed.SETTING_SENDER_COMP_ID, "test-sender");
//        map.put(MarketceteraFeed.SETTING_TARGET_COMP_ID, "test-target");
//        feed = new MockMarketceteraFeed("FIX.4.4://localhost:1234", null, null, map);
    }
    
    public void testEmpty()
    {
        
    }

//    public void testSubscribe() throws Exception {
//        ISubscription sub1 = feed.asyncQuery(MarketDataUtils.newSubscribeBBO(new MSymbol("XYZ")));
//        ISubscription sub2 = feed.asyncQuery(MarketDataUtils.newSubscribeBBO(new MSymbol("ABC")));
//        assertNotSame("same subscriptions", sub1, sub2);
//    }

//    public void testUnsubscribe() throws Exception {
//    	feed.start();
//        ISubscription sub = feed.asyncQuery(MarketDataUtils.newSubscribeBBO(new MSymbol("XYZ")));
//        feed.messages.clear();
//        feed.asyncUnsubscribe(sub);
//        Message unsubMsg = feed.messages.get(0);
//        assertEquals("unsubscribe message doesn't have NoRelatedSym", 0, unsubMsg.getInt(NoRelatedSym.FIELD));
//        assertEquals("unsubscribe message doesn't have NoMDEntryTypes", 0, unsubMsg.getInt(NoMDEntryTypes.FIELD));
//    }

//    private class MockMarketceteraFeed extends MarketceteraFeed
//    {
//        private Vector<Message> messages;
//        public MockMarketceteraFeed(String url, String userName, String password, Map<String, Object> properties)
//                throws MarketceteraException {
//            super(url, userName, password, properties, new NoOpLogger(""));
//            messages = new Vector<Message>();
//        }
//
//        protected void sendMessage(Message message) {
//            messages.add(message);
//        }
//    }
}
