package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public interface IVMInstall {
	
	public static final String CYWGIN_PLATFORM = "cygwin";
	public static final String MSWIN32_PLATFORM = "mswin32";

	public File getInstallLocation();

	public void setInstallLocation(File validInstallLocation);

	public String getName();

	public void setName(String newName);

	public IPath[] getLibraryLocations();

	public String getId();

	public IVMInstallType getVMInstallType();

	public void setLibraryLocations(IPath[] paths);

	/**
	 * @deprecated Please use {@link #getVMArgs()} instead
	 * @return
	 */
	public String[] getVMArguments();
	
	public String getVMArgs();
	
	public void setVMArgs(String vmArgs);
	
	public IVMRunner getVMRunner(String mode);
	
	public String getRubyVersion();
	
	public String getPlatform();
	
}