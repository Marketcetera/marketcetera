package org.marketcetera.photon.commons;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Data structure representing credentials.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class Credentials {

    private final String mUsername;
    private final String mPassword;

    /**
     * Constructor.
     * 
     * @param username
     *            the user name
     * @param password
     *            the password
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public Credentials(String username, String password) {
        Validate.notNull(username, "username", //$NON-NLS-1$
                password, "password"); //$NON-NLS-1$
        mUsername = username;
        mPassword = password;
    }

    /**
     * Return the user name.
     * 
     * @return the user name
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Return the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return mPassword;
    }

}
