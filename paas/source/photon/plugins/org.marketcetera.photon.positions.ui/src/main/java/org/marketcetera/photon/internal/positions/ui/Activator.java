package org.marketcetera.photon.internal.positions.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.core.position.PositionEngine;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.marketcetera.photon.positions.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private ServiceTracker positionEngineTracker;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		positionEngineTracker = new ServiceTracker(context, PositionEngine.class.getName(), null);
		positionEngineTracker.open();
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		positionEngineTracker.close();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns the position engine service.
	 * 
	 * @return the position engine service, or null if none exists.
	 */
	public PositionEngine getPositionEngine() {
		return (PositionEngine) positionEngineTracker.getService();
	}

}
