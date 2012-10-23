/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.util.concurrent;

/**
 * This factory provides an implementation of {@link ca.odell.glazedlists.util.concurrent.Lock} that is optimized
 * for the current Java Virtual Machine.
 *
 */
public interface LockFactory {

    /** The Lock factory for this JVM. */
    public static final LockFactory DEFAULT = new DelegateLockFactory();

    /**
     * Create a {@link ca.odell.glazedlists.util.concurrent.ReadWriteLock}.
     */
    public ReadWriteLock createReadWriteLock();

    /**
     * Create a {@link ca.odell.glazedlists.util.concurrent.Lock}.
     */
    public Lock createLock();
}

