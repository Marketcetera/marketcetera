package org.marketcetera.photon.internal.strategy;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle. This class is single
 * threaded and all methods are expected to be called from the UI thread.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class Activator extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.strategy"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static Activator mPlugin;

	/**
	 * The {@link StrategyManager} singleton for this plug-in instance.
	 */
	private StrategyManager mStrategyManager;

	/**
	 * The {@link TradeSuggestionManager} singleton for this plug-in instance.
	 */
	private TradeSuggestionManager mTradeSuggestionManager;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		mPlugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mPlugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return mPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns the {@link StrategyManager} singleton for this plug-in.
	 * Typically, this should be accessed through
	 * {@link StrategyManager#getCurrent()}.
	 * 
	 * @return the StrategyManager singleton for this plug-in
	 */
	StrategyManager getStrategyManager() {
		if (mStrategyManager == null) {
			mStrategyManager = new StrategyManager();
		}
		return mStrategyManager;
	}

	/**
	 * Returns the {@link TradeSuggestionManager} singleton for this plug-in.
	 * Typically, this should be accessed through
	 * {@link TradeSuggestionManager#getCurrent()}.
	 * 
	 * @return the TradeSuggestionManager singleton for this plug-in
	 */
	TradeSuggestionManager getTradeSuggestionManager() {
		if (mTradeSuggestionManager == null) {
			mTradeSuggestionManager = new TradeSuggestionManager();
		}
		return mTradeSuggestionManager;
	}
}
