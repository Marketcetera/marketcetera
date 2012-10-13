package org.marketcetera.webservices.systemmodel;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.codehaus.jackson.map.annotate.JsonRootName;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.AssignToRole;
import org.marketcetera.api.security.MutableAssignToRole;
import org.marketcetera.webservices.systemmodel.impl.JsonMarshallingProvider;

/* $License$ */

/**
 * Specifies {@link Permission} objects to assign to the implied {@link Role}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="assignToRole")
@XmlAccessorType(XmlAccessType.NONE)
@JsonRootName(value="assignToRole")
public class WebServicesAssignToRole
        implements MutableAssignToRole, Serializable
{
    /**
     * Create a new WebServicesAssignToRole instance.
     */
    public WebServicesAssignToRole() {}
    /**
     * Create a new WebServicesAssignToRole instance.
     *
     * @param inData an <code>AssignToRole</code> value
     */
    public WebServicesAssignToRole(AssignToRole inData)
    {
        copyAttributes(inData);
    }
    /**
     * Create a new WebServicesAssignToRole instance.
     *
     * @param inData a <code>String</code> value
     */
    public WebServicesAssignToRole(String inData)
    {
        copyAttributes((AssignToRole)JsonMarshallingProvider.getInstance().getService().unmarshal(inData,
                                                                                                  WebServicesAssignToRole.class));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if(JsonMarshallingProvider.getInstance() != null &&
           JsonMarshallingProvider.getInstance().getService() != null) {
            return JsonMarshallingProvider.getInstance().getService().marshal(this);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesAssignToRole [role=").append(role).append(", users=").append(users)
                .append(", permissions=").append(permissions).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getRole()
     */
    @Override
    public String getRole()
    {
        return role;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getUsers()
     */
    @Override
    public Set<String> getUsers()
    {
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getPermissions()
     */
    @Override
    public Set<String> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getMutableView()
     */
    @Override
    public MutableAssignToRole getMutableView()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableAssignToRole#setRole(java.lang.String)
     */
    @Override
    public void setRole(String inRoleName)
    {
        role = inRoleName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableAssignToRole#setUsers(java.util.Set)
     */
    @Override
    public void setUsers(Set<String> inUsers)
    {
        users = inUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableAssignToRole#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<String> inPermissions)
    {
        permissions = inPermissions;
    }
    private void copyAttributes(AssignToRole inData)
    {
        if(inData == null) {
            return;
        }
        setUsers(inData.getUsers());
        setPermissions(inData.getPermissions());
        setRole(inData.getRole());
    }
    /**
     * role name to which to assign users and permissions
     */
    @XmlAttribute
    private String role;
    /**
     * user names to assign to the role
     */
    @XmlElementWrapper(name="users")
    @XmlElement(name="user")
    private Set<String> users;
    /**
     * permission names to assign to the role
     */
    @XmlElementWrapper(name="permissions")
    @XmlElement(name="permission")
    private Set<String> permissions;
    private static final long serialVersionUID = 1L;
}
