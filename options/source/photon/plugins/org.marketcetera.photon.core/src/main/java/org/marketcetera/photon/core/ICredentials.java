package org.marketcetera.photon.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An object that represents credentials.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ICredentials {

    /**
     * Return the user name.
     * 
     * @return the user name
     */
    public abstract String getUsername();

    /**
     * Return the password.
     * 
     * @return the password
     */
    public abstract String getPassword();
}