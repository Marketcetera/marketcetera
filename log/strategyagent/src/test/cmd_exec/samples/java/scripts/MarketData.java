package sample;

import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Content;

/* $License$ */
/**
 * Strategy that receives market data
 *
 * @author anshul@marketcetera.com
 * @version $Id: MarketData.java 16154 2012-07-14 16:34:05Z colin $
 * @since 2.0.0
 */
public class MarketData extends Strategy {
    private static final String SYMBOLS = "AMZN,GOOG"; //Depends on MD - can be other symbols
    private static final String OPTION_OSI_SYMBOL = "AAPL  091121C00123450"; //AAPL, Nov'09 $123.45 Call
    private static final String MARKET_DATA_PROVIDER = "bogus"; // Can be activ, bogus, marketcetera
    /**
     * Executed when the strategy is started.
     * Use this method to set up data flows
     * and other initialization tasks.
     */
    @Override
    public void onStart() {
        //equity
        requestMarketData(MarketDataRequestBuilder.newRequest().
                withSymbols(SYMBOLS).
                withProvider(MARKET_DATA_PROVIDER).
                withContent(Content.TOP_OF_BOOK).create());
        //option
        requestMarketData(MarketDataRequestBuilder.newRequest().
                withSymbols(OPTION_OSI_SYMBOL).
                withAssetClass(AssetClass.OPTION).
                withProvider(MARKET_DATA_PROVIDER).
                withContent(Content.LATEST_TICK).create());
    }

    /**
     * Executed when the strategy receives an ask event.
     *
     * @param inAsk the ask event.
     */
    @Override
    public void onAsk(AskEvent inAsk) {
        warn("Ask " + inAsk);
    }

    /**
     * Executed when the strategy receives a bid event.
     *
     * @param inBid the bid event.
     */
    @Override
    public void onBid(BidEvent inBid) {
        warn("Bid " + inBid);
    }

    /**
     * Executed when the strategy receives a trade event.
     *
     * @param inTrade the ask event.
     */
    @Override
    public void onTrade(TradeEvent inTrade) {
        warn("Trade " + inTrade);
    }

}
