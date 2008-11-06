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
@ClassVersion("$Id$") //$NON-NLS-1$
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
