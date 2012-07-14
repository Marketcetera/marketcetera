package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;

/* $License$ */
/**
 * Instances of this class uniquely identify an order. If the underlying
 * message protocol is FIX, this value typically translates to
 * <code>ClOrdID</code> or <code>OrigClOrdID</code> fields.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderID implements Serializable {
    /**
     * Creates an instance, given the text value of the OrderID.
     *
     * @param inValue the text value of the OrderID. Cannot be null.
     */
    public OrderID(String inValue) {
        setValue(inValue);
    }

    /**
     * The text value of the orderID.
     *
     * @return the text value of the orderID.
     */
    public String getValue() {
        return mValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderID orderID = (OrderID) o;

        return mValue.equals(orderID.mValue);

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
     * Sets the value of the ID.
     *
     * @param inValue the value of this ID. Cannot be null
     */
    private void setValue(String inValue) {
        if(inValue == null) {
            throw new NullPointerException();
        }
        mValue = inValue;
    }

    /**
     * Defined for JPA.
     */
    OrderID() {
    }

    @XmlValue
    private String mValue;
    private static final long serialVersionUID = 1L;
}
