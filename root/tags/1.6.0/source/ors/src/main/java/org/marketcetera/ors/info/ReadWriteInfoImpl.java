package org.marketcetera.ors.info;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A generic store of key-value pairs whose contents are checked
 * against certain conditions during management operations:
 * implementation.
 *
 * <p>This class is not intended to be thread-safe.</p>
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
class ReadWriteInfoImpl
    extends ReadInfoImpl
    implements ReadWriteInfo
{

    // CONSTRUCTORS.

    /**
     * Creates a new store with the given name.
     *
     * @param name The store name.
     */

    ReadWriteInfoImpl
        (String name)
    {
        super(name);
    }


    // ReadWriteInfo.

    @Override
    public void setValue
        (String key,
         Object value)
    {
        assertNonNullKey(key);
        if (value==null) {
            value=NULL_VALUE;
        }
        getMap().put(key,value);
        if (SLF4JLoggerProxy.isDebugEnabled(this)) {
            SLF4JLoggerProxy.debug
                (this,
                 "Store '{}': set key '{}' to value '{}'.", //$NON-NLS-1$
                 getPath(),key,value);
        }
    }

    @Override
    public void setValueIfUnset
        (String key,
         Object value)
        throws InfoException
    {
        assertNonNullKey(key);
        if (contains(key)) {
            throw new InfoException
                (new I18NBoundMessage3P(Messages.VALUE_EXISTS,key,
                                        ObjectUtils.toString(getValue(key)),
                                        ObjectUtils.toString(value)));
        }
        setValue(key,value);
    }

    @Override
    public void removeValue
        (String key)
    {
        assertNonNullKey(key);
        getMap().remove(key);
        if (SLF4JLoggerProxy.isDebugEnabled(this)) {
            SLF4JLoggerProxy.debug
                (this,
                 "Store '{}': removed key '{}'.", //$NON-NLS-1$
                 getPath(),key);
        }
    }

    @Override
    public void removeValueIfSet
        (String key)
        throws InfoException
    {
        assertNonNullKey(key);
        if (!contains(key)) {
            throw new InfoException
                (new I18NBoundMessage1P(Messages.MISSING_VALUE,key));
        }
        removeValue(key);
    }
}
