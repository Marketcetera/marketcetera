package org.marketcetera.util.ws.wrappers;

import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A generic wrapper for marshalling a data value via JAXB. This class
 * provides only a protected getter/setter because the raw form
 * (namely the standard class used to represent that value in Java)
 * may not be suitable for JAXB marshalling; but that raw form is
 * suitable for (and is, in fact used by this class) to override
 * certain standard {@link Object} methods.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class BaseWrapper<T>
{

    // INSTANCE DATA.

    private T mValue;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapper with the given value.
     *
     * @param value The value, which may be null.
     */

    public BaseWrapper
        (T value)
    {
        setValue(value);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB.
     */

    protected BaseWrapper() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's data to the given value.
     *
     * @param value The value, which may be null.
     */

    protected void setValue
        (T value)
    {
        mValue=value;
    }

    /**
     * Returns the receiver's data.
     *
     * @return The data, which may be null.
     */

    protected T getValue()
    {
        return mValue;
    }


    // Object.

    @Override
    public String toString()
    {
        return ArrayUtils.toString(getValue());
    }

    @Override
    public int hashCode()
    {
        return ArrayUtils.hashCode(getValue());
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        BaseWrapper<?> o=(BaseWrapper<?>)other;
        return ArrayUtils.isEquals(getValue(),o.getValue());
    }
}
