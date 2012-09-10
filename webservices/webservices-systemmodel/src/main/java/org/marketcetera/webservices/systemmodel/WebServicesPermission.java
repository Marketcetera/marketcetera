package org.marketcetera.webservices.systemmodel;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionAttribute;

/* $License$ */

/**
 * Provides a web-services appropriate permission implementation.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name = "permissions")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebServicesPermission
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
        name = inPermission.getName();
        description = inPermission.getDescription();
        method = inPermission.getMethod();
        id = inPermission.getId();
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId()
    {
        return id;
    }
    /**
     * Sets the id value.
     *
     * @param inId <code>long</code> value
     */
    public void setId(long inId)
    {
        id = inId;
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
    /**
     * id value
     */
    private long id;
    /**
     * permission value
     */
    private String name;
    private String description;
    private Set<PermissionAttribute> method;
}
