import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.*;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.SecureRandom;
import java.math.BigDecimal;

/* $License$ */
/**
 * Given parameters indicating symbol and size, slices the total size into
 * smaller chunks and sends market orders off at random times.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
public class OrderSlicer extends Strategy {
    /**
     * Executed when the strategy is started.
     * Use this method to set up data flows
     * and other initialization tasks.
     */
    @Override
    public void onStart() {
        String symbol = getParameter("symbol");
        String qty = getParameter("quantity");
        if (symbol == null || qty == null) {
            String msg = "Please specify the 'symbol' and/or 'quantity' parameters (right-click on registered Strategy and go to Properties)";
            error(msg);
            notifyHigh("Strategy missing parameters", msg);
            //throw an exception to prevent the strategy from starting.
            throw new IllegalArgumentException(msg);
        }
        int quantity = Integer.parseInt(qty);
        info("Partioning " + quantity + " " + symbol);
        List<Integer> partition;
        //if it's more than 100 shares, do partitioning in "round lots"
        if (quantity > 100) {
            // Figure out if we have a odd lot
            int oddQty = quantity % 100;
            quantity = quantity / 100;
            partition = generatePartition(quantity, 100);
            if (oddQty > 0) {
                partition.add(oddQty);
            }
        } else {
            partition = generatePartition(quantity, 1);
        }
        mNumPartitions = partition.size();
        //Output the partitioning
        info("Partitions: " + partition.toString());
        //Generate order objects from partition sizes
        for(int size: partition) {
            OrderSingle order = Factory.getInstance().createOrderSingle();
            order.setOrderType(OrderType.Market);
            order.setQuantity(new BigDecimal(size));
            order.setSide(Side.Buy);
            order.setInstrument(new Equity(symbol));
            order.setTimeInForce(TimeInForce.Day);
            // request a callback for each order at a random time (up to 10 seconds)
            requestCallbackAfter(1000 * sRandom.nextInt(10), order);
        }
    }

    /**
     * Executed when the strategy receives a callback requested via
     * {@link #requestCallbackAt(java.util.Date, Object)} or
     * {@link #requestCallbackAfter(long, Object)}. All timer
     * callbacks come with the data supplied when requesting callback,
     *  as an argument.
     *
     * @param inData the callback data
     */
    @Override
    public void onCallback(Object inData) {
        send(inData);
        int sent = mNumSent.incrementAndGet();
        info("sent order " + sent + "/" + mNumPartitions);
    }

    /**
     * Generate random partitions of the given quantity. Multiplies each
     * partition with the supplied multiple.
     *
     * @param inQuantity the quantity to be partitioned.
     * @param inMultiple the multiple to be applied to each partition.
     *
     * @return the list of partitions.
     */
    private static List<Integer> generatePartition(int inQuantity,
                                                   int inMultiple) {
        List<Integer> list = new ArrayList<Integer>();
        while (inQuantity > 0) {
            int split = sRandom.nextInt(inQuantity) + 1;
            list.add(split * inMultiple);
            inQuantity -= split;
        }
        Collections.shuffle(list, sRandom);
        return list;
    }
    private volatile int mNumPartitions;
    private final AtomicInteger mNumSent = new AtomicInteger(0);
    private final static Random sRandom = new SecureRandom();
}
