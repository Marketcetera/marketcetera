package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.event.MessageEvent;
import org.marketcetera.event.MockEventTranslator;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.IMarketDataFeedToken.Status;

/* $License$ */

/**
 * Tests {@link AbstractMarketDataFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AbstractMarketDataFeedTest
    extends MarketDataFeedTestBase
{
    /**
     * Create a new <code>AbstractMarketDataFeedTest</code> instance.
     *
     * @param inArg0
     */
    public AbstractMarketDataFeedTest(String inArg0)
    {
        super(inArg0);
    }
    public static Test suite()
    {
        TestSuite suite = (TestSuite)MarketDataFeedTestBase.suite(AbstractMarketDataFeedTest.class);
        return suite;
    }
    public void testConstructor()
        throws Exception
    {
        final String providerName = "TestProviderName"; //$NON-NLS-1$
        final FeedType type = FeedType.UNKNOWN;
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                new MockMarketDataFeed(null,
                                       providerName,
                                       null,
                                       0);
            }
        }.run();                             
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
            throws Throwable
            {
                new MockMarketDataFeed(type,
                                       null,
                                       null,
                                       0);
            }
        }.run();                             
        
        MockMarketDataFeed feed = new MockMarketDataFeed(type,
                                                         providerName,
                                                         null,
                                                         0);
        assertNotNull(feed);
        assertEquals(type,
                     feed.getFeedType());
        assertNotNull(feed.getID());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
        feed = new MockMarketDataFeed(type,
                                      providerName,
                                      new MockMarketDataFeedCredentials(),
                                      0);
        assertNotNull(feed);
        assertEquals(type,
                     feed.getFeedType());
        assertNotNull(feed.getID());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
    }
    
    public void testNoCredentialsSupplied()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.execute(dataRequest,
                             new ISubscriber[0]);
            }
        }.run();
    }
    
    public void testCancel()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN,
                                                               new MockMarketDataFeedCredentials());
        feed.start();
        ISubscriber[] subscribers = new ISubscriber[0];
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.cancel(null);
            }
        }.run();

        MockMarketDataFeedToken token = feed.execute(dataRequest,
                                                     subscribers);
        feed.cancel(token);
        verifyAllCanceled(feed);
        feed.setCancelFails(true);
        token = feed.execute(dataRequest,
                             subscribers);
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
    
    public void testStart()
    	throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        assertFalse(feed.isRunning());
        feed.start();
        assertTrue(feed.isRunning());
        feed.stop();
        assertFalse(feed.isRunning());
    }
    
    public void testStop()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        DataRequest request1 = MarketDataRequest.newFullBookRequest("GOOG", 
                                                                    "MSFT");
        DataRequest request2 = MarketDataRequest.newFullBookRequest("YHOO"); 
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        MockSubscriber subscriber = new MockSubscriber();
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> spec1 = MarketDataFeedTokenSpec.generateTokenSpec(credentials,
                                                                                                                 request1, 
                                                                                                                 new MockSubscriber[] { subscriber } );
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> spec2 = MarketDataFeedTokenSpec.generateTokenSpec(credentials,
                                                                                                                 request2, 
                                                                                                                 new MockSubscriber[] { subscriber } );
        feed.start();
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
    
    public void testDoInitialize()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.setInitFails(false);
        assertTrue(feed.doInitialize(null));
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(new MockMarketDataFeedCredentials(),
                                                                                                                     dataRequest, 
                                                                                                                     new ISubscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(tokenSpec,
                                                                         feed);
        assertTrue(feed.doInitialize(token));
    }
    
    public void testBeforeDoExecute()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        assertTrue(feed.beforeDoExecute(null));
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(new MockMarketDataFeedCredentials(),
                                                                                                                     dataRequest, 
                                                                                                                     new ISubscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(tokenSpec,
                                                                         feed);
        assertTrue(feed.beforeDoExecute(token));
    }
    
    public void testAfterDoExecute()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(new MockMarketDataFeedCredentials(),
                                                                                                                     dataRequest, 
                                                                                                                     new ISubscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(tokenSpec,
                                                                         feed);
        feed.afterDoExecute(null, null);
        feed.afterDoExecute(token, null);
    }
    
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
        feed.dataReceived("handle", //$NON-NLS-1$
                          null);
    }
    /**
     * Tests the subscribe to all queries function.
     *
     * @throws Exception
     */
    public void testSubscribeAll()
        throws Exception
    {
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.start();
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        // create three subscribers - 1 & 3 will be assigned to specific queries,
        //  2 will be a general subscriber (all messages)
        MockSubscriber s1 = new MockSubscriber();
        MockSubscriber s2 = new MockSubscriber();
        MockSubscriber s3 = new MockSubscriber();
        feed.subscribeToAll(s2);
        // execute two queries
        feed.execute(credentials,
                     dataRequest,
                     s1);
        feed.execute(credentials,
                     dataRequest,
                     s3);
        // wait for the results to come in
        waitForPublication(s1);
        waitForPublication(s2);
        waitForPublication(s3);
        // 1 & 3 should have gotten one event, 2 should get both
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(2,
                     s2.getPublishCount());
        assertEquals(1,
                     s3.getPublishCount());
        // unsubscribe from general
        feed.unsubscribeFromAll(s2);
        // reset the subscriber counters
        resetSubscribers(new MockSubscriber[] { s1, s2, s3 } );
        // re-execute the queries
        feed.execute(credentials,
                     dataRequest,
                     s1);
        feed.execute(credentials,
                     dataRequest,
                     s3);
        // wait for the publications, making sure 2 receives none
        // this is deterministic because 3 won't be notified until 2 would have been by the first query
        waitForPublication(s1);
        waitForPublication(s3);
        // 2 did not get notified this time
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(0,
                     s2.getPublishCount());
        assertEquals(1,
                     s3.getPublishCount());
    }
    /**
     * Tests the feed's ability to timeout a request and throw the correct exception.
     *
     * @throws Exception if an error occurs
     */
    public void testTimeout()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN);
        feed.start();
        feed.setShouldTimeout(true);
        final MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> spec = MarketDataFeedTokenSpec.generateTokenSpec(new MockMarketDataFeedCredentials(),
                                                                                                                      MarketDataRequest.newFullBookRequest("GOOG"), 
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
        assertEquals(FeedStatus.AVAILABLE,
                     feed.getFeedStatus());
        assertTrue(feed.getFeedStatus().isRunning());
        assertTrue(feed.getCreatedHandles().isEmpty());
        // #3
        MockSubscriber s1 = new MockSubscriber();
        DataRequest request0 = MarketDataRequest.newFullBookRequest("test");
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> spec = MarketDataFeedTokenSpec.generateTokenSpec(new MockMarketDataFeedCredentials(),
                                                                                                                request0,
                                                                                                                new MockSubscriber[] { s1 } );
        MockMarketDataFeedToken token = feed.execute(spec);
        waitForPublication(s1);
        assertEquals(request0,
                     ((MessageEvent)s1.getData()).getRequest());
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(Status.ACTIVE,
                     token.getStatus());
        // #4
        assertTrue(feed.isLoggedIn(spec.getCredentials()));
        feed.stop();
        assertFalse(feed.isLoggedIn(spec.getCredentials()));
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
        assertTrue(feed.isLoggedIn(spec.getCredentials()));
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
        assertEquals(request0,
                     ((MessageEvent)s1.getData()).getRequest());
        assertEquals(Status.ACTIVE,
                     token.getStatus());
        // now check to make sure that the resubmitted query has a new handle
        List<String> handleList2 = feed.getCreatedHandles();
        assertEquals(2,
                     handleList2.size());
        // create two new requests to use
        DataRequest request1 = MarketDataRequest.newFullBookRequest("COLIN");
        DataRequest request2 = MarketDataRequest.newFullBookRequest("NOT-COLIN");
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
        assertEquals(request2,
                     ((MessageEvent)s1.getPublications().get(0)).getRequest());
        // bonus testing - make a resubmission fail and verify that the token status is set correctly
        // there is already one active query represented by "spec" and "token" - add another one that
        //  we can set to fail when it is resubmitted
        s1.reset();
        MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> spec2 = MarketDataFeedTokenSpec.generateTokenSpec(spec.getCredentials(),
                                                                                                                 spec.getDataRequest(), 
                                                                                                                 spec.getSubscribers());
        MockMarketDataFeedToken token2 = feed.execute(spec2);
        waitForPublication(s1);
        assertEquals(spec.getDataRequest(),
                     ((MessageEvent)s1.getData()).getRequest());
        assertEquals(1,
                     s1.getPublishCount());
        assertEquals(Status.ACTIVE,
                     token2.getStatus());
        // the feed now has 2 active queries
        assertTrue(feed.isLoggedIn(spec.getCredentials()));
        feed.stop();
        assertFalse(feed.isLoggedIn(spec.getCredentials()));
        // before we restart the feed, set the first query to fail on resubmission
        s1.reset();
        token.setShouldFail(true);
        feed.start();
        assertTrue(feed.isLoggedIn(spec.getCredentials()));
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
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        ISubscriber[] subscribers = new ISubscriber[] { subscriber1, subscriber2, subscriber3 };
        MockMarketDataFeedToken token = feed.execute(credentials,
                                                     dataRequest,
                                                     subscribers);
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
        DataRequest fullDepthMessage = MarketDataRequest.newFullBookRequest("GOOG");
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

    public void testParallelExecution()
        throws Exception
    {
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN,
                                                         "MockMarketDataFeed", //$NON-NLS-1$
                                                         credentials,
                                                         25);
        feed.start();
        List<MockSubscriber> subscribers = new ArrayList<MockSubscriber>();
        List<MockMarketDataFeedToken> tokens = new ArrayList<MockMarketDataFeedToken>();
        for(int i=0;i<1000;i++) {
            MockSubscriber s = new MockSubscriber();
            subscribers.add(s);
            MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(credentials,
                                                                                                                         dataRequest, 
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

    public void testExecuteFailures()
        throws Exception
    {
        final MockMarketDataFeed feed = new MockMarketDataFeed();
        feed.start();
        final MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        final MockSubscriber subscriber = new MockSubscriber();
        
        // test nulls
        // test execute I
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.execute(null);
            }
        }.run();
        // test execute overloads
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                for(int c=0;c<=1;c++) {
                    final MockMarketDataFeedCredentials myCredentials = a==0 ? null : credentials;
                    final DataRequest myRequest = b==0 ? null : dataRequest; 
                    final ISubscriber mySubscriber = c==0 ? null : subscriber;
                    final ISubscriber[] mySubscribers = c==0 ? null : new ISubscriber[] { mySubscriber };
                    // null subscribers are OK, any other null should cause a problem
                    // also protect against all non-null (that should succeed)
                    if(myCredentials != null &&
                       mySubscribers != null &&
                       myRequest != null) {
                        // test execute II
                        feed.execute(myCredentials,
                                     myRequest,
                                     mySubscriber);
                        feed.execute(myCredentials,
                                     myRequest,
                                     mySubscribers);
                        // test execute III
                        feed.execute(myRequest,
                                     mySubscriber);
                        feed.execute(myRequest,
                                     mySubscribers);
                    } else {
                        // execute II
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myCredentials,
                                             myRequest,
                                             mySubscribers);
                            }
                        }.run();
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myCredentials,
                                             myRequest,
                                             mySubscribers);
                            }
                        }.run();
                        // execute III
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myRequest,
                                             mySubscriber);
                            }
                        }.run();
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myRequest,
                                             mySubscribers);
                            }
                        }.run();
                    }
                }
            }
        }
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
    private void doExecuteTest(final DataRequest inRequest,
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
        final MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        if(inRequest == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                        throws Throwable
                {
                    MarketDataFeedTokenSpec.generateTokenSpec(credentials, 
                                                              inRequest, 
                                                              new MockSubscriber[] { inSubscriber });
                }
            }.run();                             
        } else {
            MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(credentials,
                                                                                                                         inRequest, 
                                                                                                                         new MockSubscriber[] { inSubscriber });
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
    private void doExecuteTest(final MarketDataFeedTokenSpec<MockMarketDataFeedCredentials> inTokenSpec,
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
                                                               "obnoxious-feed-name-with-dashes", //$NON-NLS-1$
                                                               null,
                                                               0);
        feed.start();
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
        // execute a test with each of the execute overloads
        MockMarketDataFeedToken token = null;
        if(inGenerateTokenThrows) {
            new ExpectedTestFailure(FeedException.class,
                                    Messages.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getText()) {
                protected void execute()
                    throws Throwable
                {
                    feed.execute(inTokenSpec);
                }
            }.run();
        } else {
            token = feed.execute(inTokenSpec);
        }
        verifyExecution(inLoginFails,
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
                        inRequestReturnsNull, 
                        feed, 
                        token, 
                        subscribers);
        resetSubscribers(subscribers);
        if(inGenerateTokenThrows) {
            new ExpectedTestFailure(FeedException.class,
                                    Messages.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getText()) {
                protected void execute()
                    throws Throwable
                {
                    feed.execute(inTokenSpec.getCredentials(),
                                 inTokenSpec.getDataRequest(),
                                 inTokenSpec.getSubscribers());
                }
            }.run();
        } else {
            token = feed.execute(inTokenSpec.getCredentials(),
                                 inTokenSpec.getDataRequest(),
                                 inTokenSpec.getSubscribers());
        }
        verifyExecution(inLoginFails,
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
                        false, 
                        false, 
                        feed, 
                        token, 
                        subscribers);
        resetSubscribers(subscribers);
        if(subscribers == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                throws Throwable
                {
                    feed.execute(inTokenSpec.getCredentials(),
                                 inTokenSpec.getDataRequest(),
                                 inTokenSpec.getSubscribers()[0]);
                }
            }.run();
        } else {
            if(inGenerateTokenThrows) {
                new ExpectedTestFailure(FeedException.class,
                                        Messages.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getText()) {
                    protected void execute()
                    throws Throwable
                    {
                        feed.execute(inTokenSpec.getCredentials(),
                                     inTokenSpec.getDataRequest(),
                                     inTokenSpec.getSubscribers()[0]);
                    }
                }.run();
            } else {
                token = feed.execute(inTokenSpec.getCredentials(),
                                     inTokenSpec.getDataRequest(),
                                     inTokenSpec.getSubscribers()[0]);
            }
        }
        ISubscriber[] listOfOne = new ISubscriber[] { subscribers[0] };
        verifyExecution(inLoginFails,
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
                        false, 
                        false, 
                        feed, 
                        token, 
                        listOfOne);
        resetSubscribers(subscribers);
        if(subscribers == null) {
                 new ExpectedTestFailure(NullPointerException.class) {
                     protected void execute()
                     throws Throwable
                     {
                         feed.execute(inTokenSpec.getCredentials(),
                                      inTokenSpec.getDataRequest(),
                                      inTokenSpec.getSubscribers()[0]);
                     }
                 }.run();
        } else {
            if(inGenerateTokenThrows) {
                new ExpectedTestFailure(FeedException.class,
                                        Messages.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getText()) {
                    protected void execute()
                    throws Throwable
                    {
                        feed.execute(inTokenSpec.getDataRequest(),
                                     subscribers[0]);
                    }
                }.run();
            } else {
                token = feed.execute(inTokenSpec.getDataRequest(),
                                     subscribers[0]);
            }
        }
        verifyExecution(inLoginFails,
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
                        false, 
                        false, 
                        feed, 
                        token, 
                        subscribers);
    }
    private void verifyExecution(boolean inLoginFails,
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
                                 boolean inRequestReturnsNull, 
                                 MockMarketDataFeed inFeed,
                                 MockMarketDataFeedToken inToken,
                                 ISubscriber... inSubscribers)
        throws Exception
    {
        if(inLoginFails ||
           inInitFails ||
           inInitThrows ||
           inExecThrows ||
           inLoginThrows ||
           inIsLoggedInThrows ||
           inBeforeExecuteReturnsFalse ||
           inGetMessageTranslatorThrows ||
           inTranslateThrows ||
           inAfterExecuteThrows ||
           inBeforeExecuteThrows) {
            assertEquals(FeedStatus.AVAILABLE,
                         inFeed.getFeedStatus());
            if(inLoginFails ||
               inLoginThrows ||
               inIsLoggedInThrows) {
                assertEquals(IMarketDataFeedToken.Status.LOGIN_FAILED,
                             inToken.getStatus());
            } else if(inInitFails ||
                      inInitThrows) {
                assertEquals(IMarketDataFeedToken.Status.INITIALIZATION_FAILED,
                             inToken.getStatus());
            } else if(inExecThrows ||
                      inBeforeExecuteReturnsFalse ||
                      inBeforeExecuteThrows ||
                      inAfterExecuteThrows) {
                assertEquals(IMarketDataFeedToken.Status.EXECUTION_FAILED,
                             inToken.getStatus());
            }
        } else {
            if(inGenerateTokenThrows ||
               inGetEventTranslatorThrows ||
               inTranslateToEventsThrows ||
               inTranslateToEventsReturnsNull ||
               inTranslateToEventsReturnsZeroEvents) {
                if(inGenerateTokenThrows) {
                    assertNull(inToken);                    
                    assertEquals(FeedStatus.AVAILABLE,
                                 inFeed.getFeedStatus());
                } else {
                    assertNotNull(inToken);
                    assertEquals(Status.ACTIVE,
                                 inToken.getStatus());
                    assertEquals(FeedStatus.AVAILABLE,
                                 inFeed.getFeedStatus());
                }
                if(inSubscribers != null) {
                    for(ISubscriber subscriber : inSubscribers) {
                        if(subscriber != null) {
                            MockSubscriber s = (MockSubscriber)subscriber;
                            assertEquals(0,
                                         s.getPublishCount());
                        }
                    }
                }
            } else {
                assertNotNull(inToken);
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
    }    
}
