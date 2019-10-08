package com.marketcetera.colin.app.security;

import java.util.Collection;
import java.util.Date;

import org.marketcetera.admin.User;
import org.marketcetera.trade.UserID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UIUser
        implements UserDetails,User
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("User [user=").append(user).append(", grantedAuthorities=").append(grantedAuthorities)
                .append("]");
        return builder.toString();
    }
    /**
     * Create a new UiUser instance.
     *
     * @param inUser
     * @param inGrantedAuthorities
     */
    UIUser(User inUser,
           Collection<? extends GrantedAuthority> inGrantedAuthorities)
    {
        user = inUser;
        userId = new UserID(inUser.getId());
        grantedAuthorities = inGrantedAuthorities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryNDEntityBase#getName()
     */
    @Override
    public String getName()
    {
        return user.getName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryNDEntityBase#getDescription()
     */
    @Override
    public String getDescription()
    {
        return user.getDescription();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryEntityBase#getId()
     */
    @Override
    public long getId()
    {
        return user.getId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryEntityBase#getUpdateCount()
     */
    @Override
    public int getUpdateCount()
    {
        return user.getUpdateCount();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SummaryEntityBase#getLastUpdated()
     */
    @Override
    public Date getLastUpdated()
    {
        return user.getLastUpdated();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.User#isActive()
     */
    @Override
    public boolean isActive()
    {
        return user.isActive();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.User#getHashedPassword()
     */
    @Override
    public String getHashedPassword()
    {
        return user.getHashedPassword();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.User#getUserID()
     */
    @Override
    public UserID getUserID()
    {
        return userId;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return grantedAuthorities;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    public String getPassword()
    {
        return user.getHashedPassword();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    @Override
    public String getUsername()
    {
        return user.getName();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return user.isActive();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return user.isActive();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return user.isActive();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return user.isActive();
    }
    private User user;
    private final UserID userId;
    private final Collection<? extends GrantedAuthority> grantedAuthorities;
    private static final long serialVersionUID = -5902434767476106921L;
}
