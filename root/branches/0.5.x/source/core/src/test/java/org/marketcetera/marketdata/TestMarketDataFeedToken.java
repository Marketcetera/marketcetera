package org.marketcetera.marketdata;

import java.util.concurrent.ExecutionException;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Test implementation of {@link AbstractMarketDataFeedToken}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class TestMarketDataFeedToken
    extends AbstractMarketDataFeedToken<TestMarketDataFeed,TestMarketDataFeedCredentials>
{
    private String mHandle;
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
    void setHandle(String inHandle)
    {
        mHandle = inHandle;
    }
    public String getHandle()
    {
        return mHandle;
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