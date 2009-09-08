package org.marketcetera.photon.internal.marketdata.ui;

import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/* $License$ */

/**
 * This class controls the plug-in life cycle and manages singleton state.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class Activator implements BundleActivator {

	/**
	 * The singleton instance
	 */
	private static Activator sInstance;
	
	/**
	 * Tracks the {@link IMarketDataManager} service 
	 */
	private ServiceTracker mMarketDataManagerTracker;

	@Override
	public void start(BundleContext context) throws Exception {
		synchronized (getClass()) {
			mMarketDataManagerTracker = new ServiceTracker(context, IMarketDataManager.class
					.getName(), null);
			mMarketDataManagerTracker.open();
			sInstance = this;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		synchronized (getClass()) {
			sInstance = null;
			if (mMarketDataManagerTracker != null) {
				mMarketDataManagerTracker.close();
				mMarketDataManagerTracker = null;
			}
		}
	}
	
	/**
	 * Returns the market data manager for the singleton instance of this plug-in.
	 * 
	 * @return the market data manager, or null if one is not available
	 */
	public static IMarketDataManager getMarketDataManager() {
		synchronized (Activator.class) {
			if (sInstance != null) {
				return (IMarketDataManager) sInstance.mMarketDataManagerTracker.getService();
			} else {
				return null;
			}
		}
	}
}
