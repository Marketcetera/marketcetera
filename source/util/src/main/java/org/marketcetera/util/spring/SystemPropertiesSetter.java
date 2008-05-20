package org.marketcetera.util.spring;

import java.util.Map;
import org.marketcetera.core.ClassVersion;

/**
 * Sets system properties from a map. Its intended usage is to enable
 * setting of system properties from a Spring configuration file.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SystemPropertiesSetter
{

    /**
     * For each entry in the given map, a system property is set
     * wherein the key of each entry is the property name, and the
     * entry value is the property value. System properties whose
     * names are not present as entry keys are not affected. A null
     * entry value removes the associated system property.
     *
     * @param map The map.
     */

    public void setMap(Map<String,String> map)
    {
        for (Map.Entry<String,String> entry:map.entrySet()) {
            String name=entry.getKey();
            String value=entry.getValue();
            if (value==null) {
                System.getProperties().remove(name);
            } else {
                System.setProperty(name,value);
            }
        }
    }
}
