/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.util.concurrent;

/**
 * An implementation of {@link ca.odell.glazedlists.util.concurrent.LockFactory} that detects and delegates to
 * a JVM specific LockFactory implementation optimized for the current JVM.
 */
class DelegateLockFactory implements LockFactory {

    /** The true JVM-specific LockFactory to which we delegate. */
    private LockFactory delegate;

    DelegateLockFactory() {
        try {
            // if the J2SE 5.0 ReadWriteLock class can be loaded, we're running on a JDK 1.5 VM
            Class.forName("java.util.concurrent.locks.ReadWriteLock");

            // and if we can load our J2SE 5.0 LockFactory implementation
            // (i.e. it's not a Glazed Lists 1.4 implementation running on a JDK 1.5 VM)
            // then use the J2SE 5.0 LockFactory implementation
            delegate = (LockFactory) Class.forName("ca.odell.glazedlists.impl.java15.J2SE50LockFactory").newInstance();

        } catch (Throwable t) {
            // otherwise fall back to a J2SE 1.4 LockFactory
            delegate = new J2SE14LockFactory();
        }
    }

    public ReadWriteLock createReadWriteLock() {
        return delegate.createReadWriteLock();
    }

    public Lock createLock() {
        return delegate.createLock();
    }
}