package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.TestSubscriber;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.quickfix.AbstractMessageTranslator;
import org.marketcetera.quickfix.FIXMessageUtil;

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
        return MarketDataFeedTestBase.suite(AbstractMarketDataFeedTest.class);
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
        doExecuteTest(mMessage, 
                      null, 
                      true, 
                      false, 
                      false);
        doExecuteTest(mMessage, 
                      null, 
                      false, 
                      true, 
                      false);
        doExecuteTest(mMessage, 
                      null, 
                      false, 
                      false, 
                      true);
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
    
    private void doExecuteTest(final MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> inTokenSpec,
                               boolean inLoginFails, 
                               boolean inInitFails, 
                               boolean inExecFails)
        throws Exception
    {
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN);
        List<? extends ISubscriber> subscribers = inTokenSpec.getSubscribers();
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
        feed.setExecutionFails(inExecFails);
        
        TestMarketDataFeedToken token = feed.execute(inTokenSpec);
        verifyExecution(inLoginFails,
                        inInitFails,
                        inExecFails,
                        feed,
                        token,
                        subscribers);
        resetSubscribers(subscribers);
        token = feed.execute(inTokenSpec.getCredentials(),
                             inTokenSpec.getMessage(),
                             inTokenSpec.getSubscribers());
        verifyExecution(inLoginFails,
                        inInitFails,
                        inExecFails,
                        feed,
                        token,
                        subscribers);
        resetSubscribers(subscribers);
        token = feed.execute(inTokenSpec.getCredentials(),
                             inTokenSpec.getMessage(),
                             inTokenSpec.getSubscribers().get(0));
        List<ISubscriber> listOfOne = new ArrayList<ISubscriber>();
        listOfOne.add(subscribers.get(0));
        verifyExecution(inLoginFails,
                        inInitFails,
                        inExecFails,
                        feed,
                        token,
                        listOfOne);
    }
    private void verifyExecution(boolean inLoginFails,
                                 boolean inInitFails,
                                 boolean inExecFails,
                                 TestMarketDataFeed inFeed,
                                 TestMarketDataFeedToken inToken,
                                 List<? extends ISubscriber> inSubscribers)
        throws Exception
    {
        if(inLoginFails ||
           inInitFails ||
           inExecFails) {
            assertEquals(FeedStatus.ERROR,
                         inFeed.getFeedStatus());
            if(inLoginFails) {
                assertEquals(IMarketDataFeedToken.Status.LOGIN_FAILED,
                             inToken.getStatus());
            } else if(inInitFails) {
                assertEquals(IMarketDataFeedToken.Status.INITIALIZATION_FAILED,
                             inToken.getStatus());
            } else if(inExecFails) {
                assertEquals(IMarketDataFeedToken.Status.EXECUTION_FAILED,
                             inToken.getStatus());
            }
        } else {
            assertNotNull(inToken);
            if(inSubscribers != null) {
                for(ISubscriber subscriber : inSubscribers) {
                    if(subscriber != null) {
                        TestSubscriber s = (TestSubscriber)subscriber;
                        while(s.getData() == null) {
                            Thread.sleep(100);
                        }
                        assertEquals(1,
                                     s.getPublishCount());
                    }
                }
            }
            assertEquals(FeedStatus.AVAILABLE,
                         inFeed.getFeedStatus());
        }
    }
    private void doExecuteTest(final Message inMessage,
                               final TestSubscriber inSubscriber, 
                               boolean inLoginFails, 
                               boolean inInitFails, 
                               boolean inExecFails)
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
                          inExecFails);
        }
    }
}
