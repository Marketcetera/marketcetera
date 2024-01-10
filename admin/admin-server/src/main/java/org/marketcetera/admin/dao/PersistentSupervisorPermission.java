package org.marketcetera.admin.dao;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.marketcetera.admin.Permission;
import org.marketcetera.admin.SupervisorPermission;
import org.marketcetera.admin.User;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a persistent {@link SupervisorPermission} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity(name="SupervisorPermission")
@Table(name="metc_supervisor_permissions")
public class PersistentSupervisorPermission
        extends NDEntityBase
        implements SupervisorPermission
{
    /**
     * Create a new PersistentSupervisorPermission instance.
     */
    public PersistentSupervisorPermission()
    {
    }
    /**
     * Create a new PersistentSupervisorPermission instance.
     *
     * @param inSupervisorPermission a <code>SupervisorPermission</code> value
     */
    public PersistentSupervisorPermission(SupervisorPermission inSupervisorPermission)
    {
        setName(inSupervisorPermission.getName());
        setDescription(inSupervisorPermission.getDescription());
        setSupervisor(inSupervisorPermission.getSupervisor());
        getPermissions().addAll(inSupervisorPermission.getPermissions());
        getSubjects().addAll(inSupervisorPermission.getSubjects());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.SupervisorPermission#getSupervisor()
     */
    @Override
    public User getSupervisor()
    {
        return supervisor;
    }
    /**
     * Sets the supervisor value.
     *
     * @param inSupervisor a <code>User</code> value
     */
    public void setSupervisor(User inSupervisor)
    {
        supervisor = inSupervisor;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.SupervisorPermission#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.SupervisorPermission#getSubjects()
     */
    @Override
    public Set<User> getSubjects()
    {
        return subjects;
    }
    /**
     * supervisor value
     */
    @JoinColumn(name="user_id")
    @ManyToOne(targetEntity=PersistentUser.class,optional=false)
    private User supervisor; 
    /**
     * subjects assigned to this supervisor
     */
    @JoinTable(name="metc_supervisor_permissions_users")
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentUser.class)
    private Set<User> subjects = new HashSet<>();
    /**
     * permissions granted to this role
     */
    @JoinTable(name="metc_supervisor_permissions_permissions")
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentPermission.class,cascade={ CascadeType.ALL })
    private Set<Permission> permissions = new HashSet<>();
    private static final long serialVersionUID = 3776647081736905130L;
}
