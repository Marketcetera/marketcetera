package org.marketcetera.photon.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for a service that logs out the current user. There should only be
 * one {@link ILogoutService} active.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ILogoutService {

    /**
     * Performs the logout and runs all runnables registered with
     * {@link #addLogoutRunnable(Runnable)}.
     */
    void logout();

    /**
     * Registers a runnable to be run when {@link #logout()} is next called.
     * Calling multiple times with the same runnable has no effect unless
     * {@link #removeLogoutRunnable(Runnable)} is called in between.
     * 
     * @param runnable
     *            the code to run on logout
     * @throws IllegalArgumentException
     *             if runnable is null
     */
    void addLogoutRunnable(Runnable runnable);

    /**
     * Unregisters the runnable. Has no effect if the runnable was never
     * registered with {@link #addLogoutRunnable(Runnable)}.
     * 
     * @param runnable
     *            the code to run on logout
     * @throws IllegalArgumentException
     *             if runnable is null
     */
    void removeLogoutRunnable(Runnable runnable);
}
