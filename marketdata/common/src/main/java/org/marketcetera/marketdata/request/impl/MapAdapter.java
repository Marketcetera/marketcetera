package org.marketcetera.marketdata.request.impl;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MapAdapter
        extends XmlAdapter<MapElements[], Map<String,String>>
{
    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public Map<String, String> unmarshal(MapElements[] inV)
            throws Exception
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public MapElements[] marshal(Map<String, String> inV)
            throws Exception
    {
        throw new UnsupportedOperationException(); // TODO
    }
}
