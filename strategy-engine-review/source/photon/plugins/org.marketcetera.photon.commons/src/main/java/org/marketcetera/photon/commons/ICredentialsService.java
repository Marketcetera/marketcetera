package org.marketcetera.photon.commons;

import java.util.concurrent.CancellationException;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An interface for a service that provides credentials.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ICredentialsService {

    /**
     * Returns the credentials.
     * 
     * @return the credentials, or null if none are available
     * @throws CancellationException
     *             if the operation was canceled.
     */
    Credentials getCredentials() throws CancellationException;
}
