package org.marketcetera.trade;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlValue;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.marketcetera.util.misc.ClassVersion;

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
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class OrderID
        implements Serializable,Comparable<OrderID>
{
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(OrderID inO)
    {
        return new CompareToBuilder().append(inO.mValue,mValue).toComparison();
    }
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
    @Column(name="order_id")
    private String mValue;
    private static final long serialVersionUID = 1L;
}
