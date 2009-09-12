package org.marketcetera.photon.commons.osgi;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/* $License$ */

/**
 * Tracks OSGi services for a particular class name, selecting the service with
 * the highest {@link Constants#SERVICE_RANKING ranking}.
 * <p>
 * The {@link HighestRankedTracker} must be {@link ServiceTracker#open() opened}
 * for tracking to begin. When opened the {@link IHighestRankedTrackerListener}
 * will be notified of the current highest ranked service if one exists.
 * <p>
 * Note that clients may not be updated if a service ranking changes. In other
 * words, the behavior of this object is only predictable when services are
 * added and removed. This is due to <a
 * href="http://bugs.eclipse.org/288365">http://bugs.eclipse.org/288365</a>.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class HighestRankedTracker extends ServiceTracker {

    /**
     * Callback interface for clients to be notified when the highest ranked
     * service changes.
     */
    @ClassVersion("$Id$")
    public interface IHighestRankedTrackerListener {

        /**
         * The highest ranked service changed.
         * <p>
         * This method will not be called concurrently since it is called while
         * the {@link HighestRankedTracker} is holding a lock. It may, however,
         * be called from different threads.
         * 
         * @param newService
         *            the new service, may be null if none are available
         */
        void highestRankedServiceChanged(Object newService);
    }

    private final String mClazz;
    private final IHighestRankedTrackerListener mCallback;
    private final Object mLock = new Object();
    private ServiceReference mLatest;

    /**
     * Constructor.
     * 
     * @param context
     *            the bundle context used to obtain services
     * @param clazz
     *            the class name of the services to be tracked
     * @param callback
     *            callback for clients to handle state changes
     */
    public HighestRankedTracker(BundleContext context, String clazz,
            IHighestRankedTrackerListener callback) {
        super(context, clazz, null);
        Validate.notNull(callback, "callback"); //$NON-NLS-1$
        mClazz = clazz;
        mCallback = callback;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object service = super.addingService(reference);
        update();
        return service;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        update();
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        super.removedService(reference, service);
        update();
    }

    private void update() {
        synchronized (mLock) {
            ServiceReference previous = mLatest;
            /*
             * Use the context to get the highest ranked service reference.
             */
            mLatest = context.getServiceReference(mClazz);
            if (!ObjectUtils.equals(mLatest, previous)) {
                if (mLatest != null) {
                    /*
                     * Since we are not maintaining a map from service reference
                     * to service, we get the service again from the context.
                     * This will increase the reference count again (it was
                     * already done in serviceAdded) so we need to unget after
                     * we are done.
                     */
                    Object service = context.getService(mLatest);
                    try {
                        mCallback.highestRankedServiceChanged(service);
                    } finally {
                        context.ungetService(mLatest);
                    }
                } else {
                    mCallback.highestRankedServiceChanged(null);
                }
            }
        }
    }
}