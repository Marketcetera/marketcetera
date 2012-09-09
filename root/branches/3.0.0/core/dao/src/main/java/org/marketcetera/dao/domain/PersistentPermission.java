package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.Permission;

/* $License$ */

/**
 * Persistent implementation of {@link org.marketcetera.api.dao.Permission}.
 *
 * @version $Id: PersistentPermission.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@Entity
@NamedQueries( { @NamedQuery(name="findUserByName",query="select s from PersistentPermission s where s.permission = :name"),
                 @NamedQuery(name="findAllPermissions",query="select s from PersistentPermission s")})
@Table(name="permissions", uniqueConstraints = { @UniqueConstraint(columnNames= { "permission" } ) } )
@XmlRootElement(name = "permission")
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
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
    private String permission;
    private String method;
    private String name;
    private final Set<Permissions> permissions = new HashSet<Permissions>();

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    @XmlAttribute
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    /* (non-Javadoc)
    * @see org.marketcetera.api.systemmodel.NamedObject#getName()
    */
    @Override
    @XmlAttribute
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
    @XmlAttribute
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
        builder.append("Permission ").append(getName()).append(" [").append(getId()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    @XmlElementWrapper(name="permissionsAttributes")
    @XmlElement(name="permissionAttribute",type=PersistentPermission.Permissions.class)
    public Set<Permissions> getPermissions() {
        return permissions;
    }
    public void setPermissions(Set<Permissions> inPermissions) {
        permissions.clear();
        permissions.addAll(inPermissions);
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

    @XmlRootElement(name = "permissionAttribute")
    public enum Permissions {
        Create,
        Read,
        Update,
        Delete
    }
}
