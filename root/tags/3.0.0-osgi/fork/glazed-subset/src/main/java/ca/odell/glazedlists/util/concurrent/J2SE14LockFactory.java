/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.util.concurrent;

/**
 * An implementation of {@link LockFactory} that has been derived from Doug Lea's
 * <a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html">util.concurrent</a>.
 */
final class J2SE14LockFactory implements LockFactory {

    /**
     * Create a {@link ReadWriteLock}.
     *
     * <p>The default implementation returns an implementation that has been
     * derived from Doug Lea's <a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html">util.concurrent</a>.
     */
    public ReadWriteLock createReadWriteLock() {
        return new J2SE14ReadWriteLock();
    }

    /**
     * Create a {@link Lock}.
     *
     * <p>The default implementation returns an implementation that has been
     * derived from Doug Lea's <a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html">util.concurrent</a>.
     */
    public Lock createLock() {
        return new J2SE14ReadWriteLock().writeLock();
    }
}
