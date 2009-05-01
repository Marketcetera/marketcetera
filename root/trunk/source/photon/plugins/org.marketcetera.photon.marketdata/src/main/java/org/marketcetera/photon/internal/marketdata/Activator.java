package org.marketcetera.photon.internal.marketdata;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.Plugin;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.internal.marketdata.DataFlowManager.MarketDataExecutor;
import org.marketcetera.photon.marketdata.MarketDataManager;
import org.marketcetera.photon.module.ModuleSupport;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

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
	 * The {@link Executor} used by data flow managers to perform market data related operations
	 * serially in a background thread.
	 */
	private ExecutorService mMarketDataExecutor;

	/**
	 * The {@link MarketDataManager} singleton for this plug-in instance.
	 */
	private MarketDataManager mMarketDataManager;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		sInstance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		sInstance = null;
		mMarketDataManager = null;
		if (mMarketDataExecutor != null) {
			mMarketDataExecutor.shutdownNow();
			mMarketDataExecutor = null;
		}
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
	 * Returns the {@link MarketDataManager} singleton for this plug-in. Typically, this should be
	 * accessed through {@link MarketDataManager#getCurrent()}.
	 * 
	 * @return the MarketDataManager singleton for this plug-in
	 */
	public synchronized MarketDataManager getMarketDataManager() {
		if (mMarketDataManager == null) {
			mMarketDataManager = Guice.createInjector(new Module()).getInstance(
					MarketDataManager.class);
		}
		return mMarketDataManager;
	}
	
	private synchronized Executor getMarketDataExecutor() {
		if (mMarketDataExecutor == null) {
			mMarketDataExecutor = Executors.newSingleThreadExecutor();
		}
		return mMarketDataExecutor;
	}

	private class Module extends AbstractModule {

		@Override
		protected void configure() {
			bind(ModuleManager.class).toInstance(ModuleSupport.getModuleManager());
			bind(Executor.class).annotatedWith(MarketDataExecutor.class).toInstance(getMarketDataExecutor());
		}

	}

}
