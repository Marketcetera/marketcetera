package org.marketcetera.util.ws.tags;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlValue;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A generic ID tag.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class Tag
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // INSTANCE DATA.

    private String mValue;


    // CONSTRUCTORS.

    /**
     * Creates a new tag with the given ID value.
     *
     * @param value The ID value.
     */

    protected Tag
        (String value)
    {
        setValue(value);
    }

    /**
     * Creates a new tag. This empty constructor is intended for use
     * by JAXB.
     */

    protected Tag() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's ID value. This setter is intended for use
     * by JAXB.
     *
     * @param value The ID value.
     */

    public void setValue
        (String value)
    {
        mValue=value;
    }

    /**
     * Returns the receiver's ID value.
     *
     * @return The ID value.
     */

    @XmlValue
    public String getValue()
    {
        return mValue;
    }


    // Object.

    @Override
    public String toString()
    {
        return getValue();
    }

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(getValue());
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
        Tag o=(Tag)other;
        return ObjectUtils.equals(getValue(),o.getValue());
    }
}
