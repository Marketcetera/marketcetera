package org.marketcetera.core.systemmodel;

import java.util.Collection;

import org.marketcetera.api.security.GrantedPermission;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides a mutable view of a <code>User</code> object.
 *
 * @version $Id$
 * @since $Release$
 */
public interface MutableUser
        extends User
{
    /**
     * Set the permissions value.
     *
     * @param inPermissions a <code>Collection&lt;? extends GrantedPermission&gt;</code> value
     */
    public void setPermissions(Collection<? extends GrantedPermission> inPermissions);
    /**
     * Set the password value. 
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword);
    /**
     * Set the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername);
    /**
     * Set the account non-expired value.
     *
     * @param inIsNonExpired a <code>boolean</code> value
     */
    public void setIsAccountNonExpired(boolean inIsNonExpired);
    /**
     * Set the non-locked value.
     *
     * @param inIsNonLocked a <code>boolean</code> value
     */
    public void setIsAccountNonLocked(boolean inIsNonLocked);
    /**
     * Set the credentials non-expired value.
     *
     * @param inIsNonExpired a <code>boolean</code> value
     */
    public void setIsCredentialsNonExpired(boolean inIsNonExpired);
    /**
     * Set the enabled value.
     *
     * @param inIsEnabled a <code>boolean</code> value
     */
    public void setIsEnabled(boolean inIsEnabled);
}
