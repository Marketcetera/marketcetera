package org.marketcetera.marketdata;

import java.util.List;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A market data provider.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface Exchange<T>
{
    /**
     * Returns statistical data for the given <code>ExchangeRequest</code>.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @return a <code>List&lt;MarketstatEvent&gt;</code> value
     */
    public List<MarketstatEvent> getStatistics(ExchangeRequest inExchangeRequest);
    /**
     * Establishes a subscription to statistical data for the given <code>ExchangeRequest</code>.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getStatistics(ExchangeRequest inExchangeRequest,
                           ISubscriber inSubscriber);
    /**
     * Gets the top of the exchange book for the given <code>ExchangeRequest</code>.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @return a <code>List&lt;QuoteEvent&gt;</code> value
     */
    public List<QuoteEvent> getTopOfBook(ExchangeRequest inExchangeRequest);
    /**
     * Establishes a subscription to the top of the exchange book for the given <code>ExchangeRequest</code>.
     * 
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getTopOfBook(ExchangeRequest inExchangeRequest,
                          ISubscriber inSubscriber);
    /**
     * Gets the depth of the exchange book for the given <code>ExchangeRequest</code>.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @return a <code>List&lt;QuoteEvent&gt;</code> value
     */
    public List<QuoteEvent> getDepthOfBook(ExchangeRequest inExchangeRequest);
    /**
     * Establishes a subscription to the depth of the exchange book for the given <code>ExchangeRequest</code>.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     * @return a <code>T</code> value representing the subscription
     */
    public T getDepthOfBook(ExchangeRequest inExchangeRequest,
                            ISubscriber inSubscriber);
    /**
     * Gets the latest trade for the given <code>ExchangeRequest</code>.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @return a <code>List&lt;TradeEvent&gt;</code> value
     */
    public List<TradeEvent> getLatestTick(ExchangeRequest inExchangeRequest);
    /**
     * Establishes a subscription to the latest trade for the given <code>ExchangeRequest</code>.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     *
     * @return a <code>T</code> value representing the subscription
     */
    public T getLatestTick(ExchangeRequest inExchangeRequest,
                           ISubscriber inSubscriber);
    /**
     * Gets the dividends for the given <code>ExchangeRequest</code>.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @return a <code>List&lt;DividendEvent&gt;</code> value
     */
    public List<DividendEvent> getDividends(ExchangeRequest inExchangeRequest);
    /**
     * Establishes a subscription to the dividends for the given <code>ExchangeRequest</code>.
     *
     * <p>The subscription will remain active until canceled via {@link Exchange#cancel(Object)} or
     * the exchange is stopped via {@link Exchange#stop}.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value containing the recipient of subscription updates
     *
     * @return a <code>T</code> value representing the subscription
     */
    public T getDividends(ExchangeRequest inExchangeRequest,
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
     * @since 1.5.0
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
         * statistics for an instrument
         */
        STATISTICS,
        /**
         * dividends for an instrument
         */
        DIVIDENDS
    }
}
