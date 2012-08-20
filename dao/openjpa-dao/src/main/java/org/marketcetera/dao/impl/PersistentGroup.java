package org.marketcetera.dao.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.*;

import org.marketcetera.core.systemmodel.Authority;
import org.marketcetera.core.systemmodel.Group;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Persistent implementation of {@link Group}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentGroup.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="findGroupByName",query="from PersistentGroup s where s.name = :name"),
                 @NamedQuery(name="findAllGroups",query="from PersistentGroup")})
@Table(name="groups", uniqueConstraints = { @UniqueConstraint(columnNames= { "name" } ) } )
public class PersistentGroup
        extends PersistentVersionedObject
        implements Group
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.Group#getName()
     */
    @Override
    @Column(nullable=false,unique=true)
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.Group#getUsers()
     */
    @Override
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentUser.class)
    public Set<User> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers a <code>Set&lt;User&gt;</code> value
     */
    public void setUsers(Set<User> inUsers)
    {
        users = inUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.Group#getAuthorities()
     */
    @Override
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentAuthority.class)
    public Set<Authority> getAuthorities()
    {
        return authorities;
    }
    /**
     * Sets the authorities value.
     *
     * @param inAuthorities a <code>Set&lt;Authority&gt;</code> value
     */
    public void setAuthorities(Set<Authority> inAuthorities)
    {
        authorities = inAuthorities;
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
        if (!(obj instanceof PersistentGroup)) {
            return false;
        }
        PersistentGroup other = (PersistentGroup) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }
    /**
     * authorities granted to this group
     */
    private Set<Authority> authorities = new HashSet<Authority>();
    /**
     * users in this group
     */
    private Set<User> users = new HashSet<User>();
    /**
     * name value
     */
    private volatile String name;
}
