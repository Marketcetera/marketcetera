package org.marketcetera.util.ws.wrappers;

import javax.xml.bind.annotation.XmlTransient;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A dual-form wrapper for marshalling a data value via JAXB. One of
 * the two forms is a raw one, namely the standard class used to
 * represent that value in Java; the other is a type that can be
 * marshalled via JAXB. The raw form is null if and only if the
 * marshalled one is null.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class DualWrapper<R,M>
    extends BaseWrapper<R>
{

    // INSTANCE DATA.

    private M mMarshalled;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapper with the given value, in its raw form. It
     * also sets the internal marshalled form to match.
     *
     * @param raw The value in raw form, which may be null.
     */

    public DualWrapper
        (R raw)
    {
        setRaw(raw);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB.
     */

    protected DualWrapper() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's data to the given value, in its raw
     * form. It does not set the internal marshalled form to match.
     *
     * @param raw The value in raw form, which may be null.
     */

    protected void setRawOnly
        (R raw)
    {
        setValue(raw);
    }

    /**
     * Sets the receiver's data to the given value, in its raw
     * form. It also sets the internal marshalled form to match.
     *
     * @param raw The value in raw form, which may be null. If the
     * given value is invalid, it will be replaced by null.
     */

    public void setRaw
        (R raw)
    {
        setRawOnly(raw);
        if (getRaw()==null) {
            setMarshalledOnly(null);
        } else {
            toMarshalled();
            if (getMarshalled()==null) {
                setRawOnly(null);
            }
        }
    }

    /**
     * Returns the receiver's data, in its raw form.
     *
     * @return The data, which may be null.
     */

    @XmlTransient
    public R getRaw()
    {
        return getValue();
    }

    /**
     * Sets the receiver's data to the given value, in its marshalled
     * form. It does not set the internal raw form to match.
     *
     * @param marshalled The value in marshalled form, which may be
     * null.
     */

    protected void setMarshalledOnly
        (M marshalled)
    {
        mMarshalled=marshalled;
    }

    /**
     * Sets the receiver's data to the given value, in its marshalled
     * form. It also sets the internal raw form to match.
     *
     * @param marshalled The value in marshalled form, which may be
     * null. If the given value is invalid, it will be replaced by
     * null.
     */

    public void setMarshalled
        (M marshalled)
    {
        setMarshalledOnly(marshalled);
        if (getMarshalled()==null) {
            setRawOnly(null);
        } else {
            toRaw();
            if (getRaw()==null) {
                setMarshalledOnly(null);
            }
        }
    }

    /**
     * Returns the receiver's data, in its marshalled form.
     *
     * @return The data, which may be null.
     */

    public M getMarshalled()
    {
        return mMarshalled;
    }

    /**
     * Sets the raw form of the receiver's value so that it
     * corresponds to its marshalled form. The subclass implementation
     * can assume that the latter form is non-null.  The subclass may
     * modify both the former and latter forms (for example, if the
     * latter form is invalid). Only {@link #setRawOnly(Object)} and
     * {@link #setMarshalledOnly(Object)} should be used in setting
     * either form, to prevent infinite recursion. The raw form is
     * null if and only if the marshalled one is null; the caller of
     * this method will enforce this invariant, so the subclass
     * implementation can set just the raw form to null.
     */

    protected abstract void toRaw();

    /**
     * Sets the marshalled form of the receiver's value so that it
     * corresponds to its raw form. The subclass implementation can
     * assume that the latter form is non-null. The subclass may
     * modify both the former and latter forms (for example, if the
     * latter form is invalid). Only {@link #setRawOnly(Object)} and
     * {@link #setMarshalledOnly(Object)} should be used in setting
     * either form, to prevent infinite recursion. The raw form is
     * null if and only if the marshalled one is null; the caller of
     * this method will enforce this invariant, so the subclass
     * implementation can set just the marshalled form to null.
     */

    protected abstract void toMarshalled();
}
