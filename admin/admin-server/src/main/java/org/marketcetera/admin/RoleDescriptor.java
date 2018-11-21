package org.marketcetera.admin;

import java.util.Set;

import org.marketcetera.admin.Role;


/* $License$ */

/**
 * Describes the attributes of a {@link Role}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
public class RoleDescriptor
        extends AbstractNamedDescriptor
{
    /**
     * Get the permissionNames value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getPermissionNames()
    {
        return permissionNames;
    }
    /**
     * Sets the permissionNames value.
     *
     * @param inPermissionNames a <code>Set&lt;String&gt;</code> value
     */
    public void setPermissionNames(Set<String> inPermissionNames)
    {
        permissionNames = inPermissionNames;
    }
    /**
     * Get the usernames value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getUsernames()
    {
        return usernames;
    }
    /**
     * Sets the usernames value.
     *
     * @param inUsernames a <code>Set&lt;String&gt;</code> value
     */
    public void setUsernames(Set<String> inUsernames)
    {
        usernames = inUsernames;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("RoleDescriptor [permissionNames=").append(permissionNames).append(", usernames=")
                .append(usernames).append(", getDescription()=").append(getDescription()).append(", getName()=")
                .append(getName()).append("]");
        return builder.toString();
    }
    /**
     * permission name values
     */
    private Set<String> permissionNames;
    /**
     * user name values
     */
    private Set<String> usernames;
}
