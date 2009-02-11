package org.marketcetera.photon.internal.module;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.module.IModuleAttributeSupport;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

/* $License$ */

/**
 * Controls the plug-in life cycle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class Activator implements BundleActivator {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.module"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static Activator sInstance;

	/**
	 * ModuleManager singleton
	 */
	private ModuleManager mModuleManager;

	/**
	 * Configures module creation attribute defaults
	 */
	private IModuleAttributeSupport mModuleAttributeSupport;
	
	@Override
	public void start(BundleContext context) throws Exception {
		sInstance = this;
		mModuleManager = new ModuleManager();
		mModuleAttributeSupport = new ModuleAttributeSupport(new PreferenceAttributeDefaults());
		mModuleManager
				.setConfigurationProvider(new EclipseModuleConfigurationProvider(
						mModuleAttributeSupport));
		mModuleManager.init();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		sInstance = null;
		mModuleAttributeSupport = null;
		try {
			mModuleManager.stop();
		} catch (ModuleException e) {
			Messages.ACTIVATOR_FAILED_TO_STOP_MODULE_MANAGER.error(this, e);
		} 
		mModuleManager = null;
		try {
			new InstanceScope().getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			Messages.ACTIVATOR_FAILED_TO_SAVE_PREFERENCES.error(this, e);
		}
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
	 * The core {@link ModuleManager} instance.
	 * 
	 * @return the core module manager.
	 */
	public ModuleManager getModuleManager() {
		return mModuleManager;
	}
	
	/**
	 * The {@link ModuleManager} instance.
	 * 
	 * @return the core module manager.
	 */
	public IModuleAttributeSupport getModuleAttributeSupport() {
		return mModuleAttributeSupport;
	}

}
