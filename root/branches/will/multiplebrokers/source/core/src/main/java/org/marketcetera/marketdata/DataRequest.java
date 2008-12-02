package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.INVALID_ID;
import static org.marketcetera.marketdata.Messages.INVALID_REQUEST_TYPE;
import static org.marketcetera.marketdata.Messages.INVALID_STRING_VALUE;
import static org.marketcetera.marketdata.Messages.MISSING_REQUEST_TYPE;
import static org.marketcetera.marketdata.Messages.POORLY_CONSTRUCTED_REQUEST_SUBCLASS;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.Util;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A request for data from a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class DataRequest
{
    /**
     * identifies the type field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String TYPE_KEY = "type"; //$NON-NLS-1$
    /**
     * identifies the {@link #id} field in the <code>Properties</code> and <code>String</code> representations of this object
     */
    public static final String ID_KEY = "id"; //$NON-NLS-1$
    /**
     * Creates a market data request from the given string.
     * 
     * <p>Note that the subcomponents of the request string themselves are allowed to contain neither
     * the {@link #KEY_VALUE_DELIMITER} nor the {@link #LINE_SEPARATOR}.  If a subcomponent contains the
     * <code>KEY_VALUE_DELIMITER</code>, that subcomponent will be <b>truncated</b> at the first occurrence of the delimiter.
     * If a subcomponent contains the <code>LINE_SEPARATOR</code>, an <code>IllegalArgumentException</code> will be thrown.
     *
     * @param inRequestString a <code>String</code> value
     * @return a <code>DataRequest</code> value
     * @throws IllegalArgumentException if <code>inRequestString</code> cannot be parsed properly
     */
    public static final DataRequest newRequestFromString(String inRequestString)
        throws Exception
    {
        // the request string is a list of key/value pairs separated by the KEY_VALUE_DELIMITER
        // transform that string to a properties object for easy access
        Properties request;
        // construct a properties object from the incoming string
        request = Util.propertiesFromString(inRequestString);
        // we don't yet know if this request object has all the pieces it needs
        // first, we need to find the type of data request - that must be there
        Class<? extends DataRequest> type = getClassOfRequestOrFail(request);
        // the type now contains the class of the specific request desired
        try {
            // we're now going in to some odd reflective behavior to find some static methods
            try {
                // this method allows some validation of the properties object, but it's optional
                Method requestValidator = type.getDeclaredMethod("validateAndSetRequestDefaultsIfNecessary", //$NON-NLS-1$
                                                                 Properties.class);
                requestValidator.invoke(type,
                                        request);
            } catch (Exception e) {
                // if the type doesn't have a static validator, call the base one
                validateAndSetRequestDefaultsIfNecessary(request);
            }
            // the properties object now contains all the necessary attributes as declared by the type
            //  itself
            // find the static constructor and invoke it
            Method requestCreator = type.getDeclaredMethod("newRequestFromString", //$NON-NLS-1$
                                                           Properties.class);
            return (DataRequest)requestCreator.invoke(type,
                                                      request);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            if(e.getCause() instanceof IllegalArgumentException) {
                throw ((IllegalArgumentException)e.getCause());
            }
            throw new IllegalArgumentException(POORLY_CONSTRUCTED_REQUEST_SUBCLASS.getText(inRequestString,
                                                                                           e.toString()));
        }
    }
    /**
     * Get the id value.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public final long getId()
    {
        return id;
    }
    /**
     * Gets the identifying type of this request.
     *
     * @return a <code>String</code> value
     */
    public final String getTypeIdentifier()
    {
        return typeIdentifier;
    }
    /**
     * Indicates if the given object is equal to this object in all attributes
     * except the {@link #id}.
     * 
     * <p>Subclasses may override this method to provide a suitable implementation.
     * The contract is that two data requests are equivalent if all attributes are the
     * same except for the <code>id</code>.  The subclass does not need to call this
     * method in its implementation, though that restriction may not always hold
     * for classes that do not directly subclass this one. 
     *
     * @param inOther a <code>DataRequest</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equivalent(DataRequest inOther)
    {
        return inOther != null;
    }
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
        return equivalent(other);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        Properties output = new Properties();
        populatePropertiesWithObjectAttributes(output);
        return Util.propertiesToString(output);
    }
    /**
     * Allows an implementing subclass to provide a hashCode implementation.
     * 
     * <p>A subclass should override this method if it declares any additional
     * member variables that are germane to its function as a data request.
     * 
     * <p>This implementation returns 0, which has no effect on the hashCode
     * implementation of the parent.
     *
     * @return an <code>int</code> value that adheres to the contract of {@link Object#hashCode()}
     */
    protected int doHashCode()
    {
        return 0;
    }
    /**
     * Validates a <code>String</code> value to make sure it fits within the guidelines for this object.
     * 
     * <p>Subclasses may extend this method to add validation their own, but the subclass's
     * implementation must call the parent's implementation as well.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>String</code> value guaranteed to be valid
     * @throws IllegalArgumentException if the given <code>String</code> is not valid
     */
    protected static String validateStringValue(String inValue)
    {
        if(inValue.contains(Util.KEY_VALUE_DELIMITER)) {
            throw new IllegalArgumentException(INVALID_STRING_VALUE.getText(inValue));
        }
        return inValue;
    }
    /**
     * Takes the given <code>Properties</code> object and inserts missing keys with their default values if appropriate.
     * 
     * <b>Subclasses must extend this method if they have attributes that are germane to their function
     * as a market data request.  The purpose of this function is to add key/value pairs to the given
     * <code>Properties</code> object corresponding to the method attributes if these attributes have
     * reasonable default values.  This method should add only key/values that are not already specified
     * in the <code>Properties</code> object.  Lastly, the subclass should throw an <code>IllegalArgumentException</code>
     * if an attribute must be specified in the <code>Properties</code> object that is not present.  The subclass
     * implementation should call this implementation in addition to its own.
     *
     * @param inProperties a <code>Properties</code> object
     * @throws IllegalArgumentException if a required property is missing
     */
    protected static void validateAndSetRequestDefaultsIfNecessary(Properties inProperties)
    {
        if(!inProperties.containsKey(ID_KEY)) {
            inProperties.setProperty(ID_KEY,
                                     Long.toString(counter.incrementAndGet()));
        }
    }
    /**
     * Sets the given <code>Properties</code> object with the market-data attributes of this object.
     * 
     * <p>Subclasses must override this method if they define attributes germane to their function as
     * data request objects.  Values added to the properties object must not contain either 
     * {@link DataRequest#KEY_VALUE_DELIMITER} or {@link #LINE_SEPARATOR}.  This implementation does nothing.
     *
     * @param inProperties a <code>Properties</code> value
     */
    protected void addCurrentAttributesValues(Properties inProperties)
    {
    }
    /**
     * Create a new DataRequest instance.
     *
     * @param inProperties a <code>Properties</code> value 
     */
    protected DataRequest(Properties inProperties)
    {
        if(inProperties == null) {
            inProperties = new Properties();
        }
        validateAndSetRequestDefaultsIfNecessary(inProperties);
        id = validateID(inProperties.getProperty(ID_KEY));
        typeIdentifier = validateTypeIdentifier(inProperties.getProperty(TYPE_KEY));
    }
    /**
     * Populates the given value with default values appropriate to this object if necessary.
     *
     * <p>Subclasses may extend this method to add default values of their own, but the subclass's
     * implementation must call the parent's implementation as well.  
     *
     * @param inProperties a <code>Properties</code> value
     */
    protected final void populatePropertiesWithObjectAttributes(Properties inProperties)
    {
        inProperties.setProperty(ID_KEY,
                                 Long.toString(id));
        inProperties.setProperty(TYPE_KEY,
                                 typeIdentifier);
        addCurrentAttributesValues(inProperties);
    }
    /**
     * Registers the given <code>DataRequest</code> subclass by the given identifier.
     *
     * <p>Subsequent to registering via this method, a subclass may be instantiated via {@link #newRequestFromString(String)}.
     * 
     * @param inIdentifier a <code>String</code> value
     * @param inDataRequestClass a <code>Class&lt;? extends DataRequest&gt;</code> value
     */
    protected final static void registerType(String inIdentifier,
                                             Class<? extends DataRequest> inDataRequestClass)
    {
        initializeTypeCollectionIfNecessary();
        typesByName.put(inIdentifier,
                        inDataRequestClass);
    }
    /**
     * Validates the given value to make sure it can be used as an identifier for a <code>MarketDataRequest</code>.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>long</code> value guaranteed to be valid
     * @throws IllegalArgumentException if the given <code>long</code> is not valid
     */
    private static long validateID(String inValue)
    {
        long idValue;
        try {
            idValue = Long.parseLong(inValue);
        } catch (Exception e) {
            throw new IllegalArgumentException(INVALID_ID.getText(inValue));
        }
        if(idValue < 0) {
            throw new IllegalArgumentException(INVALID_ID.getText(inValue));
        }
        return idValue;
    }
    private static String validateTypeIdentifier(String inValue)
    {
        if(!typesByName.containsKey(inValue)) {
            throw new IllegalArgumentException(INVALID_REQUEST_TYPE.getText(inValue));
        }
        return inValue;
    }
    /**
     * Initializes the data request type collection if necessary.
     *
     * <p>When complete, {@link #typesByName} will be non-null.
     */
    private synchronized static void initializeTypeCollectionIfNecessary()
    {
        typesByName.put(MarketDataRequest.TYPE,
                        MarketDataRequest.class);
        typesByName.put(DerivativeSecurityListRequest.TYPE,
                        DerivativeSecurityListRequest.class);
        typesByName.put(SecurityListRequest.TYPE,
                        SecurityListRequest.class);
    }
    /**
     * Retrieves the class of the data request.
     *
     * @param inProperties a <code>Properties</code> value containing the substance of a data request
     * @return a <code>Class&lt;? extends DataRequest&gt;</code> value containing the class of the request
     * @throws IllegalArgumentException if the given request does not specify the type of request or the specified type is not of a registered type subclass
     */
    private static Class<? extends DataRequest> getClassOfRequestOrFail(Properties inProperties)
    {
        // this method *should not* be called before the subclass registration is complete, but there's no guarantee of this so we have
        //  to make a null check first.
        initializeTypeCollectionIfNecessary();
        String typeName;
        if((typeName = inProperties.getProperty(TYPE_KEY)) == null) {
            throw new IllegalArgumentException(MISSING_REQUEST_TYPE.getText());
        }
        Class<? extends DataRequest> type = typesByName.get(typeName);
        if(type == null) {
            throw new IllegalArgumentException(INVALID_REQUEST_TYPE.getText(typeName));
        }
        return type;
    }
    /**
     * used to generate a stream of identifiers, guaranteed to be unique for this JVM session
     */
    protected static final AtomicLong counter = new AtomicLong(0);
    // this variable is intentionally non-final because the intent is for subclasses to populate it via static constructors.  since there's no guarantee
    //  of order in static constructors, the initialization of this variable is implemented in the method which the subclasses call to register themselves.
    /**
     * contains all known valid data request types by their identifier
     */
    private static final Map<String,Class<? extends DataRequest>> typesByName = new HashMap<String,Class<? extends DataRequest>>();
    /**
     * the unique identifier for the request (unique for this JVM session)
     */
    private final long id;
    private final String typeIdentifier;
}
