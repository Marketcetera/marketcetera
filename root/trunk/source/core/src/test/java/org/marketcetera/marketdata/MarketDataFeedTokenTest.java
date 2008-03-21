package org.marketcetera.marketdata;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MessageKey;
import org.marketcetera.core.publisher.IPublisher;
import org.marketcetera.core.publisher.TestSubscriber;

import quickfix.Message;

/**
 * Tests {@link MarketDataFeedToken}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedTokenTest
    extends MarketDataFeedTestBase
{
    private TestMarketDataFeedToken mToken;
    private Message mMessage;
    
    /**
     * Create a new <code>MarketDataFeedTokenTest</code> instance.
     *
     * @param inArg0
     */
    public MarketDataFeedTokenTest(String inArg0)
    {
        super(inArg0);
    }

    public static Test suite() 
    {
        return MarketDataFeedTestBase.suite(MarketDataFeedBaseTest.class);
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        mMessage = MarketDataFeedTestSuite.generateFIXMessage();
        mToken = new TestMarketDataFeedToken(mMessage);
    }

    public void testConstructor()
        throws Exception
    {
        new ExpectedTestFailure(NullPointerException.class) {
            protected void execute()
                    throws Throwable
            {
                mToken = new TestMarketDataFeedToken(null);
            }
        }.run();     
        
        // construct a FIX message w/o an ID field
        new ExpectedTestFailure(FeedException.class,
                                MessageKey.ERROR_NO_ID_FOR_TOKEN.getLocalizedMessage().toString()) {
            protected void execute()
                    throws Throwable
            {
                new TestMarketDataFeedToken(MarketDataFeedTestSuite.generateFIXMessageWithoutID());
            }
        }.run();     
    }
    
    public void testGetFixMessage()
        throws Exception
    {
        assertEquals(mMessage,
                     mToken.getFixMessage());
    }
    
    public void testGetPublisher()
        throws Exception
    {
        assertNotNull(mToken.getPublisher());
        assertTrue(IPublisher.class.isInstance(mToken.getPublisher()));
    }
    
    public void testSubscribe()
        throws Exception
    {
        // null subscribers should be ignored silently
        mToken.subscribe(null);
        // create a non-null subscriber
        TestSubscriber subscriber = new TestSubscriber();
        int counter = subscriber.getCounter();
        assertTrue(subscriber.getPublications().isEmpty());
        mToken.subscribe(subscriber);        
        mToken.getPublisher().publish(this);
        long timestamp = System.currentTimeMillis();
        while(subscriber.getCounter() == counter) {
            Thread.sleep(500);
        }
        timestamp = System.currentTimeMillis() - timestamp;
        // don't need to exhaustively test publish/subscribe here because it's tested elsewhere
        // just making sure stuff is hooked up
        assertEquals(1,
                     subscriber.getPublications().size());
        assertEquals(this,
                     subscriber.getPublications().get(0));
        // remove subscriber
        mToken.unsubscribe(null);
        mToken.unsubscribe(subscriber);
        // notifications no longer reach subscriber
        mToken.getPublisher().publish(this);
        // wait 10 times longer than it took to notify last time - I know, this isn't deterministic, but
        //  it's pretty solid, I think
        Thread.sleep(timestamp * 10);
        assertEquals(1,
                     subscriber.getPublications().size());
    }
    
    public void testCancel()
        throws Exception
    {
        TestMarketDataFeedConnector connector = new TestMarketDataFeedConnector();
        mToken.setConnector(connector);
        assertEquals(connector,
                     mToken.getConnector());
        // cancel, make sure connector gets notified
        assertTrue(connector.getCancelRequests().isEmpty());
        mToken.cancel();
        assertEquals(1,
                     connector.getCancelRequests().size());
        assertEquals(mToken,
                     connector.getCancelRequests().get(0));
    }
}
