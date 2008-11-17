package org.rubypeople.rdt.launching;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public abstract class AbstractRubyLaunchConfigurationDelegate extends
		LaunchConfigurationDelegate {
	
	/**
	 * Throws a core exception with an error status object built from the given
	 * message, lower level exception, and error code.
	 * 
	 * @param message
	 *            the status message
	 * @param exception
	 *            lower level exception associated with the error, or
	 *            <code>null</code> if none
	 * @param code
	 *            error code
	 * @throws CoreException
	 *             the "abort" core exception
	 */
	protected void abort(String message, Throwable exception, int code)
			throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin
				.getUniqueIdentifier(), code, message, exception));
	}
	
	/**
	 * Returns the VM arguments specified by the given launch configuration, as
	 * a string. The returned string is empty if no VM arguments are specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the VM arguments specified by the given launch configuration,
	 *         possibly an empty string
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException {
		String arguments = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, ""); //$NON-NLS-1$
		String args = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(arguments);
		return args;
	}

	/**
	 * Returns the program arguments specified by the given launch
	 * configuration, as a string. The returned string is empty if no program
	 * arguments are specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the program arguments specified by the given launch
	 *         configuration, possibly an empty string
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public String getProgramArguments(ILaunchConfiguration configuration)
			throws CoreException {
		String arguments = configuration.getAttribute(
				IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, ""); //$NON-NLS-1$
		return VariablesPlugin.getDefault().getStringVariableManager()
				.performStringSubstitution(arguments);
	}
	
	/** 
	 * Returns an array of environment variables to be used when
	 * launching the given configuration or <code>null</code> if unspecified.
	 * 
	 * @param configuration launch configuration
	 * @throws CoreException if unable to access associated attribute or if
	 * unable to resolve a variable in an environment variable's value
	 * @since 0.9.0
	 */	
	public String[] getEnvironment(ILaunchConfiguration configuration) throws CoreException {
		return DebugPlugin.getDefault().getLaunchManager().getEnvironment(configuration);
	}
	
	/**
	 * Verifies the working directory specified by the given launch
	 * configuration exists, and returns the working directory, or
	 * <code>null</code> if none is specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the working directory specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public File verifyWorkingDirectory(ILaunchConfiguration configuration)
			throws CoreException {
		IPath path = getWorkingDirectoryPath(configuration);
		if (path == null) {
			File dir = getDefaultWorkingDirectory(configuration);
			if (dir != null) {
				if (!dir.isDirectory()) {
					abort(
							MessageFormat.format(
									LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_Working_directory_does_not_exist___0__12, 
									dir.toString()),
									null,
									IRubyLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST); 
				}
				return dir;
			}
		} else {
			if (path.isAbsolute()) {
				File dir = new File(path.toOSString());
				if (dir.isDirectory()) {
					return dir;
				}
				// This may be a workspace relative path returned by a variable.
				// However variable paths start with a slash and thus are thought to
				// be absolute
				IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
				if (res instanceof IContainer && res.exists()) {
					return res.getLocation().toFile();
				}
				abort(
					MessageFormat
							.format(
									LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_Working_directory_does_not_exist___0__12, 
									path.toString()),
					null,
					IRubyLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST); 
			} else {
				IResource res = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(path);
				if (res instanceof IContainer && res.exists()) {
					return res.getLocation().toFile();
				}
				abort(
					MessageFormat
							.format(
									LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_Working_directory_does_not_exist___0__12, 
									path.toString()),
					null,
					IRubyLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST); 
			}
		}
		return null;
	}
	
	/**
	 * Returns the working directory path specified by the given launch
	 * configuration, or <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the working directory path specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public IPath getWorkingDirectoryPath(ILaunchConfiguration configuration)
			throws CoreException {
		String path = configuration.getAttribute(
				IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				(String) null);
		if (path != null && path.length() > 0) {
			path = VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(path);
			return new Path(path);
		}
		return null;
	}
	
	/**
	 * Returns the default working directory for the given launch configuration,
	 * or <code>null</code> if none. Subclasses may override as necessary.
	 * 
	 * @param configuration
	 * @return default working directory or <code>null</code> if none
	 * @throws CoreException if an exception occurs computing the default working
	 * 	 directory
	 * @since 0.9.0
	 */
	protected File getDefaultWorkingDirectory(ILaunchConfiguration configuration) throws CoreException {
		// default working directory is the project if this config has a project
		IRubyProject rp = getRubyProject(configuration);
		if (rp != null) {
			IProject p = rp.getProject();
			return p.getLocation().toFile();
		}
		return null;
	}
	
	/**
	 * Returns the Ruby project specified by the given launch configuration, or
	 * <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the Ruby project specified by the given launch configuration, or
	 *         <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public IRubyProject getRubyProject(ILaunchConfiguration configuration)
			throws CoreException {
		String projectName = getRubyProjectName(configuration);
		if (projectName != null) {
			projectName = projectName.trim();
			if (projectName.length() > 0) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName);
				IRubyProject rubyProject = RubyCore.create(project);
				if (rubyProject != null && rubyProject.exists()) {
					return rubyProject;
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the Ruby project name specified by the given launch
	 * configuration, or <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the Ruby project name specified by the given launch
	 *         configuration, or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public String getRubyProjectName(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME,
				(String) null);
	}
	
	/**
	 * Returns the Map of VM-specific attributes specified by the given launch
	 * configuration, or <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the <code>Map</code> of VM-specific attributes
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public Map getVMSpecificAttributesMap(ILaunchConfiguration configuration)
			throws CoreException {
		Map map = configuration
				.getAttribute(
						IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP,
						(Map) null);
		return map;
	}
	
	/**
	 * Returns the VM runner for the given launch mode to use when launching the
	 * given configuration.
	 *  
	 * @param configuration launch configuration
	 * @param mode launch node
	 * @return VM runner to use when launching the given configuration in the given mode
	 * @throws CoreException if a VM runner cannot be determined
	 * @since 0.9.0
	 */
	public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException {
		IVMInstall vm = verifyVMInstall(configuration);
		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null) {
			abort(MessageFormat.format(LaunchingMessages.JavaLocalApplicationLaunchConfigurationDelegate_0, vm.getName(), mode), null, IRubyLaunchConfigurationConstants.ERR_VM_RUNNER_DOES_NOT_EXIST); 
		}
		return runner;
	}
	
	/**
	 * Verifies the VM install specified by the given launch configuration
	 * exists and returns the VM install.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the VM install specified by the given launch configuration
	 * @exception CoreException
	 *                if unable to retrieve the attribute, the attribute is
	 *                unspecified, or if the home location is unspecified or
	 *                does not exist
	 */
	public IVMInstall verifyVMInstall(ILaunchConfiguration configuration)
			throws CoreException {
		IVMInstall vm = getVMInstall(configuration);
		if (vm == null) {
			abort(
					LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_The_specified_JRE_installation_does_not_exist_4, 
					null,
					IRubyLaunchConfigurationConstants.ERR_VM_INSTALL_DOES_NOT_EXIST); 
		}
		File location = vm.getInstallLocation();
		if (location == null) {
			abort(
					MessageFormat
							.format(
									LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_JRE_home_directory_not_specified_for__0__5, 
									vm.getName()),
					null,
					IRubyLaunchConfigurationConstants.ERR_VM_INSTALL_DOES_NOT_EXIST); 
		}
		if (!location.exists()) {
			abort(
					MessageFormat
							.format(
									LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_JRE_home_directory_for__0__does_not_exist___1__6, 
									vm.getName(), location.getAbsolutePath()),
					null,
					IRubyLaunchConfigurationConstants.ERR_VM_INSTALL_DOES_NOT_EXIST); 
		}
		return vm;
	}
	
	/**
	 * Returns the VM install specified by the given launch configuration, or
	 * <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the VM install specified by the given launch configuration, or
	 *         <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public IVMInstall getVMInstall(ILaunchConfiguration configuration)
			throws CoreException {
		return RubyRuntime.computeVMInstall(configuration);
	}
	
	/**
	 * Verifies a main type name is specified by the given launch configuration,
	 * and returns the main type name.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the main type name specified by the given launch configuration
	 * @exception CoreException
	 *                if unable to retrieve the attribute or the attribute is
	 *                unspecified
	 */
	public String verifyFileToLaunch(ILaunchConfiguration configuration) throws CoreException {
		String name = getFileToLaunch(configuration);
		if (name == null) {
			abort(
					LaunchingMessages.AbstractJavaLaunchConfigurationDelegate_Main_type_not_specified_11, 
					null,
					IRubyLaunchConfigurationConstants.ERR_UNSPECIFIED_FILE_NAME); 
		}
		
		File file = new File(name);
		if (file.isFile()) { // it was an absolute path and the file exists, so just return it
			return name;
		}
		
		IPath workingDir = getWorkingDirectoryPath(configuration);
		if (workingDir != null) {
			IPath fileToLaunch = workingDir.append(name);
			if (fileToLaunch.toFile().isFile())
				return name;
		}
		
		IRubyProject project = getRubyProject(configuration);
		if (project != null) {
			IPath fileToLaunch = project.getProject().getLocation().append(name);
			if (fileToLaunch.toFile().isFile())
				return fileToLaunch.toOSString();
		}
		
		abort(
				"File to launch does not exist: " + name, 
				null,
				IRubyLaunchConfigurationConstants.ERR_UNSPECIFIED_FILE_NAME); 
		return null;
	}
	
	/**
	 * Returns the main type name specified by the given launch configuration,
	 * or <code>null</code> if none.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the main type name specified by the given launch configuration,
	 *         or <code>null</code> if none
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public String getFileToLaunch(ILaunchConfiguration configuration)
			throws CoreException {
		String mainType = configuration.getAttribute(
				IRubyLaunchConfigurationConstants.ATTR_FILE_NAME,
				(String) null);
		if (mainType == null) {
			return null;
		}
		return VariablesPlugin.getDefault().getStringVariableManager()
				.performStringSubstitution(mainType);
	}
	
	protected void setDefaultSourceLocator(ILaunch launch,
			ILaunchConfiguration configuration) {
		// TODO Actually set up the source locator!
		
	}
	
	/**
	 * Returns the entries that should appear on the user portion of the
	 * classpath as specified by the given launch configuration, as an array of
	 * resolved strings. The returned array is empty if no classpath is
	 * specified.
	 * 
	 * @param configuration
	 *            launch configuration
	 * @return the classpath specified by the given launch configuration,
	 *         possibly an empty array
	 * @exception CoreException
	 *                if unable to retrieve the attribute
	 */
	public String[] getLoadpath(ILaunchConfiguration configuration)
			throws CoreException {
		IRuntimeLoadpathEntry[] entries = RubyRuntime
				.computeUnresolvedRuntimeLoadpath(configuration);
		entries = RubyRuntime.resolveRuntimeLoadpath(entries, configuration);
		List userEntries = new ArrayList(entries.length);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getLoadpathProperty() == IRuntimeLoadpathEntry.USER_CLASSES) {
				String location = entries[i].getLocation();
				if (location != null) {
					userEntries.add(location);
				}
			}
		}
		return (String[]) userEntries.toArray(new String[userEntries.size()]);
	}
	
	public boolean getIsSudo(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_IS_SUDO, false);
	}
	
	public String getSudoMessage(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_SUDO_MESSAGE, (String) null);
	}
}
