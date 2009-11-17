package org.marketcetera.util.misc;

import org.marketcetera.util.misc.ClassVersion;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/* $License$ */
/**
 * A Thread factory that creates threads whose names begin with a specified
 * prefix. If the specified prefix is <code>foo</code>, the threads created
 * by this factory will have names that look like <code>fooX</code> where
 * 'X' is a monotonically increasing number used to name each thread created
 * by this factory uniquely.
 * <p>
 * The factory delegates the thread creation to
 * {@link java.util.concurrent.Executors#defaultThreadFactory()}. After the
 * thread has been created by the delegated factory, this class simply
 * resets their name to the desired value.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class NamedThreadFactory implements ThreadFactory {
    /**
     * Creates an instance that will create threads whose names begin with
     * the specified prefix.
     *
     * @param inNamePrefix the prefix to use for naming every thread
     * created by this factory. Cannot be null.
     */
    public NamedThreadFactory(String inNamePrefix) {
        if(inNamePrefix == null) {
            throw new NullPointerException();
        }
        mNamePrefix = inNamePrefix;
        mDelegate =  Executors.defaultThreadFactory();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = mDelegate.newThread(r);
        t.setName(mNamePrefix + mThreadNumber.getAndIncrement());
        return t;
    }
    private final String mNamePrefix;
    private final AtomicInteger mThreadNumber = new AtomicInteger(1);
    private final ThreadFactory mDelegate;
}
