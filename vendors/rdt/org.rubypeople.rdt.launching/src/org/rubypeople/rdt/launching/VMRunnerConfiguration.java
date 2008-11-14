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

 
import java.util.Map;

import org.rubypeople.rdt.internal.launching.LaunchingMessages;

/**
 * Holder for various arguments passed to a VM runner.
 * Mandatory parameters are passed in the constructor; optional arguments, via setters.
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 */
public class VMRunnerConfiguration {
	private String fFileToLaunch;
	private String[] fVMArgs;
	private String[] fProgramArgs;
	private String[] fEnvironment;
	private String[] fLoadPath;
	private String fWorkingDirectory;
	private Map fVMSpecificAttributesMap;
	private boolean fResume = true;
	private boolean fIsSudo;
	private String fSudoMessage;
	
	private static final String[] fgEmpty= new String[0];
	
	/**
	 * Creates a new configuration for launching a VM to run the given main class
	 * using the given load path.
	 *
	 * @param fileToLaunch The fully qualified name of the file to launch. May not be null.
	 * @param loadPath 	The loadpath. May not be null.
	 */
	public VMRunnerConfiguration(String fileToLaunch, String[] loadPath) {
		if (fileToLaunch == null) {
			throw new IllegalArgumentException(LaunchingMessages.vmRunnerConfig_assert_classNotNull); 
		}
		if (loadPath == null) {
			throw new IllegalArgumentException(LaunchingMessages.vmRunnerConfig_assert_classPathNotNull); 
		}
		fFileToLaunch= fileToLaunch;
		fLoadPath= loadPath;
	}

	/**
	 * Sets the <code>Map</code> that contains String name/value pairs that represent
	 * VM-specific attributes.
	 * 
	 * @param map the <code>Map</code> of VM-specific attributes.
	 * @since 2.0
	 */
	public void setVMSpecificAttributesMap(Map map) {
		fVMSpecificAttributesMap = map;
	}

	/**
	 * Sets the custom VM arguments. These arguments will be appended to the list of 
	 * VM arguments that a VM runner uses when launching a VM. Typically, these VM arguments
	 * are set by the user.
	 * These arguments will not be interpreted by a VM runner, the client is responsible for
	 * passing arguments compatible with a particular VM runner.
	 *
	 * @param args the list of VM arguments
	 */
	public void setVMArguments(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException(LaunchingMessages.vmRunnerConfig_assert_vmArgsNotNull); 
		}
		fVMArgs= args;
	}
	
	/**
	 * Sets the custom program arguments. These arguments will be appended to the list of 
	 * program arguments that a VM runner uses when launching a VM (in general: none). 
	 * Typically, these VM arguments are set by the user.
	 * These arguments will not be interpreted by a VM runner, the client is responsible for
	 * passing arguments compatible with a particular VM runner.
	 *
	 * @param args the list of arguments	
	 */
	public void setProgramArguments(String[] args) {
		if (args == null) {
			throw new IllegalArgumentException(LaunchingMessages.vmRunnerConfig_assert_programArgsNotNull); 
		}
		fProgramArgs= args;
	}
	
	/**
	 * Sets the environment for the Ruby program. The Ruby VM will be
	 * launched in the given environment.
	 * 
	 * @param environment the environment for the Ruby program specified as an array
	 *  of strings, each element specifying an environment variable setting in the
	 *  format <i>name</i>=<i>value</i>
	 * @since 0.9.0
	 */
	public void setEnvironment(String[] environment) {
		fEnvironment= environment;
	}
		
	/**
	 * Returns the <code>Map</code> that contains String name/value pairs that represent
	 * VM-specific attributes.
	 * 
	 * @return The <code>Map</code> of VM-specific attributes or <code>null</code>.
	 * @since 2.0
	 */
	public Map getVMSpecificAttributesMap() {
		return fVMSpecificAttributesMap;
	}
	
	/**
	 * Returns the name of the file to launch.
	 *
	 * @return The fully qualified name of the file to launch. Will not be <code>null</code>.
	 */
	public String getFileToLaunch() {
		return fFileToLaunch;
	}
	
	/**
	 * Returns the loadpath.
	 *
	 * @return the loadpath
	 */
	public String[] getLoadPath() {
		return fLoadPath;
	}
	
	/**
	 * Returns the arguments to the VM itself.
	 *
	 * @return The VM arguments. Default is an empty array. Will not be <code>null</code>.
	 * @see #setVMArguments(String[])
	 */
	public String[] getVMArguments() {
		if (fVMArgs == null) {
			return fgEmpty;
		}
		return fVMArgs;
	}
	
	/**
	 * Returns the arguments to the Java program.
	 *
	 * @return The Java program arguments. Default is an empty array. Will not be <code>null</code>.
	 * @see #setProgramArguments(String[])
	 */
	public String[] getProgramArguments() {
		if (fProgramArgs == null) {
			return fgEmpty;
		}
		return fProgramArgs;
	}
	
	/**
	 * Returns the environment for the Ruby program or <code>null</code>
	 * 
	 * @return The Ruby program environment. Default is <code>null</code>
	 * @since 0.9.0
	 */
	public String[] getEnvironment() {
		return fEnvironment;
	}
	
	/**
	 * Sets the working directory for a launched VM.
	 * 
	 * @param path the absolute path to the working directory
	 *  to be used by a launched VM, or <code>null</code> if
	 *  the default working directory is to be inherited from the
	 *  current process
	 * @since 0.9.0
	 */
	public void setWorkingDirectory(String path) {
		fWorkingDirectory = path;
	}
	
	/**
	 * Returns the working directory of a launched VM.
	 * 
	 * @return the absolute path to the working directory
	 *  of a launched VM, or <code>null</code> if the working
	 *  directory is inherited from the current process
	 * @since 2.0
	 */
	public String getWorkingDirectory() {
		return fWorkingDirectory;
	}	
	
	/**
	 * Sets whether the VM is resumed on startup when launched in
	 * debug mode. Has no effect when not in debug mode.
	 *  
	 * @param resume whether to resume the VM on startup
	 * @since 3.0
	 */
	public void setResumeOnStartup(boolean resume) {
		fResume = resume;
	}
	
	/**
	 * Returns whether the VM is resumed on startup when launched
	 * in debug mode. Has no effect when no in debug mode. Default
	 * value is <code>true</code> for backwards compatibility.
	 * 
	 * @return whether to resume the VM on startup
	 * @since 3.0
	 */
	public boolean isResumeOnStartup() {
		return fResume;
	}

	public void setIsSudo(boolean isSudo) {
		fIsSudo = isSudo;		
	}
	
	public boolean isSudo() {
		return fIsSudo;
	}
	
	public void setSudoMessage(String message) {
		fSudoMessage = message;		
	}

	public String getSudoMessage() {
		if (fSudoMessage == null)
			return "Please enter your password for running command under sudo. You are being asked because some commands require access to change protected files and directories on the system.";
		return fSudoMessage;
	}
}
