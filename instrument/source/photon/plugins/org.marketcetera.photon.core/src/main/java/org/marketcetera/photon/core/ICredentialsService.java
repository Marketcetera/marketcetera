package org.marketcetera.photon.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An interface for a service that manages credentials. Clients can use
 * {@link #authenticateWithCredentials(IAuthenticationHelper)} to run an
 * {@link IAuthenticationHelper#authenticate(ICredentials) authentication
 * operation} using the credentials provided by this service.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ICredentialsService {

    /**
     * Interface for clients of the {@link ICredentialsService} to perform the
     * authentication.
     */
    interface IAuthenticationHelper {

        /**
         * Attempts to authenticate with the provided credentials.
         * 
         * @param credentials
         *            the credentials to use
         * @return true if authentication succeeded, false otherwise
         */
        boolean authenticate(ICredentials credentials);
    }

    /**
     * Runs an authentication operation, providing credentials that may be
     * cached. The first time authentication succeeds for a given
     * {@link ICredentials} object, it will be cached and used for all
     * successive authentication attempts until {@link #invalidate()} is called.
     * 
     * @param helper
     *            code to perform the authentication operation
     * @return true if authentication succeeded
     */
    boolean authenticateWithCredentials(IAuthenticationHelper helper);

    /**
     * Invalidates any cached credentials. After this has been called, the next
     * request to {@link #authenticateWithCredentials(IAuthenticationHelper)}
     * must obtain fresh credentials.
     */
    void invalidate();
}
