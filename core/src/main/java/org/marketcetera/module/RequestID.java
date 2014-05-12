package org.marketcetera.module;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;


/* $License$ */
/**
 * A handle that identifies a request. This handle can be used
 * to pause / cancel the request.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@XmlRootElement(name="requestId")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public final class RequestID
        implements Serializable
{
    /**
     * Creates an instance.
     *
     * @param inValue the string value of this ID.
     */
    public RequestID(String inValue)
    {
        if(inValue == null) {
            throw new NullPointerException();
        }
        mValue = inValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestID requestID = (RequestID) o;

        return mValue == null
                ? requestID.mValue == null
                : mValue.equals(requestID.mValue);

    }

    @Override
    public int hashCode() {
        return (mValue != null ? mValue.hashCode() : 0);
    }

    @Override
    public String toString() {
        return mValue;
    }
    /**
     * Create a new RequestID instance.
     */
    @SuppressWarnings("unused")
    private RequestID()
    {
        mValue = null;
    }
    /**
     * value of the request Id
     */
    @XmlAttribute
    private final String mValue;
    private static final long serialVersionUID = 3515248813584812567L;
}
