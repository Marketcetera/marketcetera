package org.marketcetera.core.marketdata;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.api.systemmodel.Subscriber;
import org.marketcetera.core.marketdata.MarketDataFeedToken.Status;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.core.util.except.ExpectedFailure;

import static org.junit.Assert.*;

/**
 * Tests {@link org.marketcetera.core.marketdata.AbstractMarketDataFeedToken}.
 *
 * @version $Id: AbstractMarketDataFeedTokenTest.java 82329 2012-04-10 16:28:13Z colin $
 * @since 0.5.0
 */
public class AbstractMarketDataFeedTokenTest
    extends MarketDataFeedTestBase
{
    private MockMarketDataFeedToken mToken;
    private MarketDataFeedTokenSpec mTokenSpec;
    private MockMarketDataFeedCredentials mCredentials;
    private MockMarketDataFeed mFeed;
    @Before
    public void setUp()
            throws Exception
    {
        mCredentials = new MockMarketDataFeedCredentials();
        dataRequest = MarketDataFeedTestSuite.generateDataRequest();
        mTokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(dataRequest, 
                                                               new Subscriber[0]);
        mFeed = new MockMarketDataFeed();
        mFeed.start();
        mFeed.login(mCredentials);
        mToken = MockMarketDataFeedToken.getToken(mTokenSpec,
                                                  mFeed);
    }
    @Test
    public void testConstructor()
        throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                mToken = MockMarketDataFeedToken.getToken(null,
                                                          mFeed);
            }
        };     
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                mToken = MockMarketDataFeedToken.getToken(mTokenSpec,
                                                          null);
            }
        };     
        
        // construct one where all is well with the world
        mTokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(MarketDataFeedTestSuite.generateDataRequest(), 
                                                               new Subscriber[0]);
        MockMarketDataFeedToken token = MockMarketDataFeedToken.getToken(mTokenSpec,
                                                                         mFeed);
        assertEquals(Status.NOT_STARTED,
                     token.getStatus());
        assertEquals(mTokenSpec,
                     token.getTokenSpec());
    }
    @Test
    public void testPublishAndSubscribe()
        throws Exception
    {
        // add a null subscriber
        mToken.subscribe(null);
        // publish null
        mToken.publishAndWait(null);
        // publish no subscribers
        mToken.publishAndWait(this);
        // create a subscriber
        MockSubscriber s1 = new MockSubscriber();
        // subscribe
        mToken.subscribe(s1);
        // publish to subscriber
        mToken.publishAndWait(this);
        // subscriber got publication
        verifySubscribers(new MockSubscriber[] { s1 },
                          new MockSubscriber[] { },
                          this);
        // add second subscriber
        MockSubscriber s2 = new MockSubscriber();
        mToken.subscribe(s2);
        // publish a different object this time
        mToken.publishAndWait(this.getClass());
        // make sure both subscribers got it
        verifySubscribers(new MockSubscriber[] { s1, s2 },
                          new MockSubscriber[] { },
                          this.getClass());
        // add second subscriber again
        mToken.subscribe(s2);
        // notify with a different type of object
        mToken.publishAndWait(mTokenSpec);
        // make sure subscribers were notified (make sure s2 was notified only once)
        verifySubscribers(new MockSubscriber[] { s1, s2 },
                          new MockSubscriber[] { },
                          mTokenSpec);
        // throw in some unsubscribes
        mToken.unsubscribe(null);
        MockSubscriber s3 = new MockSubscriber();
        mToken.unsubscribe(s3);
        // publish again
        mToken.publishAndWait(dataRequest);
        verifySubscribers(new MockSubscriber[] { s1, s2 },
                          new MockSubscriber[] { s3 },
                          dataRequest);
        // remove existing subscribers
        mToken.unsubscribe(s1);
        mToken.unsubscribe(s2);
        // publish again
        mToken.publishAndWait(mCredentials);
        // nobody should get anything
        verifySubscribers(new MockSubscriber[] { },
                          new MockSubscriber[] { s1, s2, s3 },
                          mCredentials);
        // repeat a few tests with the list subscribe
        mToken.subscribeAll((Subscriber[])null);
        // nobody is subscribed
        mToken.publishAndWait(s1);
        verifySubscribers(new MockSubscriber[] { },
                          new MockSubscriber[] { s1, s2, s3 },
                          s1);
        List<Subscriber> subscribers = new ArrayList<Subscriber>(); 
        // still, nobody is subscribed
        mToken.subscribeAll(subscribers.toArray(new Subscriber[subscribers.size()]));
        mToken.publishAndWait(s2);
        verifySubscribers(new MockSubscriber[] { },
                          new MockSubscriber[] { s1, s2, s3 },
                          s2);
        // add a subscriber
        subscribers.add(s1);
        mToken.subscribeAll(subscribers.toArray(new Subscriber[subscribers.size()]));
        mToken.publishAndWait(s3);
        verifySubscribers(new MockSubscriber[] { s1 },
                          new MockSubscriber[] { s2, s3 },
                          s3);
        // repeat one subscriber, add one new one
        subscribers.add(s2);
        assertEquals(2,
                     subscribers.size());
        mToken.subscribeAll(subscribers.toArray(new Subscriber[subscribers.size()]));
        // publish again, make sure each subscriber is notified only once
        mToken.publishAndWait(mToken);
        verifySubscribers(new MockSubscriber[] { s1, s2 },
                          new MockSubscriber[] { s3 },
                          mToken);
        // make sure that subscriber list can handle a mix of types
        DoNothingSubscriber x1 = new DoNothingSubscriber();
        Subscriber[] mixedSubscribers = new Subscriber[] { s1, x1 };
        mToken.subscribeAll(mixedSubscribers);
        mToken.publishAndWait(mFeed);
        verifySubscribers(new MockSubscriber[] { s1, s2 },
                          new MockSubscriber[] { s3 },
                          mFeed);
        assertEquals(mFeed,
                     x1.mData);
    }
    
    private void verifySubscribers(MockSubscriber[] inSubscribed,
                                   MockSubscriber[] inUnsubscribed,
                                   Object inData)
        throws Exception
    {
        for(MockSubscriber subscriber : inSubscribed) {
            verifySubscriber(subscriber,
                             inData,
                             1);
        }
        for(MockSubscriber subscriber : inUnsubscribed) {
            verifySubscriber(subscriber,
                             null,
                             0);
        }
        resetSubscribers(inSubscribed);
        resetSubscribers(inUnsubscribed);
    }
    private void verifySubscriber(MockSubscriber inSubscriber,
                                  Object inData,
                                  int inCount)
        throws Exception
    {
        assertEquals(inData,
                     inSubscriber.getData());
        assertEquals(inCount,
                     inSubscriber.getPublishCount());        
    }
    @Test
    public void testCancel()
        throws Exception
    {
        assertTrue(mFeed.getCreatedHandles().isEmpty());
        assertTrue(mFeed.getCanceledHandles().isEmpty());
        
        MockMarketDataFeedToken token = mFeed.execute(mTokenSpec);
        List<String> createdHandles = mFeed.getCreatedHandles();
        assertFalse(mFeed.getCreatedHandles().isEmpty());
        token.cancel();
        List<String> canceledHandles = mFeed.getCanceledHandles();
        assertTrue(canceledHandles.containsAll(createdHandles));
        assertEquals(createdHandles.size(),
                     canceledHandles.size());
        assertEquals(MockMarketDataFeedToken.Status.CANCELED,
                     token.getStatus());
        // cancel it again, make sure nothing breaks
        token.cancel();
        canceledHandles = mFeed.getCanceledHandles();
        assertTrue(canceledHandles.containsAll(createdHandles));
        assertEquals(createdHandles.size(),
                     canceledHandles.size());
        assertEquals(MockMarketDataFeedToken.Status.CANCELED,
                     token.getStatus());
    }
}
