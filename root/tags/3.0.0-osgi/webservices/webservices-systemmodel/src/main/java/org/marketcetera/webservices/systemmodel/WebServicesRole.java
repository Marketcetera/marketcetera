package org.marketcetera.webservices.systemmodel;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.apache.commons.lang.Validate;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.marketcetera.api.dao.MutableRole;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.User;
import org.marketcetera.webservices.systemmodel.impl.JsonMarshallingProvider;

/* $License$ */

/**
 * Provides a web-services appropriate <code>Role</code> implementation.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="role")
@XmlAccessorType(XmlAccessType.NONE)
@JsonRootName(value="role")
public class WebServicesRole
        extends WebServicesNamedObject
        implements MutableRole
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
        copyAttributes(inRole);
    }
    /**
     * Create a new WebServicesRole instance.
     *
     * @param inStringRepresentation a <code>String</code> valu
     */
    public WebServicesRole(String inStringRepresentation)
    {
        copyAttributes(JsonMarshallingProvider.getInstance().getService().unmarshal(inStringRepresentation,
                                                                                    WebServicesRole.class));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return JsonMarshallingProvider.getInstance().getService().marshal(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableRole#setUsers(java.util.Set)
     */
    @Override
    public void setUsers(Set<User> inUsers)
    {
        users = inUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableRole#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getUsers()
     */
    @Override
    @JsonProperty
    @JsonDeserialize(contentAs=WebServicesUser.class)
    public Set<User> getUsers()
    {
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getPermissions()
     */
    @Override
    @JsonProperty
    @JsonDeserialize(contentAs=WebServicesPermission.class)
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /**
     * Copies the attributes from the given <code>Role</code> object to this object.
     *
     * @param inRole a <code>Role</code> value
     */
    private void copyAttributes(Role inRole)
    {
        Validate.notNull(inRole);
        super.copyAttributes(inRole);
        // perform deep copy on both collections
        users = new HashSet<User>();
        if(inRole.getUsers() != null) {
            for(User user : inRole.getUsers()) {
                if(user instanceof WebServicesUser) {
                    users.add(user);
                } else {
                    users.add(new WebServicesUser(user));
                }
            }
        }
        permissions = new HashSet<Permission>();
        if(inRole.getPermissions() != null) {
            for(Permission permission : inRole.getPermissions()) {
                if(permission instanceof WebServicesPermission) {
                    permissions.add(permission);
                } else {
                    permissions.add(new WebServicesPermission(permission));
                }
            }
        }
    }
    /**
     * users value
     */
    @XmlElementWrapper(name="users")
    @XmlElement(name="user",type=WebServicesUser.class)
    private Set<User> users = new HashSet<User>();
    /**
     * permissions value
     */
    @XmlElementWrapper(name="permissions")
    @XmlElement(name="permission",type=WebServicesPermission.class)
    private Set<Permission> permissions = new HashSet<Permission>();
}
