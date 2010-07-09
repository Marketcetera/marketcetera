package org.marketcetera.util.ws.wrappers;

import java.util.Map;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A wrapper for marshalling a map value via JAXB.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class MapWrapper<K,V>
    extends BaseWrapper<Map<K,V>>
{

    // CONSTRUCTORS.

    /**
     * Creates a new wrapper for the given map.
     *
     * @param map The map, which may be null.
     */

    public MapWrapper
        (Map<K,V> map)
    {
        super(map);
    }

    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB.
     */

    protected MapWrapper() {}


    // INSTANCE METHODS.

    /**
     * Sets the receiver's map to the given one.
     *
     * @param map The map, which may be null.
     */

    public void setMap
        (Map<K,V> map)
    {
        setValue(map);
    }

    /**
     * Returns the receiver's map.
     *
     * @return The map, which may be null.
     */

    public Map<K,V> getMap()
    {
        return getValue();
    }
}
