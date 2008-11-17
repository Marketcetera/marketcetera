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
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.Assert;


public class CPVariableElement {

	private String fName;
	private IPath[] fPath;
	
	private boolean fIsReserved;

	public CPVariableElement(String name, IPath[] path, boolean reserved) {
		Assert.isNotNull(name);
		Assert.isNotNull(path);
		fName= name;
		fPath= path;
		fIsReserved= reserved;
	}
	
	/**
	 * Gets the path
	 * @return Returns a IPath
	 */
	public IPath[] getPath() {
		return fPath;
	}

	/**
	 * Sets the path
	 * @param path The path to set
	 */
	public void setPath(IPath[] path) {
		fPath= path;
	}

	/**
	 * Gets the name
	 * @return Returns a String
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Sets the name
	 * @param name The name to set
	 */
	public void setName(String name) {
		fName= name;
	}
	
	/*
	 * @see Object#equals()
	 */	
	public boolean equals(Object other) {
		if (other != null && other.getClass().equals(getClass())) {
			CPVariableElement elem= (CPVariableElement)other;
			return fName.equals(elem.fName);
		}
		return false;
	}
	
	/*
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return fName.hashCode();
	}	
	
	/**
	 * Returns true if variable is reserved
	 * @return Returns a boolean
	 */
	public boolean isReserved() {
		return fIsReserved;
	}

	/**
	 * Sets the isReserved
	 * @param isReserved The state to set
	 */
	public void setReserved(boolean isReserved) {
		fIsReserved= isReserved;
	}


}
