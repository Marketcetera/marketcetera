package org.marketcetera.photon.core;

import java.util.List;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Base implementation of {@link ILogoutService}. Subclasses can override
 * {@link #doLogout()} to perform additional logout actions.
 * <p>
 * This class is thread safe.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public class LogoutService implements ILogoutService {
    @GuardedBy("mLogoutRunnables")
    private final Set<Runnable> mLogoutRunnables = Sets.newHashSet();

    @Override
    public final void logout() {
        final List<Runnable> toRun;
        synchronized (mLogoutRunnables) {
            try {
                doLogout();
            } catch (RuntimeException e) {
                ExceptUtils.swallow(e, LogoutService.class,
                        Messages.LOGOUT_SERVICE_UNHANDLED_EXCEPTION);
            }
            toRun = ImmutableList.copyOf(mLogoutRunnables);
            mLogoutRunnables.clear();
        }
        for (Runnable r : toRun) {
            try {
                r.run();
            } catch (RuntimeException e) {
                ExceptUtils.swallow(e, LogoutService.class,
                        Messages.LOGOUT_SERVICE_UNHANDLED_EXCEPTION);
            }
        }
    }

    /**
     * Hook for subclasses to perform custom logout actions before runnables are
     * notified. This is called while the service is locked, providing
     * subclasses implementations exclusive access.
     * <p>
     * Any {@link RuntimeException} thrown by a subclass implementation will be
     * logged and swallowed.
     */
    protected void doLogout() {
    }

    @Override
    public final void addLogoutRunnable(Runnable runnable) {
        Validate.notNull(runnable, "runnable"); //$NON-NLS-1$
        synchronized (mLogoutRunnables) {
            mLogoutRunnables.add(runnable);
        }
    }

    @Override
    public final void removeLogoutRunnable(Runnable runnable) {
        Validate.notNull(runnable, "runnable"); //$NON-NLS-1$
        synchronized (mLogoutRunnables) {
            mLogoutRunnables.remove(runnable);
        }
    }
}