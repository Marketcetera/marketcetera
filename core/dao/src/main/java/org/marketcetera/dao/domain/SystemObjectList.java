package org.marketcetera.dao.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import org.marketcetera.api.systemmodel.SystemObject;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="objectList")
@XmlAccessorType(XmlAccessType.NONE)
public class SystemObjectList
{
    public List<SystemObject> getObjects()
    {
        return objects;
    }
    public void addObject(SystemObject inObject)
    {
        objects.add(inObject);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(objects);
        return builder.toString();
    }
    //    @XmlElementWrapper(name="objects")
    @XmlElement(type=PersistentSystemObject.class,name="object")
    private final List<SystemObject> objects = new ArrayList<SystemObject>();
}
