package org.marketcetera.dao.domain;

import java.lang.reflect.Field;
import java.util.EnumSet;

import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.api.dao.Permission;

/* $License$ */

/**
 * Persistent implementation of {@link org.marketcetera.api.dao.Permission}.
 *
 * @version $Id: PersistentPermission.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="findUserByName",query="select s from PersistentPermission s where s.permission = :name"),
                 @NamedQuery(name="findAllPermissions",query="select s from PersistentPermission s")})
@Table(name="permissions", uniqueConstraints = { @UniqueConstraint(columnNames= { "permission" } ) } )
@XmlRootElement(name = "permission")
@Access(AccessType.FIELD)
public class PersistentPermission
        extends PersistentVersionedObject
        implements Permission
{
// ------------------------------ FIELDS ------------------------------

    private static final long serialVersionUID = 1L;
    /**
     * permission value
     */
    @Column(nullable=false,unique=true)
    private volatile String permission;
    private volatile String method;
    private volatile String name;
    private EnumSet<Permissions> permissions;

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /* (non-Javadoc)
    * @see org.marketcetera.api.systemmodel.NamedObject#getName()
    */
    @Transient
    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedPermission#getPermission()
     */
    @Override
    public String getPermission()
    {
        return permission;
    }

    /**
     * Sets the permission value.
     *
     * @param inPermission a <code>String</code> value
     */
    public void setPermission(String inPermission)
    {
        permission = inPermission;
    }

// ------------------------ CANONICAL METHODS ------------------------

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersistentPermission)) {
            return false;
        }
        PersistentPermission other = (PersistentPermission) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (getId() ^ (getId() >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Permission ").append(getPermission()).append(" [").append(getId()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }

    public EnumSet<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(EnumSet<Permissions> permissions) {
        this.permissions = permissions;
    }

// -------------------------- OTHER METHODS --------------------------

//    @Column(name = "permissionbits")
//    @Access(AccessType.PROPERTY)
//    protected long getPermissionsBitMask() {
//        // can't even use insanceOf, the class is package protected
//        if ("RegularEnumSet".equals(permissions.getClass().getSimpleName())) {
//            try {
//                Field elementsField = permissions.getClass().getDeclaredField("elements");
//                elementsField.setAccessible(true);
//                return elementsField.getLong(permissions);
//            }
//            catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            throw new UnsupportedOperationException("JumboEnumSet is not supported for now");
//        }
//    }
//
//    protected void setPermissionsBitMask(long l) {
//        if (permissions == null) {
//            permissions = EnumSet.noneOf(Permissions.class);
//        }
//
//        // can't even use insanceOf, the class is package protected
//        if ("RegularEnumSet".equals(permissions.getClass().getSimpleName())) {
//            try {
//                Field elementsField = permissions.getClass().getDeclaredField("elements");
//                elementsField.setAccessible(true);
//                elementsField.setLong(permissions, l);
//            }
//            catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            throw new UnsupportedOperationException("JumboEnumSet is not supported for now");
//        }
//    }

// -------------------------- ENUMERATIONS --------------------------

    public enum Permissions {
        Create,
        Read,
        Update,
        Delete
    }
}
