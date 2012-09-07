package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.api.dao.Permission;

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
        permission = inPermission.getPermission();
        method = inPermission.getMethod();
        name = inPermission.getName();
        id = inPermission.getId();
    }
    /**
     * Get the permission value.
     *
     * @return a <code>String</code> value
     */
    public String getPermission()
    {
        return permission;
    }
    /**
     * Sets the permission value.
     *
     * @param inPermission <code>String</code> value
     */
    public void setPermission(String inPermission)
    {
        permission = inPermission;
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
     * id value
     */
    private long id;
    /**
     * permission value
     */
    private String permission;
    private String method;
    private String name;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
