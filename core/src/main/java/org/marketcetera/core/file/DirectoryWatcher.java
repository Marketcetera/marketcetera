package org.marketcetera.core.file;

/* $License$ */

/**
 * Watches the given directories for files to be added or modified.
 * 
 * <p>Implementers are guaranteed by contract to provide a thread-safe implementation.</p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DirectoryWatcher.java 83882 2014-08-01 22:31:54Z colin $
 * @since $Release$
 */
public interface DirectoryWatcher
{
    /**
     * Adds the given subscriber to the set of interested subscribers.
     * 
     * <p>The given subscriber is guaranteed to be notified in the order it was added. Adding the same
     * subscriber more than once will still result in the subscriber being notified only once. Adding the
     * same subscriber more than once does not affect its notification order.
     *
     * @param inSubscriber a <code>DirectoryWatcherSubscriber</code> value
     */
    public void addWatcher(DirectoryWatcherSubscriber inSubscriber);
    /**
     * Removes the given subscriber from the set of interested subscribers.
     * 
     * <p>Removing a subscriber that has not subscribed has no effect.
     *
     * @param inSubscriber a <code>DirectoryWatcherSubscriber</code> value
     */
    public void removeWatcher(DirectoryWatcherSubscriber inSubscriber);
}
