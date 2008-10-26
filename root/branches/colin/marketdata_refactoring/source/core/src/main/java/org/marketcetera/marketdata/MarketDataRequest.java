package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_DEPTH;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Represents a request for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public class MarketDataRequest
    extends DataRequest
{
    /**
     * the entire book
     */
    public static final int FULL_BOOK = 0;
    /**
     * just the top or best-bid-and-offer
     */
    public static final int TOP_OF_BOOK = 1;
    /**
     * exchange to use if no exchange is specified as part of the market data request
     */
    public static final String NO_EXCHANGE = "NO EXCHANGE SPECIFIED"; //$NON-NLS-1$
    /**
     * Request types for <code>MarketDataRequest</code> objects.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id:$
     * @since $Release$
     */
    @ClassVersion("$Id:$") //$NON-NLS-1$
    public static enum RequestType
    {
        /**
         * one single view of the current book with no updates
         */
        SNAPSHOT,
        /**
         * subscription to an order book with updates as they become available
         */
        SUBSCRIBE,
        /**
         * cancels the market data request of the same <code>id</code>
         */
        CANCEL
    };
    /**
     * Update types for <code>MarketDataRequest</code> objects.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id:$
     * @since $Release$
     */
    @ClassVersion("$Id:$") //$NON-NLS-1$
    public static enum UpdateType
    {
        /**
         * publish the full book each time an update is received
         */
        FULL_REFRESH,
        /**
         * publish only the changes each time an update is received
         */
        INCREMENTAL_REFRESH
    }
    /**
     * Creates a market data request for the given symbols.
     *
     * <p>The request will be for a subscription for full book delivered as incremental
     * updates.  This is the same as asking for the tick stream from a provider.  No exchange is
     * specified.
     *
     * @param inSymbols a <code>String...</code> value containing the symbols for which to request data
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest newFullBookRequest(String... inSymbols)
    {
        return new MarketDataRequest(counter.incrementAndGet(),
                                     FULL_BOOK,
                                     RequestType.SUBSCRIBE,
                                     UpdateType.INCREMENTAL_REFRESH,
                                     NO_EXCHANGE,
                                     inSymbols);
    }
    /**
     * Creates a market data request for the given symbols.
     *
     * <p>The request will be for a snapshot for full book delivered a full refresh.
     * This is the same as asking for the tick stream from a provider.  No exchange is
     * specified.
     *
     * @param inSymbols a <code>String...</code> value containing the symbols for which to request data
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest newFullBookSnapshotRequest(String... inSymbols)
    {
        return new MarketDataRequest(counter.incrementAndGet(),
                                     FULL_BOOK,
                                     RequestType.SNAPSHOT,
                                     UpdateType.FULL_REFRESH,
                                     NO_EXCHANGE,
                                     inSymbols);
    }
    /**
     * Creates a market data request for the given symbols.
     *
     * <p>The request will be for a subscription to the top-of-book or best-bid-and-offer delivered as incremental
     * updates.  No exchange is specified.
     *
     * @param inSymbols a <code>String...</code> value containing the symbols for which to request data
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest newTopOfBookRequest(String... inSymbols)
    {
        return new MarketDataRequest(counter.incrementAndGet(),
                                     TOP_OF_BOOK,
                                     RequestType.SUBSCRIBE,
                                     UpdateType.INCREMENTAL_REFRESH,
                                     NO_EXCHANGE,
                                     inSymbols);
    }
    /**
     * Creates a market data request for the given symbols.
     *
     * <p>The request will be for a subscription for the given book depth delivered as incremental
     * updates.  No exchange is specified.
     *
     * @param inDepth an <code>int</code> value containing the depth of the order book to request
     * @param inSymbols a <code>String...</code> value containing the symbols for which to request data
     * @return a <code>MarketDataRequest</code> value
     * @throws IllegalArgumentException if the specified depth is invalid
     */
    public static MarketDataRequest newSpecifedDepthRequest(int inDepth,
                                                            String... inSymbols)
    {
        return new MarketDataRequest(counter.incrementAndGet(),
                                     inDepth,
                                     RequestType.SUBSCRIBE,
                                     UpdateType.INCREMENTAL_REFRESH,
                                     NO_EXCHANGE,
                                     inSymbols);
    }
    /**
     * Creates a market data request from the given string.
     * 
     * <p>Note that the subcomponents of the request string themselves are allowed to contain neither
     * the {@link #KEY_VALUE_DELIMITER} nor the {@link #SYMBOL_DELIMITER}.  If a subcomponent contains the
     * <code>KEY_VALUE_DELIMITER</code>, that subcomponent will be <b>truncated</b> at the first occurrence of the delimiter.
     * If a subcomponent contains the <code>SYMBOL_DELIMITER</code>, an <code>IllegalArgumentException</code> will be thrown.  
     *
     * @param inRequestString a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IOException if the <code>String</code> could not be converted to a <code>MarketDataRequest</code>
     * @throws IllegalArgumentException if <code>inRequestString</code> cannot be parsed properly
     */
    public static MarketDataRequest newRequestFromString(String inRequestString)
    {
        Properties request;
        try {
            request = propertiesFromString(inRequestString);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        setRequestDefaultsIfNecessary(request);
        return new MarketDataRequest(Long.parseLong(request.getProperty(ID)),
                                     Integer.parseInt(request.getProperty(DEPTH)),
                                     RequestType.valueOf(request.getProperty(REQUEST_TYPE).toUpperCase()),
                                     UpdateType.valueOf(request.getProperty(UPDATE_TYPE).toUpperCase()),
                                     request.getProperty(EXCHANGE),
                                     request.getProperty(SYMBOLS).split(SYMBOL_DELIMITER));
    }
    /**
     * Create a new MarketDataRequest instance.
     * 
     * @param inID a <code>long</code> value containing an id unique to this JVM session - this is not validated by the constructor
     * @param inDepth an <code>int</code> value indicating the depth of the order book to request
     * @param inRequestType a <code>RequestType</code> value indicating whether to subscribe to the results or just get a single snapshot
     * @param inUpdateType an <code>UpdateType</code> value indicating how updates should be received
     * @param inExchange a <code>String</code> value indicating the exchange from which to request the data
     * @param inSymbols a <code>String...</code> value containing the symbols for which to request data
     * 
     * @throws IllegalArgumentException if the specified depth is invalid, the symbols cannot be parsed, or the id specified is invalid
     */
    private MarketDataRequest(long inID,
                              int inDepth,
                              RequestType inRequestType,
                              UpdateType inUpdateType,
                              String inExchange,
                              String... inSymbols)
    {
        super(inID);
        depth = validateDepth(inDepth);
        requestType = inRequestType;
        updateType = inUpdateType;
        exchange = validateStringValue(inExchange);
        symbols = validateSymbols(inSymbols);
    }
    /**
     * Get the depth value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public int getDepth()
    {
        return depth;
    }
    /**
     * Get the requestType value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public RequestType getRequestType()
    {
        return requestType;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /**
     * Get the symbols value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public String[] getSymbols()
    {
        return Arrays.asList(symbols).toArray(new String[symbols.length]);
    }
    /**
     * Get the updateType value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public UpdateType getUpdateType()
    {
        return updateType;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        Properties output = new Properties();
        populatePropertiesWithObjectAttributes(output);
        StringBuilder symbolList = new StringBuilder();
        boolean commaNeeded = false;
        for(String symbol : symbols) {
            if(commaNeeded) {
                symbolList.append(SYMBOL_DELIMITER);
            }
            symbolList.append(symbol);
            commaNeeded = true;
        }
        output.setProperty(SYMBOLS,
                             symbolList.toString());
        try {
            return propertiesToString(output);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    protected final void populatePropertiesWithObjectAttributes(Properties inProperties)
    {
        super.populatePropertiesWithObjectAttributes(inProperties);
        inProperties.setProperty(DEPTH,
                                 Integer.toString(depth));
        inProperties.setProperty(REQUEST_TYPE,
                                 requestType.toString());
        inProperties.setProperty(UPDATE_TYPE,
                                 updateType.toString());
        inProperties.setProperty(EXCHANGE,
                                 exchange);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    protected int doHashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + depth;
        result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
        result = prime * result + Arrays.hashCode(symbols);
        result = prime * result + ((updateType == null) ? 0 : updateType.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    protected boolean doEquals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MarketDataRequest other = (MarketDataRequest) obj;
        return equivalent(other);
    }
    @Override
    public boolean equivalent(DataRequest other)
    {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if(!(other instanceof MarketDataRequest)) {
            return false;
        }
        MarketDataRequest mdrOther = (MarketDataRequest)other;
        if (depth != mdrOther.depth)
            return false;
        if (exchange == null) {
            if (mdrOther.exchange != null)
                return false;
        } else if (!exchange.equals(mdrOther.exchange))
            return false;
        if (requestType == null) {
            if (mdrOther.requestType != null)
                return false;
        } else if (!requestType.equals(mdrOther.requestType))
            return false;
        if (!Arrays.equals(symbols,
                           mdrOther.symbols))
            return false;
        if (updateType == null) {
            if (mdrOther.updateType != null)
                return false;
        } else if (!updateType.equals(mdrOther.updateType))
            return false;
        return true;
    }
    /**
     * Validates the depth argument in the context of specifying depth-of-book. 
     *
     * @param inDepth an <code>int</code> value containing a book depth
     * @return an <code>int</code> value containing a valid depth
     * @throws IllegalArgumentException if the specified depth is invalid
     */
    private static int validateDepth(int inDepth)
    {
        if(inDepth < 0) {
            throw new IllegalArgumentException(INVALID_DEPTH.getText(inDepth));
        }
        return inDepth;
    }
    /**
     * Takes the given <code>Properties</code> object and inserts missing keys with their default values if appropriate.
     *
     * @param inProperties a <code>Properties</code> object
     */
    protected static void setRequestDefaultsIfNecessary(Properties inProperties)
    {
        DataRequest.setRequestDefaultsIfNecessary(inProperties);
        if(!inProperties.containsKey(DEPTH)) {
            inProperties.setProperty(DEPTH,
                                     Integer.toString(FULL_BOOK));
        }
        if(!inProperties.containsKey(REQUEST_TYPE)) {
            inProperties.setProperty(REQUEST_TYPE,
                                     RequestType.SUBSCRIBE.toString());
        }
        if(!inProperties.containsKey(UPDATE_TYPE)) {
            inProperties.setProperty(UPDATE_TYPE,
                                     UpdateType.INCREMENTAL_REFRESH.toString());
        }
        if(!inProperties.containsKey(EXCHANGE)) {
            inProperties.setProperty(EXCHANGE,
                                     NO_EXCHANGE);
        }
    }
    /**
     * the depth of the order book requested
     */
    private final int depth;
    /**
     * indicates whether to send updates or a single snapshot
     */
    private final RequestType requestType;
    /**
     * indicates how to send updates
     */
    private final UpdateType updateType;
    /**
     * the exchange from which to request market data
     */
    private final String exchange;
    /**
     * the symbols for which to request market data
     */
    private final String[] symbols;
    /**
     * identifies the {@link #depth} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    private static final String DEPTH = "depth"; //$NON-NLS-1$
    /**
     * identifies the {@link #requestType} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    private static final String REQUEST_TYPE = "requestType"; //$NON-NLS-1$
    /**
     * identifies the {@link #updateType} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    private static final String UPDATE_TYPE = "updateType"; //$NON-NLS-1$
    /**
     * identifies the {@link #exchange} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    private static final String EXCHANGE = "exchange"; //$NON-NLS-1$
    /**
     * identifies the {@link #symbols} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    private static final String SYMBOLS = "symbols"; //$NON-NLS-1$
}
