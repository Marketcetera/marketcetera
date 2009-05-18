package org.marketcetera.photon.internal.marketdata;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.Plugin;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.internal.marketdata.DataFlowManager.MarketDataExecutor;
import org.marketcetera.photon.marketdata.IMarketDataManager;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;

/* $License$ */

/**
 * This class controls the plug-in life cycle and manages singleton state.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class Activator extends Plugin {

	/**
	 * The singleton instance.
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
	public final void start(final BundleContext context) throws Exception {
		synchronized (getClass()) {
			super.start(context);
			mMarketDataExecutor = Executors.newSingleThreadExecutor();
			final Module module = new AbstractModule() {
				@Override
				protected void configure() {
					bind(ModuleManager.class).toInstance(ModuleSupport.getModuleManager());
					bind(Executor.class).annotatedWith(MarketDataExecutor.class).toInstance(
							mMarketDataExecutor);
				}
			};
			mMarketDataManager = Guice.createInjector(module).getInstance(MarketDataManager.class);
			// service is unregistered during stop
			context.registerService(IMarketDataManager.class.getName(), mMarketDataManager, null);
			sInstance = this;
		}
	}

	@Override
	public final void stop(final BundleContext context) throws Exception {
		synchronized (getClass()) {
			sInstance = null;
			mMarketDataManager = null;
			if (mMarketDataExecutor != null) {
				mMarketDataExecutor.shutdownNow();
				mMarketDataExecutor = null;
			}
			super.stop(context);
		}
	}

	/**
	 * Returns the market data manager for the singleton plug-in.
	 * 
	 * @return the market data manager, or null if the plug-in is not active
	 */
	public static MarketDataManager getMarketDataManager() {
		synchronized (Activator.class) {
			return sInstance == null ? null : sInstance.mMarketDataManager;
		}
	}
}
