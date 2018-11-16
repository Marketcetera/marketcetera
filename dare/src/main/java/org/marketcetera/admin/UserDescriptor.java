package org.marketcetera.admin;

/* $License$ */

/**
 * Describes a user object.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserDescriptor.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public class UserDescriptor
        extends AbstractNamedDescriptor
{
    /**
     * Get the password value.
     *
     * @return a <code>String</code> value
     */
    public String getPassword()
    {
        return password;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /**
     * Get the isActive value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsActive()
    {
        return isActive;
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
     * Get the isSuperuser value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getIsSuperuser()
    {
        return isSuperuser;
    }
    /**
     * Sets the isSuperuser value.
     *
     * @param inIsSuperuser a <code>boolean</code> value
     */
    public void setIsSuperuser(boolean inIsSuperuser)
    {
        isSuperuser = inIsSuperuser;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("UserDescriptor [password=********").append(", isActive=").append(isActive)
                .append(", isSuperuser=").append(isSuperuser).append(", getDescription()=").append(getDescription())
                .append(", getName()=").append(getName()).append("]");
        return builder.toString();
    }
    /**
     * password value
     */
    private String password;
    /**
     * is active value
     */
    private boolean isActive;
    /**
     * is superuser value
     */
    private boolean isSuperuser;
}
