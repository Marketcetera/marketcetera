package org.marketcetera.admin.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.admin.Permission;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a simple Permission implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="permission")
@XmlAccessorType(XmlAccessType.NONE)
public class SimplePermission
        extends NDEntityBase
        implements Permission
{
    /**
     * Create a new SimplePermission instance.
     *
     * @param inPermission a <code>Permission</code> value
     */
    public SimplePermission(Permission inPermission)
    {
        setDescription(inPermission.getDescription());
        setId(inPermission.getId());
        setLastUpdated(inPermission.getLastUpdated());
        setName(inPermission.getName());
        setUpdateCount(inPermission.getUpdateCount());
    }
    /**
     * Create a new SimplePermission instance.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     */
    public SimplePermission(String inName,
                            String inDescription)
    {
        setName(inName);
        setDescription(inDescription);
    }
    /**
     * Create a new SimplePermission instance.
     */
    public SimplePermission() {}
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimplePermission [name=").append(getName()).append(", description=").append(getDescription()).append("]");
        return builder.toString();
    }
    private static final long serialVersionUID = 3085570489230392139L;
}
