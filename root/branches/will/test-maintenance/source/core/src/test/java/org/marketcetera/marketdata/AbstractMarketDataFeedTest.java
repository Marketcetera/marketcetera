package org.marketcetera.marketdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import org.junit.Assert;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.event.AggregateEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.EventBaseTest;
import org.marketcetera.event.MockEventTranslator;
import org.marketcetera.event.AggregateEventTest.MockAggregateEvent;
import org.marketcetera.event.EventBaseTest.MockEvent;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.MarketDataFeedToken.Status;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Tests {@link AbstractMarketDataFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class AbstractMarketDataFeedTest
    extends MarketDataFeedTestBase
    implements Messages
{
    private final MSymbol metc = new MSymbol("METC");
    private final String exchange = "TEST";
    @Test
    public void testConstructor()
        throws Exception
    {
        final String providerName = "TestProviderName";
        final FeedType type = FeedType.UNKNOWN;
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                new MockMarketDataFeed(null,
                                       providerName,
                                       0);
            }
        }.run();                             
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
            throws Throwable
            {
                new MockMarketDataFeed(type,
                                       null,
                                       0);
            }
        }.run();                             
        
        MockMarketDataFeed feed = new MockMarketDataFeed(type,
                                                         providerName,
                                                         0);
        assertNotNull(feed);
        assertEquals(type,
                     feed.getFeedType());
        assertNotNull(feed.getID());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
        feed = new MockMarketDataFeed(type,
                                      providerName,
                                      0);
        assertNotNull(feed);
        assertEquals(type,
                     feed.getFeedType());
        assertNotNull(feed.getID());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
    }
    @Test
    public void testCancel()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        ISubscriber[] subscribers = new ISubscriber[0];
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.cancel(null);
            }
        }.run();

        MarketDataFeedTokenSpec spec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest,
                                                                                 subscribers);
        MockMarketDataFeedToken token = feed.execute(spec);
        feed.cancel(token);
        verifyAllCanceled(feed);
        feed.setCancelFails(true);
        token = feed.execute(token.getTokenSpec());
        feed.cancel(token);
        verifyAllCanceled(feed);
        // set it so the execution step returns no handles, thus guaranteeing that the cancel
        //  token request can't match any handles
        feed.setExecuteReturnsNothing(true);
        feed.setCancelFails(false);
        // execute the same query
        token = feed.execute(token.getTokenSpec());
        feed.cancel(token);
        verifyAllCanceled(feed);
    }
    @Test
    public void testStart()
    	throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        assertFalse(feed.isRunning());
        feed.start();
        assertFalse(feed.isRunning());
        feed.login(new MockMarketDataFeedCredentials());
        assertTrue(feed.isRunning());
        feed.stop();
        assertFalse(feed.isRunning());
    }
    @Test
    public void testStop()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        MarketDataRequest request1 = MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("GOOG,MSFT");
        MarketDataRequest request2 = MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("YHOO"); 
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        MockSubscriber subscriber = new MockSubscriber();
        MarketDataFeedTokenSpec spec1 = MarketDataFeedTokenSpec.generateTokenSpec(request1, 
                                                                                  subscriber);
        MarketDataFeedTokenSpec spec2 = MarketDataFeedTokenSpec.generateTokenSpec(request2, 
                                                                                  subscriber);
        feed.start();
        feed.login(credentials);
        MockMarketDataFeedToken token1 = feed.execute(spec1);
        MockMarketDataFeedToken token2 = feed.execute(spec2);
        assertEquals(Status.ACTIVE,
                     token1.getStatus());
        assertEquals(Status.ACTIVE,
                     token2.getStatus());
        assertTrue(feed.getCanceledHandles().isEmpty());
        feed.stop();
        assertEquals(Status.SUSPENDED,
                     token1.getStatus());
        assertEquals(Status.SUSPENDED,
                     token2.getStatus());
        assertEquals(2,
                     feed.getCanceledHandles().size());
    }
    @Test
    public void testDoInitialize()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.setInitFails(false);
        assertTrue(feed.doInitialize(null));
        MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest, 
                                                                                      new ISubscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(tokenSpec,
                                                                         feed);
        assertTrue(feed.doInitialize(token));
    }
    @Test
    public void testBeforeDoExecute()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        assertTrue(feed.beforeDoExecute(null));
        MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest, 
                                                                                      new ISubscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(tokenSpec,
                                                                         feed);
        assertTrue(feed.beforeDoExecute(token));
    }
    @Test
    public void testAfterDoExecute()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest, 
                                                                                      new ISubscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(tokenSpec,
                                                                         feed);
        feed.afterDoExecute(null, null);
        feed.afterDoExecute(token, null);
    }
    @Test
    public void testSetFeedStatus()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.setFeedStatus(null);
            }
        }.run();

        TestFeedComponentListener listener = new TestFeedComponentListener();
        assertTrue(listener.getChangedComponents().isEmpty());
        feed.addFeedComponentListener(listener);
        
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
        
        feed.setFeedStatus(FeedStatus.UNKNOWN);
        listener.mSemaphore.acquire();
        assertEquals(1,
                     listener.getChangedComponents().size());
        assertEquals(feed,
                     listener.getChangedComponents().get(0));
        listener.mSemaphore.release();
        listener.reset();
        // change feed status, make sure listener gets updated
        feed.setFeedStatus(FeedStatus.AVAILABLE);
        listener.mSemaphore.acquire();
        assertEquals(FeedStatus.AVAILABLE,
                     feed.getFeedStatus());
        assertEquals(1,
                     listener.getChangedComponents().size());
        assertEquals(feed,
                     listener.getChangedComponents().get(0));
        listener.mSemaphore.release();
        listener.reset();
        // make sure listener is not notified if set to the same status
        feed.setFeedStatus(FeedStatus.AVAILABLE);
        Thread.sleep(1000);
        assertTrue(listener.getChangedComponents().isEmpty());
    }
    @Test
    public void testFeedComponentListener()
    	throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.addFeedComponentListener(null);
            }
        }.run();
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.removeFeedComponentListener(null);
            }
        }.run();
        
    	TestFeedComponentListener listener = new TestFeedComponentListener();
    	assertTrue(listener.getChangedComponents().isEmpty());
        feed.start();
    	assertTrue(listener.getChangedComponents().isEmpty());
    	feed.addFeedComponentListener(listener);
        feed.stop();
        listener.mSemaphore.acquire();
    	assertEquals(1,
    			     listener.getChangedComponents().size());
    	assertEquals(feed,
    			     listener.getChangedComponents().get(0));
    	listener.mSemaphore.release();
    	listener.reset();
    	// re-add the same listener, make sure there's only one notification
        feed.addFeedComponentListener(listener);
        feed.start();
        listener.mSemaphore.acquire();
        assertEquals(1,
                     listener.getChangedComponents().size());
        assertEquals(feed,
                     listener.getChangedComponents().get(0));
        listener.mSemaphore.release();
        listener.reset();
    	// remove a listener that's not subscribed
    	feed.removeFeedComponentListener(new IFeedComponentListener() {
			@Override
			public void feedComponentChanged(IFeedComponent component) {
			}    		
    	});
    	feed.stop();
    	listener.mSemaphore.acquire();
    	assertEquals(1,
    			     listener.getChangedComponents().size());
    	assertEquals(feed,
    			     listener.getChangedComponents().get(0));
    	listener.mSemaphore.release();
    	listener.reset();
    	// remove actual listener
    	feed.removeFeedComponentListener(listener);
    	feed.start();
    	Thread.sleep(1000);
    	assertTrue(listener.getChangedComponents().isEmpty());
    }
    @Test
    public void testDataReceived()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.dataReceived(null, 
                                  this);
            }
        }.run();
        feed.dataReceived("handle",
                          null);
    }
    /**
     * Tests the feed's ability to timeout a request and throw the correct exception.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void testTimeout()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        feed.setShouldTimeout(true);
        final MarketDataFeedTokenSpec spec = MarketDataFeedTokenSpec.generateTokenSpec(MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("GOOG"), 
                                                                                       new ISubscriber[0]);
        new ExpectedTestFailure(FeedException.class,
                                Messages.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getText()) {
            protected void execute()
                    throws Throwable
            {
                feed.execute(spec);
            }
        }.run();
    }
    /**
     * Tests the ability of a feed to resubmit active queries
     * upon reconnect.
     * 
     * @throws Exception if the test fails
     */
    @Test
    public void testReconnect()
        throws Exception
    {
        // 1) Create feed, verify status is !running, verify no active queries
        // 2) Start feed, verify status is running, verify no active queries
        // 3) Submit a query, verify subscriber gets response
        // 4) Stop feed, verify status is !running, verify active queries
        // 5) Start feed, verify status is running, verify active queries
        // 6) Verify subscriber gets update
        // #1
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
        assertFalse(feed.getFeedStatus().isRunning());
        assertTrue(feed.getCreatedHandles().isEmpty());
        // #2
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        assertEquals(FeedStatus.AVAILABLE,
                     feed.getFeedStatus());
        assertTrue(feed.getFeedStatus().isRunning());
        assertTrue(feed.getCreatedHandles().isEmpty());
        // #3
        MockSubscriber s1 = new MockSubscriber();
        MarketDataRequest request0 = MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("test");
        MarketDataFeedTokenSpec spec = MarketDataFeedTokenSpec.generateTokenSpec(request0,
                                                                                 s1);
        MockMarketDataFeedToken token = feed.execute(spec);
        waitForPublication(s1);
        assertEquals(token,
                     ((MockEvent)s1.getData()).getSource());
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(Status.ACTIVE,
                     token.getStatus());
        // #4
        assertTrue(feed.isLoggedIn());
        feed.stop();
        assertFalse(feed.isLoggedIn());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
        assertFalse(feed.getFeedStatus().isRunning());
        List<String> handleList1 = feed.getCreatedHandles();
        assertEquals(1,
                     handleList1.size());
        assertEquals(Status.SUSPENDED,
                     token.getStatus());
        // #5
        // reset the statistics on s1
        s1.reset();
        assertEquals(0,
                     s1.getPublishCount());
        // restart feed, should trigger a resubmission of the query for message0
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        assertTrue(feed.isLoggedIn());
        assertEquals(FeedStatus.AVAILABLE,
                     feed.getFeedStatus());
        assertTrue(feed.getFeedStatus().isRunning());
        assertEquals(handleList1,
                     feed.getCanceledHandles());
        // #6
        // query should have been resubmitted,
        waitForPublication(s1);
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(token,
                     ((MockEvent)s1.getData()).getSource());
        assertEquals(Status.ACTIVE,
                     token.getStatus());
        // now check to make sure that the resubmitted query has a new handle
        List<String> handleList2 = feed.getCreatedHandles();
        assertEquals(2,
                     handleList2.size());
        // create two new requests to use
        MarketDataRequest request1 = MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("COLIN");
        MarketDataRequest request2 = MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("NOT-COLIN");
        assertFalse(request1.equals(request2));
        // reset the subscriber counters
        s1.reset();
        // submit data to the old handle
        feed.submitData(handleList1.get(0), 
                        request1);
        // we could wait for a little bit and make sure the data wasn't received,
        //  but that wouldn't be deterministic.  instead, we'll right away submit a
        //  second message to the new handle and make sure that s1 got that one and
        //  only that one.  since the second message will have to be delivered after
        //  the first one, once we're sure the second one has gotten through, if the
        //  first one still isn't there, then we know for sure it worked as planned
        feed.submitData(handleList2.get(1),
                        request2);
        waitForPublication(s1);
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(token,
                     ((MockEvent)s1.getPublications().get(0)).getSource());
        // bonus testing - make a resubmission fail and verify that the token status is set correctly
        // there is already one active query represented by "spec" and "token" - add another one that
        //  we can set to fail when it is resubmitted
        s1.reset();
        MarketDataFeedTokenSpec spec2 = MarketDataFeedTokenSpec.generateTokenSpec(spec.getDataRequest(), 
                                                                                  spec.getSubscribers());
        MockMarketDataFeedToken token2 = feed.execute(spec2);
        waitForPublication(s1);
        assertEquals(token2,
                     ((MockEvent)s1.getData()).getSource());
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(Status.ACTIVE,
                     token2.getStatus());
        // the feed now has 2 active queries
        assertTrue(feed.isLoggedIn());
        feed.stop();
        assertFalse(feed.isLoggedIn());
        // before we restart the feed, set the first query to fail on resubmission
        s1.reset();
        token.setShouldFail(true);
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        assertTrue(feed.isLoggedIn());
        assertEquals(FeedStatus.AVAILABLE,
                     feed.getFeedStatus());
        assertTrue(feed.getFeedStatus().isRunning());
        waitForPublication(s1);
        // check token status
        assertEquals(Status.EXECUTION_FAILED,
                     token.getStatus());
        assertEquals(Status.ACTIVE,
                     token2.getStatus());
    }
    /**
     * Tests the ability of the feed to catch invalid requests.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void requestValidation()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        // create an invalid request
        final MarketDataRequest invalidRequest = MarketDataRequest.newRequest();
        // prove that it's invalid
        new ExpectedFailure<IllegalArgumentException>(MISSING_SYMBOLS.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRequest.validate(invalidRequest);
            }
        };
        // try to submit the request, make sure the request fails
        MockSubscriber subscriber = new MockSubscriber();
        MarketDataFeedTokenSpec spec = MarketDataFeedTokenSpec.generateTokenSpec(invalidRequest,
                                                                                 subscriber);
        MockMarketDataFeedToken token = feed.execute(spec);
        assertEquals(Status.EXECUTION_FAILED,
                     token.getStatus());
        // fix the request
        invalidRequest.withSymbols("METC");
        // resubmit
        token = feed.execute(spec);
        assertEquals(Status.ACTIVE,
                     token.getStatus());
    }
    private static class TestFeedComponentListener
    	implements IFeedComponentListener
    {
    	private Semaphore mSemaphore = new Semaphore(1);
    	private List<IFeedComponent> mChangedComponents = new ArrayList<IFeedComponent>();
    	private TestFeedComponentListener()
    		throws Exception
    	{
    		mSemaphore.acquire();
    	}
		@Override
		public void feedComponentChanged(IFeedComponent component) 
		{
			synchronized(mChangedComponents) {
				mChangedComponents.add(component);
			}
			mSemaphore.release();
		}
		private void reset() 
			throws InterruptedException
		{
			synchronized(mChangedComponents) {
				mChangedComponents.clear();
			}
			mSemaphore.acquire();
		}
		private List<IFeedComponent> getChangedComponents()
		{
			synchronized(mChangedComponents) {
				return new ArrayList<IFeedComponent>(mChangedComponents);
			}
		}
    }
    private void verifyAllCanceled(MockMarketDataFeed inFeed)
        throws Exception
    {
        List<String> createdHandles = inFeed.getCreatedHandles();
        List<String> canceledHandles = inFeed.getCanceledHandles();
        assertEquals(createdHandles.size(),
                     canceledHandles.size());
        assertTrue(Arrays.equals(createdHandles.toArray(), 
                                 canceledHandles.toArray()));
    }
    @Test
    public void testPublishEventsThrowsException()
        throws Exception
    {
        MockSubscriber subscriber1 = new MockSubscriber();
        MockSubscriber subscriber2 = new MockSubscriber();
        MockSubscriber subscriber3 = new MockSubscriber();
        subscriber2.setPublishThrows(true);
        assertFalse(subscriber1.getPublishThrows());
        assertTrue(subscriber2.getPublishThrows());
        assertFalse(subscriber3.getPublishThrows());
        MockMarketDataFeed feed = new MockMarketDataFeed();
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        ISubscriber[] subscribers = new ISubscriber[] { subscriber1, subscriber2, subscriber3 };
        MarketDataFeedTokenSpec spec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest,
                                                                                 subscribers);
        MockMarketDataFeedToken token = feed.execute(spec);
        // make sure that 1 & 3 received publications despite 2's rudeness
        assertNotNull(token);
        while(subscriber1.getData() == null) {
            Thread.sleep(100);
        }
        assertEquals(1,
                     subscriber1.getPublishCount());
        assertEquals(0,
                     subscriber2.getPublishCount());
        while(subscriber3.getData() == null) {
            Thread.sleep(100);
        }
        assertEquals(1,
                     subscriber3.getPublishCount());
    }        
    @Test
    public void testExecute()
    	throws Exception
    {
        MockSubscriber subscriber = new MockSubscriber();
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                doExecuteTest(a==0 ? null : dataRequest,
                              b==0 ? null : subscriber, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false, 
                              false);  
            }
        }
        subscriber.reset();
        MarketDataRequest fullDepthMessage = MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("GOOG").withContent(Content.TOTAL_VIEW);
        doExecuteTest(fullDepthMessage,
                      subscriber,
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);  
    }
    @Test
    public void testParallelExecution()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN,
                                                         "MockMarketDataFeed",
                                                         25);
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        List<MockSubscriber> subscribers = new ArrayList<MockSubscriber>();
        List<MockMarketDataFeedToken> tokens = new ArrayList<MockMarketDataFeedToken>();
        for(int i=0;i<1000;i++) {
            MockSubscriber s = new MockSubscriber();
            subscribers.add(s);
            MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest, 
                                                                                          subscribers.toArray(new MockSubscriber[subscribers.size()]));
            tokens.add(feed.execute(tokenSpec));
        }
        for(MockSubscriber s : subscribers) {
            while(s.getData() == null) {
                Thread.sleep(50);
            }
        }
        for(MockMarketDataFeedToken token : tokens) {
            feed.cancel(token);
        }
        assertEquals(feed.getCreatedHandles(),
                     feed.getCanceledHandles());
        assertTrue(Arrays.equals(feed.getCreatedHandles().toArray(),
                                 feed.getCanceledHandles().toArray()));
    }
    /**
     * Tests feed's ability to return capabilities. 
     */
    @Test
    public void testCapabilities()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed();
        assertTrue(Arrays.equals(new Capability[0],
                                 feed.getCapabilities().toArray()));
        Set<Capability> capabilities = EnumSet.of(Capability.MARKET_STAT,
                                                  Capability.TOP_OF_BOOK);
        feed.setCapabilities(capabilities);
        Assert.assertArrayEquals(capabilities.toArray(),
                                 feed.getCapabilities().toArray());
    }
    /**
     * Verifies that {@link AggregateEvent} objects are properly decomposed.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void decomposition()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        // set up a subscriber to receive events
        final MockSubscriber s = new MockSubscriber();
        // a market data request (doesn't matter what)
        MarketDataRequest request = new MarketDataRequest().fromProvider("not-a-real-provider").withSymbols("METC");
        // first, have the feed return a non-aggregate event
        MockEvent e = new MockEvent();
        assertTrue(EventBase.class.isAssignableFrom(e.getClass()));
        assertFalse(AggregateEvent.class.isAssignableFrom(e.getClass()));
        feed.setEventsToReturn(Arrays.asList(new EventBase[] { e } ));
        feed.execute(MarketDataFeedTokenSpec.generateTokenSpec(request,
                                                               s));
        waitForPublication(s);
        assertEquals(1,
                     s.getPublishCount());
        assertEquals(e,
                     s.getPublications().get(0));
        // next, send in an aggregate event and make sure it gets properly decomposed
        List<EventBase> expectedEvents = Arrays.asList(new EventBase[] { EventBaseTest.generateAskEvent(metc,
                                                                                                        exchange),
                                                                         EventBaseTest.generateBidEvent(metc,
                                                                                                        exchange) } );
        MockAggregateEvent mae = new MockAggregateEvent(expectedEvents);
        feed.setEventsToReturn(Arrays.asList(new EventBase[] { mae } ));
        s.reset();
        feed.execute(MarketDataFeedTokenSpec.generateTokenSpec(request,
                                                               s));
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return s.getPublishCount() == 2;
            }
        });
        assertEquals(expectedEvents,
                     s.getPublications());
    }
    @Test
    public void testExecuteFailures()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed();
        feed.start();
        feed.login(new MockMarketDataFeedCredentials());
        
        // test nulls
        // test execute
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.execute(null);
            }
        }.run();
        // test more intricate failure conditions
        doExecuteTest(dataRequest, 
                      null, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false,
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true, 
                      false);
        doExecuteTest(dataRequest, 
                      null, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      false, 
                      true);
    }
    private void doExecuteTest(final MarketDataRequest inRequest,
                               final MockSubscriber inSubscriber,
                               boolean inLoginFails, 
                               boolean inInitFails, 
                               boolean inLoginThrows, 
                               boolean inIsLoggedInThrows, 
                               boolean inInitThrows, 
                               boolean inExecThrows, 
                               boolean inGenerateTokenThrows, 
                               boolean inGetEventTranslatorThrows, 
                               boolean inTranslateToEventsThrows, 
                               boolean inTranslateToEventsReturnsNull, 
                               boolean inTranslateToEventsReturnsZeroEvents, 
                               boolean inBeforeExecuteReturnsFalse, 
                               boolean inGetMessageTranslatorThrows, 
                               boolean inTranslateThrows, 
                               boolean inAfterExecuteThrows, 
                               boolean inBeforeExecuteThrows, 
                               boolean inRequestReturnsZeroHandles, 
                               boolean inRequestReturnsNull)
        throws Exception
    {
        if(inRequest == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                        throws Throwable
                {
                    MarketDataFeedTokenSpec.generateTokenSpec(inRequest, 
                                                              inSubscriber);
                }
            }.run();                             
        } else {
            MarketDataFeedTokenSpec tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(inRequest, 
                                                                                          inSubscriber);
            doExecuteTest(tokenSpec,
                          inLoginFails,
                          inInitFails,
                          inLoginThrows, 
                          inIsLoggedInThrows, 
                          inInitThrows, 
                          inExecThrows, 
                          inGenerateTokenThrows, 
                          inGetEventTranslatorThrows, 
                          inTranslateToEventsThrows, 
                          inTranslateToEventsReturnsNull, 
                          inTranslateToEventsReturnsZeroEvents, 
                          inBeforeExecuteReturnsFalse, 
                          inGetMessageTranslatorThrows, 
                          inTranslateThrows, 
                          inAfterExecuteThrows, 
                          inBeforeExecuteThrows, 
                          inRequestReturnsZeroHandles, 
                          inRequestReturnsNull);
        }
    }
    private void doExecuteTest(final MarketDataFeedTokenSpec inTokenSpec,
                               boolean inLoginFails, 
                               boolean inInitFails, 
                               boolean inLoginThrows, 
                               boolean inIsLoggedInThrows, 
                               boolean inInitThrows, 
                               boolean inExecThrows, 
                               boolean inGenerateTokenThrows, 
                               boolean inGetEventTranslatorThrows, 
                               boolean inTranslateToEventsThrows, 
                               boolean inTranslateToEventsReturnsNull, 
                               boolean inTranslateToEventsReturnsZeroEvents, 
                               boolean inBeforeExecuteReturnsFalse, 
                               boolean inGetMessageTranslatorThrows, 
                               boolean inTranslateThrows, 
                               boolean inAfterExecuteThrows, 
                               boolean inBeforeExecuteThrows, 
                               boolean inRequestReturnsZeroHandles, 
                               boolean inRequestReturnsNull)
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN,
                                                               "obnoxious-feed-name-with-dashes",
                                                               0);
        final ISubscriber[] subscribers = inTokenSpec.getSubscribers();
        if(subscribers != null) {
            for(ISubscriber subscriber : subscribers) {
                if(subscriber != null) {
                    MockSubscriber s = (MockSubscriber)subscriber;
                    assertNull(s.getData());
                }
            }
        }
        feed.setLoginFails(inLoginFails);
        feed.setInitFails(inInitFails);
        feed.setExecutionFails(inExecThrows);
        feed.setLoginThrows(inLoginThrows);
        feed.setIsLoggedInThrows(inIsLoggedInThrows);
        feed.setInitThrows(inInitThrows);
        feed.setGenerateTokenThrows(inGenerateTokenThrows);
        feed.setGetEventTranslatorThrows(inGetEventTranslatorThrows);
        MockEventTranslator.setTranslateToEventsThrows(inTranslateToEventsThrows);
        MockEventTranslator.setTranslateToEventsReturnsNull(inTranslateToEventsReturnsNull);
        MockEventTranslator.setTranslateToEventsReturnsZeroEvents(inTranslateToEventsReturnsZeroEvents);
        feed.setBeforeExecuteReturnsFalse(inBeforeExecuteReturnsFalse);
        feed.setGetMessageTranslatorThrows(inGetMessageTranslatorThrows);
        MockDataRequestTranslator.setTranslateThrows(inTranslateThrows);
        feed.setAfterExecuteThrows(inAfterExecuteThrows);
        feed.setBeforeExecuteThrows(inBeforeExecuteThrows);
        feed.setExecuteReturnsNothing(inRequestReturnsZeroHandles);
        feed.setExecuteReturnsNull(inRequestReturnsNull);
        // start the feed
        feed.start();
        // log in to the feed
        if(inLoginThrows ||
           inIsLoggedInThrows) {
            new ExpectedFailure<NullPointerException>(null) {
                @Override
                protected void run()
                    throws Exception
                {
                    feed.login(new MockMarketDataFeedCredentials());
                }
            };
            return;
        }
        assertEquals(!inLoginFails,
                     feed.login(new MockMarketDataFeedCredentials()));
        // check login-failed conditions first
        if(inLoginFails) {
            assertEquals(FeedStatus.OFFLINE,
                         feed.getFeedStatus());
            return;
        }
        // feed is logged in
        assertEquals(FeedStatus.AVAILABLE,
                     feed.getFeedStatus());
        // test token generation
        if(inGenerateTokenThrows) {
            new ExpectedFailure<FeedException>(Messages.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED) {
                @Override
                protected void run()
                        throws Exception
                {
                    feed.execute(inTokenSpec);
                }
            };
            return;
        }
        MockMarketDataFeedToken token = feed.execute(inTokenSpec);
        assertNotNull(token);
        verifyExecution(inExecThrows, 
                        inGetEventTranslatorThrows, 
                        inTranslateToEventsThrows, 
                        inTranslateToEventsReturnsNull, 
                        inTranslateToEventsReturnsZeroEvents, 
                        inBeforeExecuteReturnsFalse, 
                        inGetMessageTranslatorThrows, 
                        inTranslateThrows, 
                        inAfterExecuteThrows, 
                        inBeforeExecuteThrows, 
                        inRequestReturnsZeroHandles, 
                        inRequestReturnsNull, 
                        feed, 
                        token, 
                        subscribers);
    }
    private void verifyExecution(boolean inExecThrows, 
                                 boolean inGetEventTranslatorThrows, 
                                 boolean inTranslateToEventsThrows, 
                                 boolean inTranslateToEventsReturnsNull, 
                                 boolean inTranslateToEventsReturnsZeroEvents, 
                                 boolean inBeforeExecuteReturnsFalse, 
                                 boolean inGetMessageTranslatorThrows, 
                                 boolean inTranslateThrows, 
                                 boolean inAfterExecuteThrows, 
                                 boolean inBeforeExecuteThrows, 
                                 boolean inRequestReturnsZeroHandles, 
                                 boolean inRequestReturnsNull, 
                                 MockMarketDataFeed inFeed,
                                 MockMarketDataFeedToken inToken,
                                 ISubscriber[] inSubscribers)
        throws Exception
    {
        if(inExecThrows ||
           inBeforeExecuteReturnsFalse ||
           inBeforeExecuteThrows ||
           inAfterExecuteThrows) {
            assertEquals(MarketDataFeedToken.Status.EXECUTION_FAILED,
                         inToken.getStatus());
            if(inSubscribers != null) {
                for(ISubscriber subscriber : inSubscribers) {
                    if(subscriber != null) {
                        MockSubscriber s = (MockSubscriber)subscriber;
                        assertEquals(0,
                                     s.getPublishCount());
                    }
                }
            }
            return;
        }
        if(inGetMessageTranslatorThrows ||
           inTranslateThrows) {
            assertEquals(Status.EXECUTION_FAILED,
                         inToken.getStatus());
            if(inSubscribers != null) {
                for(ISubscriber subscriber : inSubscribers) {
                    if(subscriber != null) {
                        MockSubscriber s = (MockSubscriber)subscriber;
                        assertEquals(0,
                                     s.getPublishCount());
                    }
                }
            }
            return;
        }
        if(inGetEventTranslatorThrows ||
           inTranslateToEventsThrows ||
           inTranslateToEventsReturnsNull ||
           inTranslateToEventsReturnsZeroEvents) {
            assertEquals(Status.ACTIVE,
                         inToken.getStatus());
            if(inSubscribers != null) {
                for(ISubscriber subscriber : inSubscribers) {
                    if(subscriber != null) {
                        MockSubscriber s = (MockSubscriber)subscriber;
                        assertEquals(0,
                                     s.getPublishCount());
                    }
                }
            }
            return;
        }        
        if(inSubscribers != null &&
           !inRequestReturnsZeroHandles &&
           !inRequestReturnsNull) {
            for(ISubscriber subscriber : inSubscribers) {
                if(subscriber != null) {
                    MockSubscriber s = (MockSubscriber)subscriber;
                    waitForPublication(s);
                    assertEquals(1,
                                 s.getPublishCount());
                }
            }
        }
        assertEquals(FeedStatus.AVAILABLE,
                     inFeed.getFeedStatus());
        assertEquals(Status.ACTIVE,
                     inToken.getStatus());
    }    
}
