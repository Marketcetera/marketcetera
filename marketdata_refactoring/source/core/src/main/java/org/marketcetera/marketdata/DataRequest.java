package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_ID;
import static org.marketcetera.marketdata.Messages.INVALID_STRING_VALUE;
import static org.marketcetera.marketdata.Messages.INVALID_SYMBOLS;
import static org.marketcetera.marketdata.Messages.LINE_SEPARATOR_NOT_ALLOWED;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public abstract class DataRequest
{
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + doHashCode();
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataRequest other = (DataRequest) obj;
        if (id != other.id)
            return false;
        return doEquals(obj);
    }
    protected abstract int doHashCode();
    protected abstract boolean doEquals(Object obj);
    public boolean equivalent(DataRequest inOther)
    {
        return true;
    }
    /**
     * the delimiter used to distinguish between symbols in the string representation of the symbol collection
     */
    public static final String SYMBOL_DELIMITER = ","; //$NON-NLS-1$
    /**
     * the delimiter used to distinguish key/value pairs in the string representation of the request 
     */
    public static final String KEY_VALUE_DELIMITER = ":"; //$NON-NLS-1$
    /**
     * Get the id value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public long getId()
    {
        return id;
    }
    protected DataRequest(long inId)
    {
        id = validateID(inId);
    }
    protected void populatePropertiesWithObjectAttributes(Properties inProperties)
    {
        inProperties.setProperty(ID,
                                 Long.toString(id));
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
    protected static Properties propertiesFromString(String inCondensedProperties)
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
    protected static String propertiesToString(Properties inProperties)
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
     * Takes the given <code>Properties</code> object and inserts missing keys with their default values if appropriate.
     *
     * @param inProperties a <code>Properties</code> object
     */
    protected static void setRequestDefaultsIfNecessary(Properties inProperties)
    {
        if(!inProperties.containsKey(ID)) {
            inProperties.setProperty(ID,
                                     Long.toString(counter.incrementAndGet()));
        }
    }
    /**
     * Validates the given <code>long</code> value to make sure it can be used as an identifier for a <code>MarketDataRequest</code>.
     *
     * @param inValue a <code>long</code> value
     * @return a <code>long</code> value guaranteed to be valid
     * @throws IllegalArgumentException if the given <code>long</code> is not valid
     */
    protected static long validateID(long inValue)
    {
        if(inValue < 0) {
            throw new IllegalArgumentException(INVALID_ID.getText(inValue));
        }
        return inValue;
    }
    /**
     * Validates the symbols and returns a valid symbol array.
     *
     * @param inSymbols a <code>String...</code> value
     * @return a <code>String[]</code> value
     * @throws IllegalArgumentException if the symbols cannot be parsed
     */
    protected static String[] validateSymbols(String... inSymbols)
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
    protected static String validateStringValue(String inValue)
    {
        if(inValue.contains(SYMBOL_DELIMITER) ||
           inValue.contains(KEY_VALUE_DELIMITER) ||
           inValue.contains(LINE_SEPARATOR)) {
            throw new IllegalArgumentException(INVALID_STRING_VALUE.getText(inValue));
        }
        return inValue;
    }
    /**
     * the delimiter used to separate lines in the <code>Properties</code> representation of this object
     */
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
    /**
     * the unique identifier for the request (unique for this JVM session)
     */
    private final long id;
    /**
     * identifies the {@link #id} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    protected static final String ID = "id"; //$NON-NLS-1$
    /**
     * used to generate a stream of identifiers, guaranteed to be unique for this JVM session
     */
    protected static final AtomicLong counter = new AtomicLong(0);
    
}
