package org.marketcetera.persistence.hibernate;

import java.util.Properties;

import javax.persistence.*;

import org.marketcetera.systemmodel.User;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Entity
@Table(name="users",uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
@ClassVersion("$Id$")
class PersistentUser
        implements User
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getName()
     */
    @Column(nullable=false,length=255,unique=true)
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getDescription()
     */
    @Override
    @Column(length=255)
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.User#getActive()
     */
    @Column(nullable=false)
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
    @Column(nullable=false,length=255)
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
    public void setUserData(Properties inUserData)
    {
        userData = inUserData;
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
        return String.format("PersistentUser [id=%s, name=%s, description=%s, active=%s, userData=%s]",
                             id,
                             name,
                             description,
                             active,
                             userData);
    }
    /**
     * Create a new PersistentUser instance.
     *
     * @param inUser
     */
    PersistentUser(User inUser)
    {
        id = inUser.getId();
        name = inUser.getName();
        description = inUser.getDescription();
        active = inUser.getActive();
        hashedPassword = inUser.getHashedPassword();
        userData = inUser.getUserData();
    }
    /**
     * Create a new PersistentUser instance.
     */
    @SuppressWarnings("unused")
    private PersistentUser()
    {
    }
    /**
     * 
     */
    @Id
    private long id;
    /**
     * 
     */
    private String name;
    /**
     * 
     */
    private String description;
    /**
     * 
     */
    private boolean active;
    /**
     * 
     */
    private String hashedPassword;
    /**
     * 
     */
    private Properties userData;
}
