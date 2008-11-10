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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.launching.IllegalCommandException;



/**
 * An implementation of IVMInstall that is used for manipulating VMs without necessarily 
 * committing changes.
 * <p>
 * Instances of this class act like wrappers.  All other instances of IVMInstall represent 
 * 'real live' VMs that may be used for building or launching.  Instances of this class
 * behave like 'temporary' VMs that are not visible and not available for building or launching.
 * </p>
 * <p>
 * Instances of this class may be constructed as a preliminary step to creating a 'live' VM
 * or as a preliminary step to making changes to a 'real' VM.
 * </p>
 * When <code>convertToRealVM</code> is called, a corresponding 'real' VM is created
 * if one did not previously exist, or the corresponding 'real' VM is updated.
 * </p>
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 * 
 * @since 0.9.0
 */
public class VMStandin extends AbstractVMInstall {
    
    /**
     * <code>java.version</code> system property, or <code>null</code>
     * @since 0.9.0
     */
    private String fRubyVersion = null;
	private String fPlatform;

	/*
	 * @see org.eclipse.jdt.launching.AbstractVMInstall#AbstractVMInstall(org.eclipse.jdt.launching.IVMInstallType, java.lang.String)
	 */
	public VMStandin(IVMInstallType type, String id) {
		super(type, id);
		setNotify(false);
	}
	
	/**
	 * Constructs a copy of the specified VM with the given identifier.
	 * 
	 * @param sourceVM
	 * @param id
	 * @since 3.2
	 */
	public VMStandin(IVMInstall sourceVM, String id) {
		super(sourceVM.getVMInstallType(), id);
		setNotify(false);
		init(sourceVM);
	}
	
	/**
	 * Construct a <code>VMStandin</code> instance based on the specified <code>IVMInstall</code>.
	 * Changes to this standin will not be reflected in the 'real' VM until <code>convertToRealVM</code>
	 * is called.
	 * 
	 * @param realVM the 'real' VM from which to construct this standin VM
	 */
	public VMStandin(IVMInstall realVM) {
		this (realVM.getVMInstallType(), realVM.getId());
		init(realVM);
	}

	/**
	 * Initializes the settings of this standin based on the settings in the given
	 * VM install.
	 * 
	 * @param realVM VM to copy settings from
	 */
	private void init(IVMInstall realVM) {
		setName(realVM.getName());
		setInstallLocation(realVM.getInstallLocation());
		setLibraryLocations(realVM.getLibraryLocations());
		setVMArgs(realVM.getVMArgs());
	    fRubyVersion = realVM.getRubyVersion();			
	    fPlatform = realVM.getPlatform();
	}
	
	/**
	 * If no corresponding 'real' VM exists, create one and populate it from this standin instance. 
	 * If a corresponding VM exists, update its attributes from this standin instance.
	 * 
	 * @return IVMInstall the 'real' corresponding to this standin VM
	 */
	public IVMInstall convertToRealVM() {
		IVMInstallType vmType= getVMInstallType();
		IVMInstall realVM= vmType.findVMInstall(getId());
		boolean notify = true;
		
		if (realVM == null) {
			realVM= vmType.createVMInstall(getId());
			notify = false;
		}
		// do not notify of property changes on new VMs
		if (realVM instanceof AbstractVMInstall) {
			 ((AbstractVMInstall)realVM).setNotify(notify);
		}
		realVM.setName(getName());
		realVM.setInstallLocation(getInstallLocation());
		realVM.setLibraryLocations(getLibraryLocations());
		realVM.setVMArgs(getVMArgs());
		
		if (realVM instanceof AbstractVMInstall) {
			 ((AbstractVMInstall)realVM).setNotify(true);
		}		
		if (!notify) {
			RubyRuntime.fireVMAdded(realVM);
		}
		return realVM;
	}
		
    /* (non-Javadoc)
     * @see org.rubypeople.rdt.launching.IVMInstall#getRubyVersion()
     */
    public String getRubyVersion() {
        return fRubyVersion;
    }
    
    public String getPlatform() {
    	return fPlatform;
    }

	public Process exec(List commandLine, File workingDirectory)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCommand() throws IllegalCommandException {
		// TODO Auto-generated method stub
		return null;
	}
}
