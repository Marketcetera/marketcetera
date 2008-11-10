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


import org.eclipse.debug.core.DebugPlugin;

/**
 * The execution arguments for running a Java VM. The execution arguments are
 * separated into two parts: arguments to the VM itself, and arguments to the Java 
 * main program. This class provides convenience methods for parsing a string
 * of arguments into separate components.
 * <p>
 * Clients may instantiate this class; it is not intended to be subclassed.
 * </p>
 */
public class ExecutionArguments {
	private String fVMArgs;
	private String fProgramArgs;
		
	/**
	 * Creates a new execution arguments object.
	 *
	 * @param vmArgs command line argument string passed to the VM
	 * @param programArgs command line argument string passed to the program
	 */
	public ExecutionArguments(String vmArgs, String programArgs) {
		if (vmArgs == null || programArgs == null)
			throw new IllegalArgumentException();
		fVMArgs= vmArgs;
		fProgramArgs= programArgs;
	}
	
	/**
	 * Returns the VM arguments as one string.
	 *
	 * @return the VM arguments as one string
	 */
	public String getVMArguments() {
		return fVMArgs;
	}
	
	/**
	 * Returns the program arguments as one string.
	 *
	 * @return the program arguments as one string
	 */
	public String getProgramArguments() {
		return fProgramArgs;
	}
	
	/**
	 * Returns the VM arguments as an array of individual arguments.
	 *
	 * @return the VM arguments as an array of individual arguments
	 */
	public String[] getVMArgumentsArray() {
		return DebugPlugin.parseArguments(fVMArgs);
	}
	
	/**
	 * Returns the program arguments as an array of individual arguments.
	 *
	 * @return the program arguments as an array of individual arguments
	 */
	public String[] getProgramArgumentsArray() {
		return DebugPlugin.parseArguments(fProgramArgs);
	}	
			
}
