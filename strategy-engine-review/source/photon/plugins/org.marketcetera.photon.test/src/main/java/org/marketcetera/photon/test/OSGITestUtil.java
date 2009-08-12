package org.marketcetera.photon.test;

import java.util.Hashtable;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * OSGi related test utilities.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class OSGITestUtil {

    /**
     * Register the provided service with maxiumum priority.
     * 
     * @param clazz
     *            The class name under which the service can be located.
     * @param service
     *            The service object or a ServiceFactory object.
     * @return the ServiceRegistration that can be used to unregister the
     *         service
     */
    public static ServiceRegistration registerMockService(String clazz,
            Object service) {
        return Platform.getBundle("org.marketcetera.photon.test")
                .getBundleContext().registerService(
                        clazz,
                        service,
                        new Hashtable<String, Integer>(ImmutableMap.of(
                                Constants.SERVICE_RANKING, Integer.MAX_VALUE)));
    }

    private OSGITestUtil() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
