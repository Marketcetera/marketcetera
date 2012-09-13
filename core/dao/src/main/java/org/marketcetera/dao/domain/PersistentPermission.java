package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.MutablePermission;
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
        implements MutablePermission
{
    /**
     * Create a new PersistentPermission instance.
     */
    public PersistentPermission() {}
    /**
     * Create a new PersistentPermission instance.
     *
     * @param inPermission
     */
    public PersistentPermission(Permission inPermission)
    {
        methodSet = inPermission.getMethod();
        setName(inPermission.getName());
        setDescription(inPermission.getDescription());
    }
    // --------------------- GETTER / SETTER METHODS ---------------------
    /**
     * Get the permission method value describing the permission attributes assigned to this permission.
     * 
     * @return a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    @XmlElementWrapper(name="method")
    @XmlElement(name="permissionAttribute",type=PermissionAttribute.class)
    public Set<PermissionAttribute> getMethod() {
        return methodSet;
    }
    /**
     * Set the permission method value ascribing permission attributes to this permission.
     *
     * @param inMethod a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    public void setMethod(Set<PermissionAttribute> inMethod) {
        methodSet = inMethod;
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
        builder.append("Permission ").append(getName()).append(" [").append(getId()).append("] ").append(getMethod()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    // ------------------------------ CONSTRUCTORS ------------------------
    // ------------------------------ OTHER METHODS -----------------------
    @SuppressWarnings("unused")
    @PrePersist
    private void prePersist()
    {
        method = PermissionAttribute.getBitFlagValueFor(methodSet);
    }
    @SuppressWarnings("unused")
    @PostPersist
    private void postPersist()
    {
        methodSet = PermissionAttribute.getAttributesFor(method);
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
     * stores bit flag value representing permission attributes assigned to this permission
     */
    @Column(nullable=false)
    private int method;
    /**
     * mirrors the attributes established in method, exists in this object to allow JAXB to set the permissions
     */
    @Transient
    private Set<PermissionAttribute> methodSet = new HashSet<PermissionAttribute>();
}
