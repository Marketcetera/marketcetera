package org.marketcetera.marketdata.request.impl;

import javax.xml.bind.annotation.XmlElement;

/* $License$ */

/**
 * 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MapElements
{
    @SuppressWarnings("unused")
    private MapElements()
    {
    } // Required by JAXB

    public MapElements(String key,
                       String value)
    {
        this.key = key;
        this.value = value;
    }
    @XmlElement
    public String key;
    @XmlElement
    public String value;
}
