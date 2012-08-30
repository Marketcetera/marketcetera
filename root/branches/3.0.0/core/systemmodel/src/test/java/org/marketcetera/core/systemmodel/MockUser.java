package org.marketcetera.core.systemmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.marketcetera.api.security.GrantedAuthority;

/* $License$ */

/**
 * Test implementation of a <code>User</code> object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockUser
        extends MockVersionedObject
        implements MutableUser
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.unmodifiableCollection(authorities);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return isAccountNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return isNonLocked;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return isCredentialsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return isEnabled;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setAuthorities(java.util.Collection)
     */
    @Override
    public void setAuthorities(Collection<? extends GrantedAuthority> inAuthorities)
    {
        authorities.clear();
        if(inAuthorities != null) {
            authorities.addAll(inAuthorities);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setPassword(java.lang.String)
     */
    @Override
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setIsAccountNonExpired(boolean)
     */
    @Override
    public void setIsAccountNonExpired(boolean inIsNonExpired)
    {
        isAccountNonExpired = inIsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setIsAccountNonLocked(boolean)
     */
    @Override
    public void setIsAccountNonLocked(boolean inIsNonLocked)
    {
        isNonLocked = inIsNonLocked;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setIsCredentialsNonExpired(boolean)
     */
    @Override
    public void setIsCredentialsNonExpired(boolean inIsNonExpired)
    {
        isCredentialsNonExpired = inIsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setIsEnabled(boolean)
     */
    @Override
    public void setIsEnabled(boolean inIsEnabled)
    {
        isEnabled = inIsEnabled;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MockUser [authorities=").append(authorities).append(", password=").append(password)
                .append(", username=").append(username).append(", isAccountNonExpired=").append(isAccountNonExpired)
                .append(", isNonLocked=").append(isNonLocked).append(", isCredentialsNonExpired=")
                .append(isCredentialsNonExpired).append(", isEnabled=").append(isEnabled).append("]");
        return builder.toString();
    }
    /**
     * authorities value
     */
    private final Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    /**
     * password value
     */
    private String password;
    /**
     * username value
     */
    private String username;
    /**
     * account non-expired value
     */
    private boolean isAccountNonExpired;
    /**
     * account non-locked value
     */
    private boolean isNonLocked;
    /**
     * account non-expired value
     */
    private boolean isCredentialsNonExpired;
    /**
     * account enabled value
     */
    private boolean isEnabled;
    private static final long serialVersionUID = 1L;
}
