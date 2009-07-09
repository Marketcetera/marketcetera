import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.trade.*;
import org.marketcetera.marketdata.MarketDataRequest;

import java.util.concurrent.atomic.AtomicBoolean;


/* $License$ */
/**
 * Order Sender Strategy
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
public class OrderSender extends Strategy {
    private static final String SYMBOLS = "AMZN"; // Depends on MD - can be other symbols
    private static final String MARKET_DATA_PROVIDER = "marketcetera"; // Can be activ, bogus, marketcetera
    private static final String ACCOUNT = "accountable";

    /**
     * Executed when the strategy is started.
     * Use this method to set up data flows
     * and other initialization tasks.
     */
    @Override
    public void onStart() {
        mRequestID = requestMarketData(MarketDataRequest.newRequest().
                withSymbols(SYMBOLS).
                fromProvider(MARKET_DATA_PROVIDER));
        info("Issued Market Data Request " + mRequestID);
    }

    /**
     * Executed when the strategy receives a bid event.
     *
     * @param inBid the bid event.
     */
    @Override
    public void onBid(BidEvent inBid) {
        if(!mReceivedData.compareAndSet(false, true)) {
            return;
        }
        info("Bid: " + inBid);
        // Send an order to buy and cancel the request
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setAccount(ACCOUNT);
        order.setOrderType(OrderType.Limit);
        order.setPrice(inBid.getPrice());
        order.setQuantity(inBid.getSize());
        order.setSide(Side.Buy);
        order.setSymbol(inBid.getSymbol());
        order.setTimeInForce(TimeInForce.Day);
        warn("Sending Order " + order);

        OrderID orderID = sendOrder(order);
        warn("Sent Order:" + orderID);

        cancelDataRequest(mRequestID);
        info("Cancelled Market Data Request " + mRequestID);
    }

    /**
     * Executed when the strategy receives an ask event.
     *
     * @param inAsk the ask event.
     */
    @Override
    public void onAsk(AskEvent inAsk) {
        if(!mReceivedData.compareAndSet(false, true)) {
            return;
        }
        info("Ask: " + inAsk);
        // Send an order to Sell and cancel the request
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setAccount(ACCOUNT);
        order.setOrderType(OrderType.Limit);
        order.setPrice(inAsk.getPrice());
        order.setQuantity(inAsk.getSize());
        order.setSide(Side.Sell);
        order.setSymbol(inAsk.getSymbol());
        order.setTimeInForce(TimeInForce.Day);
        warn("Sending Order " + order);

        OrderID orderID = sendOrder(order);
        warn("Sent Order:" + orderID);

        cancelDataRequest(mRequestID);
        info("Cancelled Market Data Request " + mRequestID);
    }

    /**
     * Executed when the strategy receives an execution report.
     *
     * @param inExecutionReport the execution report.
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport) {
        warn("Received Execution Report:" + inExecutionReport);
    }

    private int mRequestID;
    private final AtomicBoolean mReceivedData = new AtomicBoolean();
}
