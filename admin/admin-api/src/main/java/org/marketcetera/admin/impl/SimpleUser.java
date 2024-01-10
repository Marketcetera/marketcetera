package org.marketcetera.admin.impl;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.marketcetera.admin.MutableUser;
import org.marketcetera.admin.User;
import org.marketcetera.persist.NDEntityBase;
import org.marketcetera.trade.UserID;

/* $License$ */

/**
 * Provides a simple User implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="user")
@XmlAccessorType(XmlAccessType.NONE)
public class SimpleUser
        extends NDEntityBase
        implements MutableUser
{
    /**
     * Create a new SimpleUser instance.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @param inHashedPassword a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     */
    public SimpleUser(String inName,
                      String inDescription,
                      String inHashedPassword,
                      boolean inIsActive)
    {
        setName(inName);
        setDescription(inDescription);
        hashedPassword = inHashedPassword;
        isActive = inIsActive;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.User#isActive()
     */
    @Override
    public boolean isActive()
    {
        return isActive;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.User#getHashedPassword()
     */
    @Override
    public String getHashedPassword()
    {
        return hashedPassword;
    }
    /**
     * Sets the hashedPassword value.
     *
     * @param inHashedPassword a <code>String</code> value
     */
    public void setHashedPassword(String inHashedPassword)
    {
        hashedPassword = inHashedPassword;
    }
    /**
     * Sets the isActive value.
     *
     * @param inIsActive a <code>boolean</code> value
     */
    public void setIsActive(boolean inIsActive)
    {
        isActive = inIsActive;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.User#getUserID()
     */
    @Override
    public UserID getUserID()
    {
        return new UserID(getId());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.MutableUser#setUserId(org.marketcetera.trade.UserID)
     */
    @Override
    public void setUserId(UserID inUserId)
    {
        setId(inUserId.getValue());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleUser [userId=").append(getUserID()).append(", name=").append(getName())
                .append(", description=").append(getDescription()).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(User inO)
    {
        return new CompareToBuilder().append(inO.getName(),getName()).toComparison();
    }
    /**
     * Create a new SimpleUser instance.
     */
    public SimpleUser() {}
    /**
     * password value
     */
    @XmlAttribute(name="password")
    private String hashedPassword;
    /**
     * indicates if the user is active
     */
    @XmlAttribute(name="isActive")
    private boolean isActive;
    private static final long serialVersionUID = -4756325720905227758L;
}
