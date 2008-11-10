package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

public interface IVMInstallType {

	/**
	 * Finds the VM with the given name.
	 * 
	 * @param name the VM name
	 * @return a VM instance, or <code>null</code> if not found
	 * @since 0.9.0
	 */
	IVMInstall findVMInstallByName(String vmName);

	/**
	 * Returns the globally unique id of this VM type.
	 * Clients are responsible for providing a unique id.
	 * 
	 * @return the id of this IVMInstallType
	 */ 
	String getId();

	/**
	 * Returns all VM instances managed by this VM type.
	 * 
	 * @return the list of VM instances managed by this VM type
	 */
	IVMInstall[] getVMInstalls();

	/**
	 * Validates the given location of a VM installation.
	 * <p>
	 * For example, an implementation might check whether the VM executable 
	 * is present.
	 * </p>
	 * 
	 * @param installLocation the root directory of a potential installation for
	 *   this type of VM
	 * @return a status object describing whether the install location is valid
	 */
	IStatus validateInstallLocation(File installLocation);

	/**
	 * Finds the VM with the given id.
	 * 
	 * @param id the VM id
	 * @return a VM instance, or <code>null</code> if not found
	 */
	IVMInstall findVMInstall(String id);

	/**
	 * Returns a collection of <code>IPath</code>s that represent the
	 * default system libraries of this VM install type, if a VM was installed
	 * at the given <code>installLocation</code>.
	 * The returned <code>IPath</code>s may not exist if the
	 * <code>installLocation</code> is not a valid install location.
	 * 
	 * @param installLocation home location
	 * @see IVMInstallType#validateInstallLocation(File)
	 * 
	 * @return default library locations based on the given <code>installLocation</code>.
	 * @since 0.9.0
	 */
	IPath[] getDefaultLibraryLocations(File installLocation);

	/**
	 * Returns the display name of this VM type.
	 * 
	 * @return the name of this IVMInstallType
	 */ 
	String getName();

	/**
	 * Remove the VM associated with the given id from the set of VMs managed by
	 * this VM type. Has no effect if a VM with the given id is not currently managed
	 * by this type.
	 * A VM install that is disposed may not be used anymore.
	 * 
	 * @param id the id of the VM to be disposed.
	 */
	void disposeVMInstall(String id);

	/**
	 * Creates a new instance of this VM Install type.
	 * The newly created IVMInstall is managed by this IVMInstallType.
	 * 
	 * @param	id	An id String that must be unique within this IVMInstallType.
	 * 
	 * @return the newly created VM instance
	 * 
	 * @throws	IllegalArgumentException	If the id exists already.
	 */
	IVMInstall createVMInstall(String id);
	
	/**
	 * Tries to detect an installed VM that matches this VM install type.
	 * Typically, this method will detect the VM installation found by "which ruby" on *nix systems.
	 * Implementers should return <code>null</code> if they
	 * can't assure that a given vm install matches this IVMInstallType.
	 * @return The location of an VM installation that can be used
	 * 			with this VM install type, or <code>null</code> if unable
	 * 			to locate an installed VM.
	 */
	public File detectInstallLocation();

	File findExecutable(File installLocation);

	String getVMVersion(File installLocation, File executable);
	
	String getVMPlatform(File installLocation, File executable);

}
