import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;

/* $License$ */
/**
 * Strategy implementation that calculates the VWAP in several names,
 * and at some "random" point in the future, sends an order at that price.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class VWAPStrategy extends Strategy {
    public static final String [] SYMBOLS = {"AMZN","GOOG","MSFT"}; // Depends on MD - can be other symbols
    public static final String MARKET_DATA_PROVIDER = "bogus"; // Can be activ, bogus, marketcetera
    public static final String [] CEP_QUERY =
            {"SELECT t.instrumentAsString AS instrument, sum(cast(t.price, double) * cast(t.size, double))/sum(cast(t.size, double)) AS vwap FROM trade t GROUP BY instrument"};
    public static final String CEP_PROVIDER = "esper";
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
                withContent(MarketDataRequest.Content.LATEST_TICK),
                CEP_QUERY, CEP_PROVIDER);
        requestCallbackAfter(1000 * 10, null); // register for callback in 10 seconds
    }

    /**
     * Executed when the strategy receives any other event.
     *
     * Stores the VWAP price for the symbol.
     *
     * @param inEvent the received event.
     */
    @Override
    public void onOther(Object inEvent) {
        //Multi Column selects from cep query result in Map events.
        //the map keys correspond to the column names used in the cep query.
        if (inEvent instanceof Map) {
            Map map = (Map) inEvent;
            String symbol = (String) map.get("instrument");
            Double vwap = (Double) map.get("vwap");
            info("setting vwap for symbol " + symbol + " " + vwap);
            mVWAPs.put(symbol, vwap);
        }
    }

    /**
     * Executed when the strategy receives a callback requested via
     * {@link #requestCallbackAt(java.util.Date, Object)} or
     * {@link #requestCallbackAfter(long, Object)}. All timer
     * callbacks come with the data supplied when requesting callback,
     * as an argument.
     *
     * @param inData the callback data
     */
    @Override
    public void onCallback(Object inData) {
        //send a buy order for each of the symbols
        info("inside callback iterating");
        for(String symbol: SYMBOLS) {
            Double vwap = mVWAPs.get(symbol);
            if(vwap != null) {
                info("About to send orders for " + symbol + " with vwap of " + vwap);
                OrderSingle order = Factory.getInstance().createOrderSingle();
                order.setSide(Side.Buy);
                order.setQuantity(new BigDecimal("1000.0"));
                order.setInstrument(new Equity(symbol));
                order.setOrderType(OrderType.Limit);
                order.setTimeInForce(TimeInForce.Day);
                order.setPrice(new BigDecimal(vwap));
                info("sending order " + order);
                send(order);
            } else {
                warn("didn't find anything for "+ symbol +
                        " and checked value was " + vwap + " within " + mVWAPs);
            }
        }
        requestCallbackAfter(1000 * 10, null); // register for callback in 10 seconds
    }

    private final Map<String, Double> mVWAPs = new ConcurrentHashMap<String, Double>();
}
