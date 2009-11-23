import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.marketdata.MarketDataRequest;
import static org.marketcetera.marketdata.MarketDataRequest.Content;

/* $License$ */
/**
 * Strategy that processes market data via CEP.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class ProcessData extends Strategy {
    private static final String [] SYMBOLS = {"AMZN", "JAVA"}; // Depends on MD - can be other symbols
    private static final String MARKET_DATA_PROVIDER = "marketcetera"; // Can be activ, bogus, marketcetera
    private static final String [] CEP_QUERY =
            {"select t.instrumentAsString as symbol, t.price * t.size as position from trade t"};
    private static final String CEP_PROVIDER = "esper";



    /**
     * Executed when the strategy is started.
     * Use this method to set up data flows
     * and other initialization tasks.
     */
    @Override
    public void onStart() {
        requestProcessedMarketData(MarketDataRequest.newRequest().
                withSymbols(SYMBOLS).
                fromProvider(MARKET_DATA_PROVIDER).
                withContent(Content.LATEST_TICK),
                CEP_QUERY, CEP_PROVIDER);
    }

    /**
     * Executed when the strategy receives any other event.
     *
     * @param inEvent the received event.
     */
    @Override
    public void onOther(Object inEvent) {
        warn("Trade " + inEvent);
    }
}
