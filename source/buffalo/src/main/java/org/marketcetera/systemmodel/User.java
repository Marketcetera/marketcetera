package org.marketcetera.systemmodel;

import java.io.Serializable;
import java.util.Properties;

import javax.persistence.*;

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
public class User
        implements Serializable
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    @Column(nullable=false,length=255,unique=true)
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    @Column(length=255)
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * Get the active value.
     *
     * @return a <code>boolean</code> value
     */
    @Column(nullable=false)
    public boolean getActive()
    {
        return active;
    }
    /**
     * Sets the active value.
     *
     * @param a <code>boolean</code> value
     */
    public void setActive(boolean inActive)
    {
        active = inActive;
    }
    /**
     * Get the userData value.
     *
     * @return a <code>Properties</code> value
     */
    public Properties getUserData()
    {
        return userData;
    }
    /**
     * Sets the userData value.
     *
     * @param a <code>Properties</code> value
     */
    public void setUserData(Properties inUserData)
    {
        userData = inUserData;
    }
    /**
     * Get the hashedPassword value.
     *
     * @return a <code>String</code> value
     */
    @Column(nullable=false,length=255)
    public String getHashedPassword()
    {
        return hashedPassword;
    }
    /**
     * Sets the hashedPassword value.
     *
     * @param a <code>String</code> value
     */
    public void setHashedPassword(String inHashedPassword)
    {
        hashedPassword = inHashedPassword;
    }
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId()
    {
        return id;
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
     * 
     */
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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
    private static final long serialVersionUID = 1L;
}
