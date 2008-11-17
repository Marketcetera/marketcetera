/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.launching;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
/**
 * Abstract implementation of a VM install.
 * <p>
 * Clients implementing VM installs must subclass this class.
 * </p>
 */
public abstract class AbstractVMInstall implements IVMInstall {

	private IVMInstallType fType;
	private String fId;
	private String fName;
	private File fInstallLocation;
	protected IPath[] fSystemLibraryDescriptions;
	private String fVMArgs;
	// system properties are cached in user preferences prefixed with this key, followed
	// by vm type, vm id, and system property name
	private static final String PREF_VM_INSTALL_SYSTEM_PROPERTY = "PREF_VM_INSTALL_SYSTEM_PROPERTY"; //$NON-NLS-1$
	// whether change events should be fired
	protected boolean fNotify = true;
	private HashMap<String, IVMRunner> fgVMRunners;
	
	/**
	 * Constructs a new VM install.
	 * 
	 * @param	type	The type of this VM install.
	 * 					Must not be <code>null</code>
	 * @param	id		The unique identifier of this VM instance
	 * 					Must not be <code>null</code>.
	 * @throws	IllegalArgumentException	if any of the required
	 * 					parameters are <code>null</code>.
	 */
	public AbstractVMInstall(IVMInstallType type, String id) {
		if (type == null)
			throw new IllegalArgumentException(LaunchingMessages.vmInstall_assert_typeNotNull); 
		if (id == null)
			throw new IllegalArgumentException(LaunchingMessages.vmInstall_assert_idNotNull); 
		fType= type;
		fId= id;
	}

	/* (non-Javadoc)
	 * Subclasses should not override this method.
	 * @see IVMInstall#getId()
	 */
	public String getId() {
		return fId;
	}

	/* (non-Javadoc)
	 * Subclasses should not override this method.
	 * @see IVMInstall#getName()
	 */
	public String getName() {
		return fName;
	}

	/* (non-Javadoc)
	 * Subclasses should not override this method.
	 * @see IVMInstall#setName(String)
	 */
	public void setName(String name) {
		if (!name.equals(fName)) {
			PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_NAME, fName, name);
			fName= name;
			if (fNotify) {
				RubyRuntime.fireVMChanged(event);
			}
		}
	}

	/* (non-Javadoc)
	 * Subclasses should not override this method.
	 * @see IVMInstall#getInstallLocation()
	 */
	public File getInstallLocation() {
		return fInstallLocation;
	}

	/* (non-Javadoc)
	 * Subclasses should not override this method.
	 * @see IVMInstall#setInstallLocation(File)
	 */
	public void setInstallLocation(File installLocation) {
		if (!installLocation.equals(fInstallLocation)) {
			PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_INSTALL_LOCATION, fInstallLocation, installLocation);
			fInstallLocation= installLocation;
			if (fNotify) {
				RubyRuntime.fireVMChanged(event);
			}
		}
	}

	/* (non-Javadoc)
	 * Subclasses should not override this method.
	 * @see IVMInstall#getVMInstallType()
	 */
	public IVMInstallType getVMInstallType() {
		return fType;
	}

	/* (non-Javadoc)
	 * @see IVMInstall#getVMRunner(String)
	 */
	public IVMRunner getVMRunner(String mode) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getLibraryLocations()
	 */
	public IPath[] getLibraryLocations() {
		return fSystemLibraryDescriptions;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.launching.IVMInstall#setLibraryLocations(org.eclipse.core.runtime.IPath[])
	 */
	public void setLibraryLocations(IPath[] locations) {
		// TODO What if we're loading XML and it still has old core stub path? We should use new core stub path
		if (locations == fSystemLibraryDescriptions) {
			return;
		}
		IPath[] newLocations = locations;
		if (newLocations == null) {
			newLocations = getVMInstallType().getDefaultLibraryLocations(getInstallLocation()); 
		}
		IPath[] prevLocations = fSystemLibraryDescriptions;
		if (prevLocations == null) {
			prevLocations = getVMInstallType().getDefaultLibraryLocations(getInstallLocation()); 
		}
		
		if (newLocations.length == prevLocations.length) {
			int i = 0;
			boolean equal = true;
			while (i < newLocations.length && equal) {
				equal = newLocations[i].equals(prevLocations[i]);
				i++;
			}
			if (equal) {
				// no change
				return;
			}
		}

		PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_LIBRARY_LOCATIONS, prevLocations, newLocations);
		fSystemLibraryDescriptions = locations;
		if (fNotify) {
			RubyRuntime.fireVMChanged(event);		
		}
	}

	/**
	 * Whether this VM should fire property change notifications.
	 * 
	 * @param notify
	 * @since 2.1
	 */
	protected void setNotify(boolean notify) {
		fNotify = notify;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
     * @since 2.1
	 */
	public boolean equals(Object object) {
		if (object instanceof IVMInstall) {
			IVMInstall vm = (IVMInstall)object;
			return getVMInstallType().equals(vm.getVMInstallType()) &&
				getId().equals(vm.getId());
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 * @since 2.1
	 */
	public int hashCode() {
		return getVMInstallType().hashCode() + getId().hashCode();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#getDefaultVMArguments()
	 * @since 3.0
	 */
	public String[] getVMArguments() {
		String args = getVMArgs();
		if (args == null) {
		    return null;
		}
		ExecutionArguments ex = new ExecutionArguments(args, ""); //$NON-NLS-1$
		return ex.getVMArgumentsArray();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMInstall#setDefaultVMArguments(java.lang.String[])
	 * @since 3.0
	 */
	public void setVMArguments(String[] vmArgs) {
		if (vmArgs == null) {
			setVMArgs(null);
		} else {
		    StringBuffer buf = new StringBuffer();
		    for (int i = 0; i < vmArgs.length; i++) {
	            String string = vmArgs[i];
	            buf.append(string);
	            buf.append(" "); //$NON-NLS-1$
	        }
			setVMArgs(buf.toString().trim());
		}
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jdt.launching.IVMInstall2#getVMArgs()
     */
    public String getVMArgs() {
        return fVMArgs;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.launching.IVMInstall2#setVMArgs(java.lang.String)
     */
    public void setVMArgs(String vmArgs) {
        if (fVMArgs == null) {
            if (vmArgs == null) {
                // No change
                return;
            }
        } else if (fVMArgs.equals(vmArgs)) {
    		// No change
    		return;
    	}
        PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_VM_ARGUMENTS, fVMArgs, vmArgs);
        fVMArgs = vmArgs;
		if (fNotify) {
			RubyRuntime.fireVMChanged(event);		
		}
    }	
    
    /* (non-Javadoc)
     * Subclasses should override.
     * @see org.rubypeople.rdt.launching.IVMInstall2#getRubyVersion()
     */
    public String getRubyVersion() {
        return null;
    }
    
	/**
	 * Generates a key used to cache system property for this VM in this plug-ins
	 * preference store.
	 * 
	 * @param property system property name
	 * @return preference store key
	 */
	private String getSystemPropertyKey(String property) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(PREF_VM_INSTALL_SYSTEM_PROPERTY);
		buffer.append("."); //$NON-NLS-1$
		buffer.append(getVMInstallType().getId());
		buffer.append("."); //$NON-NLS-1$
		buffer.append(getId());
		buffer.append("."); //$NON-NLS-1$
		buffer.append(property);
		return buffer.toString();
	}
	
	/**
	 * Throws a core exception with an error status object built from the given
	 * message, lower level exception, and error code.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the error, or
	 *            <code>null</code> if none
	 * @param code error code
	 * @throws CoreException the "abort" core exception
	 * @since 3.2
	 */
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin
				.getUniqueIdentifier(), code, message, exception));
	}	
	
	protected IVMRunner getVMRunner(IVMInstall vm, String mode) {
		Map<String, IVMRunner> runners = getVMRunners();
		IVMRunner runner = runners.get(mode);
		if (runner == null) return null;
		runner.setVMInstall(vm);
		return runner;
	}
	
	private Map<String, IVMRunner> getVMRunners() {
		if (fgVMRunners == null) {
			IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, "vmRunners"); //$NON-NLS-1$
			IConfigurationElement[] configs= extensionPoint.getConfigurationElements(); 
			fgVMRunners= new HashMap<String, IVMRunner>();

			for (int i= 0; i < configs.length; i++) {
				try {
					String vmType = configs[i].getAttribute("vmInstallType"); //$NON-NLS-1$
					if (vmType.equals(fType.getId())) {
						String mode = configs[i].getAttribute("mode"); //$NON-NLS-1$
						IVMRunner runner = (IVMRunner)configs[i].createExecutableExtension("class"); //$NON-NLS-1$
						fgVMRunners.put(mode, runner);
					}
				} catch (CoreException e) {
					LaunchingPlugin.log(e);
				}
			}
		}
		return fgVMRunners;
	}
    
}
