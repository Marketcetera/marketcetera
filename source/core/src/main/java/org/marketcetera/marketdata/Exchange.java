package org.marketcetera.marketdata;

import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.DepthOfBook;
import org.marketcetera.event.OpenHighLowClose;
import org.marketcetera.event.TopOfBook;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * A market data provider.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Exchange<T>
{
    /**
     * Returns the OHLC data for the given symbol over the given interval.
     *
     * @param inSymbol a <code>MSymbol</code> value
     * @param inStart a <code>Date</code> value
     * @param inEnd a <code>Date</code> value
     * @return an <code>OpenHighLowClose</code> value
     * @throws IllegalArgumentException if <code>inStart</code> &gt; <code>inEnd</code>
     */
    public OpenHighLowClose getOHLC(MSymbol inSymbol,
                                    Date inStart,
                                    Date inEnd);
    /**
     * Gets the top of the exchange book for the given symbol.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @return a <code>TopOfBook</code> value
     */
    public TopOfBook getTopOfBook(MSymbol inSymbol);
    /**
     * Establishes a subscription to the top of the exchange book for the given symbol.
     * 
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getTopOfBook(MSymbol inSymbol,
                          ISubscriber inSubscriber);
    /**
     * Gets the depth of the exchange book for the given symbol.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @return a <code>DepthOfBook</code> value
     */
    public DepthOfBook getDepthOfBook(MSymbol inSymbol);
    /**
     * Establishes a subscription to the depth of the exchange book for the given symbol.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getDepthOfBook(MSymbol inSymbol,
                            ISubscriber inSubscriber);
    /**
     * Gets the latest trade for the given symbol.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @return a <code>TradeEvent</code> value or null if there is no trade to return
     */
    public TradeEvent getLatestTick(MSymbol inSymbol);
    /**
     * Establishes a subscription to the latest trade for the given symbol.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getLatestTick(MSymbol inSymbol,
                           ISubscriber inSubscriber);
    /**
     * Establishes a subscription to all activity for the given symbol.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inSymbol an <code>MSymbol</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getStream(MSymbol inSymbol,
                       ISubscriber inSubscriber);
    /**
     * Cancels the subscription represented by the given token.
     * 
     * <p>If the subscription has already been canceled, this call has no effect.
     *
     * @param inToken a <code>T</code> value
     */
    public void cancel(T inToken);
    /**
     * Gets the name of the exchange.
     *
     * @return a <code>String</code> value
     */
    public String getName();
    /**
     * Gets the exchange code of the exchange. 
     *
     * @return a <code>String</code> value
     */
    public String getCode();
    /**
     * Starts the exchange.
     */
    public void start();
    /**
     * Stops the exchange.
     */
    public void stop();
    /**
     * The types of data that an {@link Exchange} can produce.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    public static enum Type
    {
        /**
         * the bid/ask set from the top of the order book, also known as best-bid-and-offer
         */
        TOP_OF_BOOK,
        /**
         * the most recent trade
         */
        LATEST_TICK,
        /**
         * the entire order book
         */
        DEPTH_OF_BOOK,
        /**
         * bids, asks, and trades as they are added to the book
         */
        STREAM
    }
}
