package org.marketcetera.marketdata;

import java.util.List;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.TestSubscriber;

import quickfix.Message;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Base class for Market Data Feed tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedTestBase
    extends TestCase
{
    private static MarketDataFeedTestSuite sSuite;
    protected Message mMessage;
    protected TestMarketDataFeedCredentials mCredentials;
    
    /**
     * Create a new <code>MarketDataFeedTestBase</code> instance.
     *
     * @param inArg0
     */
    public MarketDataFeedTestBase(String inArg0)
    {
        super(inArg0);
    }

    protected static TestSuite suite(Class<?> inClass) 
    {
        sSuite = new MarketDataFeedTestSuite(inClass);
        return sSuite;
    }
    
    protected static Test suite() 
    {
        sSuite = new MarketDataFeedTestSuite();
        return sSuite;
    }
    
    protected MarketDataFeedTestSuite getTestSuite()
    {
        return sSuite;
    }

    @Override
    protected void setUp()
            throws Exception
    {
        super.setUp();
        TestMarketDataFeedCredentials.sValidateThrowsThrowable = false;
        mMessage = MarketDataFeedTestSuite.generateFIXMessage();
        mCredentials = new TestMarketDataFeedCredentials();
    }    

    protected void resetSubscriber(TestSubscriber inSubscriber)
    {
        if(inSubscriber == null) {
            return;
        }
        inSubscriber.setPublishCount(0);
        inSubscriber.setData(null);        
    }
    protected void resetSubscribers(List<? extends ISubscriber> inSubscribers)
    {
        if(inSubscribers == null) {
            return;
        }
        for(ISubscriber subscriber : inSubscribers) {
            if(subscriber != null) {
                TestSubscriber s = (TestSubscriber)subscriber;
                resetSubscriber(s);
            }
        }
    }
    /**
     * This {@link ISubscriber} implementation requests all publications and does
     * nothing with them.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.43-SNAPSHOT
     */
    public static class DoNothingSubscriber implements ISubscriber {
        public Object mData;
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        public void publishTo(Object inData)
        {
            mData = inData;
        }            
    };
}
