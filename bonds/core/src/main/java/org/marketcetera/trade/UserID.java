package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlValue;

/* $License$ */
/**
 * Instances of this class uniquely identify a user (trader).
 * 
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class UserID implements Serializable {

    /**
     * Returns the long value of the ID.
     *
     * @return the long value of the ID.
     */
    public long getValue() {
        return mValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserID that = (UserID) o;

        return (mValue==that.mValue);

    }

    @Override
    public int hashCode() {
        return (int)mValue;
    }
    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
    /**
     * Creates an instance.
     *
     * @param inValue the long ID value.
     */
    public UserID(long inValue) {
        mValue = inValue;
    }

    /**
     * Creates a new ID. This empty constructor is intended for use
     * by JAXB.
     */

    protected UserID() {
        mValue = 0;
    }

    @XmlValue
    private final long mValue;
    private static final long serialVersionUID = 1L;
}
