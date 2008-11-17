/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.util.HashSet;
import java.util.Set;

/**
 * Element info for IMethod elements.
 */
public class RubyMethodElementInfo extends MemberElementInfo {

	protected String selector;
	protected int visibility;
	private Set<String> blockVars = new HashSet<String>();
	
	/**
	 * For a source method (that is, a method contained in a compilation unit)
	 * this is a collection of the names of the parameters for this method, in
	 * the order the parameters are declared. For a binary method (that is, a
	 * method declared in a binary type), these names are invented as "arg"i
	 * where i starts at 1. This is an empty array if this method has no
	 * parameters.
	 */
	protected String[] argumentNames;
	private boolean isSingleton;

	public String[] getArgumentNames() {
		return this.argumentNames;
	}

	public String getSelector() {
		return this.selector;
	}
	
	public int getVisibility() {
		return this.visibility;
	}
	
	protected void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public boolean isConstructor() {
		return selector == "initialize";
	}

	protected void setArgumentNames(String[] names) {
		this.argumentNames = names;
	}

	protected void setIsSingleton(boolean b) {
		isSingleton = b;		
	}
	
	public boolean isSingleton() {
		return isSingleton || isConstructor();
	}

	public void addBlockVar(String name) {
		blockVars.add(name);
	}

	public String[] getBlockVars() {
		return blockVars.toArray(new String[blockVars.size()]);
	}
}
