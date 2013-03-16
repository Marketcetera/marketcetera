package org.marketcetera.core.marketdata;

import java.util.concurrent.ExecutionException;

import org.marketcetera.core.marketdata.AbstractMarketDataFeedToken;
import org.marketcetera.core.marketdata.MarketDataFeedTokenSpec;

/* $License$ */

/**
 * Test implementation of {@link org.marketcetera.core.marketdata.AbstractMarketDataFeedToken}.
 *
 * @version $Id: MockMarketDataFeedToken.java 82329 2012-04-10 16:28:13Z colin $
 * @since 0.5.0
 */
public class MockMarketDataFeedToken
    extends AbstractMarketDataFeedToken<MockMarketDataFeed>
{
    private String mHandle;
    private boolean mShouldFail = false;
    /**
     * Create a new <code>TestMarketDataFeedToken</code> instance.
     *
     * @param inFixMessage
     */
    private MockMarketDataFeedToken(MarketDataFeedTokenSpec inTokenSpec,
                                    MockMarketDataFeed inFeed)
    {
        super(inTokenSpec,
              inFeed);
    }
    
    static MockMarketDataFeedToken getToken(MarketDataFeedTokenSpec inTokenSpec,
                                            MockMarketDataFeed inFeed)
    {        
        return new MockMarketDataFeedToken(inTokenSpec,
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
