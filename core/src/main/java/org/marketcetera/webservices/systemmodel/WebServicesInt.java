package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Provides a web-services capable implementation of an integer value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="int")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesInt
{
    /**
     * Create a new WebServicesInt instance.
     */
    public WebServicesInt() {}
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesInt [value=").append(value).append("]");
        return builder.toString();
    }
    /**
     * Get the value value.
     *
     * @return an <code>int</code> value
     */
    public int getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue an <code>int</code> value
     */
    public void setValue(int inValue)
    {
        value = inValue;
    }
    /**
     * wrapped inner value
     */
    @XmlAttribute
    private int value;
}
