package org.marketcetera.webservices.systemmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.Validate;
import org.marketcetera.api.systemmodel.MutableUser;
import org.marketcetera.core.security.User;

/* $License$ */

/**
 * Provides a web-services appropriate user implementation.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesUser
        extends WebServicesNamedObject
        implements MutableUser
{
    /**
     * Create a new WebServicesUser instance.
     */
    public WebServicesUser() {}
    /**
     * Create a new WebServicesUser instance.
     *
     * @param inUser a <code>User</code> value
     */
    public WebServicesUser(User inUser)
    {
        copyAttributes(inUser);
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
        return getName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return !enabled;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return !locked;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return !credentialsExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.User#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setPassword(java.lang.String)
     */
    @Override
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setUsername(java.lang.String)
     */
    @Override
    public void setUsername(String inUsername)
    {
        setName(inUsername);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsAccountNonExpired(boolean)
     */
    @Override
    public void setIsAccountNonExpired(boolean inIsNonExpired)
    {
        enabled = ! inIsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsAccountNonLocked(boolean)
     */
    @Override
    public void setIsAccountNonLocked(boolean inIsNonLocked)
    {
        locked = ! inIsNonLocked;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsCredentialsNonExpired(boolean)
     */
    @Override
    public void setIsCredentialsNonExpired(boolean inIsNonExpired)
    {
        credentialsExpired = !inIsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsEnabled(boolean)
     */
    @Override
    public void setIsEnabled(boolean inIsEnabled)
    {
        enabled = inIsEnabled;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesUser [password=").append(password).append(", enabled=").append(enabled)
                .append(", locked=").append(locked).append(", credentialsExpired=").append(credentialsExpired)
                .append("]");
        return builder.toString();
    }
    /**
     * Copies attributes from the given object to this object.
     *
     * @param inUser a <code>User</code> value
     */
    private void copyAttributes(User inUser)
    {
        Validate.notNull(inUser);
        super.copyAttributes(inUser);
        setPassword(inUser.getPassword());
        setIsAccountNonExpired(inUser.isAccountNonExpired());
        setIsAccountNonLocked(inUser.isAccountNonLocked());
        setIsCredentialsNonExpired(inUser.isCredentialsNonExpired());
        setIsEnabled(inUser.isEnabled());
    }
    /**
     * user password value
     */
    @XmlAttribute
    private String password;
    /**
     * indicates if the account is enabled
     */
    @XmlAttribute
    private boolean enabled;
    /**
     * indicates if the account is locked
     */
    @XmlAttribute
    private boolean locked;
    /**
     * indicates if the credentials have expired
     */
    @XmlAttribute
    private boolean credentialsExpired;
    private static final long serialVersionUID = 1L;
}
