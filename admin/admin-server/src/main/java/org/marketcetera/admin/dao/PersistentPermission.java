package org.marketcetera.admin.dao;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import org.marketcetera.admin.MutablePermission;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a persistable implementation of <code>Permission</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.1
 */
@Entity(name="Permission")
@Table(name="metc_permissions")
public class PersistentPermission
        extends NDEntityBase
        implements MutablePermission
{
    /**
     * Create a new PersistentPermission instance.
     *
     * @param inPermission a <code>Permission</code> value
     */
    public PersistentPermission(Permission inPermission)
    {
        setName(inPermission.getName());
        setDescription(inPermission.getDescription());
    }
    /**
     * Create a new PersistentPermission instance.
     */
    public PersistentPermission()
    {
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Permission [").append(getName()).append(" - ")
                .append(getDescription()).append("]");
        return builder.toString();
    }
    /**
     * slaved reverse mapping to roles
     */
    @ManyToMany(mappedBy="permissions",targetEntity=PersistentRole.class)
    private Set<Role> roles;
    private static final long serialVersionUID = -660018021740225797L;
}
