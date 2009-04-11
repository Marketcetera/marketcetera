import java.util.HashMap;
import java.util.Map;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Sample strategy that tests processed market data requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class CombinedRequest
        extends Strategy
{
    /**
     * stores bid counts by symbol
     */
    private final Map<String,Integer> bids = new HashMap<String,Integer>();
    /**
     * stores ask counts by symbol
     */
    private final Map<String,Integer> asks = new HashMap<String,Integer>();
    /**
     * counts total events received
     */
    private int totalEventCount = 0;
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
        doCombinedRequest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onCallback(java.lang.Object)
     */
    @Override
    public void onCallback(Object inData)
    {
        int requestID = Integer.parseInt(getProperty("requestID"));
        if(getProperty("cancelCep") != null) {
            cancelDataRequest(requestID);
        } else {
            cancelDataRequest(requestID);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        recordSymbol(inAsk.getSymbolAsString(),
                     asks);
        transcribeCollection("ask",
                             asks);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
        recordSymbol(inBid.getSymbolAsString(),
                     bids);
        transcribeCollection("bid",
                             bids);
    }
    /**
     * Records the receipt of a symbol.
     *
     * @param inSymbol
     */
    private void recordSymbol(String inSymbol,
                              Map<String,Integer> inCollection)
    {
        Integer count = inCollection.get(inSymbol);
        if(count == null) {
            inCollection.put(inSymbol,
                             1);
        } else {
            inCollection.put(inSymbol,
                             ++count);
        }
        totalEventCount += 1;
        if(totalEventCount >= 50) {
            setProperty("finished",
                        "true");
        }
    }
    /**
     * Writes the given collection into the common storage area.
     *
     * @param inKey a <code>String</code> value to prepend to each stored symbol
     * @param inCollection a <code>Map&lt;String,Integer&gt;</code> value containing the values to be stored
     */
    private void transcribeCollection(String inKey,
                                      Map<String,Integer> inCollection)
    {
        for(String symbol : inCollection.keySet()) {
            setProperty(inKey + "-" + symbol,
                        Integer.toString(inCollection.get(symbol)));
        }
    }
    /**
     * Executes a request for processed market data.
     */
    private void doCombinedRequest()
    {
        String symbols = getProperty("symbols");
        String marketDataSource = getProperty("marketDataSource");
        String compressedStatements = getProperty("statements");
        String[] statements;
        if(compressedStatements != null) {
            statements = compressedStatements.split("#");
        } else {
            statements = null;
        }
        String cepSource = getProperty("cepSource");
        String stringAPI = getProperty("useStringAPI");
        try {
            if(stringAPI != null) {
                setProperty("requestID",
                            Integer.toString(requestProcessedMarketData(MarketDataRequest.newRequest().withSymbols(symbols).fromProvider(marketDataSource).
                                                                          withContent("TOP_OF_BOOK,LATEST_TICK").toString(),
                                                                        statements,
                                                                        cepSource)));
            } else {
                setProperty("requestID",
                            Integer.toString(requestProcessedMarketData(MarketDataRequest.newRequest().withSymbols(symbols).fromProvider(marketDataSource).
                                                                          withContent("TOP_OF_BOOK,LATEST_TICK"),
                                                                        statements,
                                                                        cepSource)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
