package org.marketcetera.photon.registry;

import java.io.File;
import java.util.Hashtable;

import org.eclipse.core.internal.registry.IRegistryConstants;
import org.eclipse.core.internal.registry.RegistryProperties;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.eclipse.core.internal.registry.osgi.EquinoxRegistryStrategy;
import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.eclipse.core.internal.registry.osgi.RegistryProviderOSGI;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


/**
 * Bundle activator that starts up an extension registry and removes unwanted contributions from product 
 * dependencies. The unwanted extensions are listed in the <code>UNWANTED_EXTENSIONS</code> array.
 *  
 * @author andrei@lissovski.org
 */
public class Activator implements BundleActivator {
	/**
	 * Location of the default registry relative to the configuration area.
	 */
	private static final String STORAGE_DIR = "org.eclipse.core.runtime"; //$NON-NLS-1$

	private Object masterRegistryKey = new Object();
	private Object userRegistryKey = new Object();
	
	private BundleContext context;

	private IExtensionRegistry registry;
	
	private static final String[] UNWANTED_EXTENSIONS = new String[] {
		"org.eclipse.ui.resourcePerspective",  //$NON-NLS-1$
		"org.rubypeople.rdt.ui.RubyBrowsingPerspective",  //$NON-NLS-1$
		"org.eclipse.update.internal.ui.preferences.MainPreferencePage",  //$NON-NLS-1$
		"org.eclipse.debug.ui.launchActionSet",  //$NON-NLS-1$
		"org.rubypeople.rdt.debug.ui.RDTDebugActionSet"  //$NON-NLS-1$
	};

	private RegistryProviderOSGI defaultRegistryProvider;

	private ServiceRegistration registryRegistration;

	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		
		RegistryProperties.setContext(context);
		
		createRegistry();
		removeUnwatedExtensions();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		stopRegistry();
		
		RegistryProperties.setContext(null);		
	}

	private void removeUnwatedExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		for (String xtId : UNWANTED_EXTENSIONS) {
			IExtension xt = findExtension(xtId);
			if (xt != null)
				registry.removeExtension(xt, masterRegistryKey);
		}
	}

	private IExtension findExtension(String extensionId) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		for (String namespace : registry.getNamespaces()) {
        	for (IExtension extension : registry.getExtensions(namespace)) {
        		for (IConfigurationElement configElement : extension.getConfigurationElements()) {
        			for (String attribName : configElement.getAttributeNames()) {
        				String attribValue = configElement.getAttribute(attribName);
						if (attribName.equals("id") && attribValue.equals(extensionId)) {  //$NON-NLS-1$
        					return extension;
        				}
					}
				}
        	}
		}
        
        return null;
	}
	
	private void createRegistry() throws CoreException {
		// Determine primary and alternative registry locations. Eclipse extension registry cache 
		// can be found in one of the two locations:
		// a) in the local configuration area (standard location passed in by the platform) -> priority
		// b) in the shared configuration area (typically, shared install is used) 
		File[] registryLocations;
		boolean[] readOnlyLocations;

		Location configuration = OSGIUtils.getDefault().getConfigurationLocation();
		File primaryDir = new File(configuration.getURL().getPath() + '/' + STORAGE_DIR);
		boolean primaryReadOnly = configuration.isReadOnly();

		Location parentLocation = configuration.getParentLocation();
		if (parentLocation != null) {
			File secondaryDir = new File(parentLocation.getURL().getFile() + '/' + IRegistryConstants.RUNTIME_NAME);
			registryLocations = new File[] {primaryDir, secondaryDir};
			readOnlyLocations = new boolean[] {primaryReadOnly, true}; // secondary Eclipse location is always read only
		} else {
			registryLocations = new File[] {primaryDir};
			readOnlyLocations = new boolean[] {primaryReadOnly};
		}

		EquinoxRegistryStrategy registryStrategy = new EquinoxRegistryStrategy(registryLocations, readOnlyLocations, masterRegistryKey);
		registry = RegistryFactory.createRegistry(registryStrategy, masterRegistryKey, userRegistryKey);

		registryRegistration = context.registerService(IExtensionRegistry.class.getName(), registry, new Hashtable());
		defaultRegistryProvider = new RegistryProviderOSGI();
		
		// Set the registry provider and specify this as a default registry:
		RegistryProviderFactory.setDefault(defaultRegistryProvider);
	}

	private void stopRegistry() {
		if (registry != null) {
			RegistryProviderFactory.releaseDefault();
			defaultRegistryProvider.release();
			registryRegistration.unregister();
			registry.stop(masterRegistryKey);
		}
	}

}
