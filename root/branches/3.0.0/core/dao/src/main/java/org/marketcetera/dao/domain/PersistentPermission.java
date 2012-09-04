package org.marketcetera.dao.domain;

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
@XmlRootElement
public class PersistentPermission
        extends PersistentVersionedObject
        implements Permission
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedPermission#getPermission()
     */
    @Override
    @Column(nullable=false,unique=true)
    public String getPermission()
    {
        return permission;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Transient
    @Override
    public String getName()
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistentPermission ").append(permission).append("[").append(getId()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    /**
     * permission value
     */
    private volatile String permission;
    private static final long serialVersionUID = 1L;
}
