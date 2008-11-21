package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.CALLBACK_ERROR;
import static org.marketcetera.strategy.Messages.INVALID_MARKET_DATA_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_TRADE_SUGGESTION;
import static org.marketcetera.strategy.Messages.NO_PARAMETERS;
import static org.marketcetera.strategy.Messages.NULL_PROPERTY_KEY;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Base class for running strategies.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractRunningStrategy
        implements RunningStrategy
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        if(strategy == null) {
            return super.toString();
        }
        return strategy.toString();
    }
    /**
     * Gets the shared properties store.
     * 
     * <p>All running strategies have access to this properties store.  Changes
     * made to the object returned from this method will effect the original
     * object.
     *
     * @return a <code>Properties</code> value
     */
    static final Properties getProperties()
    {
        return properties;
    }
    /**
     * Sets the strategy object associated with this {@link RunningStrategy}.
     *
     * @param inStrategy a <code>Strategy</code> value
     */
    final void setStrategy(Strategy inStrategy)
    {
        strategy = inStrategy;
    }
    /**
     * Indicates to the <code>AbstractRunningStrategy</code> that it should stop running now.
     */
    final void stop()
    {
        callbackService.shutdownNow();
    }
    /**
     * Sets the given key to the given value.
     *
     * <p>All running strategies have access to this properties store.
     * 
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     */
    protected static void setProperty(String inKey,
                                      String inValue)
    {
        if(inKey == null) {
            NULL_PROPERTY_KEY.warn(AbstractRunningStrategy.class);
            return;
        }
        if(inValue == null) {
            properties.remove(inKey);
            return;
        }
        properties.setProperty(inKey,
                               inValue);
    }
    /**
     * Gets the value associated with the given key.
     *
     * <p>All running strategies have access to this properties store.
     * 
     * @param inKey a <code>String</code> value
     */
    protected static String getProperty(String inKey)
    {
        if(inKey == null) {
            NULL_PROPERTY_KEY.warn(AbstractRunningStrategy.class);
            return null;
        }
        return properties.getProperty(inKey);
    }
    /**
     * Gets the parameter associated with the given name.
     *
     * @param inName a <code>String</code> value containing the key of a parameter key/value value
     * @return a <code>String</code> value or null if no parameter is associated with the given name
     */
    protected final String getParameter(String inName)
    {
        Properties parameters = strategy.getParameters();
        if(parameters == null) {
            NO_PARAMETERS.info(this,
                               strategy);
            return null;
        }
        return parameters.getProperty(inName);
    }
    /**
     * Requests market data from the given source.
     *
     * @param inSymbols a <code>String</code> value containing a comma-separated list of symbols
     * @param inSource a <code>String</code> value containing a string corresponding to a market data provider identifier
     * @return a <code>long</code> value containing the handle of the request or 0 if the request or the source was invalid
     */
    protected final long requestMarketData(String inSymbols,
                                           String inSource)
    {
        if(inSymbols != null &&
           !inSymbols.isEmpty()) {
            StringBuilder request = new StringBuilder();
            request.append("type=marketdata:symbols=").append(inSymbols); //$NON-NLS-1$
            try {
                return strategy.getServicesProvider().requestMarketData(DataRequest.newRequestFromString(request.toString()),
                                                                        inSource);
            } catch (Exception e) {
                INVALID_MARKET_DATA_REQUEST.warn(this,
                                                 e,
                                                 strategy,
                                                 inSymbols,
                                                 inSource);
                return 0;
            }
        }
        INVALID_MARKET_DATA_REQUEST.warn(this,
                                         strategy,
                                         inSymbols,
                                         inSource);
        return 0;
    }
    /**
     * Cancels the given market data request.
     *
     * @param inRequestID a <code>long</code> value containing the identifier of the data request to cancel
     */
    protected final void cancelMarketDataRequest(long inRequestID)
    {
        strategy.getServicesProvider().cancelMarketDataRequest(inRequestID);
    }
    /**
     * Cancels all market data requests from this strategy.
     */
    protected final void cancelAllMarketDataRequests()
    {
        strategy.getServicesProvider().cancelAllMarketDataRequests();
    }
    /**
     * Suggests a trade.
     *
     * @param inOrder an <code>OrderSingle</code> value containing the trade to suggest
     * @param inScore a <code>BigDecimal</code> value containing the score of this suggestion.  this value is determined by the user
     *   but is recommended to fit in the interval [0..1]
     * @param inIdentifier a <code>String</code> value containing a user-specified string to identify the suggestion
     */
    protected final void suggestTrade(OrderSingle inOrder,
                                      BigDecimal inScore,
                                      String inIdentifier)
    {
        if(inOrder == null ||
           inScore == null ||
           inIdentifier == null ||
           inIdentifier.isEmpty()) {
            INVALID_TRADE_SUGGESTION.warn(this,
                                          strategy);
            return;
        }
        assert(strategy != null);
        // TODO consider defensive copying.  if used here, it should probably be used in all service and action calls
        OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
        suggestion.setOrder(inOrder);
        suggestion.setScore(inScore);
        suggestion.setIdentifier(inIdentifier);
        SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                               "{} suggesting trade {}", //$NON-NLS-1$
                               strategy,
                               suggestion);
        strategy.getServicesProvider().sendSuggestion(suggestion);
    }
    /**
     * Requests a callback after a specified delay in milliseconds.
     *
     * <p>The callback will be executed as close to the specified millisecond
     * as possible.  There is no guarantee that the timing will be exact.  If
     * more than one callback is requested by the same {@link RunningStrategy}
     * for the same millisecond, the requests will be processed serially in
     * FIFO order.  This implies that a long-running callback request may
     * delay other callbacks from the same {@link RunningStrategy} unless the
     * caller takes steps to mitigate the bottleneck.
     *
     * @param inDelay a <code>long</code> value indicating how many milliseconds
     *   to wait before executing the callback.  A value <= 0 will be interpreted
     *   as a request for an immediate callback.
     * @param inData an <code>Object</code> value to deliver along with the callback,
     *   may be null
     */
    protected final void requestCallbackAfter(long inDelay,
                                              Object inData)
    {
        callbackService.schedule(new Callback(this,
                                              inData),
                                 inDelay,
                                 TimeUnit.MILLISECONDS);
    }
    /**
     * Requests a callback at a specific point in time.
     *
     * <p>The callback will be executed as close to the specified millisecond
     * as possible.  There is no guarantee that the timing will be exact.  If
     * more than one callback is requested by the same {@link RunningStrategy}
     * for the same millisecond, the requests will be processed serially in
     * FIFO order.  This implies that a long-running callback request may
     * delay other callbacks from the same {@link RunningStrategy} unless the
     * caller takes steps to mitigate the bottleneck.
     *
     * @param inDate a <code>Date</code> value at which to execute the callback.  A date
     *   value earlier than the present will be interpreted as a request for an
     *   immediate callback.
     * @param inData an <code>Object</code> value to deliver with the callback or null
     */
    protected final void requestCallbackAt(Date inDate,
                                           Object inData)
    {
        requestCallbackAfter(inDate.getTime() - System.currentTimeMillis(),
                             inData);
    }
    /**
     * common properties store shared among all strategies
     */
    private static final Properties properties = new Properties();
    /**
     * static strategy object of which this object is a running representation
     */
    private Strategy strategy;
    /**
     * Executes a callback to a specific {@link RunningStrategy}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private static final class Callback
        implements Runnable
    {
        /**
         * the strategy which to call
         */
        private final RunningStrategy strategy;
        /**
         * the data payload to deliver, may be null
         */
        private final Object data;
        /**
         * Create a new Callback instance.
         *
         * @param inStrategy a <code>RunningStrategy</code> instance
         * @param inData an <code>Object</code> value to deliver to the {@link RunningStrategy}
         *   or null
         */
        private Callback(RunningStrategy inStrategy,
                         Object inData)
        {
            strategy = inStrategy;
            data = inData;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                   "Executing callback for {} at {}", //$NON-NLS-1$
                                   strategy,
                                   new Date());
            try {
                strategy.onCallback(data);
            } catch (Exception e) {
                CALLBACK_ERROR.warn(this,
                                    strategy);
            }
        }
    }
    /**
     * scheduler for request callbacks
     */
    private final ScheduledExecutorService callbackService = Executors.newSingleThreadScheduledExecutor();
}
