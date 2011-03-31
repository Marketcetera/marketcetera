package org.marketcetera.systemmodel.impl;

import java.util.Properties;

import org.marketcetera.systemmodel.User;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class UserImpl
        implements User
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getActive()
     */
    @Override
    public boolean getActive()
    {
        return active;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getUserData()
     */
    @Override
    public Properties getUserData()
    {
        return userData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getHashedPassword()
     */
    @Override
    public String getHashedPassword()
    {
        return hashedPassword;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getId()
     */
    @Override
    public long getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#setName(java.lang.String)
     */
    @Override
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#setActive(boolean)
     */
    @Override
    public void setActive(boolean inActive)
    {
        active = inActive;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#setUserData(java.util.Properties)
     */
    @Override
    public void setUserData(Properties inProperties)
    {
        userData = inProperties;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#setHashedPassword(java.lang.String)
     */
    @Override
    public void setHashedPassword(String inHashedPassword)
    {
        hashedPassword = inHashedPassword;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("User %s \"%s\" [id=%s, active=%s]",
                             name,
                             description,
                             id,
                             active);
    }
    /**
     * Create a new UserImpl instance.
     *
     * @param inName
     * @param inHashedPassword
     * @param inDescription
     * @param inActive
     * @param inUserData
     */
    UserImpl(long inId,
             String inName,
             String inHashedPassword,
             String inDescription,
             boolean inActive,
             Properties inUserData)
    {
        id = inId;
        name = inName;
        description = inDescription;
        active = inActive;
        hashedPassword = inHashedPassword;
        userData = inUserData;
    }
    /**
     * 
     */
    private volatile long id;
    /**
     * 
     */
    private volatile String name;
    /**
     * 
     */
    private volatile String description;
    /**
     * 
     */
    private volatile boolean active;
    /**
     * 
     */
    private volatile String hashedPassword;
    /**
     * 
     */
    private volatile Properties userData;
}
