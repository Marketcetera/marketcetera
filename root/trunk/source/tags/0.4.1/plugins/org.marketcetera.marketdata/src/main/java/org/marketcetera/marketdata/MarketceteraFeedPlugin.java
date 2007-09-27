package org.marketcetera.marketdata;

import java.io.File;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MarketceteraFeedPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.marketcetera.marketdata";

	// The shared instance
	private static MarketceteraFeedPlugin plugin;

	private BundleContext bundleContext;
	
	/**
	 * The constructor
	 */
	public MarketceteraFeedPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		bundleContext = context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static MarketceteraFeedPlugin getDefault() {
		return plugin;
	}

	public File getDataFile(String filename){
		return bundleContext.getDataFile(filename);
	}
	public File getStoreDirectory(){
		return bundleContext.getDataFile("");
	}

	@Override
	public ScopedPreferenceStore getPreferenceStore() {
		return (ScopedPreferenceStore) super.getPreferenceStore();
	}

}
