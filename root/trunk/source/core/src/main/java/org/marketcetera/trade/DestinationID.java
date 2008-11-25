package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;

/* $License$ */
/**
 * Instances of this class uniquely identify a FIX Destination / Broker to
 * which an order can be sent or from which a response to a request can
 * be received.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class DestinationID implements Serializable {

    /**
     * Creates a new ID. This empty constructor is intended for use
     * by JAXB.
     */

    protected DestinationID() {}

    /**
     * Sets the text value of the ID. This setter is intended for use
     * by JAXB.
     *
     * @param inValue the string ID value. Cannot be null.
     */
    public void setValue(String inValue) {
        if(inValue == null) {
            throw new NullPointerException();
        }
        mValue=inValue;
    }


    /**
     * Returns the text value of the ID.
     *
     * @return the text value of the ID.
     */
    public String getValue() {
        return mValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestinationID that = (DestinationID) o;

        return mValue.equals(that.mValue);

    }

    @Override
    public int hashCode() {
        return mValue.hashCode();
    }
    @Override
    public String toString() {
        return getValue();
    }
    /**
     * Creates an instance.
     *
     * @param inValue the string ID value. Cannot be null.
     */
    public DestinationID(String inValue) {
        setValue(inValue);
    }

    private String mValue;
    private static final long serialVersionUID = 1L;
}
