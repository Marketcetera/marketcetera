package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.PermissionAttribute;

/* $License$ */

/**
 * Persistent implementation of {@link org.marketcetera.api.dao.Permission}.
 *
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="PersistentPermission.findByName",query="select s from PersistentPermission s where s.name = :name"),
                 @NamedQuery(name="PersistentPermission.findAll",query="select s from PersistentPermission s")})
@Table(name="permissions", uniqueConstraints = { @UniqueConstraint(columnNames= { "name" } ) } )
@XmlRootElement(name = "permission")
@Access(AccessType.FIELD)
@XmlAccessorType(XmlAccessType.NONE)
public class PersistentPermission
        extends PersistentVersionedObject
        implements Permission
{
    // --------------------- GETTER / SETTER METHODS ---------------------
    /**
     * Get the permission method value describing the permission attributes assigned to this permission.
     * 
     * @return a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    @XmlElementWrapper(name="method")
    @XmlElement(name="permissionAttribute",type=PermissionAttribute.class)
    public Set<PermissionAttribute> getMethod() {
        return method;
    }
    /**
     * Set the permission method value ascribing permission attributes to this permission.
     *
     * @param inMethod a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    public void setMethod(Set<PermissionAttribute> inMethod) {
        method = inMethod;
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
    /**
     * Sets the permission value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getDescription()
     */
    @Override
    @XmlAttribute
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
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
        builder.append("Permission ").append(getName()).append(" [").append(getId()).append("] ").append(method); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    // ------------------------------ FIELDS ------------------------------
    /**
     * permission name value
     */
    @Column(nullable=false,unique=true)
    private String name;
    /**
     * permission description value
     */
    @Column(nullable=true)
    private String description;
    /**
     * stores set of permission attributes assigned to this permission
     */
    @ElementCollection(fetch=FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="permission_methods")
    private Set<PermissionAttribute> method = new HashSet<PermissionAttribute>();
    private static final long serialVersionUID = 1L;
}
