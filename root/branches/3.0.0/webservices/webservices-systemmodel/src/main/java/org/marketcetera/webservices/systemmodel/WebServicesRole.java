package org.marketcetera.webservices.systemmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;
import org.marketcetera.api.dao.Role;

/* $License$ */

/**
 * Provides a web-services appropriate group implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.FIELD)
public class WebServicesRole
{
    /**
     * Create a new WebServicesRole instance.
     */
    public WebServicesRole() {}
    /**
     * Create a new WebServicesRole instance.
     *
     * @param inRole a <code>Role</code> value
     */
    public WebServicesRole(Role inRole)
    {
        id = inRole.getId();
        name = inRole.getName();
        for(User user : inRole.getUsers()) {
            users.add(new WebServicesUser(user));
        }
        for(Permission permission : inRole.getPermissions()) {
            permissions.add(new WebServicesPermission(permission));
        }
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
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Get the users value.
     *
     * @return a <code>List&lt;WebServicesUser&gt;</code> value
     */
    public List<WebServicesUser> getUsers()
    {
        return Collections.unmodifiableList(users);
    }
    /**
     * Get the permissions value.
     *
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    public List<WebServicesPermission> getPermissions()
    {
        return Collections.unmodifiableList(permissions);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesRole [id=").append(id).append(", name=").append(name).append(", users=")
                .append(users).append(", permissions=").append(permissions).append("]");
        return builder.toString();
    }
    /**
     * id value
     */
    private long id;
    /**
     * name value
     */
    private String name;
    /**
     * users value
     */
    private final List<WebServicesUser> users = new ArrayList<WebServicesUser>();
    /**
     * permissions value
     */
    private final List<WebServicesPermission> permissions = new ArrayList<WebServicesPermission>();
}
