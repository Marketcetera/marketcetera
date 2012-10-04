package org.marketcetera.dao.domain;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;

/* $License$ */

/**
 * Specifies {@link Permission} objects to assign to the implied {@link Role}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@XmlRootElement(name="assignToRole")
@XmlAccessorType(XmlAccessType.NONE)
public class AssignToRole
{
    /**
     * Get the permissions value.
     *
     * @return a <code>List&lt;NameReference&gt;</code> value
     */
    public List<NameReference> getPermissionReferences()
    {
        return permissions;
    }
    /**
     * Get the users value.
     *
     * @return a <code>List&lt;NameReference&gt;</code> value
     */
    public List<NameReference> getUserReferences()
    {
        return users;
    }
    /**
     * Get the roleName value.
     *
     * @return a <code>String</code> value
     */
    public String getRoleName()
    {
        return roleName;
    }
    /**
     * Sets the roleName value.
     *
     * @param inRoleName a <code>String</code> value
     */
    public void setRoleName(String inRoleName)
    {
        roleName = inRoleName;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AssignToRole [roleName=").append(roleName).append(", users=").append(users)
                .append(", permissions=").append(permissions).append("]");
        return builder.toString();
    }
    @XmlElementWrapper(name="permissionsToRole",nillable=true,required=false)
    @XmlElement(name="nameRef",nillable=false,required=true)
    private final List<NameReference> permissions = new ArrayList<NameReference>();
    @XmlElementWrapper(name="usersToRole",nillable=true,required=false)
    @XmlElement(name="nameRef",nillable=false,required=true)
    private final List<NameReference> users = new ArrayList<NameReference>();
    @XmlAttribute(required=true)
    private String roleName;
}
