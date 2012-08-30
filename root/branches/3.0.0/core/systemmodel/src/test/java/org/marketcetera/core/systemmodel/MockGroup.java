package org.marketcetera.core.systemmodel;

import java.util.ArrayList;
import java.util.Collection;

import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides a test <code>Group</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockGroup
        extends MockVersionedObject
        implements MutableGroup
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.Group#getUsers()
     */
    @Override
    public Collection<User> getUsers()
    {
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.Group#getAuthorities()
     */
    @Override
    public Collection<Authority> getAuthorities()
    {
        return authorities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableNamedObject#setName(java.lang.String)
     */
    @Override
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableGroup#setUsers(java.util.Collection)
     */
    @Override
    public void setUsers(Collection<User> inUsers)
    {
        users.clear();
        if(inUsers == null) {
            return;
        }
        users.addAll(inUsers);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableGroup#setAuthorities(java.util.Collection)
     */
    @Override
    public void setAuthorities(Collection<Authority> inAuthorities)
    {
        authorities.clear();
        if(inAuthorities == null) {
            return;
        }
        authorities.addAll(inAuthorities);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MockGroup [name=").append(name).append(", users=").append(users).append(", authorities=")
                .append(authorities).append("]");
        return builder.toString();
    }
    /**
     * name value
     */
    private String name;
    /**
     * users value
     */
    private final Collection<User> users = new ArrayList<User>();
    /**
     * authorities value
     */
    private final Collection<Authority> authorities = new ArrayList<Authority>();
}
