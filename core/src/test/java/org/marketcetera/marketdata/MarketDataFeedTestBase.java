package org.marketcetera.marketdata;

import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.marketcetera.core.publisher.Subscriber;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.event.MockEventTranslator;

/**
 * Base class for Market Data Feed tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class MarketDataFeedTestBase
{
    protected MarketDataRequest dataRequest;
    protected MockMarketDataFeedCredentials mCredentials;
    
    @Before
    public void beforeEachTime()
            throws Exception
    {
        MockMarketDataFeedCredentials.sValidateThrowsThrowable = false;
        MockEventTranslator.reset();
        MockDataRequestTranslator.setTranslateThrows(false);
        dataRequest = MarketDataFeedTestSuite.generateDataRequest();
        mCredentials = new MockMarketDataFeedCredentials();
    }    
	@After
	public void afterEachTime()
	    throws Exception
	{
		MockEventTranslator.reset();
	}
    protected void resetSubscriber(MockSubscriber inSubscriber)
    {
        if(inSubscriber == null) {
            return;
        }
        inSubscriber.reset();
    }
    protected void resetSubscribers(Subscriber... inSubscribers)
    {
        if(inSubscribers == null) {
            return;
        }
        for(Subscriber subscriber : inSubscribers) {
            if(subscriber != null) {
                MockSubscriber s = (MockSubscriber)subscriber;
                resetSubscriber(s);
            }
        }
    }
    /**
     * This {@link Subscriber} implementation requests all publications and does
     * nothing with them.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.43-SNAPSHOT
     */
    public static class DoNothingSubscriber implements Subscriber {
        public Object mData;
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.Subscriber#isInteresting(java.lang.Object)
         */
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.Subscriber#publishTo(java.lang.Object)
         */
        public void publishTo(Object inData)
        {
            mData = inData;
        }            
    };
    /**
     * Waits for the given block to return true.
     *
     * <p>This method is guaranteed to wait for the passed block
     * to evaluate to true.  It is also guaranteed to wait in a
     * fashion that allows other threads to receive sufficient
     * cycles to work.  The block will wait for a maximum of
     * 60 seconds before throwing an exception.
     * 
     * @param inBlock a <code>Callable&lt;Boolean&gt;</code> value containing the condition to be evaluated.  If the
     *   block evaluates to true, the wait method returns immediately.
     * @throws Exception if the block throws an exception
     */
    public static void wait(Callable<Boolean> inBlock)
        throws Exception
    {
        wait(inBlock,
             60);
    }
    /**
     * Waits for the given block to return true.
     *
     * <p>This method is guaranteed to wait for the passed block
     * to evaluate to true.  It is also guaranteed to wait in a
     * fashion that allows other threads to receive sufficient
     * cycles to work.  The block will wait for a maximum of
     * the given number of seconds before throwing an exception.
     * 
     * @param inBlock a <code>Callable&lt;Boolean&gt;</code> value containing the condition to be evaluated.  If the block evaluates to true, the wait method returns immediately.
     * @param inTimeout an <code>int</code> value
     * @throws Exception if the block throws an exception
     */
    public static void wait(Callable<Boolean> inBlock,
                            int inTimeout)
        throws Exception
    {
        int iterationCount = 0;
        int maxIterations = inTimeout*10;
        while(iterationCount++ < maxIterations) {
            if(inBlock.call()) {
                return;
            }
            Thread.sleep(100);
        }
        fail("Condition not reached in " + inTimeout +"s"); //$NON-NLS-1$
    }
    /**
     * Waits until the given subscriber receives a publication.
     * 
     * <p>This method is guaranteed to wait until the passed subscriber has been notified.  Note that in order
     * for the method to be deterministic, the subscriber will have to have been reset {@link org.marketcetera.core.publisher.MockSubscriber#reset()}
     * before executing the statement that causes the subscriber to be notified.
     *
     * @param inSubscriber a <code>TestSubscriber</code> value
     * @throws Exception if an error occurs
     */
    public static void waitForPublication(final MockSubscriber inSubscriber)
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
