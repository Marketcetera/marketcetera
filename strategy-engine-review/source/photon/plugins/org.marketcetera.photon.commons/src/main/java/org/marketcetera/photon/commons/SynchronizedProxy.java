package org.marketcetera.photon.commons;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A proxy that synchronizes all method calls on the delegate object's monitor.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SynchronizedProxy implements InvocationHandler {

    private final Object mDelegate;

    /**
     * Constructor.
     * 
     * @param delegate
     *            the object to synchronize (also used as the lock)
     */
    public SynchronizedProxy(final Object delegate) {
        mDelegate = delegate;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        synchronized (mDelegate) {
            try {
                return method.invoke(mDelegate, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    /**
     * Creates a dynamic proxy for an object that synchronizes all invocations
     * via the provided interfaces.
     * 
     * @param delegate
     *            the delegate object
     * @param interfaces
     *            the interfaces for the proxy to implement.
     * @return a dynamic proxy for delegate that synchronizes method
     *         invocations via the provided interfaces
     */
    public static Object proxy(Object delegate, Class<?>... interfaces) {
        return Proxy.newProxyInstance(delegate.getClass().getClassLoader(),
                interfaces, new SynchronizedProxy(delegate));
    }
}
