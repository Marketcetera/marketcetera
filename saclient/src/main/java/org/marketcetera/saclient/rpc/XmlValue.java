package org.marketcetera.saclient.rpc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Wraps an arbitrary marshallable value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@XmlRootElement(name="valueWrapper")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class XmlValue
{
    /**
     * Create a new XmlValue instance.
     *
     * @param inValue
     */
    public XmlValue(Object inValue)
    {
        value = inValue;
    }
    /**
     * Get the value value.
     *
     * @return an <code>Object</code> value
     */
    public Object getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue an <code>Object</code> value
     */
    public void setValue(Object inValue)
    {
        value = inValue;
    }
    /**
     * Create a new XmlValue instance.
     */
    @SuppressWarnings("unused")
    private XmlValue()
    {
        value = null;
    }
    /**
     * value of the wrapped object
     */
    @XmlElement
    private Object value;
}
