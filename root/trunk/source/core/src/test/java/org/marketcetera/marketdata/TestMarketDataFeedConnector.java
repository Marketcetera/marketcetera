package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test implementation of {@link IMarketDataFeedConnector}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestMarketDataFeedConnector
    implements IMarketDataFeedConnector
{
    private final List<MarketDataFeedToken> mCancelRequests;
    private TestMarketDataFeedToken mToken;
    private static final Random sRandom = new Random(System.nanoTime());
    private final int mDelay;
    /**
     * Create a new <code>TestMarketDataFeedConnector</code> instance.
     *
     */
    public TestMarketDataFeedConnector()
    {
        this(null,
             0);
    }

    public TestMarketDataFeedConnector(TestMarketDataFeedToken inToken,
                                       int inDelay)
    {
        mCancelRequests = new ArrayList<MarketDataFeedToken>();
        mToken = inToken;
        mDelay = inDelay;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IMarketDataFeedConnector#cancel(org.marketcetera.marketdata.MarketDataFeedToken)
     */
    public void cancel(MarketDataFeedToken inToken)
            throws FeedException
    {
        mCancelRequests.add(inToken);
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public TestMarketDataFeedToken call()
            throws Exception
    {
        if(mDelay > 0) {
            Thread.sleep(sRandom.nextInt(mDelay));
        }
        mToken.getPublisher().publish(this);
        return mToken;
    }

    /**
     * Gets cancelRequests value.
     *
     * @return a <code>List<MarketDataFeedToken></code> value
     */
    public List<MarketDataFeedToken> getCancelRequests()
    {
        return mCancelRequests;
    }
}
