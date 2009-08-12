package org.marketcetera.ors.info;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A generic store of key-value pairs whose contents are checked
 * against certain conditions upon retrieval: implementation.
 *
 * <p>This class is not intended to be thread-safe.</p>
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
class ReadInfoImpl
    implements ReadInfo
{

    // CLASS DATA.

    /**
     * The sentinel value used to represent null values in the
     * receiver's map.
     */

    static final Object NULL_VALUE=
        new Object();

    /**
     * The name separator used by {@link #toString()}.
     */

    static final String TO_STRING_SEPARATOR=
        ":"; //$NON-NLS-1$


    // INSTANCE DATA.

    private final String mName;
    private final Map<String,Object> mMap;


    // CONSTRUCTORS.

    /**
     * Creates a new store with the given name.
     *
     * @param name The store name.
     */

    ReadInfoImpl
        (String name)
    {
        mName=name;
        mMap=new ConcurrentHashMap<String,Object>();
    }


    // CLASS METHODS.

    /**
     * Asserts that the given key is non-null.
     *
     * @param key The key.
     */

    static void assertNonNullKey
        (String key)
    {
        if (key==null) {
            throw new InfoRuntimeException(Messages.NULL_KEY);
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's map.
     *
     * @return The map.
     */

    Map<String,Object> getMap()
    {
        return mMap;
    }


    // ReadInfo.

    @Override
    public String getName()
    {
        return mName;
    }

    @Override
    public String getPath()
    {
        return getName();
    }

    @Override
    public boolean contains
        (String key)
    {
        assertNonNullKey(key);
        boolean result=getMap().containsKey(key);
        if (SLF4JLoggerProxy.isDebugEnabled(this)) {
            SLF4JLoggerProxy.debug
                (this,
                 "Store '{}': key '{}' is {}.", //$NON-NLS-1$
                 getPath(),key,
                 (result?"present": //$NON-NLS-1$
                  "absent")); //$NON-NLS-1$
        }
        return result;
    }

    @Override
    public Object getValue
        (String key)
    {
        assertNonNullKey(key);
        Object value=getMap().get(key);
        if (value==NULL_VALUE) {
            value=null;
        }
        if (SLF4JLoggerProxy.isDebugEnabled(this)) {
            SLF4JLoggerProxy.debug
                (this,
                 "Store '{}': got key '{}' with value '{}'.", //$NON-NLS-1$
                 getPath(),key,value);
        }
        return value;
    }

    @Override
    public Object getValueIfSet
        (String key)
        throws InfoException
    {
        assertNonNullKey(key);
        if (!contains(key)) {
            throw new InfoException
                (new I18NBoundMessage1P(Messages.MISSING_VALUE,key));
        }
        return getValue(key);
    }

    @Override
    public Object getValueIfNonNull
        (String key)
        throws InfoException
    {
        assertNonNullKey(key);
        Object result=getValueIfSet(key);
        if (result==null) {
            throw new InfoException
                (new I18NBoundMessage1P(Messages.NULL_VALUE,key));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getValueIfInstanceOf
        (String key,
         Class<T> cls)
        throws InfoException
    {
        assertNonNullKey(key);
        Object result=getValueIfSet(key);
        if ((result!=null) && (!cls.isInstance(result))) {
            throw new InfoException
                (new I18NBoundMessage3P(Messages.BAD_CLASS_VALUE,
                                        key,ObjectUtils.toString(result),cls));
        }
        return (T)result;
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getValueIfNonNullInstanceOf
        (String key,
         Class<T> cls)
        throws InfoException
    {
        assertNonNullKey(key);
        Object result=getValueIfSet(key);
        if (result==null) {
            throw new InfoException
                (new I18NBoundMessage1P(Messages.NULL_VALUE,key));
        }
        if (!cls.isInstance(result)) {
            throw new InfoException
                (new I18NBoundMessage3P(Messages.BAD_CLASS_VALUE,
                                        key,ObjectUtils.toString(result),cls));
        }
        return (T)result;
    }


    // Object.

	@Override
    public String toString()
    {
        StringBuilder builder=new StringBuilder();
        builder.append(getPath());
        builder.append(TO_STRING_SEPARATOR);
        builder.append(getMap().toString());
        return builder.toString();
    }
}
