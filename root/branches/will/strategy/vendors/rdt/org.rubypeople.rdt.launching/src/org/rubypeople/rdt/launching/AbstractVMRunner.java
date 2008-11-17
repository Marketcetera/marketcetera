/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

/**
 * Abstract implementation of a VM runner.
 * <p>
 * Clients implementing VM runners should subclass this class.
 * </p>
 * @see IVMRunner
 * @since 0.9.0
 */
public abstract class AbstractVMRunner implements IVMRunner {

	protected IVMInstall fVMInstance;
	
	/**
	 * Throws a core exception with an error status object built from
	 * the given message, lower level exception, and error code.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 * @param code error code
	 * @throws CoreException The exception encapsulating the reason for the abort
	 */
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, getPluginIdentifier(), code, message, exception));
	}
	
	/**
	 * Returns the identifier of the plug-in this VM runner 
	 * originated from.
	 * 
	 * @return plug-in identifier
	 */
	protected abstract String getPluginIdentifier();
	
	public void setVMInstall(IVMInstall vm) {
		fVMInstance = vm;
	}
	
	/**
	 * @see DebugPlugin#exec(String[], File)
	 */
	protected Process exec(String[] cmdLine, File workingDirectory) throws CoreException {
		if (cmdLine == null)
		{
			abort("Command line for process exec is null", null, -1);
		}
		LaunchingPlugin.info("Starting: " + getCmdLineAsString(cmdLine));
		return DebugPlugin.exec(cmdLine, workingDirectory);
	}
	
	/**
	 * @since 0.9.0
	 * @see DebugPlugin#exec(String[], File, String[])
	 */
	protected Process exec(String[] cmdLine, File workingDirectory, String[] envp) throws CoreException {
		if (envp == null)
		{
			return exec(cmdLine, workingDirectory);
		}
		LaunchingPlugin.info("Starting: " + getCmdLineAsString(cmdLine));
		return DebugPlugin.exec(cmdLine, workingDirectory, envp);
	}	
	
	/**
	 * Returns the given array of strings as a single space-delimited string.
	 * 
	 * @param cmdLine array of strings
	 * @return a single space-delimited string
	 */
	protected String getCmdLineAsString(String[] cmdLine) {
		if (cmdLine == null)
		{
			return "";
		}
		StringBuffer buff= new StringBuffer();
		for (int i = 0, numStrings= cmdLine.length; i < numStrings; i++) {
			String value = cmdLine[i];
			if (value == null) continue;
			if (value.indexOf(' ') != -1 && !value.startsWith("-")) {
				if (!value.startsWith("\"")) {
					value = "\"" + value;
				}
				if (!value.endsWith("\"")) {
					value = value + "\"";
				}
			}
			buff.append(value);
			buff.append(' ');	
		} 
		return buff.toString().trim();
	}
	
	/**
	 * Returns the default process attribute map for Ruby processes.
	 * 
	 * @return default process attribute map for Ruby processes
	 */
	protected Map<String, String> getDefaultProcessMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(IProcess.ATTR_PROCESS_TYPE, IRubyLaunchConfigurationConstants.ID_RUBY_PROCESS_TYPE);
		return map;
	}
	
	/**
	 * Returns a new process aborting if the process could not be created.
	 * @param launch the launch the process is contained in
	 * @param p the system process to wrap
	 * @param label the label assigned to the process
	 * @param attributes values for the attribute map
	 * @return the new process
	 * @throws CoreException problems occurred creating the process
	 * @since 0.9.0
	 */
	protected IProcess newProcess(ILaunch launch, Process p, String label, Map<String, String> attributes) throws CoreException {
		IProcess process= DebugPlugin.newProcess(launch, p, label, attributes);
		if (process == null) {
			p.destroy();
			abort(LaunchingMessages.AbstractVMRunner_0, null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
		}
		return process;
	}
	
	/**
	 * Combines and returns VM arguments specified by the runner configuration,
	 * with those specified by the VM install, if any.
	 * 
	 * @param configuration runner configuration
	 * @param vmInstall vm install
	 * @return combined VM arguments specified by the runner configuration
	 *  and VM install
	 * @since 0.9.0
	 */
	protected String[] combineVmArgs(VMRunnerConfiguration configuration, IVMInstall vmInstall) {
		String[] launchVMArgs= configuration.getVMArguments();
		String[] vmVMArgs = vmInstall.getVMArguments();
		if (vmVMArgs == null || vmVMArgs.length == 0) {
			return launchVMArgs;
		}
		String[] allVMArgs = new String[launchVMArgs.length + vmVMArgs.length];
		System.arraycopy(launchVMArgs, 0, allVMArgs, 0, launchVMArgs.length);
		System.arraycopy(vmVMArgs, 0, allVMArgs, launchVMArgs.length, vmVMArgs.length);
		return allVMArgs;
	}
}
