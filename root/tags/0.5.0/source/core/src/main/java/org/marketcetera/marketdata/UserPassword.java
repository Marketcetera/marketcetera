package org.marketcetera.marketdata;

/**
 * Encapsulation of user and password data.
 * 
 * <p>This class should be embedded as a private <em>mix-in</em> member
 * for security reasons.  Essentially, this class should be used as
 * the inner half of a <em>decorator</em> pattern.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class UserPassword
{
    /**
     * the username
     */
    private final String mUsername;
    /**
     * the password
     */
    private final String mPassword;
    /**
     * Create a new UserPassword instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>Password</code> value
     */
    public UserPassword(String inUsername,
                        String inPassword)
    {
        mUsername = inUsername;
        mPassword = inPassword;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IUserPassword#getPassword()
     */
    public String getPassword()
    {
        return mPassword == null ? null : new String(mPassword);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.IUserPassword#getUsername()
     */
    public String getUsername()
    {
        return mUsername == null ? null : new String(mUsername);
    }
}
