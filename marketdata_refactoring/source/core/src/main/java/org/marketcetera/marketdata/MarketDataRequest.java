package org.marketcetera.marketdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import static org.marketcetera.marketdata.Messages.*;

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
    public static final String NO_EXCHANGE = "NO EXCHANGE SPECIFIED";
    /**
     * the delimiter used to distinguish between symbols in the string representation of the symbol collection
     */
    public static final String SYMBOL_DELIMITER = ","; //$NON-NLS-1$
    /**
     * the delimiter used to distinguish key/value pairs in the string representation of the request 
     */
    public static final String KEY_VALUE_DELIMITER = ":"; //$NON-NLS-1$
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
        id = validateID(inID);
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
     * Get the id value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public long getId()
    {
        return id;
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
        output.setProperty(DEPTH,
                           Integer.toString(depth));
        output.setProperty(ID,
                           Long.toString(id));
        output.setProperty(REQUEST_TYPE,
                           requestType.toString());
        output.setProperty(UPDATE_TYPE,
                           updateType.toString());
        output.setProperty(EXCHANGE,
                           exchange);
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
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + depth;
        result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
        result = prime * result + Arrays.hashCode(symbols);
        result = prime * result + ((updateType == null) ? 0 : updateType.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MarketDataRequest other = (MarketDataRequest) obj;
        if (depth != other.depth)
            return false;
        if (exchange == null) {
            if (other.exchange != null)
                return false;
        } else if (!exchange.equals(other.exchange))
            return false;
        if (id != other.id)
            return false;
        if (requestType == null) {
            if (other.requestType != null)
                return false;
        } else if (!requestType.equals(other.requestType))
            return false;
        if (!Arrays.equals(symbols,
                           other.symbols))
            return false;
        if (updateType == null) {
            if (other.updateType != null)
                return false;
        } else if (!updateType.equals(other.updateType))
            return false;
        return true;
    }
    /**
     * Creates a <code>Properties</code> object from the given <code>String</code>.
     *
     * <p>This function assumes that the <code>String</code> consists of a series of key/value pairs separated by
     * the {@link #KEY_VALUE_DELIMITER}.  The <code>String</code> is not allowed to contain the {@link #LINE_SEPARATOR}.
     * 
     * @param inCondensedProperties a <code>String</code> value
     * @return a <code>Properties</code> value
     * @throws IOException if the <code>String</code> cannot be parsed into a <code>Properties</code>
     * @throws IllegalArgumentException if the <code>String</code> contains the {@link #LINE_SEPARATOR}.
     */
    private static Properties propertiesFromString(String inCondensedProperties)
        throws IOException
    {
        if(inCondensedProperties.contains(LINE_SEPARATOR)) {
            throw new IllegalArgumentException(LINE_SEPARATOR_NOT_ALLOWED.getText());
        }
        String expandedProperties = inCondensedProperties.replace(KEY_VALUE_DELIMITER,
                                                                  LINE_SEPARATOR);
        Properties incomingValues = new Properties();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(expandedProperties.getBytes(Charset.forName("UTF-8"))); //$NON-NLS-1$
        try {
            incomingValues.load(inputStream);
        } finally {
            inputStream.close();
        }
        setRequestDefaultsIfNecessary(incomingValues);
        return incomingValues;
    }
    /**
     * Creates a <code>String</code> object from the given <code>Properties</code> object. 
     *
     * <p>This function returns a <code>String</code> containing a series of key/value pairs representing this object.
     * Each key/value pair is separated by the {@link #KEY_VALUE_DELIMITER}.
     *
     * @param inProperties a <code>Properties</code> value
     * @return a <code>String</code> value
     * @throws IOException
     */
    private static String propertiesToString(Properties inProperties)
        throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            inProperties.store(outputStream,
                               null);
        } finally {
            outputStream.close();
        }
        return outputStream.toString(Charset.forName("UTF-8").toString()).replace(LINE_SEPARATOR, //$NON-NLS-1$
                                                                                  KEY_VALUE_DELIMITER);
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
     * Validates the symbols and returns a valid symbol array.
     *
     * @param inSymbols a <code>String...</code> value
     * @return a <code>String[]</code> value
     * @throws IllegalArgumentException if the symbols cannot be parsed
     */
    private static String[] validateSymbols(String... inSymbols)
    {
        if(inSymbols == null) {
            throw new NullPointerException();
        }
        if(inSymbols.length == 0) {
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
     * Validates a <code>String</code> value to make sure it fits within the guidelines for this object.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>String</code> value guaranteed to be valid
     * @throws IllegalArgumentException if the given <code>String</code> is not valid
     */
    private static String validateStringValue(String inValue)
    {
        if(inValue.contains(SYMBOL_DELIMITER) ||
           inValue.contains(KEY_VALUE_DELIMITER) ||
           inValue.contains(LINE_SEPARATOR)) {
            throw new IllegalArgumentException(INVALID_STRING_VALUE.getText(inValue));
        }
        return inValue;
    }
    /**
     * Validates the given <code>long</code> value to make sure it can be used as an identifier for a <code>MarketDataRequest</code>.
     *
     * @param inValue a <code>long</code> value
     * @return a <code>long</code> value guaranteed to be valid
     * @throws IllegalArgumentException if the given <code>long</code> is not valid
     */
    private static long validateID(long inValue)
    {
        if(inValue < 0) {
            throw new IllegalArgumentException(INVALID_ID.getText(inValue));
        }
        return inValue;
    }
    /**
     * Takes the given <code>Properties</code> object and inserts missing keys with their default values if appropriate.
     *
     * @param inProperties a <code>Properties</code> object
     */
    private static void setRequestDefaultsIfNecessary(Properties inProperties)
    {
        if(!inProperties.containsKey(ID)) {
            inProperties.setProperty(ID,
                                     Long.toString(counter.incrementAndGet()));
        }
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
     * the unique identifier for the request (unique for this JVM session)
     */
    private final long id;
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
     * used to generate a stream of identifiers, guaranteed to be unique for this JVM session
     */
    private static final AtomicLong counter = new AtomicLong(0);
    /**
     * identifies the {@link #id} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    private static final String ID = "id"; //$NON-NLS-1$
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
    /**
     * the delimiter used to separate lines in the <code>Properties</code> representation of this object
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
}
