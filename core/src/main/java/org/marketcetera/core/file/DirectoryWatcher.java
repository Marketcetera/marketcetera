package org.marketcetera.core.file;

import java.io.File;

/* $License$ */

/**
 * Watches the given directories for files to be added or modified.
 * 
 * <p>Instantiate a <code>DirectoryWatcher</code> implementer and {@link #setDirectoriesToWatch(File...) add} directories
 * to watch. {@link DirectoryWatcher#addWatcher(DirectoryWatcherSubscriber) Subscribe} to notifications and {@link #start() start} the
 * watcher.</p>
 * 
 * <p>The <code>DirectoryWatcher</code> contract directs that watchers will be notified in the order they subscribed for
 * each file that:
 * <ul>
 *   <li>exists in the directory upon the call to {@link #start start}</li>
 *   <li>is added to the directory after start</li>
 * </ul>
 * 
 * <p>Watchers are notified for each file once and only once. Watcher notifications
 * are guaranteed to occur no more frequently than the interval {@link #setPollingInterval(long) set}.</p>
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
