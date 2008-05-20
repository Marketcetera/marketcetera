package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MessageKey;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.TestSubscriber;
import org.marketcetera.event.TestEventTranslator;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.marketdata.IMarketDataFeedToken.Status;
import org.marketcetera.quickfix.AbstractMessageTranslator;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.TestMessageTranslator;

import quickfix.Group;
import quickfix.Message;

/**
 * Tests {@link AbstractMarketDataFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
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
        final String providerName = "TestProviderName";
        final FeedType type = FeedType.UNKNOWN;
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                new TestMarketDataFeed(null,
                                       providerName,
                                       null,
                                       0);
            }
        }.run();                             
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
            throws Throwable
            {
                new TestMarketDataFeed(type,
                                       null,
                                       null,
                                       0);
            }
        }.run();                             
        
        TestMarketDataFeed feed = new TestMarketDataFeed(type,
                                                         providerName,
                                                         null,
                                                         0);
        assertNotNull(feed);
        assertEquals(type,
                     feed.getFeedType());
        assertNotNull(feed.getID());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
        feed = new TestMarketDataFeed(type,
                                      providerName,
                                      new TestMarketDataFeedCredentials(),
                                      0);
        assertNotNull(feed);
        assertEquals(type,
                     feed.getFeedType());
        assertNotNull(feed.getID());
        assertEquals(FeedStatus.OFFLINE,
                     feed.getFeedStatus());
    }
    
    public void testMarketDataRequest()
        throws Exception
    {
        // null list
        List<MSymbol> symbols = new ArrayList<MSymbol>();
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                doMarketDataTest(null);
            }
        }.run();
        // empty list
        doMarketDataTest(symbols);
        // one symbol
        symbols.add(new MSymbol("GOOG"));
        doMarketDataTest(symbols);
        // more than one symbol
        symbols.add(new MSymbol("MSFT"));
        doMarketDataTest(symbols);
        // add a null
        symbols.add(null);
        doMarketDataTest(symbols);
    }
    
    public void testNoCredentialsSupplied()
        throws Exception
    {
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.execute(mMessage,
                             Arrays.asList(new ISubscriber[0]));
            }
        }.run();
    }
    
    public void testCancel()
        throws Exception
    {
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN,
                                                               new TestMarketDataFeedCredentials());
        List<ISubscriber> subscribers = Arrays.asList(new ISubscriber[0]);
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                feed.cancel(null);
            }
        }.run();

        TestMarketDataFeedToken token = feed.execute(mMessage,
                                                     subscribers);
        feed.cancel(token);
        verifyAllCanceled(feed);
        feed.setCancelFails(true);
        token = feed.execute(mMessage,
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
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        assertFalse(feed.isRunning());
        feed.start();
        assertTrue(feed.isRunning());
        feed.stop();
        assertFalse(feed.isRunning());
    }
    
    public void testDoInitialize()
        throws Exception
    {
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        feed.setInitFails(false);
        assertTrue(feed.doInitialize(null));
        MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(new TestMarketDataFeedCredentials(), 
                                                                                                                     mMessage, 
                                                                                                                     Arrays.asList(new ISubscriber[0]));
        TestMarketDataFeedToken token = TestMarketDataFeedToken.getToken(tokenSpec, 
                                                                         feed);
        assertTrue(feed.doInitialize(token));
    }
    
    public void testBeforeDoExecute()
        throws Exception
    {
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        assertTrue(feed.beforeDoExecute(null));
        MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(new TestMarketDataFeedCredentials(), 
                                                                                                                     mMessage, 
                                                                                                                     Arrays.asList(new ISubscriber[0]));
        TestMarketDataFeedToken token = TestMarketDataFeedToken.getToken(tokenSpec, 
                                                                         feed);
        assertTrue(feed.beforeDoExecute(token));
    }
    
    public void testAfterDoExecute()
        throws Exception
    {
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(new TestMarketDataFeedCredentials(), 
                                                                                                                     mMessage, 
                                                                                                                     Arrays.asList(new ISubscriber[0]));
        TestMarketDataFeedToken token = TestMarketDataFeedToken.getToken(tokenSpec, 
                                                                         feed);
        feed.afterDoExecute(null, null);
        feed.afterDoExecute(token, null);
    }
    
    public void testSetFeedStatus()
        throws Exception
    {
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
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
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
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
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
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
     * Tests the subscribe to all queries function.
     *
     * @throws Exception
     */
    public void testSubscribeAll()
        throws Exception
    {
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        TestMarketDataFeedCredentials credentials = new TestMarketDataFeedCredentials();
        // create three subscribers - 1 & 3 will be assigned to specific queries,
        //  2 will be a general subscriber (all messages)
        TestSubscriber s1 = new TestSubscriber();
        TestSubscriber s2 = new TestSubscriber();
        TestSubscriber s3 = new TestSubscriber();
        feed.subscribeToAll(s2);
        // execute two queries
        feed.execute(credentials,
                     mMessage,
                     s1);
        feed.execute(credentials,
                     mMessage,
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
        resetSubscribers(Arrays.asList(new TestSubscriber[] { s1, s2, s3 } ));
        // re-execute the queries
        feed.execute(credentials,
                     mMessage,
                     s1);
        feed.execute(credentials,
                     mMessage,
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
    private void verifyAllCanceled(TestMarketDataFeed inFeed)
        throws Exception
    {
        List<String> createdHandles = inFeed.getCreatedHandles();
        List<String> canceledHandles = inFeed.getCanceledHandles();
        assertEquals(createdHandles.size(),
                     canceledHandles.size());
        assertTrue(Arrays.equals(createdHandles.toArray(), 
                                 canceledHandles.toArray()));
    }
    
    private void doMarketDataTest(List<MSymbol> inSymbols)
        throws Exception
    {
        doMarketDataTestSingle(inSymbols,
                               true);
        doMarketDataTestSingle(inSymbols,
                               false);
    }
    
    private void doMarketDataTestSingle(List<MSymbol> inSymbols,
                                        boolean inUpdate)
        throws Exception
    {
        Message levelOneMessage = AbstractMarketDataFeed.levelOneMarketDataRequest(inSymbols, 
                                                                                   inUpdate);
        Message levelTwoMessage = AbstractMarketDataFeed.levelTwoMarketDataRequest(inSymbols, 
                                                                                   inUpdate);
        assertNotNull(levelOneMessage);
        assertNotNull(levelTwoMessage);
        // special case: if the symbol list contains nulls, those nulls will be ignored
        //  by the message creator, so we need to subtract them from the expected number
        //  of groups
        int nullCount = Collections.frequency(inSymbols, 
                                              null);
        List<Group> levelOneGroups = AbstractMessageTranslator.getGroups(levelOneMessage);
        List<Group> levelTwoGroups = AbstractMessageTranslator.getGroups(levelTwoMessage);
        verifyMarketDataGroups(inSymbols,
                               levelOneGroups,
                               levelOneMessage,
                               nullCount);
        verifyMarketDataGroups(inSymbols,
                               levelTwoGroups,
                               levelTwoMessage,
                               nullCount);
        assertEquals(inUpdate,
                     AbstractMessageTranslator.determineSubscriptionRequestType(levelOneMessage) == '1');
        assertTrue(FIXMessageUtil.isLevelOne(levelOneMessage));
        assertEquals(inUpdate,
                     AbstractMessageTranslator.determineSubscriptionRequestType(levelTwoMessage) == '1');
        assertTrue(FIXMessageUtil.isLevelTwo(levelTwoMessage));
    }
    
    private void verifyMarketDataGroups(List<MSymbol> inSymbols,
                                        List<Group> inGroups,
                                        Message inMessage,
                                        int inNullCount)
        throws Exception
    {
        assertEquals(inSymbols.isEmpty(),
                     inGroups.isEmpty());
        assertEquals(inSymbols.size() - inNullCount,
                     AbstractMessageTranslator.determineTotalSymbols(inMessage));
        assertEquals(inSymbols.size() - inNullCount,
                     inGroups.size());
        for(int i=0;i<inSymbols.size();i++) {
            MSymbol symbol = inSymbols.get(i);
            if(symbol != null) {
                Group group = inGroups.get(i);
                assertEquals(symbol,
                             AbstractMessageTranslator.getSymbol(group));
            }
        }
    }
    
    public void testPublishEventsThrowsException()
        throws Exception
    {
        TestSubscriber subscriber1 = new TestSubscriber();
        TestSubscriber subscriber2 = new TestSubscriber();
        TestSubscriber subscriber3 = new TestSubscriber();
        subscriber2.setPublishThrows(true);
        assertFalse(subscriber1.getPublishThrows());
        assertTrue(subscriber2.getPublishThrows());
        assertFalse(subscriber3.getPublishThrows());
        TestMarketDataFeed feed = new TestMarketDataFeed();
        TestMarketDataFeedCredentials credentials = new TestMarketDataFeedCredentials();
        List<ISubscriber> subscribers = Arrays.asList(new ISubscriber[] { subscriber1, subscriber2, subscriber3 } );
        TestMarketDataFeedToken token = feed.execute(credentials,
                                                     mMessage,
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
        TestSubscriber subscriber = new TestSubscriber();
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                doExecuteTest(a==0 ? null : mMessage,
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
    }

    public void testParallelExecution()
        throws Exception
    {
        TestMarketDataFeedCredentials credentials = new TestMarketDataFeedCredentials();
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN,
                                                         "TestMarketDataFeed",
                                                         credentials,
                                                         25);
        List<TestSubscriber> subscribers = new ArrayList<TestSubscriber>();
        List<TestMarketDataFeedToken> tokens = new ArrayList<TestMarketDataFeedToken>();
        for(int i=0;i<1000;i++) {
            TestSubscriber s = new TestSubscriber();
            subscribers.add(s);
            MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(credentials, 
                                                                                                                         mMessage, 
                                                                                                                         subscribers);
            tokens.add(feed.execute(tokenSpec));
        }
        for(TestSubscriber s : subscribers) {
            while(s.getData() == null) {
                Thread.sleep(50);
            }
        }
        for(TestMarketDataFeedToken token : tokens) {
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
        final TestMarketDataFeed feed = new TestMarketDataFeed();
        final TestMarketDataFeedCredentials credentials = new TestMarketDataFeedCredentials();
        final TestSubscriber subscriber = new TestSubscriber();
        
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
                    final TestMarketDataFeedCredentials myCredentials = a==0 ? null : credentials;
                    final Message myMessage = b==0 ? null : mMessage; 
                    final ISubscriber mySubscriber = c==0 ? null : subscriber;
                    final List<ISubscriber> mySubscribers = c==0 ? null : Arrays.asList(mySubscriber);
                    // null subscribers are OK, any other null should cause a problem
                    // also protect against all non-null (that should succeed)
                    if(myCredentials != null &&
                       mySubscriber != null &&
                       myMessage != null) {
                        // test execute II
                        feed.execute(myCredentials,
                                     myMessage,
                                     mySubscriber);
                        // test execute III
                        feed.execute(myCredentials,
                                     myMessage,
                                     mySubscribers);
                        feed.execute(myCredentials,
                                     myMessage,
                                     new ArrayList<ISubscriber>());
                        // test execute IV
                        feed.execute(myMessage,
                                     mySubscriber);
                        // test execute V
                        feed.execute(myMessage,
                                     mySubscribers);
                    } else {
                        // execute II
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myCredentials,
                                             myMessage,
                                             mySubscriber);
                            }
                        }.run();
                        // execute III
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myCredentials,
                                             myMessage,
                                             mySubscribers);
                            }
                        }.run();
                        // execute IV
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myMessage,
                                             mySubscriber);
                            }
                        }.run();
                        // test execute V
                        new ExpectedTestFailure(NullPointerException.class) {
                            protected void execute()
                                throws Throwable
                            {
                                feed.execute(myMessage,
                                             mySubscribers);
                            }
                        }.run();
                    }
                }
            }
        }
        // test more intricate failure conditions
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
        doExecuteTest(mMessage, 
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
    private void doExecuteTest(final Message inMessage,
                               final TestSubscriber inSubscriber, 
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
        final TestMarketDataFeedCredentials credentials = new TestMarketDataFeedCredentials();
        if(inMessage == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                        throws Throwable
                {
                    MarketDataFeedTokenSpec.generateTokenSpec(credentials, 
                                                              inMessage, 
                                                              Arrays.asList(new TestSubscriber[] { inSubscriber }));
                }
            }.run();                             
        } else {
            MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> tokenSpec = MarketDataFeedTokenSpec.generateTokenSpec(credentials, 
                                                                                                                         inMessage, 
                                                                                                                         Arrays.asList(new TestSubscriber[] { inSubscriber }));
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
    private void doExecuteTest(final MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> inTokenSpec,
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
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN,
                                                               "obnoxious-feed-name-with-dashes",
                                                               null,
                                                               0);
        final List<? extends ISubscriber> subscribers = inTokenSpec.getSubscribers();
        if(subscribers != null) {
            for(ISubscriber subscriber : subscribers) {
                if(subscriber != null) {
                    TestSubscriber s = (TestSubscriber)subscriber;
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
        TestEventTranslator.setTranslateToEventsThrows(inTranslateToEventsThrows);
        TestEventTranslator.setTranslateToEventsReturnsNull(inTranslateToEventsReturnsNull);
        TestEventTranslator.setTranslateToEventsReturnsZeroEvents(inTranslateToEventsReturnsZeroEvents);
        feed.setBeforeExecuteReturnsFalse(inBeforeExecuteReturnsFalse);
        feed.setGetMessageTranslatorThrows(inGetMessageTranslatorThrows);
        TestMessageTranslator.setTranslateThrows(inTranslateThrows);
        feed.setAfterExecuteThrows(inAfterExecuteThrows);
        feed.setBeforeExecuteThrows(inBeforeExecuteThrows);
        feed.setExecuteReturnsNothing(inRequestReturnsZeroHandles);
        feed.setExecuteReturnsNull(inRequestReturnsNull);
        // execute a test with each of the execute overloads
        TestMarketDataFeedToken token = null;
        if(inGenerateTokenThrows) {
            new ExpectedTestFailure(FeedException.class,
                                    MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getLocalizedMessage()) {
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
                                    MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getLocalizedMessage()) {
                protected void execute()
                    throws Throwable
                {
                    feed.execute(inTokenSpec.getCredentials(),
                                 inTokenSpec.getMessage(),
                                 inTokenSpec.getSubscribers());
                }
            }.run();
        } else {
            token = feed.execute(inTokenSpec.getCredentials(),
                                 inTokenSpec.getMessage(),
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
        if(subscribers == null ||
           subscribers.get(0) == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                throws Throwable
                {
                    feed.execute(inTokenSpec.getCredentials(),
                                 inTokenSpec.getMessage(),
                                 inTokenSpec.getSubscribers().get(0));
                }
            }.run();
        } else {
            if(inGenerateTokenThrows) {
                new ExpectedTestFailure(FeedException.class,
                                        MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getLocalizedMessage()) {
                    protected void execute()
                    throws Throwable
                    {
                        feed.execute(inTokenSpec.getCredentials(),
                                     inTokenSpec.getMessage(),
                                     inTokenSpec.getSubscribers().get(0));
                    }
                }.run();
            } else {
                token = feed.execute(inTokenSpec.getCredentials(),
                                     inTokenSpec.getMessage(),
                                     inTokenSpec.getSubscribers().get(0));
            }
        }
        List<ISubscriber> listOfOne = new ArrayList<ISubscriber>();
        listOfOne.add(subscribers.get(0));
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
        if(subscribers == null ||
           subscribers.get(0) == null) {
                 new ExpectedTestFailure(NullPointerException.class) {
                     protected void execute()
                     throws Throwable
                     {
                         feed.execute(inTokenSpec.getCredentials(),
                                      inTokenSpec.getMessage(),
                                      inTokenSpec.getSubscribers().get(0));
                     }
                 }.run();
        } else {
            if(inGenerateTokenThrows) {
                new ExpectedTestFailure(FeedException.class,
                                        MessageKey.ERROR_MARKET_DATA_FEED_EXECUTION_FAILED.getLocalizedMessage()) {
                    protected void execute()
                    throws Throwable
                    {
                        feed.execute(inTokenSpec.getMessage(),
                                     subscribers.get(0));
                    }
                }.run();
            } else {
                token = feed.execute(inTokenSpec.getMessage(),
                                     subscribers.get(0));
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
                                 TestMarketDataFeed inFeed, 
                                 TestMarketDataFeedToken inToken, 
                                 List<? extends ISubscriber> inSubscribers)
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
            assertEquals(FeedStatus.ERROR,
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
                    assertEquals(FeedStatus.OFFLINE,
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
                            TestSubscriber s = (TestSubscriber)subscriber;
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
                            TestSubscriber s = (TestSubscriber)subscriber;
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
    
    private void waitForPublication(final TestSubscriber inSubscriber)
        throws Exception
    {
        wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return inSubscriber.getData() != null;
            }                                
        });
    }
}
