package org.marketcetera.marketdata;

import java.util.concurrent.ExecutionException;


/**
 * Test implementation of {@link AbstractMarketDataFeedToken}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeedToken
    extends AbstractMarketDataFeedToken<TestMarketDataFeed,TestMarketDataFeedCredentials>
{
    private boolean mShouldFail = false;
    /**
     * Create a new <code>TestMarketDataFeedToken</code> instance.
     *
     * @param inFixMessage
     */
    private TestMarketDataFeedToken(MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> inTokenSpec,
                                    TestMarketDataFeed inFeed)
    {
        super(inTokenSpec,
              inFeed);
    }
    
    static TestMarketDataFeedToken getToken(MarketDataFeedTokenSpec<TestMarketDataFeedCredentials> inTokenSpec,
                                            TestMarketDataFeed inFeed) 
    {        
        return new TestMarketDataFeedToken(inTokenSpec,
                                           inFeed);
    }
    
    public void publishAndWait(Object inData) 
        throws InterruptedException, ExecutionException
    {
        getPublisher().publishAndWait(inData);
    }

    /**
     * @return the shouldFail
     */
    public boolean getShouldFail()
    {
        return mShouldFail;
    }

    /**
     * @param inShouldFail the shouldFail to set
     */
    public void setShouldFail(boolean inShouldFail)
    {
        mShouldFail = inShouldFail;
    }
}
