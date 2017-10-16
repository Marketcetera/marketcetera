package org.marketcetera.marketdata.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Adapts a <code>Map&lt;String,String&gt;</code> to and from XML.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MapElements.java 17251 2016-09-08 23:18:29Z colin $
 * @since 2.4.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id: MapElements.java 17251 2016-09-08 23:18:29Z colin $")
public class MapElements
{
    /**
     * Create a new MapElements instance.
     *
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     */
    public MapElements(String inKey,
                       String inValue)
    {
        key = inKey;
        value = inValue;
    }
    /**
     * Get the key value.
     *
     * @return a <code>String</code> value
     */
    public String getKey()
    {
        return key;
    }
    /**
     * Sets the key value.
     *
     * @param inKey a <code>String</code> value
     */
    public void setKey(String inKey)
    {
        key = inKey;
    }
    /**
     * Get the value value.
     *
     * @return a <code>String</code> value
     */
    public String getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue a <code>String</code> value
     */
    public void setValue(String inValue)
    {
        value = inValue;
    }
    /**
     * Create a new MapElements instance.
     */
    @SuppressWarnings("unused")
    private MapElements() {}
    /**
     * element key
     */
    @XmlAttribute
    private String key;
    /**
     * element value
     */
    @XmlAttribute
    private String value;
}
