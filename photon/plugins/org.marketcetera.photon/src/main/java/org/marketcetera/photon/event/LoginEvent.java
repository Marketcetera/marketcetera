package org.marketcetera.photon.event;

/* $License$ */

/**
 * Indicates that a user has successfully logged in.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LoginEvent
{
    /**
     * Return the user name value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Set the user name value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("LoginEvent [username=").append(username).append("]");
        return builder.toString();
    }
    /**
     * Create a new LoginEvent instance.
     *
     * @param inUsername a <code>String</code> value
     */
    public LoginEvent(String inUsername)
    {
        username = inUsername;
    }
    /**
     * Create a new LoginEvent instance.
     */
    public LoginEvent() {}
    /**
     * username value
     */
    private String username;
}
