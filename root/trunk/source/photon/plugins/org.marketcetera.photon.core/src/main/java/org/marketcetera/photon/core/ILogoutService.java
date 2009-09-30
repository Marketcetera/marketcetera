package org.marketcetera.photon.core;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Interface for a service that logs out the current user. There should only be
 * one {@link ILogoutService} active.
 * <p>
 * This service allows adding runnables to be run during logout.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface ILogoutService {

    /**
     * Performs the logout and runs all runnables registered with
     * {@link #addLogoutRunnable(Runnable)}. There is no guarantee to the
     * ordering in which runnables are invoked. Any {@link RuntimeException}
     * thrown by a runnable will be logged and swallowed and will not prevent
     * other runnables from running.
     * <p>
     * Runnables are only invoked once, i.e. after execution they are discarded
     * and must be added again to be run during the next logout.
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
