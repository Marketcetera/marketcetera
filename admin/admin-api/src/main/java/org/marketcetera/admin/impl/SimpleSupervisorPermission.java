package org.marketcetera.admin.impl;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.SupervisorPermission;
import org.marketcetera.admin.User;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a POJO {@link SupervisorPermission} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="supervisorPermission")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleSupervisorPermission
        extends NDEntityBase
        implements SupervisorPermission
{
    /**
     * Create a new SimpleSupervisorPermission instance.
     *
     * @param inSupervisorPermission
     */
    public SimpleSupervisorPermission(SupervisorPermission inSupervisorPermission)
    {
        setDescription(inSupervisorPermission.getDescription());
        setId(inSupervisorPermission.getId());
        setLastUpdated(inSupervisorPermission.getLastUpdated());
        setName(inSupervisorPermission.getName());
        setPermissions(inSupervisorPermission.getPermissions());
        setSubjects(inSupervisorPermission.getSubjects());
        setSupervisor(inSupervisorPermission.getSupervisor());
        setUpdateCount(inSupervisorPermission.getUpdateCount());
    }
    /**
     * Create a new SimpleSupervisorPermission instance.
     */
    public SimpleSupervisorPermission() {}
    /* (non-Javadoc)
     * @see org.marketcetera.admin.SupervisorPermission#getSupervisor()
     */
    @Override
    public User getSupervisor()
    {
        return supervisor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.SupervisorPermission#setSupervisor(org.marketcetera.admin.User)
     */
    @Override
    public void setSupervisor(User inSupervisor)
    {
        supervisor = inSupervisor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.SupervisorPermission#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.SupervisorPermission#getSubjects()
     */
    @Override
    public Set<User> getSubjects()
    {
        return subjects;
    }
    /**
     * Sets the permissions value.
     *
     * @param inPermissions a <code>Set&lt;Permission&gt;</code> value
     */
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /**
     * Sets the subjects value.
     *
     * @param inSubjects a <code>Set&lt;User&gt;</code> value
     */
    public void setSubjects(Set<User> inSubjects)
    {
        subjects = inSubjects;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleSupervisorPermission [name=").append(getName()).append(", description=").append(getDescription())
                .append(", supervisor=").append(supervisor).append(", permissions=")
                .append(permissions).append(", subjects=").append(subjects).append("]");
        return builder.toString();
    }
    /**
     * supervisor value
     */
    private User supervisor;
    /**
     * permission values
     */
    private Set<Permission> permissions = new HashSet<>();
    /**
     * subject values
     */
    private Set<User> subjects = new HashSet<>();
    private static final long serialVersionUID = -6176621491282881549L;
}
