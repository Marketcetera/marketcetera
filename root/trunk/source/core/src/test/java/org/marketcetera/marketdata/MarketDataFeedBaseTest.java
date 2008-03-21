package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.publisher.TestSubscriber;
import org.marketcetera.marketdata.IFeedComponent.FeedType;

import quickfix.Message;

/**
 * Tests {@link MarketDataFeedBase}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedBaseTest
    extends MarketDataFeedTestBase
{
    /**
     * Create a new <code>MarketDataFeedBaseTest</code> instance.
     *
     * @param inArg0
     */
    public MarketDataFeedBaseTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(MarketDataFeedBaseTest.class);
    }
        
    public void testConstructor()
        throws Exception
    {
        FeedType type = FeedType.UNKNOWN;
        TestMarketDataFeedCredentials credentials = new TestMarketDataFeedCredentials();
        
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                final FeedType myType = a == 0 ? null : type;
                final TestMarketDataFeedCredentials myCredentials = b == 0 ? null : credentials;
                if(myType == null ||
                   myCredentials == null) {
                    new ExpectedTestFailure(NullPointerException.class) {
                        protected void execute()
                                throws Throwable
                        {
                            new TestMarketDataFeed(myType,
                                                   myCredentials);
                        }
                    }.run();                             
                } else {
                    TestMarketDataFeed feed = new TestMarketDataFeed(myType,
                                                                     myCredentials);
                    assertNotNull(feed);
                    assertEquals(myType,
                                 feed.getFeedType());
                    assertEquals(myCredentials,
                                 feed.getCredentials());
                }
            }
        }
    }
    
    public void testExecute()
        throws Exception
    {
        Message message = MarketDataFeedTestSuite.generateFIXMessage();
        TestSubscriber subscriber = new TestSubscriber();
        for(int a=0;a<=1;a++) {
            for(int b=0;b<=1;b++) {
                doExecuteTest(a==0 ? null : message,
                              b==0 ? null : subscriber);        
            }
        }
    }
    
    public void testParallelExecution()
        throws Exception
    {
        TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN,
                                                         new TestMarketDataFeedCredentials(),
                                                         100);
        Message message = MarketDataFeedTestSuite.generateFIXMessage();
        List<TestSubscriber> subscribers = new ArrayList<TestSubscriber>();
        for(int i=0;i<1000;i++) {
            TestSubscriber s = new TestSubscriber();
            subscribers.add(s);
            feed.execute(message, 
                         s);
        }
        for(TestSubscriber s : subscribers) {
            while(s.getData() == null) {
                Thread.sleep(100);
            }
        }
    }
    
    private void doExecuteTest(final Message inMessage,
                               final TestSubscriber inSubscriber)
        throws Exception
    {
        final TestMarketDataFeed feed = new TestMarketDataFeed(FeedType.UNKNOWN,
                                                               new TestMarketDataFeedCredentials());
        if(inMessage == null) {
            new ExpectedTestFailure(NullPointerException.class) {
                protected void execute()
                        throws Throwable
                {
                    feed.execute(inMessage,
                                 inSubscriber);
                }
            }.run();                             
        } else {
            if(inSubscriber != null) {
                assertNull(inSubscriber.getData());
            }
            TestMarketDataFeedToken token = feed.execute(inMessage,
                                                         inSubscriber);
            assertNotNull(token);
            if(inSubscriber != null) {
                while(inSubscriber.getData() == null) {
                    Thread.sleep(100);
                }
                assertEquals(1,
                             inSubscriber.getPublishCount());
            }
        }
    }
}
