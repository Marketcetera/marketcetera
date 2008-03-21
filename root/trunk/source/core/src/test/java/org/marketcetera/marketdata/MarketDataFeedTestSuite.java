package org.marketcetera.marketdata;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageBundleInfo;
import org.marketcetera.quickfix.FIXDataDictionary;

import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.field.MsgType;

/**
 * Test suite for Market Data Feed Tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
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

    public static Message generateFIXMessageWithoutID()
    {
        MessageFactory msgFactory = new quickfix.fix44.MessageFactory();
        Message request = msgFactory.create(FIXDataDictionary.FIX_4_4_BEGIN_STRING, 
                                            MsgType.MARKET_DATA_REQUEST);
        return request;
    }
    
    public static Message generateFIXMessage() 
        throws FeedException
    {
        List<MSymbol> symbols = Arrays.asList(new MSymbol[] { new MSymbol("GOOG"), new MSymbol("MSFT"), new MSymbol("YGZ9") });
        TestMarketDataFeed feed = new TestMarketDataFeed();
        return feed.marketDataRequest(symbols, 
                                      false);
    }    
}
