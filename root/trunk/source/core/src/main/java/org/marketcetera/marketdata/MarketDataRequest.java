package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_DEPTH;
import static org.marketcetera.marketdata.Messages.INVALID_STRING_VALUE;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * A request for market data from a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class MarketDataRequest
    extends DataRequest
{
    /**
     * the delimiter used to distinguish between symbols in the string representation of the symbol collection
     */
    public static final String SYMBOL_DELIMITER = ","; //$NON-NLS-1$
    /**
     * identifies the {@link #depth} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String DEPTH_KEY = "depth"; //$NON-NLS-1$
    /**
     * identifies the {@link #requestType} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String REQUEST_TYPE_KEY = "requestType"; //$NON-NLS-1$
    /**
     * identifies the {@link #updateType} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String UPDATE_TYPE_KEY = "updateType"; //$NON-NLS-1$
    /**
     * identifies the {@link #exchange} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String EXCHANGE_KEY = "exchange"; //$NON-NLS-1$
    /**
     * identifies the {@link #symbols} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String SYMBOLS_KEY = "symbols"; //$NON-NLS-1$
    /**
     * the request type string for <code>MarketDataRequest</code> objects
     */
    public static final String TYPE = "marketdata"; //$NON-NLS-1$
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
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
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
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
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
        Properties values = new Properties();
        values.setProperty(TYPE_KEY,
                           TYPE);
        values.setProperty(DEPTH_KEY,
                           Integer.toString(FULL_BOOK));
        values.setProperty(REQUEST_TYPE_KEY,
                           RequestType.SUBSCRIBE.toString());
        values.setProperty(UPDATE_TYPE_KEY,
                           UpdateType.INCREMENTAL_REFRESH.toString());
        values.setProperty(EXCHANGE_KEY,
                           NO_EXCHANGE);
        return new MarketDataRequest(values,
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
        Properties values = new Properties();
        values.setProperty(TYPE_KEY,
                           TYPE);
        values.setProperty(DEPTH_KEY,
                           Integer.toString(FULL_BOOK));
        values.setProperty(REQUEST_TYPE_KEY,
                           RequestType.SNAPSHOT.toString());
        values.setProperty(UPDATE_TYPE_KEY,
                           UpdateType.FULL_REFRESH.toString());
        values.setProperty(EXCHANGE_KEY,
                           NO_EXCHANGE);
        return new MarketDataRequest(values,
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
        Properties values = new Properties();
        values.setProperty(TYPE_KEY,
                           TYPE);
        values.setProperty(DEPTH_KEY,
                           Integer.toString(TOP_OF_BOOK));
        values.setProperty(REQUEST_TYPE_KEY,
                           RequestType.SUBSCRIBE.toString());
        values.setProperty(UPDATE_TYPE_KEY,
                           UpdateType.INCREMENTAL_REFRESH.toString());
        values.setProperty(EXCHANGE_KEY,
                           NO_EXCHANGE);
        return new MarketDataRequest(values,
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
        Properties values = new Properties();
        values.setProperty(TYPE_KEY,
                           TYPE);
        values.setProperty(DEPTH_KEY,
                           Integer.toString(inDepth));
        values.setProperty(REQUEST_TYPE_KEY,
                           RequestType.SUBSCRIBE.toString());
        values.setProperty(UPDATE_TYPE_KEY,
                           UpdateType.INCREMENTAL_REFRESH.toString());
        values.setProperty(EXCHANGE_KEY,
                           NO_EXCHANGE);
        return new MarketDataRequest(values,
                                     inSymbols);
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
    /**
     * does class-level initialization for <code>MarketDataRequest</code>
     */
    static
    {
        DataRequest.registerType(TYPE,
                                 MarketDataRequest.class);
    }
    /**
     * Creates a market data request from the given string.
     * 
     * <p>Note that the subcomponents of the request string themselves are allowed to contain neither
     * the {@link #KEY_VALUE_DELIMITER} nor the {@link MarketDataRequest#SYMBOL_DELIMITER}.  If a subcomponent contains the
     * <code>KEY_VALUE_DELIMITER</code>, that subcomponent will be <b>truncated</b> at the first occurrence of the delimiter.
     * If a subcomponent contains the <code>SYMBOL_DELIMITER</code>, an <code>IllegalArgumentException</code> will be thrown.  
     *
     * @param inRequestString a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IOException if the <code>String</code> could not be converted to a <code>MarketDataRequest</code>
     * @throws IllegalArgumentException if <code>inRequestString</code> cannot be parsed properly
     */
    protected static MarketDataRequest newRequestFromString(Properties inRequest)
    {
        String symbolString = inRequest.getProperty(SYMBOLS_KEY);
        if(symbolString == null ||
           symbolString.length() == 0) {
            throw new IllegalArgumentException(INVALID_SYMBOLS.getText(symbolString));
        }
        return new MarketDataRequest(inRequest,
                                     symbolString.split(SYMBOL_DELIMITER));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequest#addAttributesToProperties(java.util.Properties)
     */
    @Override
    protected void addCurrentAttributesValues(Properties inProperties)
    {
        inProperties.setProperty(DEPTH_KEY,
                                 Integer.toString(depth));
        inProperties.setProperty(REQUEST_TYPE_KEY,
                                 requestType.toString());
        inProperties.setProperty(UPDATE_TYPE_KEY,
                                 updateType.toString());
        inProperties.setProperty(EXCHANGE_KEY,
                                 exchange);
        StringBuilder symbolList = new StringBuilder();
        boolean commaNeeded = false;
        for(String symbol : symbols) {
            if(commaNeeded) {
                symbolList.append(MarketDataRequest.SYMBOL_DELIMITER);
            }
            symbolList.append(symbol);
            commaNeeded = true;
        }
        inProperties.setProperty(SYMBOLS_KEY,
                                 symbolList.toString());
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
     * Takes the given <code>Properties</code> object and inserts missing keys with their default values if appropriate.
     *
     * @param inProperties a <code>Properties</code> object
     */
    protected static void validateAndSetRequestDefaultsIfNecessary(Properties inProperties)
    {
        DataRequest.validateAndSetRequestDefaultsIfNecessary(inProperties);
        if(!inProperties.containsKey(DEPTH_KEY)) {
            inProperties.setProperty(DEPTH_KEY,
                                     Integer.toString(TOP_OF_BOOK));
        }
        if(!inProperties.containsKey(REQUEST_TYPE_KEY)) {
            inProperties.setProperty(REQUEST_TYPE_KEY,
                                     RequestType.SUBSCRIBE.toString());
        }
        if(!inProperties.containsKey(UPDATE_TYPE_KEY)) {
            inProperties.setProperty(UPDATE_TYPE_KEY,
                                     UpdateType.INCREMENTAL_REFRESH.toString());
        }
        if(!inProperties.containsKey(EXCHANGE_KEY)) {
            inProperties.setProperty(EXCHANGE_KEY,
                                     NO_EXCHANGE);
        }
    }
    /**
     * Validates a <code>String</code> value to make sure it fits within the guidelines for this object.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>String</code> value guaranteed to be valid
     * @throws IllegalArgumentException if the given <code>String</code> is not valid
     */
    protected static String validateStringValue(String inValue)
    {
        DataRequest.validateStringValue(inValue);
        if(inValue.contains(MarketDataRequest.SYMBOL_DELIMITER)) {
            throw new IllegalArgumentException(INVALID_STRING_VALUE.getText(inValue));
        }
        return inValue;
    }
    /**
     * Create a new MarketDataRequest instance.
     * 
     * @throws IllegalArgumentException if the specified depth is invalid, the symbols cannot be parsed, or the id specified is invalid
     */
    private MarketDataRequest(Properties inRequest,
                              String... inSymbols)
    {
        super(inRequest);
        depth = validateDepth(inRequest.getProperty(DEPTH_KEY));
        requestType = validateRequestType(inRequest.getProperty(REQUEST_TYPE_KEY).toUpperCase());
        updateType = validateUpdateType(inRequest.getProperty(UPDATE_TYPE_KEY).toUpperCase());
        exchange = validateStringValue(inRequest.getProperty(EXCHANGE_KEY));
        symbols = validateSymbols(inSymbols);
    }
    /**
     * Validates the depth argument in the context of specifying depth-of-book. 
     *
     * @param inDepthValue an <code>int</code> value containing a book depth
     * @return an <code>int</code> value containing a valid depth
     * @throws IllegalArgumentException if the specified depth is invalid
     */
    private int validateDepth(String inDepthValue)
    {
        int depthValue;
        try {
            depthValue = Integer.parseInt(inDepthValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_DEPTH.getText(inDepthValue));
        }
        if(depthValue < 0) {
            throw new IllegalArgumentException(INVALID_DEPTH.getText(inDepthValue));
        }
        return depthValue;
    }
    /**
     * Validates the given <code>String</code> to see if it can be interpreted as a {@link RequestType}.
     *
     * @param inRequestTypeValue a <code>String</code> value
     * @return a <code>RequestType</code> value
     * @throws IllegalArgumentException if <code>inRequestTypeValue</code> is not a valid <code>RequestType</code>
     */
    private RequestType validateRequestType(String inRequestTypeValue)
    {
        return RequestType.valueOf(inRequestTypeValue.toUpperCase());
    }
    /**
     * Validates the given <code>String</code> to see if it can be interpreted as an {@link UpdateType}.
     *
     * @param inUpdateTypeValue a <code>String</code> value
     * @return an <code>UpdateType</code> value
     * @throws IllegalArgumentException if <code>inUpdateTypeValue</code> is not a valid <code>UpdateType</code>
     */
    private UpdateType validateUpdateType(String inUpdateTypeValue)
    {
        return UpdateType.valueOf(inUpdateTypeValue.toUpperCase());
    }
    /**
     * Validates the symbols and returns a valid symbol array.
     *
     * @param inSymbols a <code>String...</code> value
     * @return a <code>String[]</code> value
     * @throws IllegalArgumentException if the symbols cannot be parsed
     */
    private static String[] validateSymbols(String... inSymbols)
    {
        if(inSymbols == null ||
           inSymbols.length == 0) {
            throw new IllegalArgumentException(INVALID_SYMBOLS.getText(Arrays.toString(inSymbols)));
        }
        for(String symbol : inSymbols) {
            if(symbol.length() == 0) {
                throw new IllegalArgumentException(INVALID_SYMBOLS.getText(Arrays.toString(inSymbols)));
            }
            validateStringValue(symbol);
        }
        return inSymbols;
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
}
