package org.marketcetera.photon.internal.positions.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.photon.positions.ui.IPositionLabelProvider;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/* $License$ */

/**
 * The activator class controls the plug-in life cycle.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.marketcetera.photon.positions.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private ServiceTracker positionEngineTracker;

	private ServiceTracker positionLabelTracker;

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
		positionLabelTracker = new ServiceTracker(context, IPositionLabelProvider.class.getName(), null);
		positionLabelTracker.open();
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		positionEngineTracker.close();
		positionLabelTracker.close();
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

	/**
	 * Returns the position label provider service.
	 * 
	 * @return the position label provider, or null if none exists.
	 */
	public IPositionLabelProvider getPositionLabelProvider() {
		return (IPositionLabelProvider) positionLabelTracker.getService();
	}

}
