package org.marketcetera.webservices.systemmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.marketcetera.api.security.GrantedPermission;
import org.marketcetera.api.security.GrantedPermission;

/* $License$ */

/**
 * Test implementation of a <code>User</code> object.
 *
 * @version $Id: MockUser.java 16253 2012-09-04 18:35:21Z topping $
 * @since $Release$
 */
public class MockUser
        extends MockVersionedObject
        implements MutableUser
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#getPermissions()
     */
    @Override
    public Collection<? extends GrantedPermission> getPermissions()
    {
        return Collections.unmodifiableCollection(permissions);
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
     * @see org.marketcetera.api.systemmodel.NamedObject#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.MutableUser#setPermissions(java.util.Collection)
     */
    @Override
    public void setPermissions(Collection<? extends GrantedPermission> inPermissions)
    {
        permissions.clear();
        if(inPermissions != null) {
            permissions.addAll(inPermissions);
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
        builder.append("MockUser [permissions=").append(permissions).append(", password=").append(password)
                .append(", username=").append(username).append(", isAccountNonExpired=").append(isAccountNonExpired)
                .append(", isNonLocked=").append(isNonLocked).append(", isCredentialsNonExpired=")
                .append(isCredentialsNonExpired).append(", isEnabled=").append(isEnabled).append("]");
        return builder.toString();
    }
    /**
     * permissions value
     */
    private final Collection<GrantedPermission> permissions = new ArrayList<GrantedPermission>();
    /**
     * password value
     */
    private String password;
    /**
     * username value
     */
    private String username;
    /**
     * description value
     */
    private String description;
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
