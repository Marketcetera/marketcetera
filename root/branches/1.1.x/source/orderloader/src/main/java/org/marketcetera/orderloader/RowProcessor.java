package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.BrokerID;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

/* $License$ */
/**
 * A processor responsible for processing each order row. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public abstract class RowProcessor {

    /**
     * Initialize the instance with the headers for each column.
     *
     * @param inHeaders the headers for each column. Cannot be null.
     *
     * @throws OrderParsingException if the supplied row is not
     * a valid header row.
     */
    public final void initialize(String... inHeaders)
            throws OrderParsingException {
        mNumHeaders = inHeaders.length;
        setHeaders(inHeaders);
    }

    /**
     * Parses the order in the supplied row and processes it using the
     * {@link OrderProcessor}.
     *
     * @param inIndex the row index.
     * @param inRow the row value, cannot be null.
     */
    public final void processOrder(int inIndex, String... inRow) {
        try {
            if(getNumHeaders() != inRow.length) {
                throw new OrderParsingException(new I18NBoundMessage2P(
                        Messages.HEADER_ROW_MISMATCH,getNumHeaders(),
                        inRow.length));
            }
            Order order = parseOrder(inRow);
            getProcessor().processOrder(order, inIndex);
            mNumSuccess++;
        }catch(Exception e) {
            mNumFailed++;
            addFailed(inIndex, inRow, e);
        }
    }

    /**
     * Returns the total number of orders processed. The returned
     * value is the sum of {@link #getNumFailed()} & {@link #getNumSuccess()}.
     *
     * @return the total number of orders processed.
     */
    public final int getTotal() {
        return getNumFailed() + getNumSuccess();
    }
    /**
     * Number of orders that failed to process.
     *
     * @return number of failures encountered when processing orders.
     */
    public final int getNumFailed() {
        return mNumFailed;
    }

    /**
     * Number of orders that were successfully processed.
     *
     * @return number of orders that were successfully processed.
     */
    public final int getNumSuccess() {
        return mNumSuccess;
    }

    /**
     * The list of orders that failed to process. Each pair has the order
     * index and the actual order that could not be processed.
     *
     * @return the list of orders that failed to process.
     */
    public final List<FailedOrderInfo> getFailedOrders() {
        return mFailedOrders;
    }
    /**
     * Creates an instance.
     *
     * @param inProcessor the processor. Cannot be null.
     * @param inBrokerID the broker ID.
     */
    protected RowProcessor(OrderProcessor inProcessor,
                           BrokerID inBrokerID) {
        if(inProcessor == null) {
            throw new NullPointerException();
        }
        mProcessor = inProcessor;
        mBrokerID = inBrokerID;
    }

    /**
     * The brokerID value to use for each order.
     *
     * @return the brokerID value to use for each order.
     */
    protected final BrokerID geBrokerID() {
        return mBrokerID;
    }

    /**
     * Implemented by subclasses to initialize themselves with the
     * supplied headers.
     *
     * @param inHeaders the headers specified in the csv file.
     *
     * @throws OrderParsingException if there were errors with the supplied
     * headers.
     */
    protected abstract void setHeaders(String[] inHeaders)
            throws OrderParsingException;

    /**
     * Parses the supplied row and creates an order instance from it.
     *
     * @param inRow the supplied row.
     *
     * @return the order created from the supplied row.
     *
     * @throws OrderParsingException if there were errors parsing the
     */
    protected abstract Order parseOrder(String[] inRow)
            throws OrderParsingException;

    /**
     * Records the failure when processing an order.
     *
     * @param inIndex the row index at which this order was specified.
     * @param inRow the row value.
     * @param inException the exception indicating the failure.
     */
    private void addFailed(int inIndex, String[] inRow,
                           Exception inException) {
        mFailedOrders.add(new FailedOrderInfo(inIndex, inRow, inException));
        Messages.LOG_FAILED_ORDER.error(this, inException,
                inIndex, Arrays.toString(inRow));
    }

    /**
     * The order processor.
     *
     * @return the order processor.
     */
    private OrderProcessor getProcessor() {
        return mProcessor;
    }

    /**
     * Returns the number of header columns.
     *
     * @return the number of header columns.
     */
    private int getNumHeaders() {
        return mNumHeaders;
    }

    private int mNumHeaders;
    private int mNumSuccess = 0;
    private int mNumFailed = 0;
    private final List<FailedOrderInfo> mFailedOrders =
            new LinkedList<FailedOrderInfo>();
    private final BrokerID mBrokerID;
    private final OrderProcessor mProcessor;
}
