package org.rubypeople.rdt.launching;

import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public interface IVMInstallChangedListener {


	/**
	 * Property constant indicating the name associated
	 * with a VM install has changed.
	 */
	public static final String PROPERTY_NAME = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_NAME"; //$NON-NLS-1$
	
	/**
	 * Property constant indicating the install location of
	 * a VM install has changed.
	 */
	public static final String PROPERTY_INSTALL_LOCATION = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_INSTALL_LOCATION";	 //$NON-NLS-1$
	
	/**
	 * Property constant indicating the library locations associated
	 * with a VM install have changed.
	 */
	public static final String PROPERTY_LIBRARY_LOCATIONS = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_LIBRARY_LOCATIONS"; //$NON-NLS-1$

	/**
	 * Property constant indicating the VM arguments associated
	 * with a VM install has changed.
     * 
     * @since 0.9.0
	 */
	public static final String PROPERTY_VM_ARGUMENTS = LaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_VM_ARGUMENTS"; //$NON-NLS-1$

	
	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current);

	public void vmChanged(PropertyChangeEvent event);

	public void vmAdded(IVMInstall newVm);

	public void vmRemoved(IVMInstall removedVm);
}
