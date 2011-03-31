package org.marketcetera.systemmodel;

import java.util.Properties;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface User
{
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName();
    /**
     * 
     *
     *
     * @param inName
     */
    public void setName(String inName);
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription();
    /**
     * 
     *
     *
     * @param inDescription
     */
    public void setDescription(String inDescription);
    /**
     * Get the active value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getActive();
    /**
     * 
     *
     *
     * @param inActive
     */
    public void setActive(boolean inActive);
    /**
     * Get the userData value.
     *
     * @return a <code>Properties</code> value
     */
    public Properties getUserData();
    /**
     * 
     *
     *
     * @param inProperties
     */
    public void setUserData(Properties inProperties);
    /**
     * Get the hashedPassword value.
     *
     * @return a <code>String</code> value
     */
    public String getHashedPassword();
    /**
     * 
     *
     *
     * @param inHashedPassword
     */
    public void setHashedPassword(String inHashedPassword);
    /**
     * Get the id value.
     *
     * @return a <code>long</code> value
     */
    public long getId();
}