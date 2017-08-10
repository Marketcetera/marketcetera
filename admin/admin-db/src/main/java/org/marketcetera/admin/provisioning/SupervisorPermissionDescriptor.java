package org.marketcetera.admin.provisioning;

import java.util.Set;

import org.marketcetera.admin.SupervisorPermission;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Describes the attributes of a {@link SupervisorPermission}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SupervisorPermissionDescriptor
        extends AbstractNamedDescriptor
{
    /**
     * Get the supervisorUserName value.
     *
     * @return a <code>String</code> value
     */
    public String getSupervisorUsername()
    {
        return supervisorUsername;
    }
    /**
     * Sets the supervisorUserName value.
     *
     * @param inSupervisorUsername a <code>String</code> value
     */
    public void setSupervisorUsername(String inSupervisorUsername)
    {
        supervisorUsername = inSupervisorUsername;
    }
    /**
     * Get the permissionName value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getPermissionNames()
    {
        return permissionNames;
    }
    /**
     * Sets the permissionName value.
     *
     * @param inPermissionNames a <code>Set&lt;String&gt;</code> value
     */
    public void setPermissionNames(Set<String> inPermissionNames)
    {
        permissionNames = inPermissionNames;
    }
    /**
     * Get the subjectUsernames value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getSubjectUsernames()
    {
        return subjectUsernames;
    }
    /**
     * Sets the subjectUsernames value.
     *
     * @param inSubjectUsernames a <code>Set&lt;String&gt;</code> value
     */
    public void setSubjectUsernames(Set<String> inSubjectUsernames)
    {
        subjectUsernames = inSubjectUsernames;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SupervisorPermissionDescriptor [supervisorUserName=").append(supervisorUsername)
                .append(", permissionName=").append(permissionNames).append(", subjectUsernames=")
                .append(subjectUsernames).append("]");
        return builder.toString();
    }
    /**
     * name of the supervisor to whom permission is granted
     */
    private String supervisorUsername;
    /**
     * names of the permissions granted to the supervisor
     */
    private Set<String> permissionNames = Sets.newHashSet();
    /**
     * names of the subjects over whom permissions are granted to the supervisor
     */
    private Set<String> subjectUsernames = Sets.newHashSet();
}
