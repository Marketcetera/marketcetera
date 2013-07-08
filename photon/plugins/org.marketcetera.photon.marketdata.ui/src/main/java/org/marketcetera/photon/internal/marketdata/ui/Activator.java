package org.marketcetera.photon.internal.marketdata.ui;

import org.marketcetera.photon.core.ISymbolResolver;
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
 * @since 2.0.0
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
	/**
	 * Tracks the {@link ISymbolResolver} service
	 */
    private ServiceTracker mSymbolResolverServiceTracker;

	@Override
	public void start(BundleContext context) throws Exception {
		synchronized (getClass()) {
			mMarketDataManagerTracker = new ServiceTracker(context, IMarketDataManager.class
					.getName(), null);
			mMarketDataManagerTracker.open();
			mSymbolResolverServiceTracker = new ServiceTracker(context,
			                                                   ISymbolResolver.class.getName(),
			                                                   null);
			mSymbolResolverServiceTracker.open();
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
	 * Returns the symbol resolver service for the singleton instance of this plug-in.
	 *
	 * @return an <code>ISymbolResolver</code> value
	 */
	public static ISymbolResolver getSymbolResolver()
	{
        synchronized (Activator.class) {
            if (sInstance != null) {
                return (ISymbolResolver)sInstance.mSymbolResolverServiceTracker.getService();
            } else {
                return null;
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
