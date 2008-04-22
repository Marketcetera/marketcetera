package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.TestSubscriber;
import org.marketcetera.marketdata.IMarketDataFeedToken.Status;


/**
 * Tests {@link AbstractMarketDataFeedToken}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class AbstractMarketDataFeedTokenTest
    extends MarketDataFeedTestBase
{
    private TestMarketDataFeedToken mToken;
    private MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> mTokenSpec;
    private TestMarketDataFeedCredentials mCredentials;
    private TestMarketDataFeed mFeed;
    
    /**
     * Create a new <code>AbstractMarketDataFeedTokenTest</code> instance.
     *
     * @param inArg0
     */
    public AbstractMarketDataFeedTokenTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(AbstractMarketDataFeedTokenTest.class);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        mCredentials = new TestMarketDataFeedCredentials();
        mMessage = MarketDataFeedTestSuite.generateFIXMessage();
        mTokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(mCredentials, 
                                                               mMessage, 
                                                               Arrays.asList(new ISubscriber[0]));
        mFeed = new TestMarketDataFeed();
        mToken = TestMarketDataFeedToken.getToken(mTokenSpec,
                                                  mFeed);
    }

    public void testConstructor()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                mToken = TestMarketDataFeedToken.getToken(null,
                                                          mFeed);
            }
        }.run();     
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                mToken = TestMarketDataFeedToken.getToken(mTokenSpec,
                                                          null);
            }
        }.run();     
        
        // construct one where all is well with the world
        mTokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(mCredentials, 
                                                               MarketDataFeedTestSuite.generateFIXMessage(), 
                                                               Arrays.asList(new ISubscriber[0]));
        TestMarketDataFeedToken token = TestMarketDataFeedToken.getToken(mTokenSpec,
                                                                         mFeed);
        assertEquals(Status.NOT_STARTED,
                     token.getStatus());
        assertEquals(mTokenSpec,
                     token.getTokenSpec());
    }
    
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
        TestSubscriber s1 = new TestSubscriber();
        // subscribe
        mToken.subscribe(s1);
        // publish to subscriber
        mToken.publishAndWait(this);
        // subscriber got publication
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1 }),
                          Arrays.asList(new TestSubscriber[] { }),
                          this);
        // add second subscriber
        TestSubscriber s2 = new TestSubscriber();
        mToken.subscribe(s2);
        // publish a different object this time
        mToken.publishAndWait(this.getClass());
        // make sure both subscribers got it
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1, s2 }),
                          Arrays.asList(new TestSubscriber[] { }),
                          this.getClass());
        // add second subscriber again
        mToken.subscribe(s2);
        // notify with a different type of object
        mToken.publishAndWait(mTokenSpec);
        // make sure subscribers were notified (make sure s2 was notified only once)
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1, s2 }),
                          Arrays.asList(new TestSubscriber[] { }),
                          mTokenSpec);
        // throw in some unsubscribes
        mToken.unsubscribe(null);
        TestSubscriber s3 = new TestSubscriber();
        mToken.unsubscribe(s3);
        // publish again
        mToken.publishAndWait(mMessage);
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1, s2 }),
                          Arrays.asList(new TestSubscriber[] { s3 }),
                          mMessage);
        // remove existing subscribers
        mToken.unsubscribe(s1);
        mToken.unsubscribe(s2);
        // publish again
        mToken.publishAndWait(mCredentials);
        // nobody should get anything
        verifySubscribers(Arrays.asList(new TestSubscriber[] { }),
                          Arrays.asList(new TestSubscriber[] { s1, s2, s3 }),
                          mCredentials);
        // repeat a few tests with the list subscribe
        mToken.subscribeAll(null);
        // nobody is subscribed
        mToken.publishAndWait(s1);
        verifySubscribers(Arrays.asList(new TestSubscriber[] { }),
                          Arrays.asList(new TestSubscriber[] { s1, s2, s3 }),
                          s1);
        List<ISubscriber> subscribers = new ArrayList<ISubscriber>(); 
        // still, nobody is subscribed
        mToken.subscribeAll(subscribers);
        mToken.publishAndWait(s2);
        verifySubscribers(Arrays.asList(new TestSubscriber[] { }),
                          Arrays.asList(new TestSubscriber[] { s1, s2, s3 }),
                          s2);
        // add a subscriber
        subscribers.add(s1);
        mToken.subscribeAll(subscribers);
        mToken.publishAndWait(s3);
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1 }),
                          Arrays.asList(new TestSubscriber[] { s2, s3 }),
                          s3);
        // repeat one subscriber, add one new one
        subscribers.add(s2);
        assertEquals(2,
                     subscribers.size());
        mToken.subscribeAll(subscribers);
        // publish again, make sure each subscriber is notified only once
        mToken.publishAndWait(mToken);
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1, s2 }),
                          Arrays.asList(new TestSubscriber[] { s3 }),
                          mToken);
        // make sure that subscriber list can handle a mix of types
        DoNothingSubscriber x1 = new DoNothingSubscriber();
        List<? extends ISubscriber> mixedSubscribers = Arrays.asList(new ISubscriber[] { s1, x1 });
        mToken.subscribeAll(mixedSubscribers);
        mToken.publishAndWait(mFeed);
        verifySubscribers(Arrays.asList(new TestSubscriber[] { s1, s2 }),
                          Arrays.asList(new TestSubscriber[] { s3 }),
                          mFeed);
        assertEquals(mFeed,
                     x1.mData);
    }
    
    private void verifySubscribers(List<TestSubscriber> inSubscribed,
                                   List<TestSubscriber> inUnsubscribed,
                                   Object inData)
        throws Exception
    {
        for(TestSubscriber subscriber : inSubscribed) {
            verifySubscriber(subscriber,
                             inData,
                             1);
        }
        for(TestSubscriber subscriber : inUnsubscribed) {
            verifySubscriber(subscriber,
                             null,
                             0);
        }
        resetSubscribers(inSubscribed);
        resetSubscribers(inUnsubscribed);
    }
    private void verifySubscriber(TestSubscriber inSubscriber,
                                  Object inData,
                                  int inCount)
        throws Exception
    {
        assertEquals(inData,
                     inSubscriber.getData());
        assertEquals(inCount,
                     inSubscriber.getPublishCount());        
    }

    public void testCancel()
        throws Exception
    {
        assertTrue(mFeed.getCreatedHandles().isEmpty());
        assertTrue(mFeed.getCanceledHandles().isEmpty());
        
        TestMarketDataFeedToken token = mFeed.execute(mTokenSpec);
        List<String> createdHandles = mFeed.getCreatedHandles();
        assertFalse(mFeed.getCreatedHandles().isEmpty());
        token.cancel();
        List<String> canceledHandles = mFeed.getCanceledHandles();
        assertTrue(canceledHandles.containsAll(createdHandles));
        assertEquals(createdHandles.size(),
                     canceledHandles.size());
        assertEquals(TestMarketDataFeedToken.Status.CANCELED,
                     token.getStatus());
        // cancel it again, make sure nothing breaks
        token.cancel();
        canceledHandles = mFeed.getCanceledHandles();
        assertTrue(canceledHandles.containsAll(createdHandles));
        assertEquals(createdHandles.size(),
                     canceledHandles.size());
        assertEquals(TestMarketDataFeedToken.Status.CANCELED,
                     token.getStatus());
    }
}
