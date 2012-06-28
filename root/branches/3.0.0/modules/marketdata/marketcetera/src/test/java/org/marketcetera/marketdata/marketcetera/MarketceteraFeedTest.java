package org.marketcetera.marketdata.marketcetera;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * Testing the {@link MarketceteraFeed} class.
 * @author toli
 * @version $Id: MarketceteraFeedTest.java 16063 2012-01-31 18:21:55Z colin $
 */

@ClassVersion("$Id: MarketceteraFeedTest.java 16063 2012-01-31 18:21:55Z colin $")
public class MarketceteraFeedTest
{
    @Before
    public void setUp()
            throws Exception
    {
//        map.put(MarketceteraFeed.SETTING_SENDER_COMP_ID, "test-sender");
//        map.put(MarketceteraFeed.SETTING_TARGET_COMP_ID, "test-target");
//        feed = new MockMarketceteraFeed("FIX.4.4://localhost:1234", null, null, map);
    }
    @Test
    public void testEmpty()
    {
        
    }

//    public void testSubscribe() throws Exception {
//        ISubscription sub1 = feed.asyncQuery(MarketDataUtils.newSubscribeBBO(new Equity("XYZ")));
//        ISubscription sub2 = feed.asyncQuery(MarketDataUtils.newSubscribeBBO(new Equity("ABC")));
//        assertNotSame("same subscriptions", sub1, sub2);
//    }

//    public void testUnsubscribe() throws Exception {
//    	feed.start();
//        ISubscription sub = feed.asyncQuery(MarketDataUtils.newSubscribeBBO(new Equity("XYZ")));
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
