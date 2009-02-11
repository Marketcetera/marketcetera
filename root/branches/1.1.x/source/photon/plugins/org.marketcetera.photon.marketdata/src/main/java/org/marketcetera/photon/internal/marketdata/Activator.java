package org.marketcetera.photon.internal.marketdata;

import org.eclipse.core.runtime.Plugin;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.marketdata"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static Activator sInstance;
	
	/**
	 * The {@link MarketDataManager} singleton for this plug-in instance.
	 */
	private MarketDataManager mMarketDataManager;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		sInstance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		sInstance = null;
		mMarketDataManager = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return sInstance;
	}

	/**
	 * Returns the {@link MarketDataManager} singleton for this plug-in.
	 * Typically, this should be accessed through
	 * {@link MarketDataManager#getCurrent()}.
	 * 
	 * @return the MarketDataManager singleton for this plug-in
	 */
	public synchronized MarketDataManager getMarketDataManager() {
		if (mMarketDataManager == null) {
			mMarketDataManager = new MarketDataManager();
		}
		return mMarketDataManager;
	}

}
