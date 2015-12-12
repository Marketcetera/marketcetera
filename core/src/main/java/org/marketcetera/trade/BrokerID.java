package org.marketcetera.trade;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Instances of this class uniquely identify a FIX Broker to which an order can
 * be sent or from which a response to a request can be received.
 * 
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class BrokerID
        implements Serializable, Comparable<BrokerID>
{

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

        BrokerID that = (BrokerID) o;

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
    public BrokerID(String inValue) {
        if(inValue == null) {
            throw new NullPointerException();
        }
        mValue = inValue;
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(BrokerID inO)
    {
        return new CompareToBuilder().append(mValue,inO.mValue).toComparison();
    }
    /**
     * Creates a new ID. This empty constructor is intended for use
     * by JAXB.
     */

    protected BrokerID() {
        mValue = null;
    }

    @XmlValue
    @Column(name="broker_id")
    private final String mValue;
    private static final long serialVersionUID = 1L;
}
