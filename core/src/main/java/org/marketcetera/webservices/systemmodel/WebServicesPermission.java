package org.marketcetera.webservices.systemmodel;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.marketcetera.api.systemmodel.MutablePermission;
import org.marketcetera.api.systemmodel.Permission;
import org.marketcetera.api.systemmodel.PermissionAttribute;

/* $License$ */

/**
 * Provides a web-services appropriate permission implementation.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="permission")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesPermission
        extends WebServicesNamedObject
        implements MutablePermission
{
    /**
     * Create a new WebServicesPermission instance.
     */
    public WebServicesPermission() {}
    /**
     * Create a new WebServicesPermission instance.
     *
     * @param inPermission a <code>String</code> value
     */
    public WebServicesPermission(Permission inPermission)
    {
        copyAttributes(inPermission);
    }
    /**
     * Get the method value.
     *
     * @return a <code>Set<PermissionAttribute></code> value
     */
    public Set<PermissionAttribute> getMethod()
    {
        return method;
    }
    /**
     * Sets the method value.
     *
     * @param a <code>Set<PermissionAttribute></code> value
     */
    public void setMethod(Set<PermissionAttribute> inMethod)
    {
        method = inMethod;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Permission#getPermission()
     */
    @Override
    public String getPermission()
    {
        return permission;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutablePermission#setPermission(java.lang.String)
     */
    @Override
    public void setPermission(String inPermission)
    {
        permission = inPermission;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesPermission [method=").append(method).append(" permission=").append(permission).append("]");
        return builder.toString();
    }
    /**
     * Copies the attributes from the given object to this one.
     *
     * @param inPermission a <code>Permission</code> value
     */
    private final void copyAttributes(Permission inPermission)
    {
        if(inPermission == null) {
            return;
        }
        super.copyAttributes(inPermission);
        setMethod(inPermission.getMethod());
        setPermission(inPermission.getPermission());
    }
    /**
     * set of permissions assigned to this permission
     */
    @XmlElementWrapper(name="methods")
    @XmlElement(name="method")
    private Set<PermissionAttribute> method = new HashSet<PermissionAttribute>();
    /**
     * permission topic
     */
    @XmlAttribute
    private String permission;
    private static final long serialVersionUID = 1L;
}
