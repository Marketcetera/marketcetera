package org.marketcetera.dao.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.api.dao.Authority;

/* $License$ */

/**
 * Persistent implementation of {@link org.marketcetera.api.dao.Authority}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentAuthority.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="findUserByName",query="select s from PersistentAuthority s where s.authority = :name"),
                 @NamedQuery(name="findAllAuthorities",query="select s from PersistentAuthority s")})
@Table(name="authorities", uniqueConstraints = { @UniqueConstraint(columnNames= { "authority" } ) } )
@XmlRootElement
public class PersistentAuthority
        extends PersistentVersionedObject
        implements Authority
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    @Column(nullable=false,unique=true)
    public String getAuthority()
    {
        return authority;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Transient
    @Override
    public String getName()
    {
        return authority;
    }
    /**
     * Sets the authority value.
     *
     * @param inAuthority a <code>String</code> value
     */
    public void setAuthority(String inAuthority)
    {
        authority = inAuthority;
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
        if (!(obj instanceof PersistentAuthority)) {
            return false;
        }
        PersistentAuthority other = (PersistentAuthority) obj;
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
        builder.append("PersistentAuthority ").append(authority).append("[").append(getId()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return builder.toString();
    }
    /**
     * authority value
     */
    private volatile String authority;
    private static final long serialVersionUID = 1L;
}
