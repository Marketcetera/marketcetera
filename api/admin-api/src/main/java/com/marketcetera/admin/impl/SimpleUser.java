package com.marketcetera.admin.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.persist.NDEntityBase;

import com.marketcetera.admin.User;

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
        implements User
{
    /**
     * Create a new SimpleUser instance.
     *
     * @param inName a <code>String</code> value
     * @param inDescription a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     */
    public SimpleUser(String inName,
                      String inDescription,
                      String inPassword,
                      boolean inIsActive)
    {
        setName(inName);
        setDescription(inDescription);
        hashedPassword = inPassword;
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
