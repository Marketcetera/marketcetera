package org.marketcetera.photon.module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.MessageFormat;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.module.ModuleConfigurationProvider;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * 
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class ModulePlugin extends AbstractUIPlugin implements Messages {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.module"; //$NON-NLS-1$

	/**
	 * Special internal key to indicate instance defaults.
	 */
	public static final String INSTANCE_DEFAULTS_INDICATOR = ":"; //$NON-NLS-1$

	/**
	 * Preference key where properties are stored.
	 */
	private static final String MODULE_PROPERTIES_PREFERENCE = "MODULE_PROPERTIES_PREFERENCE"; //$NON-NLS-1$

	/**
	 * The shared instance
	 */
	private static ModulePlugin plugin;

	/**
	 * ModuleManager used by classes in this plugin, lazily instantiated.
	 */
	private ModuleManager mModuleManager;

	/**
	 * Module properties, lazily loaded from preferences.
	 */
	private PropertiesTree mModuleProperties;

	/**
	 * The constructor
	 */
	public ModulePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	public static ModulePlugin getDefault() {
		return plugin;
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
	 * The core {@link ModuleManager} instance.
	 * 
	 * @return the core module manager.
	 */
	public ModuleManager getModuleManager() {
		if (mModuleManager == null) {
			mModuleManager = new ModuleManager();
			mModuleManager
					.setConfigurationProvider(new PhotonModuleConfigurationProvider());
			try {
				mModuleManager.init();
			} catch (ModuleException e) {
				MODULE_PLUGIN_ERROR_INITIALIZING_MODULE_MANAGER.error(this, e);
			}
		}
		return mModuleManager;
	}

	/**
	 * The Module properties from this plugin's preference store.
	 * 
	 * @return the properties loaded from preferences, or a new empty properties
	 *         tree if saved preferences do not exist
	 */
	public PropertiesTree getModuleProperties() {
		if (mModuleProperties == null) {
			String serialized = getPreferenceStore().getString(
					MODULE_PROPERTIES_PREFERENCE);
			if (!serialized.isEmpty()) {
				try {
					ObjectInputStream in = new ObjectInputStream(
							new ByteArrayInputStream(serialized.getBytes()));
					mModuleProperties = (PropertiesTree) in.readObject();
				} catch (Exception e) {
					MODULE_PROPERTIES_PREFERENCE_PAGE_ERROR_LOADING_PROPERTIES
							.error(this, e);
				}
			}
			if (mModuleProperties == null)
				mModuleProperties = new PropertiesTree();
		}
		// make a copy to prevent external modification
		PropertiesTree properties = new PropertiesTree();
		properties.putAll(mModuleProperties);
		return properties;
	}

	/**
	 * Updates the module properties in the preference store.
	 * 
	 * @param newProperties
	 *            the new properties
	 * @return true if properties were successfully updated
	 */
	public boolean saveModuleProperties(PropertiesTree newProperties) {
		// set to null to trigger reloading
		mModuleProperties = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(newProperties);
		} catch (IOException e) {
			MODULE_PROPERTIES_PREFERENCE_PAGE_ERROR_SAVING_PROPERTIES.error(
					this, e);
			return false;
		}
		ModulePlugin.getDefault().getPreferenceStore().putValue(
				MODULE_PROPERTIES_PREFERENCE, baos.toString());
		return true;
	}

	private final class PhotonModuleConfigurationProvider implements
			ModuleConfigurationProvider {

		@Override
		public void refresh() throws ModuleException {
			// do nothing
		}

		@Override
		public String getDefaultFor(ModuleURN inURN, String inAttribute)
				throws ModuleException {
			String result = null;
			if (inURN.instanceURN()) {
				result = lookup(MessageFormat.format("{0}.{1}.{2}.{3}", //$NON-NLS-1$
						inURN.providerType(), inURN.providerName(), inURN
								.instanceName(), inAttribute));
				if (result == null) {
					result = lookup(MessageFormat.format(
							"{0}.{1}.{2}.{3}", //$NON-NLS-1$
							inURN.providerType(), inURN.providerName(),
							INSTANCE_DEFAULTS_INDICATOR, inAttribute));
				}
			} else {
				result = lookup(MessageFormat
						.format(
								"{0}.{1}.{2}", //$NON-NLS-1$ 
								inURN.providerType(), inURN.providerName(),
								inAttribute));
			}
			return result;
		}

		private String lookup(String key) {
			return getModuleProperties().get(key);
		}
	}
}
