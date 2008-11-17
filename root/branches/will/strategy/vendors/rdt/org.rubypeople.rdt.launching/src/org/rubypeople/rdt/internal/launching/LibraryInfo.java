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
package org.rubypeople.rdt.internal.launching;


/**
 * Stores the boot path and extension directories associated with a VM.
 */
public class LibraryInfo {

	private String fVersion;
	private String[] fBootpath;
	
	public LibraryInfo(String version, String[] bootpath) {
		fVersion = version;
		fBootpath = bootpath;
	}
	
	/**
	 * Returns the version of this VM install.
	 * 
	 * @return version
	 */
	public String getVersion() {
		return fVersion; 
	}
		
	/**
	 * Returns a collection of bootpath entries for this VM install.
	 * 
	 * @return a collection of absolute paths
	 */
	public String[] getBootpath() {
		return fBootpath;
	}
}
