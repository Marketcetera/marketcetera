package org.marketcetera.admin.dao;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import org.marketcetera.admin.MutableRole;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a persistable implementation of <code>Role</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
@Entity(name="Role")
@Table(name="metc_roles")
public class PersistentRole
        extends NDEntityBase
        implements MutableRole
{
    /**
     * Create a new PersistentRole instance.
     *
     * @param inRole a <code>Role</code> value
     */
    public PersistentRole(Role inRole)
    {
        setName(inRole.getName());
        setDescription(inRole.getDescription());
    }
    /**
     * Create a new PersistentRole instance.
     */
    public PersistentRole()
    {
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.Role#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.tiaacref.systemmodel.Role#getSubjects()
     */
    @Override
    public Set<User> getSubjects()
    {
        return subjects;
    }
    /**
     * subjects assigned to this role
     */
    @JoinTable(name="metc_roles_users")
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentUser.class)
    private Set<User> subjects = new HashSet<>();
    /**
     * permissions granted to this role
     */
    @JoinTable(name="metc_roles_permissions")
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentPermission.class,cascade={ CascadeType.ALL })
    private Set<Permission> permissions = new HashSet<>();
    private static final long serialVersionUID = -562451840955411836L;
}
