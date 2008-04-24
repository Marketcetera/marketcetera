package org.marketcetera.marketdata;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageBundleInfo;

import quickfix.Message;

/**
 * Test suite for Market Data Feed Tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MarketDataFeedTestSuite
    extends MarketceteraTestSuite
{

    /**
     * Create a new <code>MarketDataFeedTestSuite</code> instance.
     *
     */
    public MarketDataFeedTestSuite()
    {
    }

    /**
     * Create a new <code>MarketDataFeedTestSuite</code> instance.
     *
     * @param inClass
     */
    public MarketDataFeedTestSuite(Class inClass)
    {
        super(inClass);

    }

    /**
     * Create a new <code>MarketDataFeedTestSuite</code> instance.
     *
     * @param inClass
     * @param inExtraBundle
     */
    public MarketDataFeedTestSuite(Class inClass,
                                   MessageBundleInfo inExtraBundle)
    {
        super(inClass,
              inExtraBundle);

    }

    /**
     * Create a new <code>MarketDataFeedTestSuite</code> instance.
     *
     * @param inClass
     * @param inExtraBundles
     */
    public MarketDataFeedTestSuite(Class inClass,
                                   MessageBundleInfo[] inExtraBundles)
    {
        super(inClass,
              inExtraBundles);

    }

    public static Message generateFIXMessage() 
        throws FeedException
    {
        List<MSymbol> symbols = Arrays.asList(new MSymbol[] { new MSymbol("GOOG"), new MSymbol("MSFT"), new MSymbol("YGZ9") });
        return generateFIXMessage(symbols);
    }    

    public static Message generateFIXMessage(List<MSymbol> inSymbols) 
        throws FeedException
    {
        return AbstractMarketDataFeed.marketDataRequest(inSymbols, 
                                                    false);
    }    
}
