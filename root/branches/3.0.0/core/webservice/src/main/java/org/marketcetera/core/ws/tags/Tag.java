package org.marketcetera.core.ws.tags;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlValue;
import org.apache.commons.lang.ObjectUtils;

/**
 * A generic ID tag.
 * 
 * @since 1.0.0
 * @version $Id: Tag.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

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
