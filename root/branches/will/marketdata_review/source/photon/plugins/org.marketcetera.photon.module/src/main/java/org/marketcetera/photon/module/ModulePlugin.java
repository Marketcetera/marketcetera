package org.marketcetera.photon.module;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.ModuleConfigurationProvider;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.osgi.framework.BundleContext;

/* $License$ */

/**
 * Controls the plug-in life cycle and provides access to the core module framework.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public final class ModulePlugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "org.marketcetera.photon.module"; //$NON-NLS-1$

	/**
	 * Special internal key to indicate instance defaults
	 */
	public static final String INSTANCE_DEFAULTS_INDICATOR = ":"; //$NON-NLS-1$

	/**
	 * File name of the persisted module properties
	 */
	private static final String PROPERTIES_FILENAME = "moduleProperties.xml"; //$NON-NLS-1$
	
	/**
	 * Root tag for persisted properties xml
	 */
	private static final String PROPERTIES_TAG = "properties"; //$NON-NLS-1$
	
	/**
	 * Tag for a single property
	 */
	private static final String PROPERTY_TAG = "property"; //$NON-NLS-1$
	
	/**
	 * Attribute for a property key
	 */
	private static final String KEY_ATTRIBUTE = "key"; //$NON-NLS-1$
	
	/**
	 * Attribute for a property value
	 */
	private static final String VALUE_ATTRIBUTE = "value"; //$NON-NLS-1$
	
	/**
	 * The shared instance
	 */
	private static ModulePlugin plugin;

	/**
	 * ModuleManager used by classes in this plugin, lazily instantiated
	 */
	private ModuleManager mModuleManager;

	/**
	 * Module properties, lazily loaded from preferences
	 */
	private PropertiesTree mModuleProperties;

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
				Messages.MODULE_PLUGIN_ERROR_INITIALIZING_MODULE_MANAGER.error(this, e);
			}
		}
		return mModuleManager;
	}

	/**
	 * Returns the {@link MBeanServerConnection} used for module management.
	 * 
	 * @return the {@link MBeanServerConnection} used for module management
	 */
	public MBeanServerConnection getMBeanServerConnection() {
		return ManagementFactory.getPlatformMBeanServer();
	}

	/**
	 * Returns the module properties used by the module manager.  Note that
	 * changing the returned tree will not affect the module manager.  Instead,
	 * {@link #saveModuleProperties(PropertiesTree)} must be called.
	 * 
	 * @return the module framework properties
	 */
	public PropertiesTree getModuleProperties() {
		if (mModuleProperties == null) {
			mModuleProperties = readProperties();
		}
		// make a copy to prevent external modification
		PropertiesTree properties = new PropertiesTree();
		properties.putAll(mModuleProperties);
		return properties;
	}

	/**
	 * Updates and persists the module properties.  Note that the module
	 * manager will not pick these up until is is restarted, i.e. when 
	 * this plug-in is restarted.
	 * 
	 * @param newProperties
	 *            the new properties
	 */
	public void saveModuleProperties(PropertiesTree newProperties) {
		mModuleProperties = newProperties;
		writeProperties();
	}

	/**
	 * Seeds a properties tree with the known writable module properties.
	 * 
	 * @param properties the properties tree to seed
	 */
	public void seedKnownKeys(PropertiesTree properties) {
		List<ModuleURN> instances = null;
		try {
			instances = getModuleManager().getModuleInstances(null);
		} catch (InvalidURNException e) {
			// I'm not even supplying a URN, so this exception will not
			// get thrown, at least given the current behavior of 
			// getModuleInstances.  In case that ever changes...
			throw new AssertionError("Unexpected InvalidURNException"); //$NON-NLS-1$
		}

		for (ModuleURN moduleURN : instances) {
			try {
				MBeanInfo info = getMBeanServerConnection().getMBeanInfo(
						moduleURN.toObjectName());
				MBeanAttributeInfo[] attributes = info.getAttributes();
				for (MBeanAttributeInfo beanAttributeInfo : attributes) {
					if (beanAttributeInfo.isWritable()) {
						String attribute = beanAttributeInfo.getName();
						String key = MessageFormat.format("{0}.{1}.{2}.{3}", //$NON-NLS-1$
								moduleURN.providerType(), moduleURN
										.providerName(), moduleURN
										.instanceName(), attribute);
						if (!properties.containsKey(key)) {
							properties.put(key, ""); //$NON-NLS-1$
						}
					}
				}
			} catch (InstanceNotFoundException e) {
				// Continue on, nothing to process for modules that do not have a bean interface
			} catch (Exception e) {
				// Something else went wrong, log and skip
				Messages.MODULE_PLUGIN_ERROR_DISCOVERING_MODULE_PROPERTIES.warn(this, e, moduleURN.toString());
			}
		}

	}

	private PropertiesTree readProperties() {
		PropertiesTree tree = new PropertiesTree();
		File file = getStateLocation().append(PROPERTIES_FILENAME).toFile();
		if (file.exists()) {
			try {
				FileInputStream input = new FileInputStream(file);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input, "utf-8")); //$NON-NLS-1$
				IMemento mem = XMLMemento.createReadRoot(reader);
				for (IMemento property : mem.getChildren(PROPERTY_TAG)) {
					String key = property.getString(KEY_ATTRIBUTE);
					String value = property.getString(VALUE_ATTRIBUTE);
					if (StringUtils.isNotBlank(key) && value != null) {
						tree.put(key, value);
					} else {
						Messages.MODULE_PLUGIN_INVALID_PROPERTY.warn(this, key, value);
					}
				}
			} catch (Exception e) {
				Messages.MODULE_PLUGIN_ERROR_LOADING_PROPERTIES.error(this, e);
			}
		} else {
			SLF4JLoggerProxy.debug(this, "Did not load persisted module properties because the file does not exist."); //$NON-NLS-1$
		}
		return tree;
	}

	private void writeProperties() {
		File file = getStateLocation().append(PROPERTIES_FILENAME).toFile();
		XMLMemento mem = XMLMemento.createWriteRoot(PROPERTIES_TAG);
		for (Map.Entry<String, String> entry : mModuleProperties.entrySet()) {
			IMemento property = mem.createChild(PROPERTY_TAG);
			property.putString(KEY_ATTRIBUTE, entry.getKey());
			property.putString(VALUE_ATTRIBUTE, entry.getValue());
		}
		try {
			FileOutputStream stream = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(stream, "utf-8"); //$NON-NLS-1$
			mem.save(writer);
			writer.close();
		} catch (IOException e) {
			file.delete();
			Messages.MODULE_PLUGIN_ERROR_SAVING_PROPERTIES.error(this, e);
		}
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
			PropertiesTree properties = getModuleProperties();
			String value = properties.get(key);
			if (value == null) {
				// Look in the regular eclipse preferences for a seeded value. If
				// found, persist it to the regular properties store
				String defaultValue = getPreferenceStore().getString(key);
				if (StringUtils.isNotBlank(defaultValue)) {
					properties.put(key, defaultValue);
					saveModuleProperties(properties);
					return defaultValue;
				}
			}
			return value;
		}
	}
}
