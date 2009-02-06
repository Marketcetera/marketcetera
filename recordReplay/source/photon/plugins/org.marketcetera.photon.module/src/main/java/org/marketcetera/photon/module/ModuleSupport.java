package org.marketcetera.photon.module;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;

import org.marketcetera.module.ModuleManager;
import org.marketcetera.photon.internal.module.Activator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to the core module framework.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class ModuleSupport {

	/**
	 * The core {@link ModuleManager} instance.
	 * 
	 * @return the core module manager, or null if this bundle is not activated
	 */
	public static ModuleManager getModuleManager() {
		Activator instance = Activator.getDefault();
		return instance == null ? null : instance.getModuleManager();
	}

	/**
	 * Returns the {@link MBeanServerConnection} used for module management.
	 * 
	 * @return the {@link MBeanServerConnection} used for module management
	 */
	public static MBeanServerConnection getMBeanServerConnection() {
		return ManagementFactory.getPlatformMBeanServer();
	}
	
	/**
	 * {@link IModuleAttributeSupport} which enables interaction with the module attributes.
	 * 
	 * @return a module attribute support interface, or null if this bundle is not activated
	 */
	public static IModuleAttributeSupport getModuleAttributeSupport() {
		Activator instance = Activator.getDefault();
		return instance == null ? null : instance.getModuleAttributeSupport();
	}

}
